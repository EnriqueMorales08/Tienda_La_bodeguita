package com.example.tienda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tienda.adapters.CarritoAdapter;
import com.example.tienda.models.CarritoModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class OrderActitvity extends AppCompatActivity {
    FirebaseFirestore firestore;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_actitvity);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        List<CarritoModel>list =(ArrayList<CarritoModel>)getIntent().getSerializableExtra("itemList");
        if (list!=null && list.size()>0){
            for (CarritoModel model : list){
                final HashMap<String, Object> carMap = new HashMap<>();

                carMap.put("productDescription",model.getProductDescription());
                carMap.put("productPrice",model.getProductPrice());
                carMap.put("totalQuantity",model.getTotalQuantity());
                carMap.put("totalPrice",model.getTotalPrice());

                firestore.collection("CurrenUser").document(auth.getCurrentUser().getUid())
                        .collection("AddtoCart").add(carMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(OrderActitvity.this, "Productos a cancelar", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }

        //Parallamar el metodo de wssp
        String phoneNumber = "921290377"; // Reemplaza con el número de WhatsApp al que deseas enviar el mensaje
        String message = "¡Hola! He realizado el pago de la compra de productos";
        ImageButton btnWhatsApp = findViewById(R.id.btn_whatsapp);

        btnWhatsApp.setOnClickListener(view -> {
            // Abre WhatsApp con el número y el mensaje predefinido
            openWhatsApp(phoneNumber, message);
        });

    }

    private void openWhatsApp(String phoneNumber, String message) {
        // Codifica el mensaje para que pueda ser pasado como parámetro en la URI
        String encodedMessage = Uri.encode(message);

        // Crea la URI para abrir WhatsApp con el número y mensaje
        Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + encodedMessage);

        // Crea el Intent y redirige al cliente a WhatsApp
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }



}