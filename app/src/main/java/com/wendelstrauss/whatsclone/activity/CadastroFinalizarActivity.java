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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class CadastroFinalizarActivity extends AppCompatActivity {

    private String telefone;
    private String nome;
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;

    private CircleImageView imagemPerfil;
    private EditText nomeExibicao;
    private Button btnFinalizar;

    private StorageReference storageReference;
    private FirebaseAuth autenticacao;
    private DatabaseReference firebaseRef;
    private String identificadorUsuario;
    private Uri urlFoto = null;
    private Bitmap imagem = null;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_finalizar);

        //inicializar componentes
        imagemPerfil = findViewById(R.id.imagemFinalizarCadastro);
        nomeExibicao = findViewById(R.id.nomeFinalizarCadastro);
        btnFinalizar = findViewById(R.id.btnFinalizarCadastro);

        //pegando informacoes da activity anterior
        recuperarDados();

        //configuracoes iniciais
        identificadorUsuario = ConfiguracaoFirebase.getUsuarioAtual().getUid();
        firebaseRef = ConfiguracaoFirebase.getFirebaseRef();
        autenticacao = ConfiguracaoFirebase.getAutenticacao();
        storageReference = ConfiguracaoFirebase.getStorage();

        //clique na imagem pra pegar foto
        imagemPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDialog();
            }
        });


    }

    public void finalizarCadastro(View view){

        nome = nomeExibicao.getText().toString();
        if(!nome.isEmpty()){
            usuario = new Usuario();
            usuario.setIdUsuario(identificadorUsuario);
            usuario.setNome(nome);
            usuario.setTelefone(telefone);
            usuario.setRecado(getString(R.string.recado_padrao));
            usuario.salvar();
            usuario.atualizarFotoUsuario(urlFoto);
            if( usuario.atualizarNomeUsuario(nome) ){
                redirecionar();
            }


        }else{
            avisoToast("Insira um nome de Exibição!");
        }

    }

    private void redirecionar(){
        startActivity(new Intent(CadastroFinalizarActivity.this, MainActivity.class));
        finish();
    }

    private void recuperarDados() {
        Bundle bundle = getIntent().getExtras();
        telefone = bundle.getString("telefone");

        //vendo se o usuario já existe
        DatabaseReference usuarioAtualRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child( autenticacao.getUid() );
        usuarioAtualRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                String foto = usuario.getFoto();
                String nome = usuario.getNome();

                //botando a imagem como foto de perfil
                if( foto != null ) {
                    Picasso.get().load(foto).into(imagemPerfil);
                }else {
                    imagemPerfil.setImageResource( R.drawable.padrao_usuario );
                }

                //botando nome
                nomeExibicao.setText( nome );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void abrirDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("De onde deseja pegar a foto?");
        builder.setPositiveButton("CAMERA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if ( intentCamera.resolveActivity(getPackageManager()) != null ){ //é pra saber se conseguiu realmente abrir a camera
                    startActivityForResult( intentCamera, SELECAO_CAMERA);
                }
            }
        });
        builder.setNegativeButton("GALERIA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intentGaleria = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                if( intentGaleria.resolveActivity(getPackageManager()) !=null ){ //é pra saber se conseguiu realmente abrir a galeria
                    startActivityForResult( intentGaleria, SELECAO_GALERIA );
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

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
                    imagemPerfil.setImageBitmap( imagem );
                }else {
                    imagemPerfil.setImageResource( R.drawable.padrao_usuario );
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
            final StorageReference imagemRef = storageReference.child( "imagens" ).child( "perfil" ).child( identificadorUsuario + ".jpeg" );
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
                        }
                    });


                }
            });

        }

    }

    private void avisoToast(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }
}







