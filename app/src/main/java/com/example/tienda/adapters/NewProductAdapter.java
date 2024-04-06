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
import com.example.tienda.models.ProductModel;

import java.util.List;

public class NewProductAdapter extends RecyclerView.Adapter<NewProductAdapter.ViewHolder> {

    private Context context;
    private List<ProductModel> list;

    public NewProductAdapter(Context context, List<ProductModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NewProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_products, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getImg_url()).into(holder.newImg);
        holder.newName.setText(list.get(position).getName());
        holder.newDescription.setText(list.get(position).getDescription());
        holder.newPrice.setText(String.valueOf(list.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView newImg;
        TextView newName, newDescription, newPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            newImg = itemView.findViewById(R.id.new_img);
            newName = itemView.findViewById(R.id.name_product);
            newDescription = itemView.findViewById(R.id.descr_product);
            newPrice = itemView.findViewById(R.id.new_price);
        }
    }
}
