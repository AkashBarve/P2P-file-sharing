package com.messages;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.BitSet;

/**
 * Author: @satyaabhiram
 */
public class MessageUtil {

	public static byte[] concatenateByteToArray(byte[] arr, byte b) {
		byte[] concatenated = new byte[arr.length + 1];
		System.arraycopy(arr, 0, concatenated, 0, arr.length);
		concatenated[arr.length] = b;
		return concatenated;
	}

	public static byte[] concatenateTwoByteArrays(byte[] ba1, byte[] ba2) {
		byte[] concatenated = new byte[ba1.length + ba2.length];
		System.arraycopy(ba1, 0, concatenated, 0, ba1.length);
		System.arraycopy(ba2, 0, concatenated, ba1.length, ba2.length);
		return concatenated;
	}

	public static byte[] concatenateTwoByteArrays(byte[] arr1, int len1, byte[] arr2, int len2) {
		byte[] concatenated = new byte[len1 + len2];
		System.arraycopy(arr1, 0, concatenated, 0, len1);
		System.arraycopy(arr2, 0, concatenated, len1, len2);
		return concatenated;
	}

	public static byte[] convertIntToByteArray(int i) {
		byte[] intAsByte = new byte[4];
		intAsByte[3] = (byte) (i & 0xFF);
		intAsByte[2] = (byte) ((i >> 8) & 0xFF);
		intAsByte[1] = (byte) ((i >> 16) & 0xFF);
		intAsByte[0] = (byte) ((i >> 24) & 0xFF);
		return intAsByte;
	}

	public static int convertByteArrayToInt(byte[] ba) {
		int byteAsInt = 0;
		for(int i=0; i<4; i++){
			int shift = (4-1-i)*8;
			byteAsInt += (ba[i] & 0x000000FF) << shift;
		}
		return byteAsInt;
	}

	public static BitSet convertBytesArrayToBits(byte[] bytes) {
		BitSet bits = new BitSet();
		for(int i=0; i<bytes.length*8; i++) {
			if((bytes[bytes.length-i/8-1] & (1 << (i%8))) > 0){
				bits.set(i);
			}
		}
		return bits;
	}

	public static byte[] convertBitsToByteArray(BitSet bits) {
		byte[] bytes = new byte[bits.length()/8+1];
		for (int i=0; i<bits.length(); i++) {
			if(bits.get(i)){
				bytes[bytes.length-i/8-1] |= 1 << (i%8);
			}
		}
		return bytes;
	}

	public static byte[] removeFourBytesFromPayload(byte[] ba) {
		byte[] actualPayload = new byte[ba.length-4];
		for (int i=4; i<ba.length; i++) {
			actualPayload[i-4] = ba[i];
			ba[i-4] = ba[i];
		}
		return actualPayload;
	}

	public static byte[] readBytes(BufferedInputStream inputStream, byte[] byteArray, int length) throws IOException {
		int index = 0;
		for(int len=length; len!=0;){
			int availableLength = inputStream.available();
			int read = Math.min(len, availableLength);
			byte[] dataRead = new byte[read];
			if(read!=0){
				inputStream.read(dataRead);
				byteArray = MessageUtil.concatenateTwoByteArrays(byteArray, index, dataRead, read);
				index += read;
				len -= read;
			}
		}
		return byteArray;
	}

	public static byte[] getPieceIndexFromPayload(byte[] payload){
		byte[] pieceIndex = new byte[4];
		for(int i=0;i<4;i++){
			pieceIndex[i] = payload[i];
		}
		return pieceIndex;
	}
}
