package com.wendelstrauss.whatsclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.wendelstrauss.whatsclone.R;
import com.wendelstrauss.whatsclone.adapter.AdapterContatos;
import com.wendelstrauss.whatsclone.adapter.AdapterMembros;
import com.wendelstrauss.whatsclone.config.ConfiguracaoFirebase;
import com.wendelstrauss.whatsclone.config.RecyclerItemClickListener;
import com.wendelstrauss.whatsclone.model.Usuario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NovoGrupo1Activity extends AppCompatActivity {

    private Toolbar toolbar;

    private RecyclerView recyclerContatos, recyclerMembros;
    private List<Usuario> listaContatos = new ArrayList<>();
    private List<Usuario> listaMembros = new ArrayList<>();
    private AdapterContatos adapterContatos;
    private AdapterMembros adapterMembros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_grupo1);

        //inicializar componetentes
        inicializarComponentes();
        //recuperar contatos
        recuperarContatos();

    }

    private void inicializarComponentes(){

        //Configurar toolbar
        toolbar = findViewById(R.id.toolbarSecundaria);
        toolbar.setTitle("Novo Grupo");
        toolbar.setSubtitle("Adicionar Participantes");
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator( R.drawable.ic_back ); //muda o icone de voltar


        //componentes
        recyclerMembros = findViewById(R.id.recyclerMembrosNovoGrupo1);
        recyclerContatos = findViewById(R.id.recyclerContatosNovoGrupo1);

        //recycler contatos
        adapterContatos = new AdapterContatos(listaContatos, this);
        recyclerContatos.setLayoutManager(new LinearLayoutManager(this));
        recyclerContatos.setHasFixedSize(true);
        recyclerContatos.setAdapter(adapterContatos);

        //recycler membros
        adapterMembros = new AdapterMembros(listaMembros);
        recyclerMembros.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerMembros.setHasFixedSize(true);
        recyclerMembros.setAdapter( adapterMembros );

        //clique no recycler de contatos
        recyclerContatos.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerContatos,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Usuario  contato = listaContatos.get(position);
                        listaContatos.remove(contato);
                        listaMembros.add( contato );
                        adapterContatos.notifyDataSetChanged();
                        adapterMembros.notifyDataSetChanged();
                        if(listaMembros.size()>0) {
                            toolbar.setSubtitle(listaMembros.size() + " de " + (listaMembros.size() + listaContatos.size()) + " selecionados ");
                        }else{
                            toolbar.setSubtitle("Adicionar Participantes");
                        }

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        //clique no recycler de membros
        recyclerMembros.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerMembros,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Usuario membro = listaMembros.get(position);
                        listaMembros.remove(membro);
                        listaContatos.add(membro);
                        adapterContatos.notifyDataSetChanged();
                        adapterMembros.notifyDataSetChanged();
                        if(listaMembros.size()>0) {
                            toolbar.setSubtitle(listaMembros.size() + " de " + (listaMembros.size() + listaContatos.size()) + " selecionados ");
                        }else{
                            toolbar.setSubtitle("Adicionar Participantes");
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));


    }

    private void recuperarContatos(){
        DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child(ConfiguracaoFirebase.getUsuarioAtual().getUid()).child("contatos");
        usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    listaContatos.add( ds.getValue(Usuario.class) );
                    adapterContatos.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void proximaEtapa(View view){
        if(listaMembros.size()>0) {
            Intent i = new Intent(NovoGrupo1Activity.this, NovoGrupo2Activity.class);
            i.putExtra("lista", (Serializable) listaMembros);
            startActivity(i);
        }else {
            Toast.makeText(this, "Adicione membros ao grupo!", Toast.LENGTH_SHORT).show();
        }

    }

}
