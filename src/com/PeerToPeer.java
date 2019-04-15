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
    boolean communicationFlag;
    boolean mergingDone = false;
    ManageFile fileManager;
    private Long downloadSpeed = 0l;
    private Long initTime;
    private Long endTime;
    private Long pieceCount = 0l;

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
           Peer.startInstance().getLogger().logMakesConnectionTo(peerID, remotePeer.getRemotePeerId());
       }
       catch (IOException e) {
           System.out.println("Error in sending error message");
       }
        try {
            if(receiveHandshake(this.in)) {
                System.out.println("Sucessfull handhshake");
                Peer.startInstance().allPeers.putIfAbsent(remotePeer.getRemotePeerId(), remotePeer);
                Peer.startInstance().getLogger().logIsConnectedFrom(peerID, remotePeer.getRemotePeerId());
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

        if(!Peer.startInstance().getBitFieldArray().isEmpty()) {
            System.out.println("hello folks");
            message = PeerToPeerHelper.sendBitFieldMessage(this.out);
            for( int i=0; i<Peer.startInstance().getBitFieldArray().length(); i++ ){
                System.out.println("bf: "+Peer.startInstance().getBitFieldArray().get(i));
            }
        }

        while (communicationFlag) {
            Message incomingMessage = PeerToPeerHelper.getMessage(this.in);
            byte messageType = incomingMessage.getMessageType();
            byte[] messagePayload = incomingMessage.getMessagePayload();
            System.out.println(System.nanoTime() + " : got message " + (int)messageType);

            switch (messageType) {
                case (byte) 0:
                    Peer.startInstance().getLogger().receiveschoke(this.peerID, this.remotePeer.getRemotePeerId());
                    break;
                case (byte) 1:
                    // Unchoke
                    int pieceidx = PeerToPeerHelper.getPieceIndexToRequest(remotePeer);
                    this.handleUnchokeMessage(pieceidx);
                    break;
                case (byte) 2:
                    // Interested
                    Peer.startInstance().interestedPeers.putIfAbsent(remotePeer.getRemotePeerId(), remotePeer);
                    Peer.startInstance().getLogger().receivesInterested(this.peerID, this.remotePeer.getRemotePeerId());
                    for(RemotePeer rp: Peer.startInstance().interestedPeers.values()){
                        System.out.println("rp id: "+rp.getRemotePeerId());
                    }
                    break;
                case (byte) 3:
                    // Not Interested
                    Peer.startInstance().getLogger().receivesNotInterested(this.peerID, this.remotePeer.getRemotePeerId());
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
                    this.handleBitFieldMessage(messagePayload);
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

    private void handleUnchokeMessage(int pieceidx) throws Exception {
        if (pieceidx == -1 || pieceidx == -2) {
            System.out.println("sending not interested 1");
            PeerToPeerHelper.sendNotInterestedMessage(this.out);
        } else {
            Peer.startInstance().getLogger().receiveUnchoke(this.peerID, this.remotePeer.getRemotePeerId());
            sendRequestAndStartTime(pieceidx);
        }
    }

    private void handleHaveMessage(byte[] messagePayload) throws Exception {
        int pieceIndex = MessageUtil.byteArrayToInt(messagePayload);
        Peer.startInstance().getLogger().receivesHaveMessage(this.peerID, this.remotePeer.getRemotePeerId(), pieceIndex);
        this.remotePeer.getRemoteBitFieldArray().set(pieceIndex);
        if (PeerToPeerHelper.isInterested(this.remotePeer.getRemoteBitFieldArray())) {
            PeerToPeerHelper.sendInterestedMessage(this.out);
        }
    }

    private void handleBitFieldMessage(byte[] messagePayload) throws Exception {
        System.out.println("got bitfield"+ messagePayload);
        BitSet bitSet = BitSet.valueOf(messagePayload);
        for(int i=0; i<bitSet.length(); i++ ){
            System.out.println("print bf: "+bitSet.get(i));
        }
        this.remotePeer.setRemotePeerBitFieldArray(bitSet);
        if (PeerToPeerHelper.isInterested(bitSet)) {
            PeerToPeerHelper.sendInterestedMessage(this.out);
            System.out.println("got bitfield, sending interested");
        } else {
            PeerToPeerHelper.sendNotInterestedMessage(this.out);
            System.out.println("sending not interested 2");
        }
    }

    private void handleRequestMessage(byte[] messagePayload) throws Exception {
        int pieceIndex = MessageUtil.byteArrayToInt(messagePayload);
        System.out.println("received request: "+ pieceIndex);
        if (Peer.startInstance().interestedPeers.containsKey(this.remotePeer.getRemotePeerId())) {
            PeerToPeerHelper.sendPieceMessage(this.out, this.fileManager, pieceIndex, messagePayload);
        }
    }

    private void handlePieceMessage(byte[] messagePayload) throws Exception {
        endTime = System.nanoTime();
        downloadSpeed = ((pieceCount*downloadSpeed) + (messagePayload.length/(endTime-initTime)))/(pieceCount+1);
        pieceCount++;
        Peer.startInstance().downloadSpeeds.put(remotePeer.getRemotePeerId(), (double)downloadSpeed);
        // Detach first 4 bytes -> pieceIndex
        System.out.println("full payload size: "+messagePayload.length);
        byte[] pieceIndexByteArray = new byte[4];
        for (int i=0; i<4; i++) {
            pieceIndexByteArray[i] = messagePayload[i];
        }

        int pieceIndex = MessageUtil.byteArrayToInt(pieceIndexByteArray);
        BitSet temp = Peer.startInstance().getBitFieldArray();
        int trues = temp.cardinality();
        int falses = temp.length() - trues;
        Peer.startInstance().getLogger().receivesPiece(this.peerID, pieceIndex, this.remotePeer.getRemotePeerId(), falses + 1);
        System.out.println("piece index: "+pieceIndex);
        if (!Peer.startInstance().getBitFieldArray().get(pieceIndex)) {
            this.fileManager.receivePartOfFile(pieceIndex, MessageUtil.removePieceIndex(messagePayload));
            Peer.startInstance().setBitField(pieceIndex);

            PeerToPeerHelper.broadcastHaveMessage(pieceIndexByteArray);
        }

        int newPieceIndex = PeerToPeerHelper.getPieceIndexToRequest(this.remotePeer);
        if(newPieceIndex>=0)
            sendRequestAndStartTime(newPieceIndex);

        else if(newPieceIndex==-2) {
            if (!this.mergingDone) {
                System.out.println("merging");
                this.fileManager.mergefiles();
                Peer.startInstance().getLogger().downloadComplete(this.peerID);
                this.mergingDone = true;
            }
            PeerToPeerHelper.sendNotInterestedMessage(this.out);
        } else if(newPieceIndex==-1) {
            PeerToPeerHelper.sendNotInterestedMessage(this.out);
            System.out.println("sending not interested 3");
        }
    }

    private void sendRequestAndStartTime(int newPieceIndex) throws Exception {
        PeerToPeerHelper.sendRequestMessage(this.out, newPieceIndex);
        initTime = System.nanoTime();
    }

}
