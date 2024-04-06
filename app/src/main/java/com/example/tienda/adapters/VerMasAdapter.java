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
import com.example.tienda.DetailedActivity;
import com.example.tienda.R;
import com.example.tienda.models.VerMasModel;

import java.text.DecimalFormat;
import java.util.List;

public class VerMasAdapter extends RecyclerView.Adapter<VerMasAdapter.ViewHolder> {

    private Context context;
    private List<VerMasModel>list;

    public VerMasAdapter(Context context, List<VerMasModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_products, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getImg_url()).into(holder.newImg);
        holder.newName.setText(list.get(position).getName());
        holder.newDescription.setText(list.get(position).getDescription());

        double price = list.get(position).getPrice();
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        String formattedPrice = decimalFormat.format(price);
        holder.newPrice.setText(formattedPrice);

          holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailedActivity.class);
                intent.putExtra("detail", list.get(position));
                context.startActivity(intent);
            }
          });
    }

    @Override
    public int getItemCount() {
        return list.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView newImg;
        TextView newName, newDescription, newPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            newImg = itemView.findViewById(R.id.cat_nav_img);
            newName = itemView.findViewById(R.id.cat_nav_name);
            newDescription = itemView.findViewById(R.id.cat_nav_description);
            newPrice = itemView.findViewById(R.id.price);


        }

    }
}
