package com.message;

import java.io.Serializable;

public class Message implements Serializable {
    private byte messageType;
    private byte[] messageLength, messagePayload;

    public byte getMessageType() {
        return messageType;
    }

    public byte[] getMessageLength() {
        return messageLength;
    }

    public byte[] getMessagePayload() {
        return messagePayload;
    }

    public Message(byte messageType) {
        this.messageType = messageType;
        this.messageLength = MessageUtil.intToByteArray(1);
    }

    public Message(byte messageType, byte[] messagePayload) {
        this.messageType = messageType;
        this.messageLength = MessageUtil.intToByteArray(messagePayload.length + 1);
        this.messagePayload = messagePayload;
    }
}
