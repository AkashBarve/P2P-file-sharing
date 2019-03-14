package com.messages;

import java.io.*;

/**
 * Author: @satyaabhiram
 */
public class Handshake implements Serializable {
	private static final long serialVersionUID = -2094944539267135224L;
	private String header;

	public Handshake() {
		this.header = "P2PFILESHARINGPROJ";
	}

	public void sendMessage(Handshake Handshake, ObjectOutputStream outputStream) {
		try{
			outputStream.writeObject(Handshake);
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	public boolean receiveMessage(ObjectInputStream inputStreamRef) {
		Object inputStreamObj = null;
		try{
			inputStreamObj = inputStreamRef.readObject();
		} catch (IOException e){
			e.printStackTrace();
		} catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		Handshake incomingMessage = (Handshake) inputStreamObj;
		return incomingMessage.header.equals("P2PFILESHARINGPROJ");
	}
}
