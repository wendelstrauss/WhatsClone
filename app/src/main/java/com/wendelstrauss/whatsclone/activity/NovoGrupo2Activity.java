package com.wendelstrauss.whatsclone.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wendelstrauss.whatsclone.R;
import com.wendelstrauss.whatsclone.adapter.AdapterMembros;
import com.wendelstrauss.whatsclone.config.ConfiguracaoFirebase;
import com.wendelstrauss.whatsclone.config.Uteis;
import com.wendelstrauss.whatsclone.model.Conversa;
import com.wendelstrauss.whatsclone.model.Grupo;
import com.wendelstrauss.whatsclone.model.Mensagem;
import com.wendelstrauss.whatsclone.model.Usuario;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NovoGrupo2Activity extends AppCompatActivity {
    private Toolbar toolbar;
    private CircleImageView foto;
    private EditText editNome;
    private TextView participantes;
    private RecyclerView recyclerMembros;
    private AdapterMembros adapterMembros;
    private List<Usuario> listaMembros = new ArrayList<>();

    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private Bitmap imagem = null;
    private String idGrupo;
    private Uri urlFoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_grupo2);
        //recuperar dados
        recuperarDados();
        //inicializar componentes
        inicializarComponentes();

        //clique na foto
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDialogImagem();
            }
        });


    }

    private void inicializarComponentes(){
        //Configurar toolbar
        toolbar = findViewById(R.id.toolbarSecundaria);
        toolbar.setTitle("Novo Grupo");
        toolbar.setSubtitle("Adicionar nome");
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator( R.drawable.ic_back ); //muda o icone de voltar

        //componentes
        foto = findViewById(R.id.fotoNovoGrupo2);
        editNome = findViewById(R.id.editNovoGrupo2);
        participantes = findViewById(R.id.participantesNovoGrupo2);
        recyclerMembros = findViewById(R.id.recyclerNovoGrupo2);

        //recycler membros
        adapterMembros = new AdapterMembros(listaMembros);
        recyclerMembros.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerMembros.setHasFixedSize(true);
        recyclerMembros.setAdapter( adapterMembros );

        //set dados
        participantes.setText("Participantes: "+listaMembros.size());
    }

    private void recuperarDados(){

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            listaMembros = (List<Usuario>) bundle.getSerializable("lista");
        }

    }

    private void abrirDialogImagem(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Foto de Perfil");

        //definindo o layout para o dialog
        LayoutInflater inflater = NovoGrupo2Activity.this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_foto_perfil, null);
        builder.setView(layout);

        //inicializando
        CircleImageView btnRemover = layout.findViewById(R.id.dialogRemover);
        TextView textoRemover = layout.findViewById(R.id.dialogTxtRemover);
        btnRemover.setVisibility(View.GONE);
        textoRemover.setVisibility(View.GONE);
        CircleImageView btnGaleria = layout.findViewById(R.id.dialogGaleria);
        CircleImageView btnCamera = layout.findViewById(R.id.dialogCamera);

        //inicializando e colocando no rodapé
        AlertDialog dialog = builder.create();
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

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



    public void criarGrupo(View view){

        String nome = editNome.getText().toString();
        if(!nome.isEmpty()){

            //salvando no grupo
            Grupo grupo = new Grupo();
            grupo.setNome( nome );
            grupo.setIdGrupo();
            idGrupo = grupo.getIdGrupo();
            grupo.setListaMembros( listaMembros );
            if(urlFoto!=null) {
                grupo.setFoto(urlFoto.toString());
            }else{
                grupo.setFoto("");
            }

            grupo.salvarGrupo();

            //gerando primeira mensagem
            Mensagem mensagem = new Mensagem();
            mensagem.setHora(Uteis.getHora());
            mensagem.setTexto("Este grupo foi recém criado!");

            //gerando conversa
            Conversa conversa = new Conversa();
            conversa.setGrupo(true);
            conversa.setNomeDestinatario(grupo.getNome());
            conversa.setFotoDestinatario(grupo.getFoto());
            conversa.setIdDestinatario(grupo.getIdGrupo());
            conversa.setUltimaMensagem(mensagem);
            salvarConversa(conversa);

            //redirecionando
            abrirConversa(conversa);
            finish();


        }else {
            Toast.makeText(this, "Insira ao menos o nome do grupo", Toast.LENGTH_SHORT).show();
        }

    }

    private void salvarConversa(Conversa conversa){
        for (int i=0;i<listaMembros.size();i++){
            conversa.salvarConversaGrupo(listaMembros.get(i).getIdUsuario());
        }
        conversa.salvarConversaGrupo(ConfiguracaoFirebase.getUsuarioAtual().getUid());
    }

    private void abrirConversa( Conversa conversa ){

        Intent janela = new Intent(NovoGrupo2Activity.this, ConversaActivity.class);
        janela.putExtra("idDestinatario", conversa.getIdDestinatario());
        startActivity( janela );
        finish();

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
            final StorageReference imagemRef = ConfiguracaoFirebase.getStorage().child( "imagens" ).child( "grupos" ).child( idGrupo + ".jpeg" );
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
