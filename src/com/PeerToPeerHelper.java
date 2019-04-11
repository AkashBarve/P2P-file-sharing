package com;

import com.message.Message;
import com.message.MessageBuilder;
import com.message.MessageUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PeerToPeerHelper {
    public static Message sendBitFieldMessage(ObjectOutputStream out) throws Exception {
        Message message = MessageBuilder.buildMessage((byte)5, Peer.startInstance().getBitfieldArray().toByteArray());
        byte[] outMessage = MessageUtil.concatenateByteArrays(MessageUtil.concatenateByteToArray(message.getMessageLength(), message.getMessageType()), message.getMessagePayload());
        out.write(outMessage);
        out.flush();
        return message;
    }

    public static byte[] getMessage(ObjectInputStream in) throws IOException {
        byte[] lengthByte = new byte[4];
        int incomingMessageLength = -1;
        byte[] incomingData = null;

        try {
            incomingMessageLength = in.read(lengthByte);
            if (incomingMessageLength != 4)
                System.out.println("Message length mismatch!");

            int incomingDataLength = MessageUtil.byteArrayToInt(lengthByte);

            byte[] messageType = new byte[1];
            in.read(messageType);
            if (messageType[0] == (byte) 5) {
                int actualDataLength = incomingDataLength - 1;
                incomingData = new byte[actualDataLength];
                incomingData = MessageUtil.readBytes(in, incomingData, actualDataLength);
            } else {
                System.out.println("Wrong message type sent");
            }
        } catch (IOException e) {
            System.out.println("Could not read length of actual message");
            e.printStackTrace();
        }
        return incomingData;
    }
}
