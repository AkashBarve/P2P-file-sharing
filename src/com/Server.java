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
                this.incomingConnThreadPool.execute(new handler(clientSocket, peerToLookFor));
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
        private Socket socket;
        private int number;

        public handler(Socket clientSocket, int peerToLookFor) {
            this.socket = clientSocket;
            this.number = peerToLookFor;
        }

        @Override
        public void run() {
            System.out.println("Server of peerID " + Peer.startInstance().getPeerID() + " received succesfull connection from" + socket + "xyx" + number + " " +runningThread.getId());
//            try {
//                var in = new Scanner(socket.getInputStream());
//                var out = new PrintWriter(socket.getOutputStream(), true);
//                while (in.hasNextLine()) {
//                    out.println(in.nextLine().toUpperCase());
//                }
//            } catch (Exception e) {
//                System.out.println("Error:" + socket);
//            } finally {
//                try { socket.close(); } catch (IOException e) {}
//                System.out.println("Closed: " + socket);
//            }
        }




    }

//    private class IncomingConnectionRequest implements Runnable {
//        public IncomingConnectionRequest(RemotePeer remotePeer, Socket clientSocket) {
//        }
//    }
}
