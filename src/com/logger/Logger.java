package com.logger;

import com.Constants;
import com.Peer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Logger {

    int peerID;

    protected final String logFilePath;

    public Logger(int peer_ID){
        this.peerID = peer_ID;
        this.logFilePath = Constants.root+"/"+"log_peer_"+Peer.getPeerInstance().getPeerID()+".log";
    }

    public void log(String logLine) throws IOException {
        log(logFilePath, logLine);
    }

    public void log(String filePath, String logLine) throws IOException {
        Date date = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd 'at' hh:mm:ss a zzz");
        String currentTime = ft.format(date);
        FileWriter fw = new FileWriter(filePath, true);
        fw.write(currentTime + ": Peer "+peerID+": "+logLine+"\n");
        fw.flush();
        fw.close();
    }
}
