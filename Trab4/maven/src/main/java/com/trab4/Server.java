package com.trab4;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Server {
    static String topic        = "inf-1406";
    static String broker       = "tcp://localhost:1883";//"tcp://mqtt.eclipseprojects.io:1883";
    static String clientId;
    static MemoryPersistence persistence = new MemoryPersistence();

    public static void main(String args[]) {
        clientId = args[0];
        System.out.println("I'm server " + clientId);

        try {
            MqttClient mqttClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            mqttClient.connect(connOpts);
            System.out.println("ID: " + clientId + " Connected");

            mqttClient.subscribe(topic, (topicRcv, msgRcv) -> {
                System.out.println("Message received: " + msgRcv);
            });

        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
}
