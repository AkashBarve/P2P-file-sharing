package com;

import com.message.Message;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

public class PeerToPeer {
    private Socket socket;
    private RemotePeer remotePeer;
    private ObjectOutputStream out;
    private ObjectInputStream in;
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
           this.out = new ObjectOutputStream(socket.getOutputStream());
           this.remotePeer.OutputStream = this.out;
           this.out.flush();
           this.in = new ObjectInputStream(socket.getInputStream());
           this.remotePeer.InputStream = this.in;
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

    private void sendHandShake(ObjectOutputStream out) throws IOException {
        String s = this.header + this.zeroBits + Integer.toString(this.peerID);
        byte[] handshakemessage = new byte[3];
        String pid = Integer.toString(this.peerID);
        handshakemessage = s.getBytes();

        ByteBuffer handbuffer = ByteBuffer.allocate(3);
        byte[] payload = handbuffer.array();
        //Character s = 'a';
        System.out.println("sending" + handshakemessage);
        out.write(handshakemessage);
        System.out.println("Handshake message sent");
        out.flush();
    }
    private boolean receiveHandshake(ObjectInputStream in) throws IOException {
        byte[] b = new byte[32];
        in.read(b);
        byte[] header = Arrays.copyOfRange(b,0,18);
        String s = new String(header);
        byte[] pidToCompare = Arrays.copyOfRange(b, 28, 32);
        String pid = new String(pidToCompare);
        String pidc = Integer.toString(remotePeer.getRemotePeerId());

        System.out.println("reading" + s + " " + pid);
        System.out.println("Going in receive handshake" + " " + " " + peerID + " " + remotePeer.getRemotePeerId());
        if (s.equals(this.header) && pid.equals(pidc)){
            System.out.println("hand shaken");
            return true;
        }
        else {
            return false;
        }
    }

    public void startCommunication() throws Exception {
        Message message;

        if(!Peer.startInstance().getBitfieldArray().isEmpty()) {
            message = PeerToPeerHelper.sendBitFieldMessage(this.out);
        }

        while (true) {
            byte[] lengthAndMessageType = new byte[5];
            this.in.read(lengthAndMessageType);
            byte messageType = lengthAndMessageType[4];

            byte[] incomingMessagePayload = PeerToPeerHelper.getMessage(this.in);

            switch (messageType) {
                case (byte) 0:
                    // don't do anything
                    break;
                case (byte) 1:
//                    BitSet remoteBS = remotePeer.get
                    int pieceIndex;

                    break;
                case (byte) 2:
                    break;
                case (byte) 3:
                    break;
                case (byte) 4:
                    break;
                case (byte) 5:
                    break;
                case (byte) 6:
                    break;
                case (byte) 7:
                    break;
                default:
                    break;
            }
        }
    }


}
