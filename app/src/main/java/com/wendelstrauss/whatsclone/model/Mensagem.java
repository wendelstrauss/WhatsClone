package com.wendelstrauss.whatsclone.model;

import com.google.firebase.database.DatabaseReference;
import com.wendelstrauss.whatsclone.config.ConfiguracaoFirebase;

public class Mensagem {

    private String idMensagem, idAutor, idContato, texto, url, status, data, hora;
    private int tipo;

    public Mensagem() {
        //definindo id da mensagem
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef().push();
        setIdMensagem( firebaseRef.getKey() );
    }

    public void salvar(String idDestinatario){
        /*
        DEV ALERT! -> Por algum motivo, nao consigo salvar mais de uma mensagem no caminho usuarios/uid/conversas/idCnversa/mensagens/
        A solução foi salvar as conversas em um lugar e as mensagens no outro, pelo caminho usuarios/uid/mensagens/idConversa/
        */

        //salvando pro remetente
        DatabaseReference remetenteRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child( ConfiguracaoFirebase.getUsuarioAtual().getUid() ).child("mensagens").child( getIdContato() ).child( getIdMensagem() );
        remetenteRef.setValue(this);

        //salvando pro remetente
        DatabaseReference destinatarioRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child( idDestinatario ).child("mensagens").child( ConfiguracaoFirebase.getUsuarioAtual().getUid() ).child( getIdMensagem() );
        destinatarioRef.setValue(this);

    }

    public String getIdMensagem() {
        return idMensagem;
    }

    public void setIdMensagem(String idMensagem) {
        this.idMensagem = idMensagem;
    }

    public String getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(String idAutor) {
        this.idAutor = idAutor;
    }

    public String getIdContato() {
        return idContato;
    }

    public void setIdContato(String idContato) {
        this.idContato = idContato;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
