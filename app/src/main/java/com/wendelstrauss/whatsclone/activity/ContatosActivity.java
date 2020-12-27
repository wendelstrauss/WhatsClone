package com.wendelstrauss.whatsclone.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.wendelstrauss.whatsclone.R;
import com.wendelstrauss.whatsclone.adapter.AdapterContatos;
import com.wendelstrauss.whatsclone.config.ConfiguracaoFirebase;
import com.wendelstrauss.whatsclone.config.RecyclerItemClickListener;
import com.wendelstrauss.whatsclone.model.ContatoAgenda;
import com.wendelstrauss.whatsclone.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class ContatosActivity extends AppCompatActivity  {

    private MaterialSearchView searchView;

    private List<Usuario> listaContatos = new ArrayList<>();
    private AdapterContatos adapterContatos;
    private RecyclerView recyclerContatos;
    private DatabaseReference usuariosRef;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contatos);

        //permissao
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PackageManager.PERMISSION_GRANTED);

        //inicializar pesquisa
        inicializarComponentes();

        //recuperar contatos do telefone
        recuperarContatosTel();

        //Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarSecundaria);
        toolbar.setTitle("Contatos");
        toolbar.setSubtitle(listaContatos.size() + " contatos");
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator( R.drawable.ic_back ); //muda o icone de voltar



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void recuperarContatosTel() {

        Cursor cursorContatos = null;
        String idContato = "";
        String nomeExibicao = "";

        //recuperando da agenda do usuario
        try {
            cursorContatos = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null);
        }catch (Exception e){
            e.printStackTrace();
        }

        //add o nome e o id do contato na lista
        if( cursorContatos.getCount()>0 ){
            while (cursorContatos.moveToNext()){
                idContato = cursorContatos.getString( cursorContatos.getColumnIndex(ContactsContract.Contacts._ID));
                nomeExibicao = cursorContatos.getString(cursorContatos.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                //pegando telefone dos contatos
                int temTelefone = Integer.parseInt(cursorContatos.getString(cursorContatos.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if( temTelefone>0 ){
                    //cursor de telefone
                    Cursor cursorTelefone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?", new String[]{ idContato }, null);

                    while (cursorTelefone.moveToNext()){
                        String telefoneString = cursorTelefone.getString(cursorTelefone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        //procurando no firebase
                        pesquisarFirebase( telefoneString, nomeExibicao );
                    }
                    //finalizando o cursor de telefone. parou de pegar telefone
                    cursorTelefone.close();

                }
            }
        }

    }

    private void pesquisarFirebase(String telefoneString, String nomeExibicao){
        //formatando sem +55 e sem ddd
        String telPesquisa;
        if (telefoneString.length() > 9){
            telPesquisa = telefoneString.substring(telefoneString.length() - 9);
        }else{
            telPesquisa = telefoneString;
        }
        //pesquisando
        Query usuariosPesquisa = usuariosRef.orderByChild("telefone");
        usuariosPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Usuario usuarioResultado = ds.getValue(Usuario.class);
                    if(usuarioResultado.getTelefone().contains(telPesquisa)){
                        usuarioResultado.setNome( nomeExibicao );
                        listaContatos.add( usuarioResultado );
                        adapterContatos.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarComponentes() {

        //configuracoes iniciais
        usuariosRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios");

        //componentes
        searchView = findViewById(R.id.btnPesquisar);

        //recycler
        adapterContatos = new AdapterContatos(listaContatos, this);
        recyclerContatos = findViewById(R.id.recyclerContatos);
        recyclerContatos.setLayoutManager(new LinearLayoutManager(this));
        recyclerContatos.setHasFixedSize(true);
        recyclerContatos.setAdapter(adapterContatos);
        //recycler clique
        recyclerContatos.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerContatos,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Usuario usuario = listaContatos.get(position);
                        abrirConversa( usuario );
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        //searchView
        searchView.setHint("Pesquisar");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



    }

    private void abrirConversa(Usuario destinatario){

        Intent conversa = new Intent(ContatosActivity.this, ConversaActivity.class);
        conversa.putExtra("destinatario", destinatario);
        startActivity( conversa );
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pesquisa, menu);
        //configurando botao de pesquisa
        MenuItem menuItem = menu.findItem(R.id.menu_pesquisar);
        searchView.setMenuItem(menuItem);

        return super.onCreateOptionsMenu(menu);
    }

}

/*

Bethoveen - 21966666666
Eliane Ribeiro - +55 21 988888888
Gabriel - +55 21 977777777
William 21955555555

*/