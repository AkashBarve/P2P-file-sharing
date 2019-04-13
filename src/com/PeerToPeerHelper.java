package com;

import com.message.Message;
import com.message.MessageBuilder;
import com.message.MessageUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class PeerToPeerHelper {
    public static Message sendBitFieldMessage(ObjectOutputStream out) throws Exception {
        byte[] ba = Peer.startInstance().getBitFieldArray().toByteArray();
        System.out.println("ba: "+ba);
        Message message = MessageBuilder.buildMessage((byte) 5, ba);
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
        BitSet myBs = Peer.startInstance().getBitFieldArray();
        System.out.println("mybs length "+myBs.length());
        if (Peer.startInstance().getHasFileOrNot() > 0)
            return false;
        for (int i = 0; i < bs.length(); i++) {
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

    public static int getPieceIndexToRequest(RemotePeer remotePeer) {
        BitSet remoteBits = remotePeer.getRemoteBitFieldArray();
        BitSet peerbits = Peer.startInstance().getBitFieldArray();
        if(peerbits.equals(remoteBits) || remoteBits.isEmpty() ) {
            return -1;
        }
        List<Integer> IndexOptionsToRequest = new ArrayList();
        for(int i = 0; i < peerbits.size(); i++) {
            boolean self = peerbits.get(i);
            boolean remote = remoteBits.get(i);
            if (self == false && remote == true) {
                IndexOptionsToRequest.add(i);
            }
        }
        int randomElementIndex = ThreadLocalRandom.current().nextInt(IndexOptionsToRequest.size()) % IndexOptionsToRequest.size();
        return IndexOptionsToRequest.get(randomElementIndex);

    }

    public static void sendRequestMessage(ObjectOutputStream out, int pieceIndex) throws Exception {
        Message message = MessageBuilder.buildMessage((byte) 6, MessageUtil.intToByteArray(pieceIndex));
        out.writeObject(message);
        out.flush();
    }

    public static void sendPieceMessage(ObjectOutputStream out, ManageFile fileManager, int pieceIndex, byte[] pieceIndexByteArray) throws Exception {
        Message message = MessageBuilder.buildMessage((byte) 7, MessageUtil.concatenateByteArrays(pieceIndexByteArray, fileManager.getPartOfFile(pieceIndex)));
        out.writeObject(message);
        out.flush();
    }

    public static void broadcastHaveMessage(byte[] pieceIndex) throws Exception {
        for (RemotePeer rp : Peer.startInstance().allPeers.values()) {
            sendHaveMessage(rp.OutputStream, pieceIndex);
        }
    }

    private static void sendHaveMessage(ObjectOutputStream out, byte[] pieceIndex) throws Exception {
        Message message = MessageBuilder.buildMessage((byte) 3, pieceIndex);
        out.writeObject(message);
        out.flush();
    }

    public static void sendUnchokeMessage(ObjectOutputStream out) throws Exception {
        Message message = MessageBuilder.buildMessage((byte) 0);
        out.writeObject(message);
        out.flush();
    }
}
