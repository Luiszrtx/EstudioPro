package com.example.estudiopro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityLogin extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginBtn = findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityLogin.this, HomeActivity.class); // Cambia a tu actividad principal real
            startActivity(intent);
            finish(); // para no volver al login
        });
    }
}
