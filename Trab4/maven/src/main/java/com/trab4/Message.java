package com.trab4;

import java.io.BufferedReader;

import com.google.gson.*;

public class Message {
    
    private String tipomsg;
    private String chave;
    private String topico_resp;
    private String idpedido;
    private String novovalor;

    public Message(){}

    public Message(String pTipoMsg, String pChave, String pTopicoResp, String pIdPedido){
        this.tipomsg = pTipoMsg;
        this.chave = pChave;
        this.topico_resp = pTopicoResp;
        this.idpedido = pIdPedido;
    }

    public Message(String pTipoMsg, String pChave, String pTopicoResp, String pIdPedido, String pNovoValor){
        this.tipomsg = pTipoMsg;
        this.chave = pChave;
        this.topico_resp = pTopicoResp;
        this.idpedido = pIdPedido;
        this.novovalor = pNovoValor;
    }

    public String getTipoMsg(){
        return this.tipomsg;
    }

    public void setTipoMsg(String pTipoMsg){
        this.tipomsg = pTipoMsg;
    }

    public String getChave(){
        return this.chave;
    }

    public void setChave(String pChave){
        this.chave = pChave;
    }

    public String getTopicoResp(){
        return this.topico_resp;
    }

    public void setTopicoResp(String pTopicoResp){
        this.topico_resp = pTopicoResp;
    }

    public String getIdPedido(){
        return this.idpedido;
    }

    public void setIdPedido(String pIdPedido){
        this.idpedido = pIdPedido;
    }

    public String getNovoValor(){
        return this.novovalor;
    }

    public void setNovoValor(String pNovoValor){
        this.novovalor = pNovoValor;
    }

    public String serialize(Message pContent) {
        Gson gson = new Gson();
        return gson.toJson(pContent);
    }

    public void deserialize(BufferedReader pContentJson, Message pMessage){
        
        Gson gson = new Gson();
        Message content = gson.fromJson(pContentJson, Message.class);

        pMessage.tipomsg = content.tipomsg;
        pMessage.chave = content.chave;
        pMessage.topico_resp = content.topico_resp;
        pMessage.idpedido = content.idpedido;
        pMessage.novovalor = content.novovalor;
    }

}
