package com.trab4;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class ServerManager 
{
    private static int  serverCount;
    private static String logCleanupInterval;
    private static String heartbeatInterval;
    
    static MqttClient mqttClient;
    static String topic        = "inf1406-reqs";
    static int qos             = 2;
    static String broker       = "tcp://localhost:1883";
    static String clientId     = "ServerManager";
    static MemoryPersistence persistence = new MemoryPersistence();

    public static void main(String[] args)
    {
        try{
            serverCount = Integer.parseInt(args[0]);
            logCleanupInterval = args[1];
            heartbeatInterval = args[2];
        }
        catch(NumberFormatException ex){
            System.out.println("Values passed must be integers");
            ex.printStackTrace();
        }
        
        for(int i = 0 ; i < serverCount ; i++) {
            SpawnServer(i, false);
        }

        try {
            mqttClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            mqttClient.connect(connOpts);
            System.out.println("Connected");

            mqttClient.subscribe(topic, (topicRcv, msgRcv) -> {
                Message receivedMessage = Message.deserialize(msgRcv.toString());
                if(receivedMessage.getTipoMsg().equals("falhaserv")) {
                    System.out.println("ServerManager ==> !!! Server " + receivedMessage.getIdServ() + " has failed. Preparing a replacement...");
                    new RespawnServerTimer(receivedMessage.getIdServ()).start();
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

    private static Process exec(Class klass, List<String> args) throws IOException, InterruptedException {
        String mavenProjPath = System.getProperty("user.dir");

        String className = "\"" + klass.getName() + "\"";
        String argsCommand = "-Dexec.args=\"";

        List<String> command = new LinkedList<String>();
        command.add("D:\\Utilidades\\apache-maven-3.8.4\\bin\\mvn.cmd");
        command.add("exec:java");
        command.add("-Dexec.mainClass=" + className);

        argsCommand += args.get(0);
        command.add(argsCommand);
        command.add(args.get(1));
        command.add(args.get(2));
        command.add(args.get(3) + "\"");

        System.out.println(command);
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(new File(mavenProjPath));
        return builder.inheritIO().start();
    }

    public static void SpawnServer(int serverId, boolean isReplacement)
    {
        List<String> arguments = new LinkedList<String>();
        arguments.add(Integer.toString(serverId));
        arguments.add(Integer.toString(serverCount));
        arguments.add(logCleanupInterval);
        arguments.add(heartbeatInterval);
        try {
            exec(Server.class, arguments);
        } 
        catch (IOException e) {
            System.err.println("Failed to start server of ID " + serverId);
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Failed to start server of ID " + serverId);
            e.printStackTrace();
        }
        if(isReplacement) {
            try {
                Message newServMsg = new Message();
                newServMsg.setTipoMsg("novoserv");
                newServMsg.setIdServ(serverId);
                newServMsg.setTopicoResp("novoserv " + serverId);
                MqttMessage mqttMessage = new MqttMessage(Message.serialize(newServMsg).getBytes());
                System.out.println("ServerManager ==> Publishing message: "+mqttMessage);
    
                mqttClient.publish(topic, mqttMessage);
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
}

class RespawnServerTimer extends Thread {
    Long respawnInterval = 30000l;
    int serverIdToRespawn;

    public RespawnServerTimer(int serverIdToRespawn){
        this.serverIdToRespawn = serverIdToRespawn;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(respawnInterval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("ServerManager ==> !!! CREATING NEW SERVER !!!");
        ServerManager.SpawnServer(serverIdToRespawn, true);
    }
}
