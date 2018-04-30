const path = require('path');
const Utils = require(path.resolve('./utils/utils'));
const AWS = require('aws-sdk');
const mime = require('mime');
const randomString = require('randomstring');
const fs = require('fs');
const joi = require('joi');

const config = require(path.resolve('config/config'));

const s3 = new AWS.S3({
    endpoint: new AWS.Endpoint(config.cloudFiles.endpoint),
    accessKeyId: config.cloudFiles.accessKeyId,
    secretAccessKey: config.cloudFiles.secretAccessKey
});

const endpoints = [
    {
        method: 'GET',
        path: '/profile',
        config: {
            auth: 'jwt'
        },
        handler: async function (req, h) {
            return Utils.sanitizeUser(req.user);
        }
    },
    {
        method: 'POST',
        path: '/profile/picture',
        config: {
            auth: 'jwt',
            payload: {
                parse: true,
                output: 'stream',
                allow: 'multipart/form-data',
                maxBytes: 3 * 1024 * 1024 //3MB
            },
            validate: {
                payload: {
                    stream: joi.object({
                        pipe: joi.func().required()
                    }).unknown()
                }
            }
        },
        handler: async function (req, h) {
            const currentLoggedInUser = req.user;
            try {
                const profilePictureUrl = await uploadProfilePicture(req.payload);
                currentLoggedInUser.profilePicture = profilePictureUrl;
                const modifiedUser = await currentLoggedInUser.save();
                return Utils.sanitizeUser(modifiedUser);
            } catch (e) {
                return Boom.badRequest(e.message);
            }
        }
    }
];

function uploadProfilePicture(data) {
    return new Promise((resolve, reject) => {
        if (data && data.stream && data.stream.hapi && data.stream.hapi.filename) {
            const fileName = data.stream.hapi.filename;
            let fileExtension = mime.getExtension(mime.getType(fileName));
            if (!fileExtension) {
                return reject(new Error('file extension not supplied.'));
            }

            if (!fileExtension.match(/(jpg|jpeg|png)$/i)) {
                return reject(new Error('File extension not supported.'));
            }

            let name = `${randomString.generate()}.${fileExtension}`;
            let picturePath = `./uploads/${name}`;
            let file = fs.createWriteStream(picturePath);

            data.stream.pipe(file);

            data.stream.on('end', (error) => {
                if (error) {
                    return reject(error);
                }
                s3.upload({
                    Body: fs.createReadStream(picturePath),
                    Bucket: `${config.cloudFiles.bucket}`,
                    Key: `${name}`,
                    ACL: 'public-read'
                }, (error, data) => {
                    fs.unlink(picturePath, () => {
                    });
                    if (error) {
                        return reject(error);
                    }
                    if (!error && data && data.Location) {
                        return resolve(data.Location);
                    }
                });
            });
        }
    });
}

module.exports = endpoints;