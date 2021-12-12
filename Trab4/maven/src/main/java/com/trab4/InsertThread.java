package com.trab4;

public class InsertThread extends Thread{

    private HeartbeatArrayThread heartbeatArrayThread;
    private Message insertMsg;

    public InsertThread(HeartbeatArrayThread pHeartbeatArrayThread, Integer pHeartbeat){
        this.heartbeatArrayThread = pHeartbeatArrayThread;
        insertMsg = new Message();
        insertMsg.setTipoMsg("insert");
        insertMsg.setIdServ(pHeartbeat);
    }

    @Override
    public void run() {      
        heartbeatArrayThread.operate(insertMsg);
    }
    
}
