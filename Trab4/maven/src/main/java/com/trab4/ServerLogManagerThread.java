package com.trab4;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

public class ServerLogManagerThread extends Thread{
    List<LogElement> messageLog = new LinkedList<LogElement>();
    Long cleanupInterval;

    public ServerLogManagerThread(Long cleanupInterval){
        this.cleanupInterval = cleanupInterval*1000;
        System.out.println("cleanup " + this.cleanupInterval);
    }

    @Override
    public void run() {
        new CleanupTimer(cleanupInterval, this).start();;
    }

    public synchronized void SendMessage(Message msg)
    {
        if(msg.getTipoMsg().equals("cleanup"))
        {
            Long currTime = Instant.now().toEpochMilli();
            System.out.println("!!! Cleanup log !!! Currtime: " + currTime);

            for(int i = 0 ; i < messageLog.size() ; i++)
            {
                LogElement element = messageLog.get(i);
                if(currTime - element.timeReceived > cleanupInterval)
                {
                    System.out.println("Removing " + element.message + " / timediff: " + (currTime - element.timeReceived));
                    messageLog.remove(i);
                }
            }
        }
        else if(msg.getTipoMsg().equals("insert") || msg.getTipoMsg().equals("consult"))
        {
            LogElement log = new LogElement(msg, Instant.now().toEpochMilli());
            messageLog.add(log);
            System.out.println("Inserted log. Log list: " + messageLog + "\n");
        }
    }

    class LogElement {
        public Message message;
        public Long timeReceived;
        
        public LogElement(Message message, Long timeReceived) {
            this.message = message;
            this.timeReceived = timeReceived;
        }
    }

    class CleanupTimer extends Thread {
        Long cleanupInterval;
        ServerLogManagerThread logManager;
        Message cleanupMsg;

        public CleanupTimer(Long cleanupInterval, ServerLogManagerThread logManager){
            this.cleanupInterval = cleanupInterval;
            System.out.println("Sleep " + this.cleanupInterval);
            this.logManager = logManager;
            cleanupMsg = new Message();
            cleanupMsg.setTipoMsg("cleanup");
        }

        @Override
        public void run() {
            while(true){
                try {
                    Thread.sleep(cleanupInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
    
                logManager.SendMessage(cleanupMsg);
            }
        }
    }
}
