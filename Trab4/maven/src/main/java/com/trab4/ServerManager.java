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
    private static Process[] serverProcesses;
    private static int  serverCount, secondsBetweenKillingServer;
    
    static String topic        = "inf-1406";
    static String content      = "Message from MqttPublishSample";
    static int qos             = 2;
    static String broker       = "tcp://localhost:1883";
    static String clientId     = "JavaSample";
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
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            Thread.sleep(15000);
            System.out.println("Publishing message: "+content);
            MqttMessage message = new MqttMessage(content.getBytes());

            message.setQos(qos);
            sampleClient.publish(topic, message);
            System.out.println("Message published");
            Thread.sleep(5000);
            for (Process server : serverProcesses) {
                server.destroy();
            }

            sampleClient.disconnect();
            System.out.println("Disconnected");
            System.exit(0);
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        } catch(InterruptedException e)
        {
            e.printStackTrace();
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

        for(int i = 0 ; i < args.size() ; i++) {
            argsCommand += args.get(i);
            if(i+1 < args.size())
            {
                argsCommand += " ";
            }
        }
        argsCommand += "\"";
        
        command.add(argsCommand);

        System.out.println(command);
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(new File(mavenProjPath));
        return builder.inheritIO().start();
        //Process process = builder.inheritIO().start();
        //process.waitFor();
        //return process.exitValue();
    }
}
