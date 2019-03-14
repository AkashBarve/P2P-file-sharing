package com.messages;

import com.MessageType;

/**
 * Author: @satyaabhiram
 */
public class MessageHandler {
	private MessageType type;
	private byte[] payload;

	public MessageHandler(MessageType type) {
		this.type = type;
	}

	public MessageHandler(MessageType type, byte[] pl) {
		this.type = type;
		this.payload = pl;
	}

	public Message buildMessage() throws Exception {
		Message message = null;
		
		switch(this.type) {
			case choke:
				message = new Message((byte) 0, null);
				break;
			case unchoke:
				message = new Message((byte) 1, null);
				break;
			case interested:
				message = new Message((byte) 2, null);
				break;
			case notinterested:
				message = new Message((byte) 3, null);
				break;
			case have:
				message = new Message((byte) 4, this.payload);
				break;
			case bitfield:
				message = new Message((byte) 5, this.payload);
				break;
			case request:
				message = new Message((byte) 6, this.payload);
				break;
			case piece:
				message = new Message((byte) 7, this.payload);
				break;
			default:
				throw new Exception("Invalid Message Exception: "+type);
		}
		
		return message;
	}

}
