package com.message;

public class MessageUtil {
    public static byte[] lengthAsByteArray(int len) {
        byte[] length = new byte[4];

        length[0] = (byte) ((len & 0xFF000000) >> 24);
        length[1] = (byte) ((len & 0x00FF0000) >> 16);
        length[2] = (byte) ((len & 0x0000FF00) >> 8);
        length[3] = (byte) (len & 0x000000FF);

        return length;
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
}
