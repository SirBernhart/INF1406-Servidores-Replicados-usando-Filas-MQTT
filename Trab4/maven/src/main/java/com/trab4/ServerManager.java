package com.trab4;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class ServerManager 
{
    private static Process[] serverProcesses;
    private static int  serverCount, secondsBetweenKillingServer;
    
    static String topic        = "inf1406-reqs";
    static int qos             = 2;
    static String broker       = "tcp://localhost:1883";
    static String clientId     = "ServerManager";
    static MemoryPersistence persistence = new MemoryPersistence();

    public static void main(String[] args)
    {
        try{
            serverCount = Integer.parseInt(args[0]);
            secondsBetweenKillingServer = Integer.parseInt(args[1]);
        }
        catch(NumberFormatException ex){
            System.out.println("Values passed must be integers");
            ex.printStackTrace();
        }

        serverProcesses = new Process[serverCount];
        
        for(int i = 0 ; i < serverCount ; i++) {
            List<String> commands = new LinkedList<String>();
            commands.add(Integer.toString(i));
            commands.add(Integer.toString(serverCount));
            try {
                serverProcesses[i] = exec(Server.class, commands);
            } 
            catch (IOException e) {
                System.err.println("Failed to start server of ID " + i);
                e.printStackTrace();
            } catch (InterruptedException e) {
                System.err.println("Failed to start server of ID " + i);
                e.printStackTrace();
            }
        }

        try {
            MqttClient mqttClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            mqttClient.connect(connOpts);
            System.out.println("Connected");
            /*mqttClient.subscribe(topic, (topicRcv, msgRcv) -> {
                System.out.println("Message received: " + msgRcv);
            });
            
            for (Process server : serverProcesses) {
                server.destroy();
            }*/

            mqttClient.disconnect();
            System.out.println("Disconnected");
            System.exit(0);
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        } 
    }

    public static Process exec(Class klass, List<String> args) throws IOException, InterruptedException {
        String mavenProjPath = System.getProperty("user.dir");

        String className = "\"" + klass.getName() + "\"";
        String argsCommand = "-Dexec.args=\"";

        List<String> command = new LinkedList<String>();
        command.add("D:\\Utilidades\\apache-maven-3.8.4\\bin\\mvn.cmd");
        //command.add("compile");
        command.add("exec:java");
        command.add("-Dexec.mainClass=" + className);

        argsCommand += args.get(0);
        command.add(argsCommand);
        
        command.add(args.get(1) + "\"");

        System.out.println(command);
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(new File(mavenProjPath));
        return builder.inheritIO().start();
    }
}
