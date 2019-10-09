package com.portmods.PE6108G.models;

public class MQTTDataEventMessage {

    public String type = "Data";
    public String source;
    public String voltage;
    public String current;
    public String power;

    public MQTTDataEventMessage(String endpoint, String voltage, String current, String power) {
        this.source = endpoint;
        this.voltage = voltage;
        this.current = current;
        this.power = power;
    }

}
