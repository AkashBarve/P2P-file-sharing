package com;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.CommonConfig.CommonConfig;
import static com.CommonConfig.Filename;
import static com.PeerConfig.PeerConfig;


public class peerProcess {
    private static Peer peer;
    public static void main(String[] args) {
        String COMMON_CONFIG = "/src/com/Common.cfg";
        String PEER_CONFIG = "/src/com/PeerInfo.cfg";
        if (args.length == 1) {
            peer = Peer.startInstance();
            int peerID = Integer.parseInt(args[0]);
            CommonConfig(COMMON_CONFIG, peer);
            PeerConfig(PEER_CONFIG, peerID, peer);
            System.out.println("debug" + peer.getHasFileOrNot());
            if (peer.getHasFileOrNot() == 1) {
                try {
                    String filepath = System.getProperty("user.dir") + "/peer_" + Integer.toString(peerID) + "/" + Filename;
                    File f = new File(filepath);
                    peer.setBitFieldArray();
                    if (!f.exists()) {
                        throw new FileNotFoundException();
                    }
                    int Piecesize = CommonConfig.getPieceSize();
                    ManageFile.fileSplit(f, Piecesize);
                } catch (FileNotFoundException e) {
                    System.out.println("File to share not present, add it as ~/project/peer_<ID>/xyz.pdf");
                }
                catch(NullPointerException e)
                {
                    throw new RuntimeException(e);
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

            TimerTask unchokePreferredPeersTask = new TimerTask() {
                @Override
                public void run() {
                    peer.unchokePreferredPeers();
                }
            };

            (new Timer()).scheduleAtFixedRate(unchokePreferredPeersTask, (long) 1000*CommonConfig.getUnchokingInterval(), (long) 1000*CommonConfig.getUnchokingInterval());

            TimerTask optimisticallyUnchokeRandomPeerTask = new TimerTask() {
                @Override
                public void run() {
                    peer.optimisticallyUnchokeRandomPeer();
                }
            };

            (new Timer()).scheduleAtFixedRate(optimisticallyUnchokeRandomPeerTask, (long) 1000*CommonConfig.getOptimisticUnchokingInterval(), (long) 1000*CommonConfig.getOptimisticUnchokingInterval());

        }
        else {
            System.out.println("peerProcess should be appended by peerID");
        }
    }
}
