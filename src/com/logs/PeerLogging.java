package com.logs;

import com.RemotePeer;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;
import java.util.Map;
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

public void totalPreferredNeighbors(Map<BitSet, RemotePeer> PreferredNeighbors){
        String preferredString = "";
        for(Map.Entry<BitSet, RemotePeer> entry: PreferredNeighbors.entrySet())
        {
            try{
                    preferredString = preferredString+entry.getKey();
            }
            catch(Exception e) {

            }
        }
        changePrefferedNeighbors(preferredString.substring(0, preferredString.length() - 1));
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
}
