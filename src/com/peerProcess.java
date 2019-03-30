package com;

public class peerProcess {
    private static Peer peer;
    public static void main(String[] args) {
        String COMMON_CONFIG = "/src/com/Common.cfg";
        String PEER_CONFIG = "/src/com/PeerInfo.cfg";
        if (args.length > 0) {
            int peerID = Integer.parseInt(args[0]);
            CommonConfig.CommonConfig(COMMON_CONFIG);
            PeerConfig.PeerConfig(PEER_CONFIG, peerID);


        }
        else {
            System.out.println("peerProcess should be appended by peerID");
        }
    }
}
