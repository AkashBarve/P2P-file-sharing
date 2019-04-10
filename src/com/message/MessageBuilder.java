package com.message;

public class MessageBuilder {
    public static Message buildMessage(byte messageType, byte[] messagePayload) throws Exception {
        Message message;
        switch (messageType) {
            case (byte) 0:
                message = new Choke();
                break;
            case (byte) 1:
                message = new UnChoke();
                break;
            case (byte) 2:
                message = new Interested();
                break;
            case (byte) 3:
                message = new NotInterested();
                break;
            case (byte) 4:
                message = new Have(messagePayload);
                break;
            case (byte) 5:
                message = new BitField(messagePayload);
                break;
            case (byte) 6:
                message = new Request(messagePayload);
                break;
            case (byte) 7:
                message = new Piece(messagePayload);
                break;
            default:
                throw new Exception("Invalid message type: " + messageType);
        }
        return message;
    }
}
