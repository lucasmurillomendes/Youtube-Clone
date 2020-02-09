package com.lucas.youtubeclone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lucas.youtubeclone.R;
import com.lucas.youtubeclone.model.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterVideo extends RecyclerView.Adapter<AdapterVideo.MyViewHolder> {

    private List<Item> listaVideos;
    private final OnItemClickListener listener;

    public AdapterVideo(OnItemClickListener listener) {
        this.listaVideos = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_video, parent, false);
        return new AdapterVideo.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Item video = listaVideos.get(position);
        holder.bind(video, listener);
    }

    public void updateData(List<Item> videos) {
        listaVideos.clear();
        listaVideos.addAll(videos);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listaVideos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView titulo;
        private TextView descricao;
        private TextView data;
        private ImageView capa;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.textTitulo);
            capa = itemView.findViewById(R.id.imageCapa);
        }

        void bind(final Item item, final OnItemClickListener listener) {
            titulo.setText(item.snippet.title);

            String url = item.snippet.thumbnails.high.url;

            //Carregando imagem com a biblioteca
            Picasso.get().load(url).into(capa);

            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }
}
