package com.wendelstrauss.whatsclone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wendelstrauss.whatsclone.R;
import com.wendelstrauss.whatsclone.model.Usuario;

public class DetalhesContatoActivity extends AppCompatActivity {

    private Usuario destinatario;
    private ImageView foto;
    private TextView nome, status, recado, telefone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_contato);
        //inicializar componentes
        inicializarComponentes();
        //recuperando contato
        recuperarContato();

    }
    private void inicializarComponentes(){
        foto = findViewById(R.id.fotoDetalheContato);
        nome = findViewById(R.id.nomeDetalheContato);
        status = findViewById(R.id.statusDetalheContato);
        recado = findViewById(R.id.recadoDetalheContato);
        telefone= findViewById(R.id.telefoneDetalheContato);
    }

    private void recuperarContato(){
        Bundle bundle = getIntent().getExtras();
        destinatario = (Usuario)bundle.getSerializable( "destinatario" );

        //botando informacoes
        //foto
        if(destinatario.getFoto()!=null){
            Picasso.get().load(destinatario.getFoto()).into(foto);
        }else {
            foto.setImageResource(R.drawable.padrao_usuario);
        }
        //nome
        nome.setText(destinatario.getNome());
        //recado
        recado.setText(destinatario.getRecado());
        //telefone
        telefone.setText(destinatario.getTelefone());

    }

}
