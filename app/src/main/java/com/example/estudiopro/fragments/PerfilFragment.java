package com.example.estudiopro.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.estudiopro.R;

public class PerfilFragment extends Fragment {

    private EditText editTextNombre;
    private Button btnGuardar;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MisPreferencias";
    private static final String KEY_NOMBRE = "nombre_usuario";

    public PerfilFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_usuario, container, false);

        editTextNombre = view.findViewById(R.id.editTextNombre);
        btnGuardar = view.findViewById(R.id.btnGuardar);

        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Opcional: cargar el nombre guardado si ya existe
        String nombreGuardado = sharedPreferences.getString(KEY_NOMBRE, "");
        editTextNombre.setText(nombreGuardado);

        btnGuardar.setOnClickListener(v -> {
            String nombre = editTextNombre.getText().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_NOMBRE, nombre);
            editor.apply();
        });

        return view;
    }
}
