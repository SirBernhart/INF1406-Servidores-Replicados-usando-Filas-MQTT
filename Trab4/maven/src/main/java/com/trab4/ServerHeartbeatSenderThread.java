package com.trab4;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class ServerHeartbeatSenderThread extends Thread{
    Message heartbeatMsg;
    String topic        = "inf1406-monitor";
    int qos             = 2;
    String broker       = "tcp://localhost:1883";
    String clientId;
    String content;
    MemoryPersistence persistence = new MemoryPersistence();
    MqttClient heartbeatClient;
    MqttMessage message;

    public ServerHeartbeatSenderThread(int serverId, Long heartbeatInterval){
        heartbeatMsg = new Message();
        heartbeatMsg.setTipoMsg("heartbeat");
        heartbeatMsg.setIdServ(serverId);
        clientId = "Heartbeat " + serverId;
        
        try {
            heartbeatClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            heartbeatClient.connect(connOpts);
            message = new MqttMessage(Message.serialize(heartbeatMsg).getBytes());
    
            message.setQos(qos);
        }
        catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }

        new HeartbeatTimer(heartbeatInterval, this).start();
    }

    public synchronized void SendHeartbeat(){
        try {
            System.out.println(clientId + " ==> Publishing message: " + message);
			heartbeatClient.publish(topic, message);
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

    class HeartbeatTimer extends Thread {
        Long heartbeatInterval;
        ServerHeartbeatSenderThread heartbeatSender;
        
        public HeartbeatTimer(Long heartbeatInterval, ServerHeartbeatSenderThread heartbeatSender){
            this.heartbeatInterval = heartbeatInterval * 1000;
            this.heartbeatSender = heartbeatSender;
        }

        @Override
        public void run() {
            while(true){
                heartbeatSender.SendHeartbeat();
                try {
                    Thread.sleep(heartbeatInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }    
            }
        }
    }
}
