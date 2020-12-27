package com.wendelstrauss.whatsclone.model;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.wendelstrauss.whatsclone.config.ConfiguracaoFirebase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {

    private String idUsuario, nome, email, recado, telefone, foto, senha;

    public Usuario() {
    }

    public void salvar(){
        //pegando id de usuario
        setIdUsuario( ConfiguracaoFirebase.getUsuarioAtual().getUid() );
        //acessando nó do firebase
        DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child(getIdUsuario() );
        //salvando usuario
        usuarioRef.setValue( this );

    }

    public boolean atualizarNomeUsuario(String nome){
        //atualizando no firebase
        try{
            FirebaseUser user = ConfiguracaoFirebase.getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName( nome ).build();

            user.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if( !task.isSuccessful() ){
                        Log.d( "Perfil", "Erro ao atualizar nome de perfil" );
                    }
                }
            });

            //atualizando no database
            HashMap<String, Object> mapNome = new HashMap<>();
            mapNome.put( "nome", nome );

            DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child(getIdUsuario());
            usuarioRef.updateChildren( mapNome );
            setNome(nome);

            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public void atualizarRecado(){
        HashMap<String, Object> mapRecado = new HashMap<>();
        mapRecado.put( "recado", getRecado() );

        DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child(getIdUsuario());
        usuarioRef.updateChildren( mapRecado );
    }

    public boolean atualizarFotoUsuario(Uri url){
        try {
            FirebaseUser user = ConfiguracaoFirebase.getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setPhotoUri(url).build();
            user.updateProfile(profile);

            //atualizando no database
            HashMap<String, Object> mapFoto = new HashMap<>();
            if(url!=null) {
                mapFoto.put("foto", url.toString());
                setFoto(url.toString());
            }else {
                mapFoto.put("foto", "");
                setFoto("");
            }

            DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child(getIdUsuario());
            usuarioRef.updateChildren( mapFoto );


            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRecado() {
        return recado;
    }

    public void setRecado(String recado) {
        this.recado = recado;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
