package com.example.tienda.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tienda.R;
import com.example.tienda.models.CarritoModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.BreakIterator;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.ViewHolder> {

    Context context;
    List<CarritoModel> carritoModelList;
    double totalPrice = 0;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    TextView costoTotal;

    public CarritoAdapter(Context context, List<CarritoModel> carritoModelList) {
        this.context = context;
        this.carritoModelList = carritoModelList;
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.description.setText(carritoModelList.get(position).getProductDescription());
        holder.price.setText(carritoModelList.get(position).getProductPrice());
        holder.quantity.setText(carritoModelList.get(position).getTotalQuantity());

        double totalPrice1 = carritoModelList.get(position).getTotalPrice();
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        String formattedPrice = decimalFormat.format(totalPrice1);
        holder.totalPrice.setText(formattedPrice);

         //Total del carrito
        totalPrice = totalPrice + carritoModelList.get(position).getTotalPrice();
        Intent intent = new Intent("PrecioTotal");
        intent.putExtra("preciototal",totalPrice);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        // Agregar OnClickListener para el botón Eliminar
        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(position);
              }
            });
        }

        public void removeItem(int position) {
        firestore.collection("CurrenUser")
                .document(auth.getCurrentUser().getUid())
                .collection("AddtoCart")
                .document(carritoModelList.get(position).getDocumentId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            double removedProduct = carritoModelList.get(position).getTotalPrice();
                            carritoModelList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show();

                            // Ajustar el precio total después de la eliminación
                            totalPrice = removedProduct;
                            Intent intent = new Intent("PrecioTotal");
                            intent.putExtra("preciototal", totalPrice);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        } else {
                            Toast.makeText(context, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
       });
    }

    @Override
    public int getItemCount() {
        return carritoModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView description, price, quantity,totalPrice;
        ImageView   deleteItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            description = itemView.findViewById(R.id.product_description);
            price = itemView.findViewById(R.id.product_price);
            quantity = itemView.findViewById(R.id.total_quantity);
            totalPrice = itemView.findViewById(R.id.total_price);
            deleteItem = itemView.findViewById(R.id.delete);

        }
    }
}
