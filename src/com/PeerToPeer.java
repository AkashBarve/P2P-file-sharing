package com;

import com.logs.PeerLogging;
import com.message.Message;
import com.message.MessageUtil;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

public class PeerToPeer {
    private Socket socket;
    private RemotePeer remotePeer;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String header;
    private String zeroBits;
    private int peerID;
    Long downloadInitTime;
    boolean communicationFlag;
    boolean checkFlag;
    ManageFile fileManager;

    public PeerToPeer(Socket socket, RemotePeer remotePeer) {
        this.socket = socket;
        this.remotePeer = remotePeer;
        this.header = "P2PFILESHARINGPROJ";
        this.zeroBits = "0000000000";
        this.peerID = Peer.startInstance().getPeerID();
        this.fileManager = new ManageFile();
    }

    public void initialize() {
       try{
           this.out = new ObjectOutputStream(socket.getOutputStream());
           this.remotePeer.OutputStream = this.out;
           this.out.flush();
           this.in = new ObjectInputStream(socket.getInputStream());
           this.remotePeer.InputStream = this.in;
       }
       catch(IOException e) {
           System.out.println("Peer to peer initialization failed");
        }
       try {
           sendHandShake(this.out);
           Peer.startInstance().peerLogger.logMakesConnectionTo(peerID, remotePeer.getRemotePeerId());
       }
       catch (IOException e) {
           System.out.println("Error in sending error message");
       }
        try {
            if(receiveHandshake(this.in)) {
                System.out.println("Sucessfull handhshake");
                Peer.startInstance().peerLogger.logIsConnectedFrom(peerID, remotePeer.getRemotePeerId());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void sendHandShake(ObjectOutputStream out) throws IOException {
        String s = this.header + this.zeroBits + Integer.toString(this.peerID);
        byte[] handshakemessage = new byte[3];
        String pid = Integer.toString(this.peerID);
        handshakemessage = s.getBytes();

        ByteBuffer handbuffer = ByteBuffer.allocate(3);
        byte[] payload = handbuffer.array();
        //Character s = 'a';
        System.out.println("sending" + handshakemessage);
        out.write(handshakemessage);
        System.out.println("Handshake message sent");
        out.flush();
    }
    private boolean receiveHandshake(ObjectInputStream in) throws IOException {
        byte[] b = new byte[32];
        in.read(b);
        byte[] header = Arrays.copyOfRange(b,0,18);
        String s = new String(header);
        byte[] pidToCompare = Arrays.copyOfRange(b, 28, 32);
        String pid = new String(pidToCompare);
        String pidc = Integer.toString(remotePeer.getRemotePeerId());

        System.out.println("reading" + s + " " + pid);
        System.out.println("Going in receive handshake" + " " + " " + peerID + " " + remotePeer.getRemotePeerId());
        if (s.equals(this.header) && pid.equals(pidc)){
            System.out.println("hand shaken");
            return true;
        }
        else {
            return false;
        }
    }

    public void startCommunication() throws Exception {
        Message message;
        communicationFlag = true;

        if(Peer.startInstance().getBitFieldArray().isEmpty() == false) {
            System.out.println("hello folks");
            message = PeerToPeerHelper.sendBitFieldMessage(this.out);
        }

        while (communicationFlag) {
            Message incomingMessage = PeerToPeerHelper.getMessage(this.in);
            byte messageType = incomingMessage.getMessageType();
            byte[] messagePayload = incomingMessage.getMessagePayload();

            switch (messageType) {
                case (byte) 0:
                    while (this.in.available() == 0) {
                        // Choke
                        // don't do anything
                    }
                    break;
                case (byte) 1:
                    // Unchoke
                    int pieceidx = PeerToPeerHelper.getPieceIndexToRequest(remotePeer);
                    Unchoke(pieceidx);
                    break;
                case (byte) 2:
                    // Interested
                    Peer.startInstance().interestedPeers.putIfAbsent(remotePeer.getRemotePeerId(), remotePeer);
                    break;
                case (byte) 3:
                    // Not Interested
                    if (Peer.startInstance().interestedPeers.containsKey(remotePeer.getRemotePeerId())) {
                        Peer.startInstance().interestedPeers.remove(remotePeer.getRemotePeerId());
                    }
                    break;
                case (byte) 4:
                    // Have
                    this.handleHaveMessage(messagePayload);
                    break;
                case (byte) 5:
                    // BitField
                    BitSet bitSet = MessageUtil.byteArrayToBitSet(messagePayload);
                    this.remotePeer.setRemotePeerBitFieldArray(bitSet);
                    if (PeerToPeerHelper.isInterested(bitSet)) {
                        message = PeerToPeerHelper.sendInterestedMessage(this.out);
                    } else {
                        message = PeerToPeerHelper.sendNotInterestedMessage(this.out);
                    }
                    break;
                case (byte) 6:
                    // Request
                    this.handleRequestMessage(messagePayload);
                    break;
                case (byte) 7:
                    // Piece
                    this.handlePieceMessage(messagePayload);
                    break;
                default:
                    break;
            }
        }
    }

    private void Unchoke(int pieceidx) throws Exception {
        if (pieceidx == -1) {
            PeerToPeerHelper.sendNotInterestedMessage(this.out);
        }
        if(pieceidx != -1) {
            PeerToPeerHelper.sendRequestMessage(this.out, pieceidx);
            this.downloadInitTime = System.nanoTime();
            this.checkFlag = true;
        }
    }

    private void handleHaveMessage(byte[] messagePayload) throws Exception {
        int pieceIndex = MessageUtil.byteArrayToInt(messagePayload);
        this.remotePeer.getRemoteBitFieldArray().set(pieceIndex);
        if (PeerToPeerHelper.isInterested(this.remotePeer.getRemoteBitFieldArray())) {
            PeerToPeerHelper.sendInterestedMessage(this.out);
        }
    }

    private void handleRequestMessage(byte[] messagePayload) throws Exception {
        int pieceIndex = MessageUtil.byteArrayToInt(messagePayload);
        if (Peer.startInstance().interestedPeers.containsKey(this.remotePeer.getRemotePeerId())) {
            PeerToPeerHelper.sendPieceMessage(this.out, this.fileManager, pieceIndex, messagePayload);
        }
    }

    private void handlePieceMessage(byte[] messagePayload) throws Exception {
        // Detach first 4 bytes -> pieceIndex
        byte[] pieceIndexByteArray = new byte[4];
        for (int i=0; i<4; i++) {
            pieceIndexByteArray[i] = messagePayload[i];
        }

        int pieceIndex = MessageUtil.byteArrayToInt(pieceIndexByteArray);
        if (!Peer.startInstance().getBitFieldArray().get(pieceIndex)) {
            this.fileManager.receivePartOfFile(pieceIndex, MessageUtil.removePieceIndex(messagePayload));
            Peer.startInstance().setBitField(pieceIndex);

            PeerToPeerHelper.broadcastHaveMessage(pieceIndexByteArray);
        }

        int newPieceIndex = PeerToPeerHelper.getPieceIndexToRequest(this.remotePeer);
        PeerToPeerHelper.sendRequestMessage(this.out, newPieceIndex);
    }
}
