package com.trab4;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import com.trab4.ServerLogManagerThread.LogElement;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Server {
    static String topic        = "inf1406-reqs";
    static String broker       = "tcp://localhost:1883";//"tcp://mqtt.eclipseprojects.io:1883";
    static String clientId;
    static MemoryPersistence persistence = new MemoryPersistence();
    static int serverId;
    static int serverAmount;
    static List<Integer> serverIdResponsibilities = new LinkedList<Integer>();
    
    public static void main(String args[]) {
        clientId = "Server " + args[0];
        serverAmount = Integer.parseInt(args[1]);
        serverId = Integer.parseInt(args[0]);
        serverIdResponsibilities.add(serverId);
        Long cleanupInterval = Long.parseLong(args[2]);
        Long heartbeatinterval = Long.parseLong(args[3]);

        System.out.println("====> Initializing server " + clientId + "\n");

        ServerContentTableThread contentTable = new ServerContentTableThread(clientId);
        contentTable.start();
        ServerLogManagerThread logManager = new ServerLogManagerThread(cleanupInterval);
        logManager.start();
        new ServerHeartbeatSenderThread(serverId, heartbeatinterval).start();

        try {
            MqttClient mqttClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker + "\n");
            mqttClient.connect(connOpts);
            System.out.println("ID: " + clientId + " Connected\n");

            mqttClient.subscribe(topic, (topicRcv, msgRcv) -> {
                System.out.println("ID: " + clientId + " Message received: " + msgRcv.toString()+ "\n");
            
                Message msg = Message.deserialize(msgRcv.toString());

                if(msg.getTipoMsg().equals("insert")) {
                    logManager.SendMessage(msg);
                    new ServerMessageHandlerThread(contentTable, msgRcv.toString()).start(); 
                }
                else if((msg.getTipoMsg().equals("consult"))) {
                    logManager.SendMessage(msg);

                    if(ConsultIsForThisServer(msg.getChave())){
                        System.out.println("!!! " + clientId + " will answer consult");
                        new ServerMessageHandlerThread(contentTable, msgRcv.toString()).start();    
                    }
                }
                else if(msg.getTipoMsg().equals("falhaserv")) {
                    if((msg.getIdServ() + 1)%serverAmount == serverId) {
                        System.out.println("!!! " + clientId + " will handle the failed requests");
                        serverIdResponsibilities.add(msg.getIdServ());
                        new HandleAllFailedServerRequests(logManager, msg, contentTable).start();;
                    }
                }
                else if(msg.getTipoMsg().equals("novoserv")) {
                    serverIdResponsibilities.remove(msg.getIdServ());
                }
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

    public static boolean ConsultIsForThisServer(String key) {
        int byteSum = 0;
        char[] keyAsCharArray = key.toCharArray();

        for(int i = 0 ; i < keyAsCharArray.length ; i++)
        {
            byteSum += (int)keyAsCharArray[i];
        }
        int hash = byteSum % serverAmount;

        for (Integer serverId : serverIdResponsibilities) {
            if(serverId == hash) {
                return true;
            }
        }

        return false;
    }   
}