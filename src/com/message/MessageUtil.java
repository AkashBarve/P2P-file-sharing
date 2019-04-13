package com.message;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.BitSet;

public class MessageUtil {
    public static byte[] intToByteArray(int i) {
        byte[] byteArray = new byte[4];

        byteArray[0] = (byte) ((i & 0xFF000000) >> 24);
        byteArray[1] = (byte) ((i & 0x00FF0000) >> 16);
        byteArray[2] = (byte) ((i & 0x0000FF00) >> 8);
        byteArray[3] = (byte) (i & 0x000000FF);

        return byteArray;
    }

    public static byte[] concatenateByteToArray(byte[] bArray, byte b) {
        byte[] concatenated = new byte[bArray.length + 1];
        System.arraycopy(bArray, 0, concatenated, 0, bArray.length);
        concatenated[bArray.length] = b;
        return concatenated;
    }

    public static byte[] concatenateByteArrays(byte[] array1, byte[] array2) {
        byte[] concatenated = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, concatenated, 0, array1.length);
        System.arraycopy(array2, 0, concatenated, array1.length, array2.length);
        return concatenated;
    }

    public static byte[] concatenateByteArrays(byte[] array1, int length1, byte[] array2, int length2) {
        byte[] concatenated = new byte[length1 + length2];
        System.arraycopy(array1, 0, concatenated, 0, length1);
        System.arraycopy(array2, 0, concatenated, length1, length2);
        return concatenated;
    }

    public static int byteArrayToInt(byte[] b) {
        int baAsInt = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            baAsInt += (b[i] & 0x000000FF) << shift;
        }
        return baAsInt;
    }

    public static BitSet byteArrayToBitSet(byte[] bArray) {
        BitSet bs = new BitSet();
        for (int i=0; i<8*bArray.length; i++) {
            if ((bArray[(bArray.length-1) - i/8] & ((byte) 1 << (i%8)))>0) {
                bs.set(i);
            }
        }
        return bs;
    }

    public static byte[] readBytes(ObjectInputStream in, byte[] byteArray, int length) throws IOException {
        int ilength = length;
        int index = 0;
        while (ilength!=0) {
            int dataAvailableLength = in.available();
            int incomingMessageLength = Math.min(ilength, dataAvailableLength);
            byte[] dataRead = new byte[incomingMessageLength];
            if (incomingMessageLength != 0) {
                in.read(dataRead);
                byteArray = MessageUtil.concatenateByteArrays(byteArray, index, dataRead, incomingMessageLength);
                index += incomingMessageLength;
                ilength -= incomingMessageLength;
            }
        }
        return byteArray;
    }

    public static byte[] removePieceIndex (byte[] payload) {
        byte[] piece = new byte[payload.length - 4];
        System.arraycopy(payload, 4, piece, 0, payload.length - 4);
        return piece;
    }
}
