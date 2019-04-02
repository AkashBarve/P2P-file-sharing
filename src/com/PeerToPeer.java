package com;

import java.io.*;
import java.net.Socket;

public class PeerToPeer {
    private Socket socket;
    private RemotePeer remotePeer;
    private BufferedOutputStream out;
    private BufferedInputStream in;
    private String header;
    private String zeroBits;
    private int peerID;

    public PeerToPeer(Socket socket, RemotePeer remotePeer) {
        this.socket = socket;
        this.remotePeer = remotePeer;
        this.header = "P2PFILESHARINGPROJ";
        this.zeroBits = "0000000000";
        this.peerID = Peer.startInstance().getPeerID();
    }

    public void initialize() {
       try{
           this.out = new BufferedOutputStream(socket.getOutputStream());
           this.remotePeer.BufferedOutputStream = this.out;
           this.out.flush();
           this.in = new BufferedInputStream(socket.getInputStream());
           this.remotePeer.BufferedInputStream = this.in;
       }
       catch(IOException e) {
           System.out.println("Peer to peer initialization failed");
        }
       try {
           sendHandShake(this.out);
       }
       catch (IOException e) {
           System.out.println("Error in sending error message");
       }
        try {
            if(receiveHandshake(this.in)) {
                System.out.println("Sucessfull handhshake");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void sendHandShake(BufferedOutputStream out) throws IOException {
        byte[] handshakemessage = "P2P".getBytes("UTF-8");
        System.out.println("imp" + handshakemessage);
        out.write(1);
        System.out.println("Handshake message sent");
        out.flush();
    }
    private boolean receiveHandshake(BufferedInputStream in) throws IOException {
        //byte[] b = new byte[1];
        int i = in.read();
        //in.read();
        //System.out.println("imp1" + b);
        byte[] test = "akash".getBytes("US-ASCII");
        System.out.println("Going in receive handshake" + i + " " + test + " " + peerID + " " + remotePeer.getRemotePeerId());
        if (i == 1){
            System.out.println("hand shaken");
            return true;
        }
        else {
            return false;
        }
    }


}
