package com.example.tienda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class pageWelcome1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_welcome1);
    }
    public void irHome(View view){
        Intent intent =
                new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}