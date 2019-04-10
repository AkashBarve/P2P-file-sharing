package com;

import com.message.Message;
import com.message.MessageBuilder;
import com.message.MessageUtil;

import java.io.BufferedOutputStream;

public class PeerToPeerHelper {
    public static Message sendBitFieldMessage(BufferedOutputStream out) throws Exception {
        Message message = MessageBuilder.buildMessage((byte)5, Peer.startInstance().getBitfieldArray().toByteArray());
        byte[] outMessage = MessageUtil.concatenateByteArrays(MessageUtil.concatenateByteToArray(message.getMessageLength(), message.getMessageType()), message.getMessagePayload());
        out.write(outMessage);
        out.flush();
        return message;
    }
}
