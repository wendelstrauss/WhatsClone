package com.wendelstrauss.whatsclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.wendelstrauss.whatsclone.R;
import com.wendelstrauss.whatsclone.adapter.AdapterMensagens;
import com.wendelstrauss.whatsclone.config.ConfiguracaoFirebase;
import com.wendelstrauss.whatsclone.config.Uteis;
import com.wendelstrauss.whatsclone.model.Conversa;
import com.wendelstrauss.whatsclone.model.Mensagem;
import com.wendelstrauss.whatsclone.model.Usuario;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversaActivity extends AppCompatActivity {

    private LinearLayout layoutContato;
    private CircleImageView fotoDestinatario;
    private TextView nomeDestinatario, statusDestinatario;
    private EditText editMensagem;
    private ImageView btnFoto;
    private FloatingActionButton fabEnviar;
    private Conversa conversa;

    private RecyclerView recyclerConversa;
    private List<Mensagem> listaMensagens = new ArrayList<>();
    private AdapterMensagens adapterMensagens;

    private String idDestinatario;
    private Usuario destinatario = new Usuario();

    private static final String STATUS_ENVIADA = "enviada";
    private static final int MSG_TEXTO = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        //recuperar destinatario
        recuperarDestinatario();
        //inicializar componentes
        inicializarComponentes();
        //recupera conversa
        recuperarConversa();
        //recupera mensagens
        recuperarMensagens();

        //clique no nome
        layoutContato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDetalhesContato();
            }
        });

        //clique no fab
        fabEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( !editMensagem.getText().toString().isEmpty() ) {
                    enviarMensagem();
                } else{
                    Toast.makeText(ConversaActivity.this, "Digite uma mensagem!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //clique no icone de foto
        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    abrirCamera();
            }
        });

    }

    private void recuperarDestinatario(){
        Bundle bundle = getIntent().getExtras();
        if(bundle!= null) {
            idDestinatario = bundle.getString("idDestinatario");
        }

        //recuperar Destinatario
        if(!idDestinatario.isEmpty()) {
            DatabaseReference contatosRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child(ConfiguracaoFirebase.getUsuarioAtual().getUid()).child("contatos");
            Query pesquisa = contatosRef.orderByKey().equalTo(idDestinatario);
            pesquisa.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getChildrenCount()>0) {
                        for (DataSnapshot ds:snapshot.getChildren()) {
                            destinatario = ds.getValue(Usuario.class);
                        }
                    }
                    configurarElementos();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }



    }

    private void inicializarComponentes(){
        //inicializar componentes
        layoutContato = findViewById(R.id.layoutToolbarConversa);
        fotoDestinatario = findViewById(R.id.fotoToolbarConversa);
        nomeDestinatario = findViewById(R.id.nomeToolbarConversa);
        statusDestinatario = findViewById(R.id.statusToolbarConversa);
        recyclerConversa = findViewById(R.id.recyclerConversa);
        editMensagem = findViewById(R.id.editRodapeConversa);
        btnFoto = findViewById(R.id.cameraRodapeConversa);
        fabEnviar = findViewById(R.id.fabRodapeConversa);

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbarConversa);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator( R.drawable.ic_back ); //muda o icone de voltar

        //recyclerView
        adapterMensagens = new AdapterMensagens(listaMensagens, this);
        recyclerConversa.setLayoutManager(new LinearLayoutManager(this));
        recyclerConversa.setHasFixedSize(true);
        recyclerConversa.setAdapter(adapterMensagens);

    }

    private void configurarElementos(){
        //carregando nome
        if(!destinatario.getNome().isEmpty()){//eu tenho o contato salvo
            nomeDestinatario.setText(destinatario.getNome());
        }else{//nao tenho o contato salvo
            nomeDestinatario.setText( idDestinatario );
        }

        //carregando foto
        if(!destinatario.getFoto().isEmpty()){
            Picasso.get().load(destinatario.getFoto()).into(fotoDestinatario);
        }else {
            fotoDestinatario.setImageResource(R.drawable.padrao_usuario);
        }

    }

    private void recuperarConversa(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child( ConfiguracaoFirebase.getUsuarioAtual().getUid() ).child("conversas").child( idDestinatario );
        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //se existir conversa
                if(snapshot.getChildrenCount()!=0) {
                    conversa = snapshot.getValue(Conversa.class);
                }else{
                    conversa = new Conversa();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void recuperarMensagens(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child(ConfiguracaoFirebase.getUsuarioAtual().getUid()).child("mensagens").child( idDestinatario );
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaMensagens.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    listaMensagens.add( ds.getValue(Mensagem.class) );
                    adapterMensagens.notifyDataSetChanged();
                    recyclerConversa.scrollToPosition(listaMensagens.size()-1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void abrirDetalhesContato(){

        Intent detalhesContato = new Intent(ConversaActivity.this, DetalhesContatoActivity.class);
        detalhesContato.putExtra("destinatario", destinatario);
        startActivity( detalhesContato );

    }

    private void enviarMensagem(){
        //pegando texto da mensagem
        String texto = editMensagem.getText().toString();

        //configurando mensagem
        Mensagem mensagem = new Mensagem();
        mensagem.setIdAutor( ConfiguracaoFirebase.getUsuarioAtual().getUid() );
        mensagem.setStatus( STATUS_ENVIADA );
        mensagem.setTipo( MSG_TEXTO );
        mensagem.setIdContato( idDestinatario );
        mensagem.setTexto( texto );
        mensagem.setUrl("");
        mensagem.setData( Uteis.getData() );
        mensagem.setHora( Uteis.getHora() );
        mensagem.salvar( idDestinatario );
        conversa.setIdDestinatario( idDestinatario );
        conversa.setFotoDestinatario( destinatario.getFoto() );
        conversa.setUltimaMensagem( mensagem );
        conversa.registrar();

        //limpando texto
        editMensagem.setText("");
        editMensagem.requestFocus();

    }

    private void abrirCamera(){
        Intent camera = new Intent(ConversaActivity.this, CameraActivity.class);
        startActivity( camera );
    }

}

/*
foto do remetente
nome de contato dele no celular do destinatario
*/