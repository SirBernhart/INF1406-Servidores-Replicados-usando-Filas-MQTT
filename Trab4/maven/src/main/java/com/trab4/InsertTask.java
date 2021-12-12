package com.trab4;

public class InsertTask implements Runnable{
    private Message msgHeartbeat = new Message();
    private Integer serverCount;

    public InsertTask(Integer pServerCount, Message pMsgHeartbeat){
        this.serverCount = pServerCount;
        this.msgHeartbeat = pMsgHeartbeat;
    }

    @Override
    public void run() {      
        HeartbeatArrayTask heartbeatArrayTask = new HeartbeatArrayTask(serverCount, msgHeartbeat);
        Thread threadHeartbeatArray = new Thread(heartbeatArrayTask);
        threadHeartbeatArray.start();
    }
    
}
