package com;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommonConfig {
    public static String Filename;

    public static void CommonConfig(String filename, Peer peer) {
        filename = System.getProperty("user.dir") + filename;
        System.out.println(filename);
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
            String s;
            Map<String, String> common_config_details= new HashMap<>();
            //ArrayList<String> common_config_ip = new ArrayList<>();
            while((s = br.readLine()) != null) {
                String s1[] = s.split(" ");
                common_config_details.put(s1[0], s1[1]);
            }
            Integer NumberOfPreferredNeighbors = Integer.parseInt(common_config_details.get("NumberOfPreferredNeighbors"));
            Integer UnchokingInterval = Integer.parseInt(common_config_details.get("UnchokingInterval"));
            Integer OptimisticUnchokingInterval = Integer.parseInt(common_config_details.get("OptimisticUnchokingInterval"));
            Filename = common_config_details.get("FileName");
            Integer FileSize = Integer.parseInt(common_config_details.get("FileSize"));
            Integer PieceSize = Integer.parseInt(common_config_details.get("PieceSize"));
            peer.TotalPieces(FileSize, PieceSize);
            br.close();
        }
        catch (FileNotFoundException e){
            System.out.println("Common.cfg does not exist");
        }
        catch (IOException e) {
            System.out.println("IO Exception");
        }
    }

}
