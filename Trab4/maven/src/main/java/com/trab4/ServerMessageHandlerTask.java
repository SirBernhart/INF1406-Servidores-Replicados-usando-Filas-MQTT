package com.trab4;

public class ServerMessageHandlerTask extends Thread{
    ServerContentTableTask table;
    String msg;
    
    ServerMessageHandlerTask(ServerContentTableTask table, String msg)
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
