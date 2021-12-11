package com.trab4;

public class ServerMessageHandler extends Thread{
    ServerContentTable table;
    String msg;
    
    ServerMessageHandler(ServerContentTable table, String msg)
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
