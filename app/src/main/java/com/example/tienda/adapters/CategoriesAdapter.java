package com.example.tienda.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tienda.R;
import com.example.tienda.models.CategoriesModel;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    Context context;
    List<CategoriesModel>list;

    public CategoriesAdapter(Context context, List<CategoriesModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.descrip_categories,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.ViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getImg_url()).into(holder.popImg);
        holder.name.setText(list.get(position).getName());
        holder.descripcion.setText(list.get(position).getDescripcion());
        holder.rating.setText(list.get(position).getRating());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView popImg;
        TextView name,descripcion, rating;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            popImg = itemView.findViewById(R.id.pop_img);
            name = itemView.findViewById(R.id.pop_name);
            descripcion = itemView.findViewById(R.id.pop_Des);
            rating = itemView.findViewById(R.id.pop_rating);
            }
        }
}

