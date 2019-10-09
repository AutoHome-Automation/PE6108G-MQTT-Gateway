package com.portmods.PE6108G.models;

public class MQTTSocketEventMessage {

    public String type = "Socket";
    public String source;
    public int socketNumber;
    public String state;

    public MQTTSocketEventMessage(String endpoint, int socketNumber, String state) {
        this.source = endpoint;
        this.socketNumber = socketNumber;
        this.state = state;
    }

}
