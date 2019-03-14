package com.model;


import com.messages.Message;
import com.messages.MessagePayload;

/**
 * Author: @susmithaaa
 */
public class Interested extends Message
{
    public Interested()
    {
        super((byte) 2);
    }
}