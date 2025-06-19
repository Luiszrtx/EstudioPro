package com.example.estudiopro.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.estudiopro.DetallesTarActivity;
import com.example.estudiopro.Materia;
import com.example.estudiopro.R;
import com.example.estudiopro.Tareas;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CalendarioFragment extends Fragment {

    private LinearLayout contenedorPendientes, contenedorCompletadas;
    private String nombreMateria;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_todaslastareas, container, false);
        if (getArguments() != null) {
            nombreMateria = getArguments().getString("nombreMateria");
        }

        contenedorPendientes = view.findViewById(R.id.contenedor_pendientes);
        contenedorCompletadas = view.findViewById(R.id.contenedor_completadas);

        cargarTodasLasTareas();

        return view;
    }

    private void cargarTodasLasTareas() {
        SharedPreferences prefs = requireContext().getSharedPreferences("TAREAS_PREF", requireContext().MODE_PRIVATE);
        Gson gson = new Gson();

        Map<String, ?> todasLasClaves = prefs.getAll();

        for (Map.Entry<String, ?> entry : todasLasClaves.entrySet()) {
            String clave = entry.getKey();  // Ej: "TAREAS_Matemáticas"
            String json = (String) entry.getValue();

            // Extraemos el nombre de la materia
            String nombreMateria = clave.replace("TAREAS_", "");

            Type type = new TypeToken<ArrayList<Tareas>>() {}.getType();
            ArrayList<Tareas> listaTareas = gson.fromJson(json, type);

            for (Tareas tarea : listaTareas) {
                agregarCard(tarea, nombreMateria);
            }
        }
    }

    private void agregarCard(Tareas tarea, String nombreMateria) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View cardView = inflater.inflate(R.layout.ventana_tareas, null);

        TextView tituloCard = cardView.findViewById(R.id.titulo_card);
        TextView fechaCard = cardView.findViewById(R.id.fecha_card);
        ImageView icono = cardView.findViewById(R.id.circuloColor);

        tituloCard.setText(tarea.getTitulo());
        fechaCard.setText(tarea.getFecha());

        if (tarea.isCompleta()) {
            icono.setImageResource(R.drawable.baseline_playlist_add_check_24);
            icono.setColorFilter(getResources().getColor(android.R.color.holo_green_light));
            contenedorCompletadas.addView(cardView);
        } else {
            icono.setImageResource(R.drawable.baseline_menu_book_24);
            icono.setColorFilter(getResources().getColor(android.R.color.holo_blue_light));
            contenedorPendientes.addView(cardView);
        }

        // Cuando clickeas el CardView
        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), DetallesTarActivity.class);
            intent.putExtra("titulo", tarea.getTitulo());
            intent.putExtra("fecha", tarea.getFecha());
            intent.putExtra("prioridad", tarea.getPrioridad());
            intent.putExtra("descripcion", tarea.getDescripcion());
            intent.putExtra("nota", tarea.getNota());
            intent.putExtra("completa", tarea.isCompleta());
            intent.putExtra("materia", nombreMateria);  // tú ya lo estás obteniendo
            intent.putExtra("claveTareas", "TAREAS_" + nombreMateria);  // ESTA LÍNEA ES LA CLAVE 🔑
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        contenedorPendientes.removeAllViews();
        contenedorCompletadas.removeAllViews();

        cargarTodasLasTareas();
    }
}
