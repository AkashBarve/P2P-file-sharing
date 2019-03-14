package com.messages;

import java.io.Serializable;

/**
 * Author: @satyaabhiram
 */
public class Message implements Serializable {
	private static final long serialVersionUID = -2094944539267135224L;
	private byte[] length, payload;
	private byte type;

	public Message(byte messageType, byte[] messagePayload) {
		this.type = messageType;
		this.payload = messagePayload;
		this.length = messagePayload==null ? MessageUtil.convertIntToByteArray(1) : MessageUtil.convertIntToByteArray(messagePayload.length + 1);
	}

	public byte[] getLengthOfMessage() {
		return length;
	}

	public byte getTypeOfMessage() {
		return type;
	}

	public byte[] getPayloadOfMessage() {
		return payload;
	}

}
