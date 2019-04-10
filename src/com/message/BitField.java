package com.message;

public class BitField extends Message {
    private byte[] bitField;

    public BitField(byte[] bitField) {
        super((byte) 5, bitField);
        this.bitField = bitField;
    }
}
