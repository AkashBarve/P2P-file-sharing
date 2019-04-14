package com;

import java.rmi.Remote;
//import com.logs.Logger;
import com.logs.PeerLogging;

import java.rmi.UnexpectedException;
import java.util.*;

public class Peer {
    public static Peer peer;
    private int peerID;
    private String hostName;
    private int portNo;
    private int hasFileOrNot;
    private int NoOfPreferredNeighbors = CommonConfig.getNumberOfPreferredNeighbors();
    //private int noOfPiece
    Map<Integer, RemotePeer> peersStartedBefore = Collections.synchronizedMap(new LinkedHashMap<>());
    Map<Integer, RemotePeer> peersYetToStart = Collections.synchronizedMap(new LinkedHashMap<>());
    Map<Integer, RemotePeer> interestedPeers = Collections.synchronizedMap(new LinkedHashMap<>());
    Map<Integer, RemotePeer> allPeers = Collections.synchronizedMap(new LinkedHashMap<>());
    Map<Integer, RemotePeer> chokedPeers = Collections.synchronizedMap(new LinkedHashMap<>());
    Map<Integer, RemotePeer> PreferedPeers = Collections.synchronizedMap(new LinkedHashMap<>());
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
    private PeerLogging logger;

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
        this.logger = new PeerLogging();
        logger.createLogger(peerID);
    }
    public PeerLogging getLogger() {
        return logger;
    }

    public BitSet getBitFieldArray() {
        return this.bitfieldArray;
    }

    public void unchokePreferredPeers() {
        int k = NoOfPreferredNeighbors;
        Set<Integer> temp = new HashSet<>();
        Random random = new Random();
        List<Integer> keys = new ArrayList<>(interestedPeers.keySet());
        while(temp.size() < k+1) {
            int randomPeer = keys.get(random.nextInt(keys.size()));
            temp.add(randomPeer);
        }
        for(int p : PreferedPeers.keySet()) {
            if(temp.contains(p)) {
                temp.remove(p);
            }
            else {
                RemotePeer remPeer = PreferedPeers.get(p);
                PeerToPeerHelper.sendChokeMessage(remPeer.OutputStream);
                //send choke message
                chokedPeers.put(p, remPeer);
                PreferedPeers.remove(p);
            }
        }
        for (int i : temp) {
            RemotePeer remPeer1  = interestedPeers.get(i);
            //send unchoke message
            PeerToPeerHelper.sendUnchokeMessage(remPeer1.OutputStream);
            PreferedPeers.put(i, remPeer1);
        }
        // Unchoke k willingPeers based onl
    }

    public void optimisticallyUnchokeRandomPeer() {
        // Unchoke 1 chokedPeer randomly
    }
}
