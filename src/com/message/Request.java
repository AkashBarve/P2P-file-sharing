package com.message;

public class Request extends Message {
    public Request(byte[] index) {
        super((byte) 6, index);
    }
}
