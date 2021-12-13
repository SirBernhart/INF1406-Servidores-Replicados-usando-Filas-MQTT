package com.trab4;

import org.eclipse.paho.client.mqttv3.MqttException;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Monitor {
    private static int  serverCount;

    private static Message msgHeartbeat  = new Message();
    private static Integer timeLimit;

    static String topic                  = "inf1406-monitor";
    static String broker                 = "tcp://localhost:1883";
    static String clientId               = "Monitor";
    static MemoryPersistence persistence = new MemoryPersistence();

    public static void main(String[] args) 
    {
        try{
            serverCount = Integer.parseInt(args[0]);
            timeLimit = Integer.parseInt(args[1]) * 1000;
        }
        catch(NumberFormatException ex){
            System.out.println("Values passed must be integers");
            ex.printStackTrace();
        }

        HeartbeatArrayThread heartbeatArrayThread = new HeartbeatArrayThread(serverCount, timeLimit);
        heartbeatArrayThread.start();

        try {
            MqttClient mqttClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            mqttClient.connect(connOpts);
            System.out.println("ID: " + clientId + " Connected");

            mqttClient.subscribe(topic, (topicRcv, heartBeatRcv) -> {
                msgHeartbeat = Message.deserialize(heartBeatRcv.toString());
                InsertThread insertThread = new InsertThread(heartbeatArrayThread, msgHeartbeat.getIdServ());
                insertThread.start();
            });

        } 
        catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
}
