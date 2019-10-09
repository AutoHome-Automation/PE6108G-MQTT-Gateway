package com.portmods.PE6108G.handlers;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTT {

    public MqttClient mqttClient;

    private static MQTT instance;

    public static MQTT Instance() throws MqttException {
        if(instance == null)
            instance = new MQTT();
        return instance;
    }

    public void Init(String ipaddress, String username, String password ) throws MqttException {

        MemoryPersistence persistence = new MemoryPersistence();
        mqttClient = new MqttClient(ipaddress, MqttClient.generateClientId(), persistence);

        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setKeepAliveInterval(120);

        if(username != null && password != null){
            connOpts.setUserName(username);
            connOpts.setPassword(password.toCharArray());
        }

        mqttClient.setCallback( new SimpleMqttCallback() );
        mqttClient.connect(connOpts);
        mqttClient.subscribe("/gateway/PE6108G/commands");

    }

}
