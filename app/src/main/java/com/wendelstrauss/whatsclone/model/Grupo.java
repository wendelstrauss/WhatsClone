package com.wendelstrauss.whatsclone.model;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.wendelstrauss.whatsclone.config.ConfiguracaoFirebase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Grupo implements Serializable {

    private String idGrupo, nome, foto;
    private List<Usuario> listaMembros;

    public Grupo() {
    }

    public void salvarGrupo(){
        DatabaseReference grupoRef = ConfiguracaoFirebase.getFirebaseRef().child("grupos").child( getIdGrupo() );
        grupoRef.setValue(this);
    }

    public void atualizarNome(String nome){
        //atualizando no database
        HashMap<String, Object> mapNome = new HashMap<>();
        mapNome.put( "nome", nome );

        //atualizando no grupos
        DatabaseReference grupoRef = ConfiguracaoFirebase.getFirebaseRef().child("grupos").child( getIdGrupo() );
        grupoRef.updateChildren( mapNome );
        setNome(nome);

        //atualizando para os membros
        HashMap<String, Object> mapNomeUsuarios = new HashMap<>();
        mapNomeUsuarios.put( "nomeDestinatario", nome );
        //usuario atual
        DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child(ConfiguracaoFirebase.getUsuarioAtual().getUid()).child("conversas").child( getIdGrupo() );
        usuarioRef.updateChildren( mapNomeUsuarios );
        //outros membros
        for(int i=0;i<listaMembros.size();i++){

            DatabaseReference membroRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child(listaMembros.get(i).getIdUsuario()).child("conversas").child( getIdGrupo() );
            membroRef.updateChildren( mapNomeUsuarios );

        }
    }

    public void atualizarFoto(Uri url){
            //atualizando no nó de grupos
            HashMap<String, Object> mapFoto = new HashMap<>();
            if(url!=null) {
                mapFoto.put("foto", url.toString());
                setFoto(url.toString());
            }else {
                mapFoto.put("foto", "");
                setFoto("");
            }

            DatabaseReference grupoRef = ConfiguracaoFirebase.getFirebaseRef().child("grupos").child( getIdGrupo() );
        grupoRef.updateChildren( mapFoto );

            //atualizando para os membros
            HashMap<String, Object> mapMembros = new HashMap<>();
            if(url!=null) {
                mapFoto.put("fotoDestinatario", url.toString());
                setFoto(url.toString());
            }else {
                mapFoto.put("fotoDestinatario", "");
                setFoto("");
            }
            DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child( ConfiguracaoFirebase.getUsuarioAtual().getUid() ).child("conversas").child(getIdGrupo());
            usuarioRef.updateChildren( mapFoto );

            //outros membros
            for(int i=0;i<listaMembros.size();i++){

                DatabaseReference membroRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child(listaMembros.get(i).getIdUsuario()).child("conversas").child( getIdGrupo() );
                membroRef.updateChildren( mapFoto );

            }


    }

    public String getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo() {
        DatabaseReference grupoRef = ConfiguracaoFirebase.getFirebaseRef().child("grupos");
        this.idGrupo = grupoRef.push().getKey();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public List<Usuario> getListaMembros() {
        return listaMembros;
    }

    public void setListaMembros(List<Usuario> listaMembros) {
        this.listaMembros = listaMembros;
    }
}
