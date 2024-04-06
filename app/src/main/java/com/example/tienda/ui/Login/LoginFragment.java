package com.example.tienda.ui.Login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.tienda.MainActivity;
import com.example.tienda.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginFragment extends Fragment {

    private FirebaseAuth auth;
    private EditText loginEmail ,loginPassword;
    private Button loginButton;
    private TextView signupRedirectText;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        auth=FirebaseAuth.getInstance();
        loginEmail=view.findViewById(R.id.emailRegistrar);
        loginPassword=view.findViewById(R.id.contraseñaIngresar);
        loginButton=view.findViewById(R.id.buttonIngresar);
        signupRedirectText=view.findViewById(R.id.textirRegistrar);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString().trim();
                String pass = loginPassword.getText().toString().trim();
                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!pass.isEmpty()) {
                        auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                auth.signInWithEmailAndPassword(email,pass)
                                        .addOnCompleteListener(task -> {
                                           if(task.isSuccessful()){
                                               FirebaseUser user = auth.getCurrentUser();
                                               if (user != null) {
                                                   Toast.makeText(getActivity(), "Inicio de sesion exitoso ", Toast.LENGTH_SHORT).show();
                                                   startActivity(new Intent(getActivity(), MainActivity.class));
                                                   getActivity().finish();

                                               }
                                           }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Inicio de sesion fallido", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        loginPassword.setError("La contraseña no puede estar vacia");
                    }
                } else if (email.isEmpty()) {
                    loginEmail.setError("El correo no puede estar vacio");

                } else {
                    loginEmail.setError("Por favor introduzca un correo valido ");
                }
            }
        });
        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.principal,new RegistrarFragment()); // Reemplaza el Fragment actual con el nuevo Fragment
                fragmentTransaction.addToBackStack(null); // Agrega la transacción al back stack
                fragmentTransaction.commit();
            }
        });
        return view;

    }
}
