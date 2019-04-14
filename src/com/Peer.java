package com;

import java.rmi.Remote;
//import com.logs.Logger;
import com.logs.PeerLogging;

import java.rmi.UnexpectedException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Peer {
    public static Peer peer;
    private int peerID;
    private String hostName;
    private int portNo;
    private int hasFileOrNot;
    //private int noOfPiece
    Map<Integer, RemotePeer> peersStartedBefore = Collections.synchronizedMap(new LinkedHashMap<>());
    Map<Integer, RemotePeer> peersYetToStart = Collections.synchronizedMap(new LinkedHashMap<>());
    Map<Integer, RemotePeer> interestedPeers = Collections.synchronizedMap(new LinkedHashMap<>());
    Map<Integer, RemotePeer> allPeers = Collections.synchronizedMap(new LinkedHashMap<>());
    Map<Integer, RemotePeer> chokedPeers = Collections.synchronizedMap(new LinkedHashMap<>());
    Map<Integer, RemotePeer> unchokedPeers = Collections.synchronizedMap(new LinkedHashMap<>());
    RemotePeer optimisticallyUnchokedPeer;
    private int totalPieces;
    private volatile BitSet bitfieldArray = new BitSet(this.getTotalPieceCount());
    //private volatile Boolean[] bitfieldArray = new Boolean[this.getTotalPieceCount()];
    public static Peer startInstance() {
        if (peer == null) {
            synchronized (Peer.class) {
                if (peer == null) {
                    peer = new Peer();
                }
            }
        }
        return peer;
    }
    PeerLogging peerLogger = new PeerLogging();

    public void setPeerID(int peerID) {
        this.peerID = peerID;
        System.out.println(peerID);
    }
    public int getPeerID() {
        return peerID;
    }
    public void setHostName(String s) {
        this.hostName = s;
    }

    public void setPortNo(int portNo) {
        this.portNo = portNo;
    }
    public int getPortNo() {
        return portNo;
    }

    public void setHasFileOrNot(int hasOrNot) throws UnexpectedException {
        if(hasOrNot >= 0 && hasOrNot <= 1) {
            this.hasFileOrNot = hasOrNot;
            System.out.println(hasFileOrNot);
        }
        else {
            throw new UnexpectedException("HasFileOrNO is either 0 or 1");
        }
    }

    public int getHasFileOrNot() {
        return hasFileOrNot;
    }

    public void TotalPieces(Integer fileSize, Integer pieceSize) {
        this.totalPieces = (int) Math.ceil((double)fileSize/pieceSize);
        System.out.println(totalPieces);
    }

    int getTotalPieceCount() {
        return totalPieces;
    }

    public void setBitFieldArray() {
        System.out.println("Here" + getTotalPieceCount());
        for(int i = 0; i < getTotalPieceCount(); i++) {
            peer.setBitField(i);
        }
        System.out.println("Hello" + bitfieldArray);
    }

    public void setBitField(int i) {
        this.bitfieldArray.set(i);
    }

    public void initLogger(int peerID) {
        PeerLogging logger = new PeerLogging();
        logger.createLogger(peerID);
    }

    public BitSet getBitFieldArray() {
        return this.bitfieldArray;
    }

    public void unchokePreferredPeers() {
        // Unchoke k willingPeers based onl
    }

    public void optimisticallyUnchokeRandomPeer() {
        // Unchoke 1 chokedPeer randomly
        List<RemotePeer> interestedPeersList = new ArrayList<>(this.interestedPeers.values());
        int randomPeerIndex = ThreadLocalRandom.current().nextInt(interestedPeersList.size());
        this.optimisticallyUnchokedPeer = interestedPeersList.get(randomPeerIndex);

        if (this.chokedPeers.containsKey(this.optimisticallyUnchokedPeer.getRemotePeerId())) {
            try {
                PeerToPeerHelper.sendUnchokeMessage(this.optimisticallyUnchokedPeer.OutputStream);
                this.chokedPeers.remove(this.optimisticallyUnchokedPeer.getRemotePeerId());
            } catch (Exception e) {
                throw new RuntimeException("Could not send unchoke message", e);
            }
        }
    }
}
