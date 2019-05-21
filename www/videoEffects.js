var exec = require('cordova/exec');

exports.showVideoEffects = function (arg0, success, error) {
    exec(success, error, 'callPlugin', 'showCamera', [arg0]);
};
