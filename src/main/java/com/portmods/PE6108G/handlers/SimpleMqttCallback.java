package com.portmods.PE6108G.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portmods.PE6108G.models.MQTTCommadMessage;
import com.portmods.PE6108G.models.MQTTDataEventMessage;
import com.portmods.PE6108G.models.MQTTSocketEventMessage;
import org.eclipse.paho.client.mqttv3.*;

public class SimpleMqttCallback implements MqttCallback {

    ObjectMapper objectMapper = new ObjectMapper();

    public void connectionLost(Throwable throwable) {
        System.out.println("Connection to MQTT broker lost!");
    }

    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        System.out.println("Message received:\n\t" + new String(mqttMessage.getPayload()));
        System.out.println("Message received:\n\t" + s);

        if (s.equals("/gateway/PE6108G/commands")) {

            final MQTTCommadMessage message = objectMapper.readValue(new String(mqttMessage.getPayload()), MQTTCommadMessage.class);
            PE6108G device = new PE6108G(message.endpoint);

            switch (message.command){

                case "GetAll": {

                    for(int i = 1; i <= 8; i++){
                        sendMessage(new MQTTSocketEventMessage(message.endpoint, i, device.getOutlet(i).name()));
                    }

                    break;
                }

                case "GetData" : {

                    sendMessage(new MQTTDataEventMessage(message.endpoint, device.getVoltage(), device.getCurrent(), device.getPower()));

                    break;
                }

                case "GetSocket" : {

                    int socket = Integer.parseInt(message.argument);
                    sendMessage(new MQTTSocketEventMessage(message.endpoint, socket, device.getOutlet(socket).name()));

                    break;
                }

                case "SetSocket" : {

                    String[] split = message.argument.split(",");
                    int socket = Integer.parseInt(split[0]);
                    device.setOutlet(socket, PE6108G.OutletState.valueOf(split[1]));
                    sendMessage(new MQTTSocketEventMessage(message.endpoint, socket, PE6108G.OutletState.valueOf(split[1]).name()));

                    break;
                }

            }

        }

    }

    void sendMessage(Object message)   {

        new Thread(() -> {

            try{
                MQTT.Instance().mqttClient.publish("/gateway/PE6108G/events", new MqttMessage(objectMapper.writeValueAsBytes(message)));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (MqttPersistenceException e) {
                e.printStackTrace();
            } catch (MqttException e) {
                e.printStackTrace();
            }

        }).start();

    }

    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        // not used in this example
    }
}
