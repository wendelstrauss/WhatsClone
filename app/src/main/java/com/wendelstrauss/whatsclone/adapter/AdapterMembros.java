package com.wendelstrauss.whatsclone.adapter;

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

public class AdapterMembros extends RecyclerView.Adapter<AdapterMembros.MyViewHolder> {

    List<Usuario> listaMembros;

    public AdapterMembros(List<Usuario> listaMembros) {
        this.listaMembros = listaMembros;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_membros, parent, false);
        return new AdapterMembros.MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Usuario membro = listaMembros.get(position);
        holder.nome.setText(membro.getNome());

        if(!membro.getFoto().isEmpty()){
            Picasso.get().load(membro.getFoto()).into(holder.foto);
        }else{
            holder.foto.setImageResource(R.drawable.padrao_usuario);
        }
    }

    @Override
    public int getItemCount() {
        return listaMembros.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView foto;
        TextView nome;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.fotoAdapterMembro);
            nome = itemView.findViewById(R.id.nomeAdapterMembro);

        }
    }

}
