window.getLocation = function(successCallback, errorCallback) {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position) {
            successCallback(position.coords.latitude, position.coords.longitude);
        }, function(error) {
            errorCallback(error.message);
        });
    } else {
        errorCallback("Geolocation is not supported by this browser.");
    }
}