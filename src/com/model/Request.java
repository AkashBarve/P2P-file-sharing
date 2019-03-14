package com.model;

import com.messages.Message;

/**
 * Author: @susmithaaa
 */
public class Request extends Message
{

    public Request(byte[] pieceIndex)
    {
        super((byte) 7,pieceIndex);
    }


}