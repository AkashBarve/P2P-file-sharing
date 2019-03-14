package com;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.BitSet;
public class RemotePeerInfo {
    private int _peerID;
    private String _hostName;
    private int _portNo;
    private int _hasFile;
    private long download_rate;
    private BitSet bitfield;
    private Enum state;
    private Socket socket;
    public ObjectOutputStream bufferOutputStream;

    public Socket getSocket()
    {
        return socket;
    }

    public Enum getState()
    {
        return state;
    }

    public void setState(Enum state)
    {
        this.state = state;
    }

    public long getDownload_rate()
    {
        return download_rate;
    }

    public void setDownload_rate(long download_rate)
    {
        this.download_rate = download_rate;
    }

    public BitSet getBitfield()
    {
        return bitfield;
    }

    public void setBitfield(BitSet bitfield)
    {
        this.bitfield = bitfield;
    }

    public int get_peerID()
    {
        return _peerID;
    }

    public void set_peerID(int _peerID)
    {
        this._peerID = _peerID;
    }

    public String get_hostName()
    {
        return _hostName;
    }

    public void set_hostName(String _hostName)
    {
        this._hostName = _hostName;
    }

    public int get_portNo()
    {
        return _portNo;
    }

    public void set_portNo(int _portNo)
    {
        this._portNo = _portNo;
    }

    public int get_hasFile()
    {
        return _hasFile;
    }

    public void set_hasFile(int _hasFile)
    {
        this._hasFile = _hasFile;
    }

    public RemotePeerInfo(int _peerID, String _hostName, int _portNo, int _hasFile)
    {
        this._peerID = _peerID;
        this._hostName = _hostName;
        this._portNo = _portNo;
        this._hasFile = _hasFile;
        this.download_rate = 0L;
        this.bitfield = new BitSet(Peer.getPeerInstance().get_pieceCount());
        this.state = MessageType.choke;
        this.bufferOutputStream = null;

        if (this.get_hasFile() == 1)
        {
            for (int i = 0; i < this.bitfield.size(); i++)
            {
                this.bitfield.set(i);
            }
        }
    }

    @Override
    public int compareTo(RemotePeerInfo o)
    {
        return Math.toIntExact(this.download_rate - o.download_rate);
    }
}
