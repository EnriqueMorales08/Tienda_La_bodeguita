package com.example.tienda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.tienda.models.VerMasModel;
import com.example.tienda.ui.carrito.CarritoFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DetailedActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    ImageView detailedImg;
    TextView price, description, quantity;
    int totalQuantity = 0;
    double totalPrice = 0;
    Button addToCart;
    ImageView removeItem,  addItem;

    VerMasModel verMasModel = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        final Object object = getIntent().getSerializableExtra("detail");
        if (object instanceof VerMasModel){
            verMasModel = (VerMasModel) object;
        }

        detailedImg = findViewById(R.id.detal_img);
        price = findViewById(R.id.detailed_price);
        description = findViewById(R.id.description);

        if (verMasModel !=null){
            Glide.with(getApplicationContext()).load(verMasModel.getImg_url()).into(detailedImg);
            description.setText(verMasModel.getDescription());
            price.setText(String.valueOf(verMasModel.getPrice()));
            totalPrice = verMasModel.getPrice() * totalQuantity;
        }

        addItem = findViewById(R.id.add_item);
        removeItem = findViewById(R.id.remove_item);
        quantity = findViewById(R.id.quantity);

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalQuantity<10){
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));
                    totalPrice = verMasModel.getPrice() * totalQuantity;
                }
            }
        });
        removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalQuantity>0){
                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));
                    totalPrice = verMasModel.getPrice() * totalQuantity;
                }
            }
        });

        addToCart = findViewById(R.id.add_to_cart);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });

    }
    private void addToCart() {

        final HashMap<String, Object> carMap = new HashMap<>();

        carMap.put("productDescription",verMasModel.getDescription());
        carMap.put("productPrice",price. getText().toString());
        carMap.put("totalQuantity",quantity.getText().toString());
        carMap.put("totalPrice",totalPrice);

        firestore.collection("CurrenUser").document(auth.getCurrentUser().getUid())
                 .collection("AddtoCart").add(carMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Toast.makeText(DetailedActivity.this, "Añadido al carrito", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

    }


}