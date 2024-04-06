package com.example.tienda.ui.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tienda.MainActivity;
import com.example.tienda.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegistrarFragment extends Fragment {

    private FirebaseAuth auth;
    private EditText signupEmail ,signupPassword;
    private Button signudButton;
    private TextView loginRedirectText;

    public RegistrarFragment() {
        // Required empty public constructor
    }


    public static RegistrarFragment newInstance(String param1, String param2) {
        RegistrarFragment fragment = new RegistrarFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registrar, container, false);
        auth=FirebaseAuth.getInstance();
        signupEmail=view.findViewById(R.id.emailRegistrar);
        signupPassword=view.findViewById(R.id.contraseñaRegistrar);
        signudButton=view.findViewById(R.id.buttonRegistrar);
        loginRedirectText=view.findViewById(R.id.textirLoguear);

        signudButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user=signupEmail.getText().toString().trim();
                String pass=signupPassword.getText().toString().trim();
                if (user.isEmpty()){
                    signupEmail.setError("Este campo no puede estar vacio");
                }
                if (pass.isEmpty()){
                    signupPassword.setError("Este campo no puede estar vacio");
                }else {
                    auth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getActivity(),"Registro Exitoso",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity(), MainActivity.class));

                            }else {
                                Toast.makeText(getActivity(),"Error de registro"+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }));

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginFragment loginFragment = new LoginFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.principal,new LoginFragment()); // Reemplaza el Fragment actual con el nuevo Fragment
                fragmentTransaction.addToBackStack(null); // Agrega la transacción al back stack
                fragmentTransaction.commit();

            }
        });
        return view;
    }
}
