const path = require('path');
const Boom = require('boom');
const mongoose = require('mongoose');
const joi = require('joi');
const _ = require('lodash');

const User = mongoose.model('User');
const UserEventSchema = require(path.resolve('models/user_event_schema'));
const UserEvent = mongoose.model('UserEvent', UserEventSchema);

const endpoints = [
    {
        method: 'GET',
        path: '/events',
        config: {
            auth: 'jwt'
        },
        handler: async function (request, h) {
            const loggedInUser = request.user;
            return loggedInUser.events;
        }
    },
    {
        method: 'POST',
        path: '/events',
        config: {
            auth: 'jwt',
            validate: {
                payload: {
                    name: joi.string().min(5).required(),
                    location: joi.string().min(5).required()
                }
            }
        },
        handler: async function (request, h) {
            const loggedInUser = request.user;
            const userEvent = new UserEvent(request.payload);
            loggedInUser.events.push(userEvent);
            try {
                const modifiedUser = await loggedInUser.save();
                return modifiedUser.events;
            } catch (e) {
                return Boom.badRequest(e.message);
            }
        }
    },
    {
        method: 'DELETE',
        path: '/events/{eventId}',
        config: {
            auth: 'jwt',
            validate: {
                params: {
                    eventId: joi.string().required()
                }
            }
        },
        handler: async function (request, h) {
            const loggedInUser = request.user;
            const eventIndex = _.findIndex(loggedInUser.events, function (currentEvent) {
                return (currentEvent._id.toString() === request.params.eventId);
            });
            if (eventIndex === -1) {
                return Boom.badRequest('The event you are trying to delete cannot be found');
            }
            loggedInUser.events.splice(eventIndex, 1);
            try {
                const modifiedUser = await loggedInUser.save();
                return modifiedUser.events;
            } catch (e) {
                return Boom.badRequest(e.message);
            }
        }
    }
];

module.exports = endpoints;