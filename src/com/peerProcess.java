package com;

import com.logs.PeerLogging;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class peerProcess {
    private static Peer peer;
    public static void main(String[] args) {
        String COMMON_CONFIG = "/src/com/Common.cfg";
        String PEER_CONFIG = "/src/com/PeerInfo.cfg";
        if (args.length == 1) {
            peer = Peer.startInstance();
            int peerID = Integer.parseInt(args[0]);
            CommonConfig.CommonConfig(COMMON_CONFIG, peer);
            PeerConfig.PeerConfig(PEER_CONFIG, peerID, peer);
            peer.initLogger(peer.getPeerID());
            System.out.println("debug" + peer.getHasFileOrNot());
            if (peer.getHasFileOrNot() == 1) {
                try {
                    String filepath = System.getProperty("user.dir") + "/peer_" + Integer.toString(peerID) + "/" + CommonConfig.Filename;
                    System.out.println(filepath);
                    File f = new File(filepath);
                    if (!f.exists())
                        throw new FileNotFoundException();
                    peer.setBitFieldArray();
                } catch (FileNotFoundException e) {
                    System.out.println("File to share not present, add it as ~/project/peer_<ID>/xyz.pdf");
                }
            }
            else {
                try {
                    String filepath = System.getProperty("user.dir") + "/peer_" + Integer.toString(peerID);
                    File f = new File(filepath);
                    if (!f.exists())
                        f.mkdir();
                }
                catch (RuntimeException e) {
                    System.out.println("Error creating directory");
                }
            }
            ScheduledThreadPoolExecutor executorServer = new ScheduledThreadPoolExecutor(1);
            executorServer.schedule(() -> {
                Server server = new Server();
                new Thread(server).start();
                }, 0, TimeUnit.MILLISECONDS);

            ScheduledThreadPoolExecutor executorClient = new ScheduledThreadPoolExecutor(1);
            executorClient.schedule(() -> {
                Client client = new Client(peer.peersStartedBefore);
                new Thread(client).start(); }, 1, TimeUnit.MILLISECONDS);



        }
        else {
            System.out.println("peerProcess should be appended by peerID");
        }
    }
}
