package com.wendelstrauss.whatsclone.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.wendelstrauss.whatsclone.R;
import com.wendelstrauss.whatsclone.config.ConfiguracaoFirebase;
import com.wendelstrauss.whatsclone.model.Usuario;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesActivity extends AppCompatActivity {

    private CircleImageView foto;
    private ImageView btnNome, btnRecado;
    private FloatingActionButton fabFoto;
    private TextView txtNome, txtRecado, txtTelefone;

    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private Bitmap imagem = null;
    private Uri urlFoto;

    private FirebaseAuth autenticacao;
    private StorageReference storageReference;
    private FirebaseUser usuarioFire;
    private Usuario usuarioRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        //Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarSecundaria);
        toolbar.setTitle("Perfil");
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator( R.drawable.ic_back ); //muda o icone de voltar

        //inicializar componetes
        inicializarComponentes();

        //recuperando informacoes
        recuperarInformacoes();

    }

    public void abrirDialogImagem(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Foto de Perfil");

        //definindo o layout para o dialog
        LayoutInflater inflater = ConfiguracoesActivity.this.getLayoutInflater();
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

    public void abrirDialogNome(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insira seu Nome");

        //definindo o layout para o dialog
        LayoutInflater inflater = ConfiguracoesActivity.this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_texto, null);
        builder.setView(layout);

        //inicializando
        EditText texto = layout.findViewById(R.id.editDialogTexto);
        texto.setText( usuarioFire.getDisplayName() );
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
                usuarioRef.atualizarNomeUsuario( texto.getText().toString() );
                txtNome.setText( usuarioRef.getNome() );
            }
        });

        //inicializando e colocando no rodapé
        AlertDialog dialog = builder.create();
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(800, 200);
        dialog.show();

    }

    public void abrirDialogRecado(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Adicionar recado");

        //definindo o layout para o dialog
        LayoutInflater inflater = ConfiguracoesActivity.this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_texto, null);
        builder.setView(layout);

        //inicializando
        EditText texto = layout.findViewById(R.id.editDialogTexto);
        texto.setText( usuarioRef.getRecado() );
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
                usuarioRef.setRecado( texto.getText().toString() );
                usuarioRef.atualizarRecado();
                txtRecado.setText( usuarioRef.getRecado() );
            }
        });

        //inicializando e colocando no rodapé
        AlertDialog dialog = builder.create();
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

    }

    private void recuperarInformacoes(){
        //inicializando usuario
        DatabaseReference usuarios = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child(usuarioFire.getUid());
        usuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuarioRef = snapshot.getValue(Usuario.class);
                //recuperando recado
                txtRecado.setText( usuarioRef.getRecado() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //recuperando foto de usuario
        Uri url = usuarioFire.getPhotoUrl();
        if( url !=null ){
            Picasso.get().load(url).into(foto);
        } else {
            foto.setImageResource(R.drawable.padrao_usuario);
        }

        //recuperando nome
        txtNome.setText(usuarioFire.getDisplayName());
        //recuperando telefone
        txtTelefone.setText(usuarioFire.getPhoneNumber());

    }

    private void inicializarComponentes() {

        foto = findViewById(R.id.circleFotoConfig);
        fabFoto = findViewById(R.id.fabConfig);
        btnNome = findViewById(R.id.imgConfigNome);
        btnRecado = findViewById(R.id.imgConfigRecado);
        txtNome = findViewById(R.id.textConfigNome);
        txtRecado = findViewById(R.id.textConfigRecado);
        txtTelefone = findViewById(R.id.textConfigTelefone);

        //configuracoes iniciais
        usuarioFire = ConfiguracaoFirebase.getUsuarioAtual();
        storageReference = ConfiguracaoFirebase.getStorage();
        autenticacao = ConfiguracaoFirebase.getAutenticacao();
        usuarioRef = new Usuario();

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
            final StorageReference imagemRef = storageReference.child( "imagens" ).child( "perfil" ).child( usuarioFire.getUid() + ".jpeg" );
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
                            usuarioRef.atualizarFotoUsuario( urlFoto );
                        }
                    });


                }
            });

        }

    }

    private void removerFoto(){
        //salvando imagem no firebase
        final StorageReference imagemRef = storageReference.child( "imagens" ).child( "perfil" ).child( usuarioFire.getUid() + ".jpeg" );
        imagemRef.delete().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                avisoToast("Foto removida!");
                foto.setImageResource(R.drawable.padrao_usuario);
                //atualizando foto no database
                usuarioRef.atualizarFotoUsuario( null );
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

}
