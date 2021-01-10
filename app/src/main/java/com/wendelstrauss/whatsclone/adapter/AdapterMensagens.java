package com.wendelstrauss.whatsclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wendelstrauss.whatsclone.R;
import com.wendelstrauss.whatsclone.config.ConfiguracaoFirebase;
import com.wendelstrauss.whatsclone.model.Mensagem;

import java.util.List;

public class AdapterMensagens extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Mensagem> listaMensagens;
    private Context c;
    private boolean isGrupo;

    //tipos de views
    private static int TIPO_VERDE = 0;
    private static int TIPO_BRANCO = 1;

    //tipos de conteudos
    private static final int MSG_TEXTO = 0;
    private static final int MSG_IMG = 1;
    private static final int MSG_TEXTO_IMG = 2;

    public AdapterMensagens(List<Mensagem> listaMensagens, Context c, boolean isGrupo) {
        this.listaMensagens = listaMensagens;
        this.c = c;
        this.isGrupo = isGrupo;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if( viewType == TIPO_VERDE){
            View view = LayoutInflater.from( c ).inflate(R.layout.adapter_txt_verde,parent, false);
            return new VerdeViewHolder( view );
        } else {
            View view = LayoutInflater.from( c ).inflate(R.layout.adapter_txt_branco,parent, false);
            return new BrancoViewHolder( view );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //pegando mensagens
        Mensagem mensagem = listaMensagens.get(position);
        Mensagem mensagemAnterior;
        if(position>0) {
            mensagemAnterior = listaMensagens.get(position - 1);
        }else{
            mensagemAnterior = listaMensagens.get(position);
        }

        if(getItemViewType(position) == TIPO_VERDE){//adapter verde
            ((VerdeViewHolder) holder).textoVerde.setText(mensagem.getTexto());
            ((VerdeViewHolder) holder).horaVerde.setText(mensagem.getHora());

            //botando a ponta no inicio do balão
            if( !mensagemAnterior.getIdAutor().equals( mensagem.getIdAutor() ) || position<1 ){
                ((VerdeViewHolder) holder).pontaVerde.setVisibility(View.VISIBLE);
            }else{
                ((VerdeViewHolder) holder).pontaVerde.setVisibility(View.GONE);
            }

        }else{//adapter branco
            if(isGrupo) {
                ((BrancoViewHolder) holder).nomeBranco.setText(mensagem.getIdAutor());
                ((BrancoViewHolder) holder).telefoneBranco.setVisibility(View.GONE);
                ((BrancoViewHolder) holder).textoBranco.setText(mensagem.getTexto());
                ((BrancoViewHolder) holder).horaBranco.setText(mensagem.getHora());

                //botando a ponta no inicio do balão
                if (!mensagemAnterior.getIdAutor().equals(mensagem.getIdAutor()) || position < 1) {
                    ((BrancoViewHolder) holder).pontaBranco.setVisibility(View.VISIBLE);
                } else {
                    ((BrancoViewHolder) holder).pontaBranco.setVisibility(View.GONE);
                }
            }else {
                ((BrancoViewHolder) holder).nomeBranco.setVisibility(View.GONE);
                ((BrancoViewHolder) holder).telefoneBranco.setVisibility(View.GONE);
                ((BrancoViewHolder) holder).textoBranco.setText(mensagem.getTexto());
                ((BrancoViewHolder) holder).horaBranco.setText(mensagem.getHora());

                //botando a ponta no inicio do balão
                if (!mensagemAnterior.getIdAutor().equals(mensagem.getIdAutor()) || position < 1) {
                    ((BrancoViewHolder) holder).pontaBranco.setVisibility(View.VISIBLE);
                } else {
                    ((BrancoViewHolder) holder).pontaBranco.setVisibility(View.GONE);
                }
            }

        }
    }

    @Override
    public int getItemCount() {
        return listaMensagens.size();
    }

    @Override
    public int getItemViewType(int position) {

        if(  listaMensagens.get(position).getIdAutor().equals( ConfiguracaoFirebase.getUsuarioAtual().getUid() ) ){
            return TIPO_VERDE;
        }else{
            return TIPO_BRANCO;
        }

    }

    public class BrancoViewHolder extends RecyclerView.ViewHolder {

        private TextView textoBranco, horaBranco, telefoneBranco, nomeBranco;
        private ImageView pontaBranco;

        public BrancoViewHolder(@NonNull View itemView) {
            super(itemView);

            textoBranco = itemView.findViewById(R.id.textoDestinatarioAdapter);
            horaBranco = itemView.findViewById(R.id.horaDestinatarioAdapter);
            telefoneBranco = itemView.findViewById(R.id.telefoneDestinatarioAdapter);
            nomeBranco = itemView.findViewById(R.id.nomeDestinatarioAdapter);
            pontaBranco = itemView.findViewById(R.id.pontaDestinatarioAdapter);

        }
    }

    public class VerdeViewHolder extends RecyclerView.ViewHolder {

        private TextView textoVerde, horaVerde;
        private ImageView pontaVerde;

        public VerdeViewHolder(@NonNull View itemView) {
            super(itemView);

            textoVerde = itemView.findViewById(R.id.textRemetenteAdapter);
            horaVerde = itemView.findViewById(R.id.horaRemetenteAdapter);
            pontaVerde = itemView.findViewById(R.id.pontaRemetenteAdapter);
        }
    }

}
