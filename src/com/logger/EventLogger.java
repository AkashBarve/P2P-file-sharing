package com.logger;

import java.io.IOException;
import java.util.BitSet;
import java.util.Map;
import com.RemotePeerInfo;

public class EventLogger
{
    public Logger logger;

    public EventLogger(int peerID){
        logger = new Logger(peerID);
    }

    public void TCPConnection(int peerID, boolean isConnectionMakingPeer){
        String msg = (isConnectionMakingPeer) ? " makes a connection to Peer " + peerID + ".": " is connected from Peer " + peerID + "." ;
        write(msg);
    }

    public void changeOfPreferredNeighbors(Map<RemotePeerInfo, BitSet> PreferredNeighbors){
        String preferredNeighbors = "";
        for(Map.Entry<RemotePeerInfo, BitSet> entry: PreferredNeighbors.entrySet()){
        	preferredNeighbors += entry.getKey().get_peerID() + ",";
        }
        changeOfPreferredNeighbors(preferredNeighbors.substring(0,preferredNeighbors.length()-1));
    }

    private void changeOfPreferredNeighbors(String preferredNeighbors){
        String msg = " has the preferred neighbors " + preferredNeighbors + ".";
        write(msg);
    }

    public void changeOfOptimisticallyUnchokedNeighbor(int unchockedNeighborID){
        String msg = " has the optimistically unchoked neighbor " + unchockedNeighborID + ".";
        write(msg);
    }

    public void unchoking(int peerID){
        String msg = " is unchoked by " + peerID + ".";
        write(msg);
    }

    public void choking(int peerID){
        String msg = " is choked by " + peerID + ".";
        write(msg);
    }

    public void have(int peerID, int pieceIndex){
        String msg = " received the ‘have’ message from " + peerID + " for the piece " + pieceIndex + ".";
        write(msg);
    }

    public void interested(int peerID){
        String msg = " received the ‘interested’ message from " + peerID + ".";
        write(msg);
    }

    public void notInterested(int peerID){
        String msg = " received the ‘not interested’ message from " + peerID + ".";
        write(msg);
    }

    public void downloadAPiece(int peerID, int pieceIndex, int numberOfPieces){
        String msg = " has downloaded the piece " + pieceIndex + " from " + peerID + ". Now the number of pieces it has is " + numberOfPieces + ".";
        write(msg);
    }

    public void completionOfDownload(){
        String msg = " has downloaded the complete file.";
        write(msg);
    }

    private void write(String msg) {
        try {
            logger.log(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
