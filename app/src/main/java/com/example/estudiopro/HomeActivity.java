package com.example.estudiopro;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.example.estudiopro.fragments.CalendarioFragment;
import com.example.estudiopro.fragments.InicioFragment;
import com.example.estudiopro.fragments.MateriasFragment;
import com.example.estudiopro.fragments.PerfilFragment;
import com.example.estudiopro.fragments.TodasTareasFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Mostrar fragmento inicial
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new InicioFragment()).commit();

        Intent intent = getIntent();
        if (intent != null && "materias".equals(intent.getStringExtra("ir_a_fragment"))) {
            // Cambia al fragmento MateriasFragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new MateriasFragment())
                    .commit();

            // Opcional: si estás usando BottomNavigationView, actualiza el item seleccionado
            BottomNavigationView nav = findViewById(R.id.bottom_navigation);
            nav.setSelectedItemId(R.id.nav_materias); // id de tu item
        }


        //Revisar que item esta selecionado
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                selectedFragment = new InicioFragment();
            } else if (id == R.id.nav_materias) {
                selectedFragment = new MateriasFragment();
            } else if (id == R.id.nav_calendario) {
                selectedFragment = new CalendarioFragment();
            } else if (id == R.id.nav_perfil) {
                selectedFragment = new PerfilFragment();
            } else if (id == R.id.nav_tareas) {
                selectedFragment = new TodasTareasFragment();
            }

            if (selectedFragment != null) {
                MostrarFragment(selectedFragment);
                return true;
            }

            return false;
        });
    }

    //Mostrar la pantalla del item selecionado
    private void MostrarFragment(Fragment selectedFragment) {
        Fragment fragment = selectedFragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();
    }
}
