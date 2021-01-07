package com.wendelstrauss.whatsclone.config;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wendelstrauss.whatsclone.model.Usuario;

public class ConfiguracaoFirebase {

    private static DatabaseReference firebaseRef;
    private static FirebaseAuth autenticacao;
    private static StorageReference storage;
    private static Usuario usuarioRemetente;

    public static DatabaseReference getFirebaseRef(){

        if(firebaseRef==null){
            firebaseRef = FirebaseDatabase.getInstance().getReference();
        }
        return firebaseRef;
    }

    public static FirebaseAuth getAutenticacao(){
        if(autenticacao==null){
            autenticacao = FirebaseAuth.getInstance();
        }
        return autenticacao;
    }

    public static StorageReference getStorage(){
        if(storage == null){
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getAutenticacao();
        return usuario.getCurrentUser();
    }

    public static Usuario getRemetente(){
        if(usuarioRemetente == null) {
            DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(getUsuarioAtual().getUid());
            usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    usuarioRemetente = snapshot.getValue(Usuario.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        return usuarioRemetente;
    }
}
