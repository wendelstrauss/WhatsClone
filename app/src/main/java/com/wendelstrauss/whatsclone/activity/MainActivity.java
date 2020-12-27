package com.wendelstrauss.whatsclone.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.wendelstrauss.whatsclone.R;
import com.wendelstrauss.whatsclone.config.ConfiguracaoFirebase;
import com.wendelstrauss.whatsclone.fragment.ConversasFragment;
import com.wendelstrauss.whatsclone.fragment.StatusFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private MaterialSearchView searchView;
    private FloatingActionButton fab;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarSecundaria);
        setSupportActionBar(toolbar);

        //inicializar TabLayout
        inicializarTabLayout();
        //inicializar pesquisa
        inicializarComponentes();

        //configuracoes iniciais
        autenticacao = ConfiguracaoFirebase.getAutenticacao();
    }

    private void inicializarTabLayout() {

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.conversas, ConversasFragment.class)
                .add(R.string.status, StatusFragment.class)
                .create());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);

    }

    private void inicializarComponentes() {

        //componentes
        searchView = findViewById(R.id.btnPesquisar);
        fab = findViewById(R.id.fabPrincipal);

        //configurar searchview
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

        //clique no FAB
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTela(ContatosActivity.class);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //configurando pesquisa
        MenuItem item = menu.findItem( R.id.menu_pesquisar );
        searchView.setMenuItem( item );

        return super.onCreateOptionsMenu(menu);
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
