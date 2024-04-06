package com.example.tienda.ui.carrito;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tienda.DetailedActivity;
import com.example.tienda.OrderActitvity;
import com.example.tienda.R;
import com.example.tienda.adapters.CarritoAdapter;
import com.example.tienda.models.CarritoModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CarritoFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth auth;
    RecyclerView recyclerView;
    CarritoAdapter carritoAdapter;
    List<CarritoModel> carritoModelList;
    TextView costoTotal;
    Button byNow;
    ProgressBar progressBar;

    public CarritoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_carrito, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //Opciones para icono de espera mientras carga la imagen
        progressBar = root.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView = root.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Precio total
        costoTotal = root.findViewById(R.id.textView1);
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mMessageReciver, new IntentFilter("PrecioTotal"));

        carritoModelList = new ArrayList<>();
        recyclerView.setVisibility(View.GONE);
        carritoAdapter =  new CarritoAdapter(getActivity(), carritoModelList);
        recyclerView.setAdapter(carritoAdapter);

        byNow = root.findViewById(R.id.buy_now);

        db.collection("CurrenUser").document(auth.getCurrentUser().getUid())
                .collection("AddtoCart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){

                                String documentId = documentSnapshot.getId();
                                CarritoModel carritoModel = documentSnapshot.toObject(CarritoModel.class);
                                carritoModel.setDocumentId(documentId);

                                carritoModelList.add(carritoModel);
                                carritoAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                            updateTotalPrice();
                        }
                    }
                });

        //Boton para realizar pedido
        byNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OrderActitvity.class);
                intent.putExtra("itemList", (Serializable) carritoModelList);
                startActivity(intent);
            }
        });

        return root;
    }

    public BroadcastReceiver mMessageReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double pricetotal = intent.getDoubleExtra("preciototal", 0);
            costoTotal.setText("Precio Total: S/. " + pricetotal);
        }
    };
    private void updateTotalPrice() {
        double totalPrice = 0;
        for (CarritoModel carritoModel : carritoModelList) {
            totalPrice += carritoModel.getTotalPrice();
        }
        String formattedPrice = String.format(Locale.getDefault(), "%.2f", totalPrice);
        costoTotal.setText("Precio Total: S/. " + formattedPrice);
    }
}