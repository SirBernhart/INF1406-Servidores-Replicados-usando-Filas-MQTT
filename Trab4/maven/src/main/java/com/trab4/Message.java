package com.trab4;

import java.io.BufferedReader;

import com.google.gson.*;

public class Message {
    
    private String tipomsg;
    private String chave;
    private String topico_resp;
    private Integer idpedido;
    private Integer idServ;
    private String novovalor;
    private String vistoem;

    public Message(){}

    public Message(String pTipoMsg, String pChave, String pTopicoResp, Integer pIdPedido){
        this.tipomsg = pTipoMsg;
        this.chave = pChave;
        this.topico_resp = pTopicoResp;
        this.idpedido = pIdPedido;
    }

    public Message(String pTipoMsg, String pChave, String pTopicoResp, Integer pIdPedido, String pNovoValor){
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

    public Integer getIdPedido(){
        return this.idpedido;
    }

    public void setIdPedido(Integer pIdPedido){
        this.idpedido = pIdPedido;
    }

    public String getNovoValor(){
        return this.novovalor;
    }

    public void setNovoValor(String pNovoValor){
        this.novovalor = pNovoValor;
    }

    public String getVistoEm(){
        return this.vistoem;
    }

    public void setVistoEm(String pVistoEm){
        this.vistoem = pVistoEm;
    }

    public Integer getIdServ(){
        return this.idServ;
    }
    
    public void setIdServ(Integer pIdServ){
        this.idServ = pIdServ;
    }

    public static String serialize(Message pContent) {
        Gson gson = new Gson();
        return gson.toJson(pContent);
    }

    public static Message deserialize(String pContentJson){
        Gson gson = new Gson();
        return gson.fromJson(pContentJson, Message.class);
    }

}
