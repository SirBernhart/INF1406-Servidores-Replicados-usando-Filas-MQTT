package com.trab4;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.io.File;

public class ServerManager 
{
    private static Process[] serverProcesses;
    private static int  serverCount, secondsBetweenKillingServer;
    

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
            System.out.println("Loop " + i);
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
    }

    public static Process exec(Class klass, List<String> args) throws IOException,
                                               InterruptedException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = klass.getName();

        List<String> command = new LinkedList<String>();
        command.add(javaBin);
        command.add("-cp");
        command.add(classpath);
        command.add(className);
        if (args != null) {
            command.addAll(args);
        }

        ProcessBuilder builder = new ProcessBuilder(command);

        return builder.inheritIO().start();
        //Process process = builder.inheritIO().start();
        //process.waitFor();
        //return process.exitValue();
    }
}
