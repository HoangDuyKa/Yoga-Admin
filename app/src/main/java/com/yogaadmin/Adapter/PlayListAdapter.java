package com.yogaadmin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yogaadmin.Model.PlayListModel;
import com.yogaadmin.R;
import com.yogaadmin.databinding.RvPlaylistDesignBinding;

import java.util.ArrayList;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {
    Context context;
    ArrayList<PlayListModel> list;

    videoListener listener;

    public PlayListAdapter(Context context, ArrayList<PlayListModel> list, videoListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_playlist_design,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayListModel model = list.get(position);

        // Set course details
        holder.binding.title.setText(model.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(position,list.get(position).getKey(),model.getVideoUri(),list.size());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RvPlaylistDesignBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RvPlaylistDesignBinding.bind(itemView);
        }


    }
    public interface videoListener{
        public void onClick(int position,String key,String videoUrl,int size);
    }
}
