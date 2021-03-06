package com.logs;

import com.Peer;
import com.RemotePeer;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.logging.*;
import com.RemotePeer;

public class PeerLogging {
    int peerID;
    Logger PeerLogger;
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");

    public PeerLogging() {
        PeerLogger = Logger.getLogger(Logger.class.getName());
    }

    public void logMakesConnectionTo(int peerID, int remotePeerId) {
        String msg = (dateFormat.format(new Date()).toString() + ": Peer " + peerID + " makes a connection to Peer " + remotePeerId +".");
        PeerLogger.log (Level.INFO,msg);
    }

    public void logIsConnectedFrom(int peerID, int remotePeerId) {
        String msg = (dateFormat.format(new Date()).toString() + ": Peer " + peerID + " is connected from Peer " + remotePeerId +".");
        PeerLogger.log (Level.INFO,msg);
    }

    public void createLogger(int peerID) {

        String logFileName =  System.getProperty("user.dir") + "/log_peer_" + Integer.toString(peerID) + ".log";
        Handler logHandler = null;
        try {
            logHandler = new FileHandler(logFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logHandler.setLevel(Level.parse("INFO"));
        logHandler.setFormatter(new SimpleFormatter());
        PeerLogger.addHandler(logHandler);
        PeerLogger.setUseParentHandlers(false);
        this.peerID = peerID;
        System.out.println("logger created");
    }

    public void receiveschoke(int peerID, int remotePeerId){
        String msg = (dateFormat.format(new Date()).toString() + ": Peer " + peerID + " is choked by " + remotePeerId + ".");
        PeerLogger.log(Level.INFO, msg);
    }

    public void receiveUnchoke(int peerID, int remotePeerId){
        String msg = (dateFormat.format(new Date()).toString() + ": Peer " + peerID + " is unchoked by " + remotePeerId + ".");
        PeerLogger.log(Level.INFO, msg);
    }

    public void changePrefferedNeighbors(String preferredString){
        String msg = (dateFormat.format(new Date()).toString() + ": Peer " + peerID + " has the preferred neighbors " + preferredString + ".");
        PeerLogger.log(Level.INFO, msg);
    }

    public void changeOptimisticallyUnchoked(int peerID, int unchokeid) {
        String msg = (dateFormat.format(new Date()).toString() + ": Peer " + peerID + " has the optimistically unchoked neighbor " + unchokeid + ".");
        PeerLogger.log(Level.INFO, msg);
    }

    public void receivesHaveMessage(int peerID, int remotePeerId, int pieceIndex) {
        String msg = (dateFormat.format(new Date()).toString() + ": Peer " + peerID + " received the 'have' message from " + remotePeerId + " for the piece " + pieceIndex + ".");
        PeerLogger.log(Level.INFO, msg);
    }

    public void receivesInterested(int peerID, int remotePeerId) {
        String msg = (dateFormat.format(new Date()).toString() + ": Peer " + peerID + " received the 'interested' message from " + remotePeerId + ".");
        PeerLogger.log(Level.INFO, msg);
    }

    public void receivesNotInterested(int peerID, int remotePeerId) {
        String msg = (dateFormat.format(new Date()).toString() + ": Peer " + peerID + " received the 'not interested' message from " + remotePeerId + ".");
        PeerLogger.log(Level.INFO, msg);
    }

    public void receivesPiece(int peerID, int pieceIndex, int remotePeerId, int i) {
        String msg = (dateFormat.format(new Date()).toString() + ": Peer " + peerID + " has downloaded the piece " + pieceIndex + " from " + remotePeerId + ". Now the number of pieces it has is " + i +".");
        PeerLogger.log(Level.INFO, msg);
    }

    public void downloadComplete(int peerID) {
        String msg = (dateFormat.format(new Date()).toString() + ": Peer " + peerID + " has downloaded the complete file.");
        PeerLogger.log(Level.INFO, msg);
    }

    public void receiveReqMsg(int peerID, int remotePeerId, int pieceIndex) {
        String msg = (dateFormat.format(new Date()).toString() + ": Peer " + peerID + " receives a 'request' message from Peer" + remotePeerId + " for piece at index " + pieceIndex + "." );
        PeerLogger.log(Level.INFO, msg);
    }

    public void changeOfPrefferedNeigbors(int peerID, Set<Integer> keySet) {
        StringBuilder sb = new StringBuilder();
        for(int i : keySet) {
            sb.append(", " + i);
        }
        if(sb.length()>0)
            sb.deleteCharAt(0);
        sb.toString();
        String msg = (dateFormat.format(new Date()).toString() + ": Peer " + peerID + " has the preferred neighbors" + sb + ".");
        PeerLogger.log(Level.INFO, msg);
    }
}
