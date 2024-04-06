package com.example.tienda.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tienda.Categorias_descrip;
import com.example.tienda.R;
import com.example.tienda.models.PopularModel;

import java.util.List;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {

    private Context context;
    private List<PopularModel> popularModelList;

    public PopularAdapter(Context context, List<PopularModel> popularModelList) {
        this.context = context;
        this.popularModelList = popularModelList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(popularModelList.get(position).getImg_url()).into(holder.popImg);
        /*holder.popImg.setImageResource(popularModelList.get(position).getImg_url());*/
        holder.name.setText(popularModelList.get(position).getName());
        holder.descripcion.setText(popularModelList.get(position).getDescripcion());
        holder.rating.setText(popularModelList.get(position).getRating());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Categorias_descrip.class );
                intent.putExtra("type",popularModelList.get(position).getType());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return popularModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
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
