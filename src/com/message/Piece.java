package com.message;

public class Piece extends Message {
    public Piece(byte[] index) {
        super((byte) 7, index);
    }
}
