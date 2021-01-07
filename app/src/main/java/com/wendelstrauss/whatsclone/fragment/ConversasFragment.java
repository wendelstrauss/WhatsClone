package com.wendelstrauss.whatsclone.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.wendelstrauss.whatsclone.R;
import com.wendelstrauss.whatsclone.activity.ConversaActivity;
import com.wendelstrauss.whatsclone.adapter.AdapterConversas;
import com.wendelstrauss.whatsclone.config.ConfiguracaoFirebase;
import com.wendelstrauss.whatsclone.config.RecyclerItemClickListener;
import com.wendelstrauss.whatsclone.model.Conversa;

import java.util.ArrayList;
import java.util.List;

public class ConversasFragment extends Fragment {

    private RecyclerView recyclerConversas;
    private List<Conversa> listaConversas = new ArrayList<>();
    private AdapterConversas adapterConversas;

    public ConversasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        //inicializar componentes
        inicializarComponentes(view);
        //recuperar conversas
        recuperarConversas();

        //clique no recycler
        recyclerConversas.addOnItemTouchListener(new RecyclerItemClickListener(
                getContext(),
                recyclerConversas,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        abrirConversa( listaConversas.get(position) );

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        return view;
    }

    private void abrirConversa( Conversa conversa ){

        Intent janela = new Intent(getActivity(), ConversaActivity.class);
        janela.putExtra("idDestinatario", conversa.getIdDestinatario());
        startActivity( janela );

    }

    private void inicializarComponentes(View view) {

        recyclerConversas = view.findViewById(R.id.recyclerConversasFragment);

        //configurar recycler
        adapterConversas = new AdapterConversas(listaConversas);
        recyclerConversas.setLayoutManager(new LinearLayoutManager( getContext() ));
        recyclerConversas.setHasFixedSize(true);
        recyclerConversas.setAdapter(adapterConversas);

    }

    private void recuperarConversas(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseRef().child("usuarios").child(ConfiguracaoFirebase.getUsuarioAtual().getUid()).child("conversas");
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaConversas.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    Conversa conversa = ds.getValue(Conversa.class);
                        listaConversas.add( conversa );
                        adapterConversas.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
