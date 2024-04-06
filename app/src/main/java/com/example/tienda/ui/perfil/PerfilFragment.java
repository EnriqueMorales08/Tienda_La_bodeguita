package com.example.tienda.ui.perfil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.tienda.R;
import com.example.tienda.models.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {
    CircleImageView profileImg;
    EditText name, email, number, address;
    Button update;

    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;

    private Uri profileUri; // Variable para almacenar la URI de la imagen seleccionada

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        profileImg = view.findViewById(R.id.profile_img);
        name = view.findViewById(R.id.profile_name);
        email = view.findViewById(R.id.profile_email);
        number = view.findViewById(R.id.profile_number);
        address = view.findViewById(R.id.profile_adress);
        update = view.findViewById(R.id.update);

        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        if (userModel != null) {
                            name.setText(userModel.getName());
                            email.setText(userModel.getEmail());
                            number.setText(userModel.getNumber());
                            address.setText(userModel.getAddress());
                            // Carga la imagen utilizando Glide
                            Glide.with(getContext()).load(userModel.getProfileImg()).into(profileImg);
                        }
                        // Glide.with(getContext()).load(userModel.getProfileImg()).into(profileImg);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 33);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserProfile();
            }
        });

        return view;
    }

    private void updateUserProfile() {
        if (profileUri != null) {
            final StorageReference reference = storage.getReference().child("profile_picture")
                    .child(FirebaseAuth.getInstance().getUid());
            reference.putFile(profileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Imagen subida correctamente", Toast.LENGTH_SHORT).show();
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.w("ruta", uri.toString());
                            UserModel datos = new UserModel(name.getText().toString(), email.getText().toString(),
                                    number.getText().toString(), address.getText().toString(), uri.toString());
                            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).setValue(datos);
                            Toast.makeText(getContext(), "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else {
            // Si no se seleccionó una imagen nueva, solo actualiza los datos sin subir una imagen
            UserModel datos = new UserModel(name.getText().toString(), number.getText().toString(),
                    email.getText().toString(), address.getText().toString(), "");
            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).setValue(datos);
            Toast.makeText(getContext(), "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getData() != null) {
            profileUri = data.getData();
            profileImg.setImageURI(profileUri);
        }
    }
}
