package com.trab4;

import java.util.List;

import com.trab4.ServerLogManagerThread.LogElement;

public class HandleAllFailedServerRequests extends Thread{
    ServerLogManagerThread logManager;
    Message getLogMsg = new Message();
    Message failedServerMsg;
    ServerContentTableThread table;

    public HandleAllFailedServerRequests(ServerLogManagerThread logManager, Message failedServerMsg, ServerContentTableThread table) {
        this.logManager = logManager;
        getLogMsg.setTipoMsg("getlog");
        this.failedServerMsg = failedServerMsg;
        this.table = table;
    }

    @Override
    public void run() {
        List<LogElement> log = logManager.SendMessage(getLogMsg);

        long lastHeartbeat = failedServerMsg.getVistoEm();
        System.out.println("!! CHECKING LOGGED MESSAGES !!");
        for(int i = 0 ; i < log.size() ; i++) {
            Message loggedMessage = log.get(i).message;
            System.out.println(Message.serialize(loggedMessage));
            if(log.get(i).timeReceived >= lastHeartbeat) {
                System.out.println("Is in timeframe");
                if(loggedMessage.getTipoMsg().equals("consult") && Server.ConsultIsForThisServer(loggedMessage.getChave())) {
                    System.out.println("^^^ Will answer this one ^^^");
                    new ServerMessageHandlerThread(table, Message.serialize(loggedMessage)).start();
                }
            }
        }  
    }
}
1639363988689
1639364003382