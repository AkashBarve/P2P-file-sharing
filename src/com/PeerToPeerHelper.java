package com;

import com.message.Message;
import com.message.MessageBuilder;
import com.message.MessageUtil;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.BitSet;

public class PeerToPeerHelper {
    public static Message sendBitFieldMessage(ObjectOutputStream out) throws Exception {
        Message message = MessageBuilder.buildMessage((byte) 5, Peer.startInstance().getBitfieldArray().toByteArray());
        byte[] outMessage = MessageUtil.concatenateByteArrays(MessageUtil.concatenateByteToArray(message.getMessageLength(), message.getMessageType()), message.getMessagePayload());
        out.writeObject(message);
        out.flush();
        return message;
    }

    public static Message getMessage(ObjectInputStream in) throws Exception {
        Message message = (Message) in.readObject();
        return message;

        /*byte[] lengthByte = new byte[4];
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
        return incomingData;*/
    }

    public static synchronized boolean isInterested(BitSet bs) {
        BitSet myBs = Peer.startInstance().getBitfieldArray();
        if (Peer.startInstance().getHasFileOrNot() > 0)
            return false;
        for (int i = 0; i < myBs.length(); i++) {
            if (bs.get(i) && !myBs.get(i)) {
                return true;
            }
        }
        return false;
    }

    public static Message sendInterestedMessage(ObjectOutputStream out) throws Exception {
        Message message = MessageBuilder.buildMessage((byte) 2);
        out.writeObject(message);
        out.flush();
        return message;
    }

    public static Message sendNotInterestedMessage(ObjectOutputStream out) throws Exception {
        Message message = MessageBuilder.buildMessage((byte) 3);
        out.writeObject(message);
        out.flush();
        return message;
    }

    public static int getPieceIndex(RemotePeer remotePeer) {
        BitSet remoteBits = remotePeer.getRemoteBitfieldArray();
        BitSet peerbits = Peer.startInstance().getBitfieldArray();
        if(peerbits.equals(remoteBits) || remoteBits.isEmpty() ) {
            return 0;
        }
        BitSet temp = (BitSet)remoteBits.clone();
        temp.xor(peerbits);
        int firstMismatch = temp.length()-1;
        if (firstMismatch == -1) {
            return 0;
        }
        return peerbits.get(firstMismatch) ? 1 : -1;
    }

    public static void sendRequestMessage(ObjectOutputStream out, RemotePeer remotePeer) throws Exception {
        Message message = MessageBuilder.buildMessage((byte) 6);
        out.writeObject(message);
        out.flush();
    }
}
