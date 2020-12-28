package com.wendelstrauss.whatsclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.squareup.picasso.Picasso;
import com.wendelstrauss.whatsclone.R;
import com.wendelstrauss.whatsclone.config.ConfiguracaoFirebase;
import com.wendelstrauss.whatsclone.model.Usuario;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversaActivity extends AppCompatActivity {

    private LinearLayout layoutContato;
    private CircleImageView fotoDestinatario;
    private TextView nomeDestinatario, statusDestinatario;
    private RecyclerView recyclerConversa;
    private EditText editMensagem;
    private ImageView btnFoto;
    private FloatingActionButton fabEnviar;

    private Usuario destinatario;
    private String idRemetente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);
        //inicializar componentes
        inicializarComponentes();
        //recuperar remetente
        configurarConversa();

        //clique no nome
        layoutContato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDetalhesContato();
            }
        });

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

    }

    private void configurarConversa(){
        Bundle bundle = getIntent().getExtras();
        destinatario = (Usuario)bundle.getSerializable( "destinatario" );

        //configurar remetente
        idRemetente = ConfiguracaoFirebase.getUsuarioAtual().getUid();

        //definindo campos
        if(!destinatario.getFoto().isEmpty()){
            Picasso.get().load(destinatario.getFoto()).into(fotoDestinatario);
        }else {
            fotoDestinatario.setImageResource(R.drawable.padrao_usuario);
        }

        //definindo nome
        nomeDestinatario.setText(destinatario.getNome());

    }

    private void abrirDetalhesContato(){

        Intent detalhesContato = new Intent(ConversaActivity.this, DetalhesContatoActivity.class);
        detalhesContato.putExtra("destinatario", destinatario);
        startActivity( detalhesContato );

    }

}
