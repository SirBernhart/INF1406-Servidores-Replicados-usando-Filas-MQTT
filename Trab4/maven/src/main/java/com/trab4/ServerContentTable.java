package com.trab4;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class ServerContentTable extends Thread{
    private String ownerServerId;
    MemoryPersistence persistence = new MemoryPersistence();
    int qos = 2;
    String broker = "tcp://localhost:1883";

    private Map<String, String> hashTable = new HashMap<String, String>();

    public ServerContentTable(String ownerServerId)
    {
        this.ownerServerId = ownerServerId;
    }

    public synchronized void OperateTable(String message)
    {
        Message deserializedMessage = Message.deserialize(message);

        String messageType = deserializedMessage.getTipoMsg();
        if(messageType.equals("consult"))
        {
            String result = hashTable.getOrDefault(deserializedMessage.getChave(), "!!ERROR!! Server doesn't have key registered");
            System.out.println("Table: Key " + deserializedMessage.getChave() + " keeps value " + result);

            try {
                MqttClient requestingClient = new MqttClient(broker, "Table of " + ownerServerId, persistence);
				MqttConnectOptions connOpts = new MqttConnectOptions();
				connOpts.setCleanSession(true);
				requestingClient.connect(connOpts);
				System.out.println("Table of " + ownerServerId +" ==> Publishing message: "+ result);
				MqttMessage responseMessage = new MqttMessage(result.getBytes());

				responseMessage.setQos(qos);
				requestingClient.publish(deserializedMessage.getTopicoResp(), responseMessage);
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
        else if(messageType.equals("insert"))
        {
            System.out.println("Table received: key: " + deserializedMessage.getChave() + " value: " + deserializedMessage.getNovoValor()+"\n");
            hashTable.put(deserializedMessage.getChave(), deserializedMessage.getNovoValor());
            System.out.println(hashTable + "\n");
        }
    }
}
