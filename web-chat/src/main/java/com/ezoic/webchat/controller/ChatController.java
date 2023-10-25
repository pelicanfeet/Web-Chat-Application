package com.ezoic.webchat.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.ezoic.webchat.model.Message;

@Controller
public class ChatController {
	
	List<Message> messageLog = new ArrayList<>();

	/* Given the configuration specified via the WebSocketConfig class,
	** any message starting with "/app" will be routed to the message
	** handling methods annotated with '@MessageMapping'.
	** So, a message with the destination "/app/chat.register" will be
	** routed to this method.
	*/
	@MessageMapping("/chat.register")
	@SendTo("/topic/public")
	public Message register(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
		headerAccessor.getSessionAttributes().put("username", message.getSender());
		// Loop through the message log and display older messages in order,
		// if there are any
		if(messageLog.size() > 0) {
			replayChat();
		}
		return message;
	}

	/* This method is responsible for adding the username into the WebSocket session.
	** A message with the destination "/app/chat.send" will be routed to this method.
	*/
	@MessageMapping("/chat.send")
	@SendTo("/topic/public")
	public Message sendMessage(@Payload Message message) {
		messageLog.add(message);
		return message;
	}
	
	@MessageMapping("/chat.replay")
	@SendTo("/topic/public")
	public Message sendOldMessage(@Payload Message message) {
		return message;
	}
	
	public void replayChat() {
		for(Message m : messageLog) {
			// Add to the chat window
			System.out.println("Message sender: " + m.getSender());
			System.out.println("Message contents: " + m.getContent());
			System.out.println("Message timestamp: " + m.getTimestampString());
			sendOldMessage(m);
		}
	}

}