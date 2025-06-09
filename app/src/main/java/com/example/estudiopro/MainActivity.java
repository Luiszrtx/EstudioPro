package com.example.estudiopro;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Asegúrate que este archivo tenga el ID @+id/main si usas insets

        // Aquí puedes agregar lógica adicional más adelante si lo necesitas,
        // como listeners para botones, inicialización de datos, navegación, etc.
    }
}
