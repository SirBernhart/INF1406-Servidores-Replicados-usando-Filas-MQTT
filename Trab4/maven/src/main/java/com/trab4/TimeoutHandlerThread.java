package com.trab4;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class TimeoutHandlerThread extends Thread{

    private Message msgContent;
    private Integer idserv;
    private long heartbeat;
    private String content;

    static String topic           = "inf1406-reqs";
    static String broker          = "tcp://localhost:1883";
    static String clientId        = "TimeoutHandler";
    int qos                       = 2;
    MemoryPersistence persistence = new MemoryPersistence();

    public TimeoutHandlerThread(Integer pIdServ, long pHeartbeat) {
        this.idserv = pIdServ;
        this.heartbeat = pHeartbeat;
        clientId += " Server " + pIdServ;
    }

    @Override
    public void run() {
        msgContent = new Message();

        msgContent.setTipoMsg("falhaserv");
        msgContent.setIdServ(this.idserv);
        msgContent.setVistoEm(heartbeat);
        content = Message.serialize(msgContent);
        
        try {
            MqttClient serverFailureClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            serverFailureClient.connect(connOpts);
            System.out.println("Monitor ==> Publishing message: "+ content);
            MqttMessage message = new MqttMessage(content.getBytes());

            message.setQos(qos);
            serverFailureClient.publish(topic, message);

            serverFailureClient.disconnect();
            serverFailureClient.close();
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
