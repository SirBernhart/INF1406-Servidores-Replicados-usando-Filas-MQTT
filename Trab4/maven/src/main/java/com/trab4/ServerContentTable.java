package com.trab4;

import java.util.HashMap;
import java.util.Map;

public class ServerContentTable extends Thread{
    private Map<String, String> hashTable = new HashMap<String, String>();

    public synchronized void OperateTable(String message)
    {
        Message deserializedMessage = Message.deserialize(message);

        String messageType = deserializedMessage.getTipoMsg();
        if(messageType.equals("consult"))
        {
            
        }
        else if(messageType.equals("insert"))
        {
            System.out.println("Table received: key: " + deserializedMessage.getChave() + " value: " + deserializedMessage.getNovoValor()+"\n");
            hashTable.put(deserializedMessage.getChave(), deserializedMessage.getNovoValor());
            System.out.println(hashTable + "\n");
        }
    }
}
