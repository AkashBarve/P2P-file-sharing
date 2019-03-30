package com;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class PeerConfig {

    public static void PeerConfig(String peer_config, int peerID) {
        String filename = System.getProperty("user.dir") + peer_config;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            System.out.println("Im here");

        } catch (FileNotFoundException e) {
            System.out.println("peerInfo.cfg not found");
//            e.printStackTrace();
        }

    }
}
