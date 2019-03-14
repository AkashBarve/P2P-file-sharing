package com.model;

import com.messages.Message;
//import com.messages.MessagePayload;

/**
 * Author: @susmithaaa
 */
public class UnChoke extends Message
{
    public UnChoke()
    {
        super((byte) 1, null);
    }
}