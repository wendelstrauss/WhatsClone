package com.wendelstrauss.whatsclone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.wendelstrauss.whatsclone.R;
import com.wendelstrauss.whatsclone.config.ConfiguracaoFirebase;
import com.wendelstrauss.whatsclone.model.Conversa;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterConversas extends RecyclerView.Adapter<AdapterConversas.MyViewHolder> {

    private List<Conversa> listaConversas;

    public AdapterConversas(List<Conversa> listaConversas) {
        this.listaConversas = listaConversas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_conversas, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Conversa conversa = listaConversas.get(position);
            //foto
            if (!conversa.getFotoDestinatario().isEmpty()) {
                Picasso.get().load(conversa.getFotoDestinatario()).into(holder.foto);
            } else {
                holder.foto.setImageResource(R.drawable.padrao_usuario);
            }
            //ultima mensagem
            holder.txtUltima.setText(conversa.getUltimaMensagem().getTexto());
            holder.txtHora.setText(conversa.getUltimaMensagem().getHora());

            //nome
            holder.txtNome.setText(conversa.getNomeDestinatario());

    }

    @Override
    public int getItemCount() {
        return listaConversas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView foto;
        private ImageView imgStatus;
        private TextView txtNome, txtUltima, txtHora;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.fotoAdapterConversa);
            imgStatus = itemView.findViewById(R.id.statusAdapterConversa);
            txtNome = itemView.findViewById(R.id.nomeAdapterConversa);
            txtUltima = itemView.findViewById(R.id.ultimaAdapterConversa);
            txtHora = itemView.findViewById(R.id.horaAdapterConversa);

        }
    }

}
