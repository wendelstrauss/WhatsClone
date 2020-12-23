package com.wendelstrauss.whatsclone.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.wendelstrauss.whatsclone.R;
import com.wendelstrauss.whatsclone.config.ConfiguracaoFirebase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //configuracoes iniciais
        autenticacao = ConfiguracaoFirebase.getAutenticacao();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()){
            case R.id.menu_configuracoes:
                abrirTela(ConfiguracoesActivity.class);
                break;
            case R.id.menu_desconectar:
                desconectar();
                break;
        }

        return true;
    }

    private void abrirTela(Class activity){
        startActivity(new Intent(MainActivity.this, activity));
    }

    private void desconectar(){

        if(autenticacao!=null){
            autenticacao.signOut();
            startActivity(new Intent(MainActivity.this, CadastroTelefoneActivity.class));
            finish();
        }

    }

}
