package com.gunay.photonoteapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gunay.photonoteapp.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder> {

    ArrayList<Photo> photoArrayList;

    public PhotoAdapter(ArrayList<Photo> photoArrayList) {
        this.photoArrayList = photoArrayList;
    }

    @NonNull
    @Override
    public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new  PhotoHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
        holder.binding.recyclerViewTextView.setText(photoArrayList.get(position).name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(holder.itemView.getContext(), AddActivity.class);
                intent.putExtra("info","go");
                intent.putExtra("photoId",photoArrayList.get(holder.getAdapterPosition()).id);
                holder.itemView.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return photoArrayList.size();
    }

    public class PhotoHolder extends RecyclerView.ViewHolder{

        private RecyclerRowBinding binding;

        public PhotoHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
