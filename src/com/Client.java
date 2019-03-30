package com;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client implements Runnable{
    private final ExecutorService outgoingConnThreadPool;
    private Map<Integer, RemotePeer> peersStartedBefore;
    int expectedConnectionsCount = Peer.startInstance().peersStartedBefore.size();
    private Thread runningThread;

    public Client(Map<Integer, RemotePeer> peersStartedBefore) {
        this.peersStartedBefore = peersStartedBefore;
        this.outgoingConnThreadPool = Executors.newFixedThreadPool(expectedConnectionsCount);
    }

    @Override
    public void run() {
        System.out.println("Starting Client");
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        for(Integer i : this.peersStartedBefore.keySet()) {
            try{
                this.outgoingConnThreadPool.execute( new clienthandler(peersStartedBefore.get(i)));
            }
            catch (Exception ex) {
                System.out.println("Client thread pool error");
            }
        }
    }

    private class clienthandler implements Runnable {
        private final RemotePeer remotepeer;

        public clienthandler(RemotePeer remotePeer) {
            this.remotepeer = remotePeer;

        }

        @Override
        public void run() {
            int peerID = this.remotepeer.getRemotePeerId();
            int portNo = this.remotepeer.getRemotePortNo();
            String hostname = this.remotepeer.getHostName();
            try {
                Socket socket = new Socket(hostname, portNo);
                System.out.println("Client code connected succesfully to " + peerID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
