package com;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private final int portNo;
    private final ExecutorService incomingConnThreadPool;
    int expectedConnectionsCount = Peer.startInstance().peersYetToStart.size();
    private ServerSocket ServerSocket;
    private Thread runningThread;

    Server() {
        this.portNo = Peer.startInstance().getPortNo();
        incomingConnThreadPool = Executors.newFixedThreadPool(expectedConnectionsCount);
    }

    @Override
    public void run() {
        System.out.println("Starting server threadpool");
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        ServerSocketOpen();
        int peerToLookFor = Peer.startInstance().getPeerID();
        int i = 0;
        while (i < expectedConnectionsCount) {
            peerToLookFor++;
            Socket clientSocket;
            try {
                clientSocket = ServerSocket.accept();
                this.incomingConnThreadPool.execute(new handler(clientSocket, Peer.startInstance().peersYetToStart.get(peerToLookFor)));
                        //new IncomingConnectionRequest(Peer.startInstance().peersYetToStart.get(peerToLookFor),clientSocket));
            } catch (IOException e) {
                System.out.println("Error accepting connection from: " + peerToLookFor);
                e.printStackTrace();
            }

        }
        this.incomingConnThreadPool.shutdown();
        try {
            this.ServerSocket.close();
        } catch (IOException e) {
            System.out.println("Server closing failed");
            e.printStackTrace();
        }
        System.out.println("Server threadpool stopped");
    }

    private void ServerSocketOpen() {
        try {
            this.ServerSocket = new ServerSocket(this.portNo);
        } catch (IOException e) {
            System.out.println("Error when trying to open port" + this.portNo);
            e.printStackTrace();
        }
    }

    private class handler implements Runnable {
        private RemotePeer remotePeer;
        private Socket socket;
        private int number;
        private BufferedOutputStream out;
        private BufferedInputStream in;

        public handler(Socket clientSocket, RemotePeer remotePeerDetails) {
            this.socket = clientSocket;
            this.remotePeer = remotePeerDetails;
        }

        @Override
        public void run() {
            System.out.println("Server of peerID " + Peer.startInstance().getPeerID() + " received succesfull connection from" + socket + "xyx" + number);
            PeerToPeer p2p = new PeerToPeer(this.socket, this.remotePeer);
            p2p.initialize();

            try {
                p2p.startCommunication();
            } catch (Exception e) {
                throw new RuntimeException("Can't communicate with: "+this.remotePeer.getRemotePeerId(), e);
            }

        }




    }

//    private class IncomingConnectionRequest implements Runnable {
//        public IncomingConnectionRequest(RemotePeer remotePeer, Socket clientSocket) {
//        }
//    }
}
