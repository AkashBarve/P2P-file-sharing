package com.model;

import com.messages.Message;
import com.messages.MessagePayload;

/**
 * Author: @susmithaaa
 */
public class Have extends Message
{

    public Have(byte[] piece_index)
    {
        super((byte) 4,piece_index);
    }
}