package com.wendelstrauss.whatsclone.model;

import com.google.firebase.database.DatabaseReference;
import com.wendelstrauss.whatsclone.config.ConfiguracaoFirebase;

import java.io.Serializable;

public class Conversa implements Serializable {

    private String idDestinatario, nomeDestinatario, fotoDestinatario;
    private Mensagem ultimaMensagem;

    public Conversa() {
    }

    public void registrar(){
        //registrando pro remetente
        DatabaseReference remetenteRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child( ConfiguracaoFirebase.getUsuarioAtual().getUid() ).child("conversas").child( getIdDestinatario() );
        remetenteRef.setValue( this );

        //registrando pro destinatario
        DatabaseReference destinatarioRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child( getIdDestinatario() ).child("conversas").child( ConfiguracaoFirebase.getUsuarioAtual().getUid() );
        setFotoDestinatario( ConfiguracaoFirebase.getUsuarioAtual().getPhotoUrl().toString() );
        destinatarioRef.setValue( this );

    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getNomeDestinatario() {
        return nomeDestinatario;
    }

    public void setNomeDestinatario(String nomeDestinatario) {
        this.nomeDestinatario = nomeDestinatario;
    }

    public String getFotoDestinatario() {
        return fotoDestinatario;
    }

    public void setFotoDestinatario(String fotoDestinatario) {
        this.fotoDestinatario = fotoDestinatario;
    }

    public Mensagem getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(Mensagem ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;

    }
}