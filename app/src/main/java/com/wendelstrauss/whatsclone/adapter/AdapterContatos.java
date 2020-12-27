package com.wendelstrauss.whatsclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.wendelstrauss.whatsclone.R;
import com.wendelstrauss.whatsclone.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterContatos extends RecyclerView.Adapter<AdapterContatos.MyViewHolder> {

    private List<Usuario> lista;
    private Context context;

    public AdapterContatos(List<Usuario> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contatos, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Usuario usuario = lista.get(position);

        if(!usuario.getFoto().isEmpty()) {
            Picasso.get().load(usuario.getFoto()).into(holder.foto);
        }else {
            holder.foto.setImageResource(R.drawable.padrao_usuario);
        }
        holder.txtNome.setText(usuario.getNome());
        holder.txtRecado.setText(usuario.getRecado());

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView foto;
        TextView txtNome, txtRecado;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imageContatosAdapter);
            txtNome = itemView.findViewById(R.id.nomeContatosAdapter);
            txtRecado = itemView.findViewById(R.id.recadoContatosAdapter);

        }
    }

}
