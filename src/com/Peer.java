package com;

import java.sql.Time;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Peer {

    private PeerCommunicationHelper peerCommunicationHelper;
    private Timer prefTimer, optTimer;
    private static Peer peer;
    //communication helper needed

    private volatile BitSet bitField;

    // based on timer
    private volatile RemotePeerInfo optimisticallyUnchokedNeighbor;

    //established during peerProcess, unchanged
    Map<Integer, RemotePeerInfo> peers2conn;

    //established during peerProcess, unchanged
    Map<Integer, RemotePeerInfo> peers2getConnection;

    //set right after each handshake
    volatile List<RemotePeerInfo> connectedPeers;


    //keep track of choked and unchoked peers
    volatile List<RemotePeerInfo> chokedPeers;
    volatile List<RemotePeerInfo> unchokedPeers;

    //timer
    volatile Map<RemotePeerInfo, BitSet> preferredNeighbours;
    volatile Map<Integer, RemotePeerInfo> peersInterested;


    private int peerID;
    private String host;
    private int port;
    public int hasFile;
    private int pieceCount;


    /**
     * for terminating conditions
     */
    BitSet idealBitset;
    public int expected;

    private Peer() {
        peerCommunicationHelper = new PeerCommunicationHelper();
        this.bitField = new BitSet(this.getPieceCount());
        this.expected = 0;
        this.peers2conn = Collections.synchronizedMap(new TreeMap<>());
        this.peers2getConnection = Collections.synchronizedMap(new TreeMap<>());
        this.connectedPeers = Collections.synchronizedList(new ArrayList<>());
        this.chokedPeers = Collections.synchronizedList(new ArrayList<>());
        this.unchokedPeers = Collections.synchronizedList(new ArrayList<>());
        this.peersInterested = Collections.synchronizedMap(new HashMap<>());
        this.idealBitset = new BitSet(this.getPieceCount());
        for (int i = 0; i < this.idealBitset.length(); i++)
            this.idealBitset.set(i);
    }

    public static Peer getPeerInstance() {
        if (peer == null) {
            synchronized (Peer.class) {
                if (peer == null)
                    peer = new Peer();
            }
        }
        return peer;
    }

    public void sendHaveToAll(int receivedPieceIndex) {
        for (RemotePeerInfo remote : this.connectedPeers) {
            try {
                //Todo: peer communication helper
            } catch (Exception e) {
                //   e.printStackTrace();
            }
        }
    }

    /**
     * Getters and setters
     */
    public int getPeerID() {
        return peerID;
    }

    void setPeerID(int peerID) {
        this.peerID = peerID;
    }

    String getHost() {
        return host;
    }

    void setHostName(String host) {
        this.host = host;
    }

    int getPort() {
        return port;
    }

    void setPort(int port) {
        this.port = port;
    }

    int getHasFile() {
        return hasFile;
    }

    void setHasFile(int hasFile) {
        this.hasFile = hasFile;
    }

    boolean getBitField(int i) {
        return bitField.get(i);
    }

    private void setBitField(int i) {
        this.bitField.set(i);
    }

    public RemotePeerInfo getOptimisticallyUnchokedNeighbor() {
        return this.optimisticallyUnchokedNeighbor;
    }

    public Map<Integer, RemotePeerInfo> getPeersToConnectTo() {
        return peers2conn;
    }

    public Map<Integer, RemotePeerInfo> getPeersToExpectConnectionsFrom() {
        return peers2getConnection;
    }

    public Map<Integer, RemotePeerInfo> getPeersInterested() {
        return peersInterested;
    }

    BitSet getBitSet() {
        return bitField;
    }

    int getPieceCount() {
        return pieceCount;
    }

    public void setBitset() {
        for (int i = 0; i < getPieceCount(); i++) {
            peer.setBitField(i);
        }
    }

    public void setPieceCount() {
        int f = Constants.getFileSize();
        int p = Constants.getPieceSize();

        this.pieceCount = (int) Math.ceil((double) f / p);
    }

    /*
    Timer based tasks
     */

    void OptimisticallyUnchokedNeighbor() {

        TimerTask repeatedTask = new TimerTask() {
            @Override
            public void run() {
                if (!Peer.getPeerInstance().checkKill()) {
                    setOptimisticallyUnchokedNeighbor();
                } else {
                    Peer.getPeerInstance().optTimer.cancel();
                    Peer.getPeerInstance().optTimer.purge();
                    System.out.println("came to system exit 0 in optimistically unchoked neighbour");
                }
            }
        };

        this.optTimer = new Timer();
        long delay = (long) Constants.getOptimisticUnchokingInterval() * 1000;
        long period = (long) Constants.getOptimisticUnchokingInterval() * 1000;
        this.optTimer.scheduleAtFixedRate(repeatedTask, delay, period);
    }

    private void setOptimisticallyUnchokedNeighbor() {
        this.optimisticallyUnchokedNeighbor = this.connectedPeers.get(ThreadLocalRandom.current().nextInt(this.connectedPeers.size()));
        List<RemotePeerInfo> interestedPeers = new ArrayList<>(this.peersInterested.values());

        interestedPeers.removeIf(r -> !this.chokedPeers.contains(r));

        if (interestedPeers.size() > 0) {
            RemotePeerInfo optimisticPeer = interestedPeers.get(ThreadLocalRandom.current().nextInt(interestedPeers.size()));
            System.out.println("There are no choked Peers currently");
            this.chokedPeers.remove(optimisticPeer);
            this.unchokedPeers.add(optimisticPeer);
            interestedPeers.clear();

            try {
                if (!this.preferredNeighbours.containsKey(this.optimisticallyUnchokedNeighbor)){
                    //peerCommunicationHelper.sendMessage(this.optimisticallyUnchokedNeighbor.objectOutputStream, MessageType.choke);
                    this.optimisticallyUnchokedNeighbor.setState(MessageType.choke);
                }

            } catch (Exception e) {
                          e.printStackTrace();
            }

            try {
                //peerCommunicationHelper.sendMessage(optimisticPeer.objectOutputStream, MessageType.unchoke);
                optimisticPeer.setState(MessageType.unchoke);

            } catch (Exception e) {
                        e.printStackTrace();
            }

            this.optimisticallyUnchokedNeighbor = optimisticPeer;
            System.out.println(this.optimisticallyUnchokedNeighbor.get_peerID());
            //TODO: log
        }
    }

    void PreferredNeighbors() {
        preferredNeighbours = Collections.synchronizedMap(new HashMap<>());

        TimerTask repeatedTask = new TimerTask() {
            @Override
            public void run() {
                if (!Peer.getPeerInstance().checkKill()) {
                    setPreferredNeighbours();
                } else {
                    Peer.getPeerInstance().prefTimer.cancel();
                    Peer.getPeerInstance().prefTimer.purge();
                    System.out.println("came to system exit 0 in preferred neighbours");
                }
            }
        };

        this.prefTimer = new Timer();
        long delay = (long) Constants.getUnchokingInterval() * 1000;
        long period = (long) Constants.getUnchokingInterval() * 1000;
        this.prefTimer.scheduleAtFixedRate(repeatedTask, delay, period);
    }

    private void setPreferredNeighbours() {
        /**
         * This list gets populated whenever there is a file transfer going on
         * between the local peer and the corresponding remote peer where state remains unchoked.
         */
        List<RemotePeerInfo> remotePeerInfoList = new ArrayList<>(this.peersInterested.values());
        Map<RemotePeerInfo, BitSet> temporaryPreferred = new HashMap<>();

        if (remotePeerInfoList.size() > 0) {

            //random choosing preferred
            if (this.hasFile == 1) {
                int count = 0;
                while (remotePeerInfoList.size() > 0 && count < Constants.getNumberOfPreferredNeighbors() ) {
                    count++;
                    int temp = ThreadLocalRandom.current().nextInt(remotePeerInfoList.size());
                    System.out.println(temp);
                    RemotePeerInfo r = remotePeerInfoList.get(temp);
                    decider(r);

                    temporaryPreferred.put(r, r.getBitfield());
                    if (!this.unchokedPeers.contains(r))this.unchokedPeers.add(r);
                    this.chokedPeers.remove(r);
                    remotePeerInfoList.remove(r);
                }

                while (remotePeerInfoList.size() != 0) {
                    RemotePeerInfo r = remotePeerInfoList.get(0);
                    choker(r);
                    remotePeerInfoList.remove(0);
                }
            } else {

                RemotePeerInfo remote;
                int count = 0;

                while (!remotePeerInfoList.isEmpty() && count < Constants.getNumberOfPreferredNeighbors()) {
                    remote = remotePeerInfoList.get(0);
                    decider(remote);
                    temporaryPreferred.put(remote, remote.getBitfield());
                    this.unchokedPeers.add(remote);
                    this.chokedPeers.remove(remote);
                    count++;
                    remotePeerInfoList.remove(0);
                }

                for (RemotePeerInfo r : remotePeerInfoList) {
                    choker(r);
                }
            }

        }

        this.preferredNeighbours = temporaryPreferred;

        //if (!preferredNeighbours.isEmpty())
            /*
            TODO: log
             */
    }

    private void decider(RemotePeerInfo r) {
        if ((r != null ? r.getState() : null) == MessageType.choke) {
            try {
                //TODO: message
                //peerCommunicationHelper.sendMessage
                r.setState(MessageType.unchoke);
                System.out.println("unchoke sent to"+r.get_peerID() +"from"+ Peer.getPeerInstance().getPeerID() );
            } catch (Exception e) {
                throw new RuntimeException("Could not send unchoke message from the peer class", e);
            }
        }
    }

    private void choker (RemotePeerInfo r) {
        try {
            //peerCommunicationHelper.sendMessage
            r.setState(MessageType.choke);
            System.out.println("choke sent to"+r.get_peerID() +"from"+ Peer.getPeerInstance().getPeerID() );

            if (!this.chokedPeers.contains(r)) this.chokedPeers.add(r);
            this.unchokedPeers.remove(r);
        } catch (Exception e) {
//            throw new RuntimeException("Could not send choke message from the peer class", e);
        }
    }

    public boolean checkKill() {

        if (Peer.getPeerInstance().getBitSet().equals(this.idealBitset)) {
            int count=0;
            for (RemotePeerInfo remotePeerInfo : this.connectedPeers) {
                if (remotePeerInfo.getBitfield().equals(this.idealBitset)) count++;
            }
            return this.connectedPeers.size() != 0 && count == this.connectedPeers.size();
        }
        else return false;
    }

}