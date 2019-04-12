package com.logs;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

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

        String logFileName =  "log_peer_" + Integer.toString(peerID) + ".log";
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
}
