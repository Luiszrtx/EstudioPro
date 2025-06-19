package com.example.estudiopro.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.estudiopro.R;
import com.example.estudiopro.Tareas;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class TodasTareasFragment extends Fragment {

    private LinearLayout contenedorPendientes, contenedorCompletadas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_todaslastareas, container, false);

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
            String clave = entry.getKey();
            String json = (String) entry.getValue();

            Type type = new TypeToken<ArrayList<Tareas>>(){}.getType();
            ArrayList<Tareas> listaTareas = gson.fromJson(json, type);

            for (Tareas tarea : listaTareas) {
                agregarCard(tarea, clave);
            }
        }
    }

    private void agregarCard(Tareas tarea, String materiaClave) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View cardView = inflater.inflate(R.layout.ventana_tareas, null);  // reutilizamos tu layout de cards

        TextView tituloCard = cardView.findViewById(R.id.titulo_card);
        TextView fechaCard = cardView.findViewById(R.id.fecha_card);
        ImageView icono = cardView.findViewById(R.id.circuloColor);
        CardView card = cardView.findViewById(R.id.cardTreas);

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
    }
}

