package com;

import java.rmi.Remote;
//import com.logs.Logger;
import com.logs.PeerLogging;
import java.util.Comparator;

import java.rmi.UnexpectedException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Peer {
    public static Peer peer;
    private int peerID;
    private String hostName;
    private int portNo;
    private volatile int hasFileOrNot;
    private int NoOfPreferredNeighbors;
    public int TotalNumberOfPeers;
    //private int noOfPiece
    Map<Integer, RemotePeer> peersStartedBefore = Collections.synchronizedMap(new LinkedHashMap<>());
    Map<Integer, RemotePeer> peersYetToStart = Collections.synchronizedMap(new LinkedHashMap<>());
    volatile Map<Integer, RemotePeer> interestedPeers = Collections.synchronizedMap(new LinkedHashMap<>());
    volatile Map<Integer, RemotePeer> allPeers = Collections.synchronizedMap(new LinkedHashMap<>());
    volatile Map<Integer, RemotePeer> chokedPeers = Collections.synchronizedMap(new LinkedHashMap<>());
    volatile Map<Integer, RemotePeer> PreferedPeers = Collections.synchronizedMap(new LinkedHashMap<>());
    volatile Map<Integer, Long> downloadSpeeds = Collections.synchronizedMap(new LinkedHashMap<>());
    volatile RemotePeer optimisticallyUnchokedPeer;
    private int totalPieces;
    private volatile BitSet bitfieldArray = new BitSet(this.getTotalPieceCount());
    private volatile BitSet FullBitSet = new BitSet(this.getTotalPieceCount());
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
    public String getHostName() {
        return this.hostName;
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

    void setTotalNumberOfPeers(int total) {
        this.TotalNumberOfPeers = total;
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
        if(this.bitfieldArray.equals(FullBitSet)){
            this.hasFileOrNot = 1;
        }
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

    public void setFullBitFieldArray() {
        for(int i = 0; i < getTotalPieceCount(); i++) {
            FullBitSet.set(i);
        }
    };

    public void unchokePreferredPeers() {
        System.out.println("doing unchoke");
        if (!interestedPeers.isEmpty()) {
            System.out.println("inside upp");
            int k = NoOfPreferredNeighbors;
            Set<Integer> temp = new HashSet<>();
            List<Integer> keys = new ArrayList<>(interestedPeers.keySet());
            System.out.println("***************");
            System.out.println(downloadSpeeds);
            Map<Integer, Long> downloadSpeedsSorted = Collections.synchronizedMap(new LinkedHashMap<>());
            downloadSpeeds.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(x -> downloadSpeedsSorted.put(x.getKey(), x.getValue()));
            System.out.println(downloadSpeedsSorted);
            List<Integer> tempIds = new ArrayList<>(downloadSpeedsSorted.keySet());
            for(int i=0; i<downloadSpeedsSorted.size();) {
                if(temp.size() >= k)
                    break;
                if(interestedPeers.containsKey(tempIds.get(i)))
                    temp.add(tempIds.get(i++));
            }

            for(int i=0; i<interestedPeers.size();) {
                if(temp.size() >= k)
                    break;
                int randomIdx = ThreadLocalRandom.current().nextInt(interestedPeers.size());
                int randomPeer = keys.get(randomIdx);
                if(!temp.contains(randomPeer)) {
                    temp.add(randomPeer);
                    i++;
                }
            }

            if (PreferedPeers.isEmpty()) {
                for(int key : keys) {
                    if(!temp.contains(key) && !allPeers.get(key).equals(optimisticallyUnchokedPeer)) {
                        RemotePeer rm = interestedPeers.get(key);
                        try {
                            PeerToPeerHelper.sendChokeMessage(rm.OutputStream);
                            System.out.println("sending choke");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        chokedPeers.put(key, rm);
                    }
                }
            }
            Set<Integer> temp1 = PreferedPeers.keySet();
            for(int p : temp1) {
                if(temp.contains(p)) {
                    temp.remove(p);
                }
                else {
                    if (!allPeers.get(p).equals(optimisticallyUnchokedPeer)) {
                        RemotePeer remPeer = PreferedPeers.get(p);
                        try {
                            PeerToPeerHelper.sendChokeMessage(remPeer.OutputStream);
                            System.out.println("sending Choke");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //send choke message
                        chokedPeers.put(p, remPeer);
                    }
                    PreferedPeers.remove(p);

                }
            }
            for (int i : temp) {
                System.out.println("i in temp: " + i);
                RemotePeer remPeer1  = allPeers.get(i);
                System.out.println("remPeer1: " + remPeer1.getRemotePeerId());
                //send unchoke message
                try {
                    PeerToPeerHelper.sendUnchokeMessage(remPeer1.OutputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                PreferedPeers.put(i, remPeer1);
            }
            Peer.startInstance().getLogger().changeOfPrefferedNeigbors(this.peerID, this.PreferedPeers.keySet());

//            for(temp.size()>0){
//                Peer.startInstance().getLogger().totalPreferredNeighbors(temp);
//            }
            // Unchoke k willingPeers based onl
        }

    }

    public void optimisticallyUnchokeRandomPeer() {
        // Unchoke 1 chokedPeer randomly
        System.out.println("doing optimisticallyUnchokeRandomPeer: "+ interestedPeers.isEmpty());
        if (!interestedPeers.isEmpty()) {
            List<RemotePeer> interestedPeersList = new ArrayList<>(this.interestedPeers.values());
            int randomPeerIndex = ThreadLocalRandom.current().nextInt(interestedPeersList.size());
            this.optimisticallyUnchokedPeer = interestedPeersList.get(randomPeerIndex);
            System.out.println("got optimisticallyUnchokeRandomPeer: "+ optimisticallyUnchokedPeer.getRemotePeerId());

            if (!this.PreferedPeers.containsKey(this.optimisticallyUnchokedPeer.getRemotePeerId())) {
                System.out.println("send unchoke: "+ optimisticallyUnchokedPeer.getRemotePeerId());
                try {
                    PeerToPeerHelper.sendUnchokeMessage(this.optimisticallyUnchokedPeer.OutputStream);
                    if (this.chokedPeers.containsKey(this.optimisticallyUnchokedPeer.getRemotePeerId()))
                        this.chokedPeers.remove(this.optimisticallyUnchokedPeer.getRemotePeerId());
                    Peer.startInstance().getLogger().changeOptimisticallyUnchoked(this.peerID, this.optimisticallyUnchokedPeer.getRemotePeerId());
                } catch (Exception e) {
                    throw new RuntimeException("Could not send unchoke message", e);
                }
            }
        }

    }

    public void setPreferredNeigborCount(int numberOfPreferredNeighbors) {
        this.NoOfPreferredNeighbors = numberOfPreferredNeighbors;
    }

    public boolean isDownloadComplete() {
        if (hasFileOrNot!=1) {
            return false;
        }
        if (allPeers.size() < TotalNumberOfPeers-1) {
            return false;
        }
        for(RemotePeer rp : allPeers.values()) {
            if(rp.getRemoteBitFieldArray().cardinality() != totalPieces){
                System.out.println(rp.getRemotePeerId() + " doesn't have all files");
                return false;
            }
        }
        return true;
    }
}
