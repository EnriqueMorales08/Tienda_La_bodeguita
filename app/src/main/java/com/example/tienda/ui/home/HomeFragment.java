package com.example.tienda.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.transition.Transition;
import com.example.tienda.R;
import com.example.tienda.VerMas1;
import com.example.tienda.adapters.NewProductAdapter;
import com.example.tienda.adapters.PopularAdapter;
import com.example.tienda.adapters.VerMasAdapter;
import com.example.tienda.models.PopularModel;
import com.example.tienda.models.ProductModel;
import com.example.tienda.models.VerMasModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    RecyclerView popularRec, productRec;
    FirebaseFirestore db;
    List<PopularModel> popularModelList;
    PopularAdapter popularAdapter;

    List<ProductModel> productModelList;
    NewProductAdapter newProductAdapter;

    TextView CatShowAll;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home,container, false);

       db=FirebaseFirestore.getInstance();

        popularRec = root.findViewById(R.id.pop_rec);
        popularModelList = new ArrayList<>();

      /*  popularModelList.add(new PopularModel(R.drawable.abarrotes, "Abarrotes"));
        popularModelList.add(new PopularModel(R.drawable.utiles, "Abarrotes"));*/


        popularAdapter = new PopularAdapter(getActivity(), popularModelList);
        popularRec.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        popularRec.setAdapter(popularAdapter);

        db.collection("Categorias")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PopularModel popularModel = document.toObject(PopularModel.class);
                                popularModelList.add(popularModel);
                                popularAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error"+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        productRec = root.findViewById(R.id.products_rec);

        productRec.setLayoutManager(new GridLayoutManager(getActivity(),2));
        productModelList = new ArrayList<>();
        newProductAdapter = new NewProductAdapter(getActivity(),productModelList);
        productRec.setAdapter(newProductAdapter );

        db.collection("TodoProductos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ProductModel productModel = document.toObject(ProductModel.class);
                                productModelList.add(productModel);
                                newProductAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        CatShowAll = root.findViewById(R.id.ver_mas_productos);
        CatShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), VerMas1.class);
                startActivity(intent);
            }
        });

        return root;
        }

}