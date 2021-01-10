package com.wendelstrauss.whatsclone.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.wendelstrauss.whatsclone.R;
import com.wendelstrauss.whatsclone.config.ConfiguracaoFirebase;
import com.wendelstrauss.whatsclone.model.Grupo;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetalhesGrupoActivity extends AppCompatActivity {

    private Grupo grupo;

    private ImageView foto;
    private TextView nome;
    private Button botao;

    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private Bitmap imagem = null;
    private Uri urlFoto;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_grupo);

        //recuperar informacoes
        recuperarInformacoes();
        //inicializar componentes
        inicializarComponentes();
        //cliques
        configurarCliques();

    }

    private void recuperarInformacoes(){
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            grupo = (Grupo) bundle.getSerializable("grupo");
        }
    }

    private void inicializarComponentes(){

        foto = findViewById(R.id.fotoDetalhesGrupo);
        nome = findViewById(R.id.nomeDetalhesGrupo);
        botao = findViewById(R.id.btnDetalhesGrupo);

        //nome
        nome.setText(grupo.getNome());
        //foto
        if(!grupo.getFoto().isEmpty()){
            Picasso.get().load(grupo.getFoto()).into(foto);
        }else{
            foto.setImageResource(R.drawable.padrao_usuario);
        }


    }

    private void configurarCliques(){
        //nome
        nome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDialogNome();
            }
        });

        //foto
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDialogImagem();
            }
        });

        //botao
        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sairGrupo();
            }
        });
    }

    private void abrirDialogNome(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nome do Grupo");

        //definindo o layout para o dialog
        LayoutInflater inflater = DetalhesGrupoActivity.this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_texto, null);
        builder.setView(layout);

        //inicializando
        EditText texto = layout.findViewById(R.id.editDialogTexto);
        texto.setText( grupo.getNome() );
        texto.requestFocus();

        //cliques
        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton("SALVAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               grupo.atualizarNome( texto.getText().toString() );
               nome.setText( grupo.getNome() );
            }
        });

        //inicializando e colocando no rodapé
        AlertDialog dialog = builder.create();
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(800, 200);
        dialog.show();

    }

    private void abrirDialogImagem(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Foto de Perfil");

        //definindo o layout para o dialog
        LayoutInflater inflater = DetalhesGrupoActivity.this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_foto_perfil, null);
        builder.setView(layout);

        //inicializando
        CircleImageView btnRemover = layout.findViewById(R.id.dialogRemover);
        CircleImageView btnGaleria = layout.findViewById(R.id.dialogGaleria);
        CircleImageView btnCamera = layout.findViewById(R.id.dialogCamera);

        //inicializando e colocando no rodapé
        AlertDialog dialog = builder.create();
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

        //cique no botao de remover
        btnRemover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removerFoto();
                dialog.dismiss();
            }
        });

        //clique no botao de camera
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if ( intentCamera.resolveActivity(getPackageManager()) != null ){ //é pra saber se conseguiu realmente abrir a camera
                    startActivityForResult( intentCamera, SELECAO_CAMERA);
                    dialog.dismiss();
                }
            }
        });

        //clique no botao de galeria
        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentGaleria = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                if( intentGaleria.resolveActivity(getPackageManager()) !=null ){ //é pra saber se conseguiu realmente abrir a galeria
                    startActivityForResult( intentGaleria, SELECAO_GALERIA );
                    dialog.dismiss();
                }
            }
        });




    }

    private void removerFoto(){
        //salvando imagem no firebase
        final StorageReference imagemRef = ConfiguracaoFirebase.getStorage().child( "imagens" ).child( "grupos" ).child( grupo.getIdGrupo() + ".jpeg" );
        imagemRef.delete().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                avisoToast("Foto removida!");
                foto.setImageResource(R.drawable.padrao_usuario);
                //atualizando foto no database
                grupo.atualizarFoto( null );
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                avisoToast("Falha ao remover");
            }
        });
    }

    private void avisoToast(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }

    private void sairGrupo(){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //pegando a imagem resultante
        if(resultCode == RESULT_OK) { //saber se conseguiu recuperar a imagem
            try{
                switch ( requestCode ){
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get( "data" ); //tem que fazer casting pq o dado retornado é um object
                        break;
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();

                        if (android.os.Build.VERSION.SDK_INT >= 29) {
                            ImageDecoder.Source imageDecoder = ImageDecoder.createSource(getContentResolver(), localImagemSelecionada);
                            imagem = ImageDecoder.decodeBitmap(imageDecoder);
                        } else {
                            imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        }
                        break;
                }

                //botando a imagem como foto de perfil
                if( imagem != null ) {
                    foto.setImageBitmap( imagem );
                }

                //salvando foto no firebase
                salvarFoto();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void salvarFoto(){

        if(imagem!=null){

            //recuperar dados da imagem
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imagem.compress( Bitmap.CompressFormat.JPEG, 70, baos );
            byte[] dadosImagem = baos.toByteArray();

            //salvando imagem no firebase
            final StorageReference imagemRef = ConfiguracaoFirebase.getStorage().child( "imagens" ).child( "grupos" ).child( grupo.getIdGrupo() + ".jpeg" );
            UploadTask uploadTask = imagemRef.putBytes( dadosImagem );

            uploadTask.addOnFailureListener(new OnFailureListener() {//caso o upload falhe
                @Override
                public void onFailure(@NonNull Exception e) {
                    avisoToast("Falha ao fazer upload da imagem");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {//caso o upload dê certo
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    avisoToast("Sucesso ao fazer Upload!");

                    //recuperando URL da imagem
                    imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            urlFoto = task.getResult();

                            //atualizando foto no database
                            grupo.atualizarFoto( urlFoto );
                        }
                    });


                }
            });

        }

    }
}
