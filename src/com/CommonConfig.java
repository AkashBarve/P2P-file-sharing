package com;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommonConfig {

    public static void CommonConfig(String filename) {
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
            setNumberofPreferredNeighbors(common_config_details);
            setUnchokingInterval(common_config_details);
            setOptimisticUnchokingInterval(common_config_details);
            setFilename(common_config_details);
            setFileSize(common_config_details);
            setPieceSize(common_config_details);
            br.close();
        }
        catch (FileNotFoundException e){
            System.out.println("Common.cfg does not exist");
        }
        catch (IOException e) {
            System.out.println("IO Exception");
        }
    }

    private static void setPieceSize(Map<String, String> common_config_details) {
        Integer PieceSize = Integer.parseInt(common_config_details.get("PieceSize"));
        System.out.println(PieceSize);
    }

    private static void setFileSize(Map<String, String> common_config_details) {
        Integer FileSize = Integer.parseInt(common_config_details.get("FileSize"));
        System.out.println(FileSize);
    }

    private static void setFilename(Map<String, String> common_config_details) {
        String Filename = common_config_details.get("FileName");
        System.out.println(Filename);
    }

    private static void setOptimisticUnchokingInterval(Map<String, String> common_config_details) {
        Integer OptimisticUnchokingInterval = Integer.parseInt(common_config_details.get("OptimisticUnchokingInterval"));
        System.out.println(OptimisticUnchokingInterval);
    }

    private static void setUnchokingInterval(Map<String, String> common_config_details) {
        Integer UnchokingInterval = Integer.parseInt(common_config_details.get("UnchokingInterval"));
        System.out.println(UnchokingInterval);
    }

    private static void setNumberofPreferredNeighbors(Map<String, String> common_config_details) {
        Integer NumberOfPreferredNeighbors = Integer.parseInt(common_config_details.get("NumberOfPreferredNeighbors"));
        System.out.println(NumberOfPreferredNeighbors);
    }


}
