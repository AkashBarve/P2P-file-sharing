package com;

import java.rmi.Remote;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Client class
 */
public class Client implements Runnable {
    private ExecutorService outThreads;
    private Thread runningThread;
    private HashMap<Integer, RemotePeerInfo> peersToConn;

    public Client(HashMap<Integer, RemotePeerInfo> peersToConn) {
        this.peersToConn = peersToConn;
        this.outThreads = Executors.newFixedThreadPool(this.peersToConn.size());
    }

    @Override
    public void run() {

        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        for (Integer key : peersToConn.keySet()) {
            RemotePeerInfo remotePeer = peersToConn.get(key);
//            try {
//                this.outThreads.execute(
//                        //TODO: Implement outgoing request handler
//                        //new OutgoingResquestHanndler(remotePeer)
//                );
//            }
//            catch (Exception ex) {
//                throw new RuntimeException();
//            }
        }

        this.outThreads.shutdown();
    }
}
