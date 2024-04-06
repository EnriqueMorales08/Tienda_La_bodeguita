package com.example.tienda.ui.grocery;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tienda.DetailedActivity;
import com.example.tienda.R;
import com.example.tienda.VerMas1;
import com.example.tienda.adapters.VerMasAdapter;
import com.example.tienda.models.VerMasModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GroceryFragment extends Fragment {
    FirebaseFirestore db;
    VerMasAdapter verMasAdapter;
    List<VerMasModel> verMasModelList;

    EditText search_box;
    RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_grocery,container, false);

        db=FirebaseFirestore.getInstance();

        String type = ("type");

        recyclerView = root.findViewById(R.id.ver_mas_rec);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),1));
        verMasModelList = new ArrayList<>();
        verMasAdapter = new VerMasAdapter(getActivity(), verMasModelList);
        recyclerView.setAdapter(verMasAdapter);


        db.collection("VerMas_Products").whereEqualTo("type","abarrotes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                VerMasModel verMasModel = documentSnapshot.toObject(VerMasModel.class);
                                verMasModelList.add(verMasModel);
                                verMasAdapter.notifyDataSetChanged();
                            }
                        }
                    });

        search_box = root.findViewById(R.id.search_box);
        recyclerView.setHasFixedSize(true);
        search_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty());

            }
        });
        return root;
    }

}