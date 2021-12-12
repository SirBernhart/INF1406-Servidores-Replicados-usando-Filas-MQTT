package com.trab4;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Client {
    public static void main(String[] args) {
        String reqType = args[0];
        String key = args[1];
        String respTopic = args[2];
        Integer reqId = Integer.valueOf(args[3]);
        String newValue;

        Message reqMessage;

        if(reqType.equals("insert"))
        {
            newValue = args[4];
            reqMessage = new Message(reqType, key, respTopic, reqId, newValue);
        }
        else
        {
            reqMessage = new Message(reqType, key, respTopic, reqId);
        }

        SendMessage(reqMessage);
    }

    private static void SendMessage(Message msg)
    {
        String topic        = "inf1406-reqs";
        String content      = msg.serialize(msg);
        int qos             = 2;
        String broker       = "tcp://localhost:1883";
        String clientId     = msg.getIdPedido().toString();
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient requestingClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            requestingClient.connect(connOpts);
            System.out.println("Client " + clientId + " ==> Publishing message: "+content);
            MqttMessage message = new MqttMessage(content.getBytes());

            message.setQos(qos);
            

            if(msg.getTipoMsg().equals("consult"))
            {
                MqttClient clientExpectingResults = new MqttClient(broker, clientId + "Result", new MemoryPersistence());
                MqttConnectOptions connOptsResults = new MqttConnectOptions();
                connOptsResults.setCleanSession(true);
                clientExpectingResults.connect(connOptsResults);
                
                requestingClient.publish(topic, message);

                clientExpectingResults.subscribe(msg.getTopicoResp(), (topicRcv, msgRcv) -> {                    
                    System.out.println("!!! Client " + clientId + " received result: " + msgRcv.toString());
                    Thread.sleep(4000);
                    clientExpectingResults.disconnect();
                    clientExpectingResults.close();
                    System.exit(0);
                });
            }
            else
            {
                requestingClient.publish(topic, message);

                requestingClient.disconnect();
                requestingClient.close();
                System.out.println("Disconnected");
                System.exit(0);
            }
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
