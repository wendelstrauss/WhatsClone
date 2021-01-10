package com.wendelstrauss.whatsclone.model;

import com.google.firebase.database.DatabaseReference;
import com.wendelstrauss.whatsclone.config.ConfiguracaoFirebase;

import java.io.Serializable;
import java.util.List;

public class Conversa implements Serializable {

    private String idDestinatario, nomeDestinatario, fotoDestinatario;
    private boolean grupo;
    private Mensagem ultimaMensagem;

    public Conversa() {

    }

    public void salvarConversaDestinatario(){
        //registrando pro remetente
        DatabaseReference remetenteRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child( ConfiguracaoFirebase.getUsuarioAtual().getUid() ).child("conversas").child( getIdDestinatario() );
        remetenteRef.setValue( this );

        //registrando pro destinatario
        DatabaseReference destinatarioRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child( getIdDestinatario() ).child("conversas").child( ConfiguracaoFirebase.getUsuarioAtual().getUid() );
        setNomeDestinatario( ConfiguracaoFirebase.getUsuarioAtual().getDisplayName() );
        setFotoDestinatario( ConfiguracaoFirebase.getUsuarioAtual().getPhotoUrl().toString() );
        destinatarioRef.setValue( this );

    }

    public void salvarConversaGrupo(String idMembro){
        DatabaseReference conversaRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child( idMembro ).child("conversas").child( getIdDestinatario() );
        conversaRef.setValue( this );
    }

    public void apagar(){
        //apaga conversa
        DatabaseReference conversaRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child( ConfiguracaoFirebase.getUsuarioAtual().getUid() ).child("conversas").child( getIdDestinatario() );
        conversaRef.removeValue();
        //apaga mensagem
        DatabaseReference mensagemRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child( ConfiguracaoFirebase.getUsuarioAtual().getUid() ).child("mensagens").child( getIdDestinatario() );
        mensagemRef.removeValue();
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

    public boolean isGrupo() {
        return grupo;
    }

    public void setGrupo(boolean grupo) {
        this.grupo = grupo;
    }

    public Mensagem getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(Mensagem ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;

    }
}