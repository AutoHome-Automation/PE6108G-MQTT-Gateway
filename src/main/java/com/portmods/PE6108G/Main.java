package com.portmods.PE6108G;

import com.portmods.PE6108G.handlers.MQTT;
import com.portmods.PE6108G.handlers.PE6108G;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException, MqttException {

        if(args.length < 1){
            System.out.println("Missing arguments");
            System.out.println("Command Usage : gateway.jar <mqtt-ip:port> [mqtt-user] [mqtt-pass]");
            System.exit(0);
        }

        if(args.length == 1){
            MQTT.Instance().Init(args[0], null, null);
        } else if(args.length == 3){
            MQTT.Instance().Init(args[0], args[1], args[2]);
        }

        while(true){
            Thread.sleep(500);
        }

    }

}
