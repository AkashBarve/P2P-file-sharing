package com;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.CommonConfig.getFileName;
import static com.CommonConfig.getFileSize;
//import com.messages.Message;
//import com.messages.MessageUtil;

public class ManageFile {
    static Map<Integer, byte[]> pieceMap;
    static Map<Integer, byte[]> fileSoFar = Collections.synchronizedMap(new TreeMap<>());

    public static void fileSplit(File inputFile, int pieceSize) {
        pieceMap = Collections.synchronizedMap(new HashMap<>());
        FileInputStream inputStream;
        int fileSize;
        int remainingFileSize;
        int bytesRead, count = 0;
        byte[] filePiece;
        try {
            inputStream = new FileInputStream(inputFile);
            fileSize = CommonConfig.getFileSize();
            remainingFileSize = fileSize;
            while (fileSize > 0) {
                System.out.println("I'm inside");
                if (remainingFileSize < pieceSize) {
                    filePiece = new byte[remainingFileSize];
                } else {
                    filePiece = new byte[pieceSize];
                }
                bytesRead = inputStream.read(filePiece);
                fileSize -= bytesRead;
                remainingFileSize -= pieceSize;
                pieceMap.put(count, filePiece);
                count++;

            }
            inputStream.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }

    public byte[] acceptPartOfFile(int filePartNumber) {
        if (fileSoFar.get(filePartNumber) == null)
            return pieceMap.get(filePartNumber);
        else
            return fileSoFar.get(filePartNumber);
    }

    //public void receivePartOfFile(int filePart, Message message) {
      //  byte[] payLoadWithIndex = message.getPayloadOfMessage();
        //byte[] payLoad = MessageUtil.removeFourBytes(payLoadWithIndex);
        //fileSoFar.put(filePart, payLoad);
    //}

    public void mergefiles() throws IOException {
        FileOutputStream fileOutputStream;
        File mergeFile = new File(System.getProperty("user.dir") + "/peer_" + String.valueOf(Peer.startInstance().getPeerID()) + "/"
                + getFileName());
        byte[] combinedFile = new byte[getFileSize()];
        int count= 0;
        for (Map.Entry<Integer, byte[]> e : fileSoFar.entrySet()) {
            for(int i=0;i<e.getValue().length;i++){
                combinedFile[count] = e.getValue()[i];
                count++;
            }
        }
        fileOutputStream = new FileOutputStream(mergeFile);
        fileOutputStream.write(combinedFile);
        fileOutputStream.flush();
        fileOutputStream.close();
    }


}
