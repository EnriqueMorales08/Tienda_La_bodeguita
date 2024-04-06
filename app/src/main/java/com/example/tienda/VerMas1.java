package com.example.tienda;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tienda.adapters.VerMasAdapter;
import com.example.tienda.models.VerMasModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class VerMas1 extends AppCompatActivity {

    FirebaseFirestore db;
    RecyclerView recyclerView;
    VerMasAdapter verMasAdapter;
    List<VerMasModel> verMasModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_mas1);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.ver_mas_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        verMasModelList = new ArrayList<>();
        verMasAdapter = new VerMasAdapter(this, verMasModelList);
        recyclerView.setAdapter(verMasAdapter);

        db.collection("VerMas_Products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                VerMasModel verMasModel = documentSnapshot.toObject(VerMasModel.class);
                                verMasModelList.add(verMasModel);
                                verMasAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

    }
}

