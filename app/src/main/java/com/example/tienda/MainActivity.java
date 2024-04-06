package com.example.tienda;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.tienda.databinding.ActivityMainBinding;
import com.example.tienda.models.UserModel;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        auth = FirebaseAuth.getInstance();

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_grocery, R.id.nav_library, R.id.nav_registrar, R.id.nav_perfil, R.id.nav_location)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.principal);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View headerview = navigationView.getHeaderView(0);
        TextView headerName = headerview.findViewById(R.id.nav_header_name);
        CircleImageView headerImg = headerview.findViewById(R.id.nav_header_img);

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // Usuario autenticado, actualizar los datos del encabezado con los datos del usuario
            headerName.setText(currentUser.getDisplayName());
            Glide.with(this).load(currentUser.getPhotoUrl()).into(headerImg);

            // Obtener la referencia de la base de datos
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.getReference().child("Users").child(currentUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            if (userModel != null) {
                                headerName.setText(userModel.getName());
                                Glide.with(MainActivity.this)
                                        .load(userModel.getProfileImg())
                                        .into(headerImg);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Manejar el error de la consulta a la base de datos si es necesario
                        }
                    });
        } else {
            // No hay usuario autenticado, mostrar "No autenticado" y una imagen predeterminada
            headerName.setText("");
            headerImg.setImageResource(R.drawable.ic_perfil); // Cambiar a una imagen predeterminada o de no autenticado
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_cerrar_Sesion) {
            // Lógica para cerrar la sesión del usuario
            auth.signOut();

            // Actualizar los datos del nav-bar para mostrar el estado de "No autenticado"
            View headerview = binding.navView.getHeaderView(0);
            TextView headerName = headerview.findViewById(R.id.nav_header_name);
            CircleImageView headerImg = headerview.findViewById(R.id.nav_header_img);
            headerName.setText("");
            headerImg.setImageResource(R.drawable.ic_perfil); // Cambiar a una imagen predeterminada o de no autenticado

            // Después de cerrar la sesión, regresa al fragmento de inicio de sesión
            NavController navController = Navigation.findNavController(this, R.id.principal);
            navController.navigate(R.id.nav_home);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.principal);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}