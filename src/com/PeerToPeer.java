package com;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class PeerToPeer {
    private Socket socket;
    private RemotePeer remotePeer;
    private BufferedOutputStream out;
    private BufferedInputStream in;

    public PeerToPeer(Socket socket, RemotePeer remotePeer) {
        this.socket = socket;
        this.remotePeer = remotePeer;
    }

    public void initialize() {
       try{
           this.out = new BufferedOutputStream(socket.getOutputStream());
           this.remotePeer.bufferedOutputStream = this.out;
           this.out.flush();
           this.in = new BufferedInputStream(socket.getInputStream());
           this.remotePeer.bufferedInputStream = this.in;
       }
       catch(IOException e) {
           System.out.println("Peer to peer initialization failed");
        }
    }
}
