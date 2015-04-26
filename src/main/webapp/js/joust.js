"use strict";

var stompClient = null;

function setConnected(connected) {
}

function connect(callback) {
    var socket = new SockJS('/test');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        setConnected(true);
        stompClient.subscribe('/topic/slots', function(slot) {
        	slot = JSON.parse(slot.body);
            console.log('Teams: ' + slot.teamIds);
            callback(slot.teamIds);
        });
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
        stompClient = null;
    }
    setConnected(false);
}

// @param filename The name of the file WITHOUT ending
function playSound(element, filename) {
    document.getElementById(element).innerHTML =
        '<audio autoplay="autoplay">'
      + '    <source src="' + filename + '.mp3" type="audio/mpeg" />'
      + '    <source src="' + filename + '.ogg" type="audio/ogg" />'
      + '    <embed hidden="true" autostart="true" loop="false" src="' + filename +'.mp3" />'
      + '</audio>';
}
