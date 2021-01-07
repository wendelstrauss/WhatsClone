package com.wendelstrauss.whatsclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wendelstrauss.whatsclone.R;
import com.wendelstrauss.whatsclone.config.ConfiguracaoFirebase;

public class TratarImagemActivity extends AppCompatActivity {

    private ImageView imageView;
    private EditText txtLegenda;
    private FloatingActionButton fab;
    private StorageReference storageReference;
    private Bitmap imagemGerada = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tratar_imagem);

        //inicializar componentes
        inicializarComponentes();
    }

    private void inicializarComponentes(){
        imageView = findViewById(R.id.imagemTratarImagem);
        txtLegenda = findViewById(R.id.legendaTratarImagem);
        fab = findViewById(R.id.fabTratarImg);
    }

}
