const mongoose = require('mongoose');

const UserEventSchema = new mongoose.Schema({
    name: {
        type: String,
        required: true
    },
    location: {
        type: String,
        required: true
    }
});

module.exports = UserEventSchema;