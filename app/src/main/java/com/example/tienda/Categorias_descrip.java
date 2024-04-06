package com.example.tienda;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tienda.adapters.CategoriesAdapter;
import com.example.tienda.models.CategoriesModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Categorias_descrip extends AppCompatActivity {

    FirebaseFirestore db;
    RecyclerView recyclerView;
    CategoriesAdapter categoriesAdapter;
    List<CategoriesModel> categoriesModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias_descrip);

        db = FirebaseFirestore.getInstance();
        String type = getIntent().getStringExtra("type");

        recyclerView = findViewById(R.id.cate_descrip_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoriesModelList = new ArrayList<>();
        categoriesAdapter = new CategoriesAdapter(this, categoriesModelList);
        recyclerView.setAdapter(categoriesAdapter);

        if (type != null && type.equalsIgnoreCase("abarrotes"))
            db.collection("Categorias").whereEqualTo("type","abarrotes")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                CategoriesModel categoriesModel = documentSnapshot.toObject(CategoriesModel.class);
                                categoriesModelList.add(categoriesModel);
                                categoriesAdapter.notifyDataSetChanged();
                            }
                    }
                });

        if (type != null && type.equalsIgnoreCase("libreria"))
            db.collection("Categorias").whereEqualTo("type","libreria")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                CategoriesModel categoriesModel = documentSnapshot.toObject(CategoriesModel.class);
                                categoriesModelList.add(categoriesModel);
                                categoriesAdapter.notifyDataSetChanged();
                            }
                        }
                    });
    }
}