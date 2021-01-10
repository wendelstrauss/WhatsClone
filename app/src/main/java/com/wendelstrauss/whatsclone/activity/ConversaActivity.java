package com.wendelstrauss.whatsclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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
import com.wendelstrauss.whatsclone.model.Grupo;
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
    private Grupo grupo = new Grupo();

    private static final String STATUS_ESPERANDO = "esperando";
    private static final int MSG_TEXTO = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        //recuperar bundle
        recuperarInformacoes();
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
                if(conversa.isGrupo()){
                    abrirDetalhesGrupo();
                }else{
                    abrirDetalhesContato();
                }
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

    private void recuperarInformacoes(){
        Bundle bundle = getIntent().getExtras();
        if(bundle!= null) {
            idDestinatario = bundle.getString("idDestinatario");
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

        //abrindo caixa de texto
        editMensagem.requestFocus();

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
                    conversa.setGrupo(false);
                }

                //recuperando destinatario ou grupo
                if(!conversa.isGrupo()){
                    //recuperar destinatario
                    recuperarDestinatario();
                }else {
                    recuperarGrupo();
                }
                //recycler view
                criarRecycler();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void recuperarDestinatario(){
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
                    //configurar conversa
                    configurarElementos();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void recuperarGrupo(){
        if(!idDestinatario.isEmpty()){
            DatabaseReference grupoRef = ConfiguracaoFirebase.getFirebaseRef().child("grupos").child(idDestinatario);
            grupoRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    grupo = snapshot.getValue(Grupo.class);

                    //configurar conversa
                    configurarElementos();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void criarRecycler(){
        //recyclerView
        if(conversa.isGrupo()) {
            adapterMensagens = new AdapterMensagens(listaMensagens, this, true);
        }else {
            adapterMensagens = new AdapterMensagens(listaMensagens, this, false);
        }
        recyclerConversa.setLayoutManager(new LinearLayoutManager(this));
        recyclerConversa.setHasFixedSize(true);
        recyclerConversa.setAdapter(adapterMensagens);
    }

    private void configurarElementos(){
        if(!conversa.isGrupo()) {
            //carregando nome
            if (!destinatario.getNome().isEmpty()) {//eu tenho o contato salvo
                nomeDestinatario.setText(destinatario.getNome());
            } else {//nao tenho o contato salvo
                nomeDestinatario.setText(idDestinatario);
            }

            //carregando foto
            if (!destinatario.getFoto().isEmpty()) {
                Picasso.get().load(destinatario.getFoto()).into(fotoDestinatario);
            } else {
                fotoDestinatario.setImageResource(R.drawable.padrao_usuario);
            }
        }else{

            //carregando nome
            nomeDestinatario.setText(grupo.getNome());

            //carregando foto
            if(!grupo.getFoto().isEmpty()){
                Picasso.get().load(grupo.getFoto()).into(fotoDestinatario);
            }else{
                fotoDestinatario.setImageResource(R.drawable.padrao_usuario);
            }

            //escondendo o visto
            statusDestinatario.setVisibility(View.GONE);

        }

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

    private void abrirDetalhesGrupo(){
        Intent detalhesGrupo = new Intent(ConversaActivity.this, DetalhesGrupoActivity.class);
        detalhesGrupo.putExtra("grupo", grupo);
        startActivity( detalhesGrupo );
    }

    private void enviarMensagem(){
        //pegando texto da mensagem
        String texto = editMensagem.getText().toString();

        //configurando mensagem
        Mensagem mensagem = new Mensagem();
        mensagem.setIdAutor( ConfiguracaoFirebase.getUsuarioAtual().getUid() );
        mensagem.setStatus( STATUS_ESPERANDO );
        mensagem.setTipo( MSG_TEXTO );
        mensagem.setIdContato( idDestinatario );
        mensagem.setTexto( texto );
        mensagem.setUrl("");
        mensagem.setData( Uteis.getData() );
        mensagem.setHora( Uteis.getHora());

        //configurar conversa
        if(conversa.isGrupo()) {
            conversa.setNomeDestinatario(grupo.getNome());
            conversa.setFotoDestinatario(grupo.getFoto());
        }else {
            conversa.setNomeDestinatario(destinatario.getNome());
            conversa.setFotoDestinatario(destinatario.getFoto());
        }

        conversa.setIdDestinatario( idDestinatario );
        conversa.setUltimaMensagem( mensagem );
        if(conversa.isGrupo()){
            salvarConversaGrupo();
        }else {
            conversa.salvarConversaDestinatario();
        }
        //salvando mensagem
        if(conversa.isGrupo()){
            enviarMensagemGrupo(mensagem, grupo.getListaMembros());
        }else{
            enviarMensagemDestinatario(mensagem, idDestinatario);
        }

        //limpando texto
        editMensagem.setText("");
        editMensagem.requestFocus();

    }

    private void salvarConversaGrupo(){
        for (int i=0;i<grupo.getListaMembros().size();i++){
            conversa.salvarConversaGrupo(grupo.getListaMembros().get(i).getIdUsuario());
        }
        conversa.salvarConversaGrupo(ConfiguracaoFirebase.getUsuarioAtual().getUid());
    }

    private void abrirCamera(){
        Intent camera = new Intent(ConversaActivity.this, CameraActivity.class);
        startActivity( camera );
    }

    private void enviarMensagemDestinatario(Mensagem mensagem, String idDest){
         /*
        DEV ALERT! -> Por algum motivo, nao consigo salvar mais de uma mensagem no caminho usuarios/uid/conversas/idCnversa/mensagens/
        A solução foi salvar as conversas em um lugar e as mensagens no outro, pelo caminho usuarios/uid/mensagens/idConversa/
        */

        //salvando pro remetente
        DatabaseReference remetenteRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child( ConfiguracaoFirebase.getUsuarioAtual().getUid() ).child("mensagens").child( idDest ).child( mensagem.getIdMensagem() );
        remetenteRef.setValue(mensagem);

        //salvando pro remetente
        DatabaseReference destinatarioRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child( idDest ).child("mensagens").child( ConfiguracaoFirebase.getUsuarioAtual().getUid() ).child( mensagem.getIdMensagem() );
        destinatarioRef.setValue(mensagem);
    }

    private void enviarMensagemGrupo(Mensagem mensagem, List<Usuario>listaMembros){
        //salvando pro remetente
        DatabaseReference remetenteRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child( ConfiguracaoFirebase.getUsuarioAtual().getUid() ).child("mensagens").child( idDestinatario ).child( mensagem.getIdMensagem() );
        remetenteRef.setValue(mensagem);

        //salvando para os destinatarios
        for(int i=0;i<listaMembros.size();i++){
            DatabaseReference membroRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child( listaMembros.get(i).getIdUsuario() ).child("mensagens").child( idDestinatario ).child( mensagem.getIdMensagem() );
            membroRef.setValue(mensagem);
        }

    }

}