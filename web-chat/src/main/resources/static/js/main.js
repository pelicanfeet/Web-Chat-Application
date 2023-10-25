'use strict';

var usernamePage = document.querySelector('#connect-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageBox = document.querySelector('#messageBox');
var timestamp;
var replay = null;

var stompClient = null;
var username = null;

function connect(event) {
    username = document.querySelector('#name').value.trim();

	/*
	If messages have been sent, set replay to true.
	*/
    if(username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        var socket = new SockJS('/chat');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected);
    }
    event.preventDefault();
}

function onConnected() {
    // Subscribes to the public topic
    stompClient.subscribe('/topic/public', onMessageReceived);

	if(replay) {
		stompClient.send("/app/chat.replay", {});
		replay = null;
	}
    // Sends username to the server
    stompClient.send("/app/chat.register",
        {},
        JSON.stringify({sender: username, type: 'CONNECT'})
    )

}

function send(event) {
    var messageContent = messageInput.value.trim();
    var date = new Date();
    timestamp = date.toLocaleTimeString();

    if(messageContent && stompClient) {
        var message = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT',
            timestampString: timestamp
        };

        stompClient.send("/app/chat.send", {}, JSON.stringify(message));
        messageInput.value = '';
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    var date = new Date();
    timestamp = date.toLocaleTimeString();

    var messageElement = document.createElement('li');

    if(message.type === 'CONNECT') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' has entered the chat.';
    }
    else if (message.type === 'DISCONNECT') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' has left the chat.';
    }
    else {
    	messageElement.classList.add('chat-message');
        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    // We want to append the timestamp to the message
    var messageText = document.createTextNode(message.content + ' (' + timestamp + ')');
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageBox.appendChild(messageElement);
    messageBox.scrollTop = messageBox.scrollHeight;
}

usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', send, true)