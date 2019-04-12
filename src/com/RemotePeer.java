package com;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.BitSet;

public class RemotePeer {
    private final int peerId;
    private final int portNo;
    private final String hostName;
    private final int hasFileOrNot;
    public ObjectOutputStream OutputStream;
    public ObjectInputStream InputStream;
    private BitSet remotePeerBitFieldArray = new BitSet(Peer.startInstance().getTotalPieceCount());
    //public java.io.ObjectOutputStream ObjectOutputStream;

    public RemotePeer(int readPeerId, String hostname, int portNo, int hasFileOrNot) {
        this.peerId = readPeerId;
        this.hostName = hostname;
        this.portNo = portNo;
        this.hasFileOrNot = hasFileOrNot;
        this.remotePeerBitFieldArray = new BitSet(Peer.startInstance().getTotalPieceCount());
        if (this.hasFileOrNot == 1) {
            for(int i = 0; i < this.remotePeerBitFieldArray.size(); i++) {
                this.remotePeerBitFieldArray.set(i);
            }
        }
    }

    public int getRemotePeerId() {
        return peerId;
    }
    public int getRemotePortNo() {
        return portNo;
    }
    public String getHostName() {
        return hostName;
    }

    public BitSet getRemoteBitFieldArray() {
        return this.remotePeerBitFieldArray;
    }

    public void setRemotePeerBitFieldArray(BitSet bitSet) {
        this.remotePeerBitFieldArray = (BitSet) bitSet.clone();
    }
}
