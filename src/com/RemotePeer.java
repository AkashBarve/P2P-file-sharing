package com;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

public class RemotePeer {
    private final int peerId;
    private final int portNo;
    private final String hostName;
    private final int hasFileOrNot;
    public BufferedOutputStream bufferedOutputStream;
    public BufferedInputStream bufferedInputStream;

    public RemotePeer(int readPeerId, String hostname, int portNo, int hasFileOrNot) {
        this.peerId = readPeerId;
        this.hostName = hostname;
        this.portNo = portNo;
        this.hasFileOrNot = hasFileOrNot;
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
}
