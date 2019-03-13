package com;

import java.awt.*;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.BitSet;

/**
 * Class handling all of the information for a remote peer
 */
public class RemotePeer implements Comparable<RemotePeer> {

    private int peerId;
    private int portNo;
    private int fileNo;
    private String host;
    private long dwnldRate;
    private BitSet bitField;
    private Socket socket;
    private Enum state;
    public ObjectOutputStream objectOutputStream;

    public int getFileNo() {
        return fileNo;
    }

    public RemotePeer(int peerId, int portNo, int fileNo, String host) {
        this.peerId = peerId;
        this.portNo = portNo;
        this.fileNo = fileNo;
        this.host = host;
        this.dwnldRate = 0L;
        //TODO:

        //this.bitField = new BitSet(Peer.getPeerInstance());
        //this.state = ;

        this.objectOutputStream = null;

        if (this.getFileNo() == 1) {
            for(int i = 0; i < this.bitField.size(); i++) {
                this.bitField.set(i);
            }
        }
    }

    public int getPeerId() {
        return peerId;
    }

    public void setPeerId(int peerId) {
        this.peerId = peerId;
    }

    public int getPortNo() {
        return portNo;
    }

    public void setPortNo(int portNo) {
        this.portNo = portNo;
    }

    public void setFileNo(int fileNo) {
        this.fileNo = fileNo;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public long getDwnldRate() {
        return dwnldRate;
    }

    public void setDwnldRate(long dwnldRate) {
        this.dwnldRate = dwnldRate;
    }

    public Socket getSocket() {
        return socket;
    }

    //TODO: Get bitfield

    //TODO: Set bitfield

    //TODO: Get State

    //Todo: Set state

    @Override
    public int compareTo(RemotePeer remotePeer) {
        return Math.toIntExact(this.dwnldRate - remotePeer.dwnldRate);
    }
}
