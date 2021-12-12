package com.trab4;

public class ServerMessageHandlerThread extends Thread{
    ServerContentTableThread table;
    String msg;
    
    ServerMessageHandlerThread(ServerContentTableThread table, String msg)
    {
        this.table = table;
        this.msg = msg;
    }

    @Override
    public void run()
    {
        table.OperateTable(msg);
    }
}
