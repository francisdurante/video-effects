var exec = require('cordova/exec');

exports.showVideoEffects = function (arg0,method, success, error) {
    exec(success, error, 'videoAndImagePicker', method, [arg0]);
};
