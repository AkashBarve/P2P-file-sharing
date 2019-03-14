package com.model;

import com.messages.Message;

/**
 * Author: @Susmithaaa
 */
public class Piece extends Message
{

    public Piece(byte[] payloadWithPiece_index)
    {
        super((byte) 7,payloadWithPiece_index);
    }
}