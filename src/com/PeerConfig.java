package com;

import java.io.*;

public class PeerConfig {

    public static void PeerConfig(String peer_config, int peerID, Peer peer) {
        String filename = System.getProperty("user.dir") + peer_config;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            //Map<Integer, RemotePeer> peersStartedBefore = new HashMap<>();
            //Map<Integer, RemotePeer> peersYetToStart = new HashMap<>();
            String s;
            int peerCount = 0;
            while ((s = br.readLine()) != null) {
                peerCount ++;
                System.out.println(s);
                String s1[] = s.split(" ");
                int readPeerId = Integer.parseInt(s1[0]);
                String hostname = s1[1];
                int portNo = Integer.parseInt(s1[2]);
                int hasFileOrNot = Integer.parseInt(s1[3]);
                if (readPeerId == peerID) {
                    peer.setPeerID(peerID);
                    peer.setHostName(hostname);
                    peer.setPortNo(portNo);
                    peer.setHasFileOrNot(hasFileOrNot);
                } else {
                    RemotePeer remotePeer = new RemotePeer(readPeerId, hostname, portNo, hasFileOrNot);
                    if ((peerID > readPeerId)) {
                        peer.peersStartedBefore.put(readPeerId, remotePeer);
                    } else {
                        peer.peersYetToStart.put(readPeerId, remotePeer);
                    }
                }
            }
            peer.setTotalNumberOfPeers(peerCount);
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("peerInfo.cfg not found");
//            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error reading file");
            e.printStackTrace();
        }

    }
}
