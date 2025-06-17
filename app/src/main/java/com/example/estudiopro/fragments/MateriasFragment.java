package com.example.estudiopro.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;

import com.example.estudiopro.DetallesMatActivity;
import com.example.estudiopro.Materia;
import com.example.estudiopro.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MateriasFragment extends Fragment {

    private List<Materia> materiasList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private static final String PREF_KEY = "materias_guardadas";
    private LinearLayout cardContainer;
    private LayoutInflater inflater;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_materias, container, false);

        this.inflater = inflater;
        sharedPreferences = requireContext().getSharedPreferences("MisMasterias", getContext().MODE_PRIVATE);
        cardContainer = view.findViewById(R.id.card_container);

        cargarMateriasGuardadas();

        CardView btnAgregar1 = view.findViewById(R.id.card_agregar_materia);
        btnAgregar1.setOnClickListener(v -> mostrarDialogoAgregar());

        return view;
    }

    private void mostrarDialogoAgregar() {
        View dialogView = inflater.inflate(R.layout.agregar_materia, null);

        EditText editTitulo = dialogView.findViewById(R.id.editTitulo);
        EditText editMaestro = dialogView.findViewById(R.id.editMaestro);
        EditText editHorario = dialogView.findViewById(R.id.editHorario);
        EditText editAula = dialogView.findViewById(R.id.editAula);
        Button btnAgregar = dialogView.findViewById(R.id.btnAgregar);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelar);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        View viewColor = dialogView.findViewById(R.id.previewColor);
        Button btnElegirColor = dialogView.findViewById(R.id.btnElegirCOlor);
        final int[] colorSeleccionado = {Color.parseColor("#FF0000")};

        btnElegirColor.setOnClickListener(v -> {
            View colorDialogView = inflater.inflate(R.layout.colores_circulo, null);
            AlertDialog colorDialog = new AlertDialog.Builder(getContext())
                    .setView(colorDialogView)
                    .create();

            int[] viewIds = {R.id.color1, R.id.color2, R.id.color3, R.id.color4, R.id.color5};

            for (int id : viewIds) {
                View colorView = colorDialogView.findViewById(id);
                String colorHex = (String) colorView.getTag();
                int parsedColor = Color.parseColor(colorHex);

                GradientDrawable drawable = (GradientDrawable) colorView.getBackground();
                drawable.setColor(parsedColor);

                colorView.setOnClickListener(v1 -> {
                    colorSeleccionado[0] = parsedColor;
                    viewColor.getBackground().setColorFilter(parsedColor, PorterDuff.Mode.SRC_IN);
                    colorDialog.dismiss();
                });
            }

            colorDialog.show();
            colorDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        });

        btnAgregar.setOnClickListener(v -> {
            String titulo = editTitulo.getText().toString().trim();
            String maestro = editMaestro.getText().toString().trim();
            String horario = editHorario.getText().toString().trim();
            String aula = editAula.getText().toString().trim();
            int color = colorSeleccionado[0];

            if (!titulo.isEmpty() && !maestro.isEmpty() && !horario.isEmpty() && !aula.isEmpty()) {
                materiasList.add(new Materia(titulo, maestro, horario, aula, color));
                guardarMaterias();
                agregarCard(titulo, maestro, horario, aula, color);
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Campos vacíos", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void agregarCard(String titulo, String maestro, String horario, String aula, int color) {
        View card = inflater.inflate(R.layout.ventana_materias, null);

        TextView tituloView = card.findViewById(R.id.titulo_card);
        TextView maestroView = card.findViewById(R.id.maestro_card);
        TextView horarioView = card.findViewById(R.id.horario_card);
        TextView aulaView = card.findViewById(R.id.aula_card);
        View colorCircle = card.findViewById(R.id.circuloColor);

        GradientDrawable background = (GradientDrawable) colorCircle.getBackground();
        background.setColor(color);

        tituloView.setText(titulo);
        maestroView.setText(maestro);
        horarioView.setText(horario);
        aulaView.setText(aula);

        card.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DetallesMatActivity.class);
            intent.putExtra("Titulo", titulo);
            intent.putExtra("Maestro", maestro);
            intent.putExtra("Horario", horario);
            intent.putExtra("Aula", aula);
            startActivityForResult(intent, 1001);
        });

        cardContainer.addView(card);
    }

    private void guardarMaterias() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(materiasList);
        editor.putString(PREF_KEY, json);
        editor.apply();
        Log.d("Guardar", "Materias guardadas: " + json);
    }

    private void cargarMateriasGuardadas() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(PREF_KEY, null);
        Type type = new TypeToken<ArrayList<Materia>>() {}.getType();

        try {
            materiasList = gson.fromJson(json, type);
        } catch (Exception e) {
            materiasList = new ArrayList<>();
            e.printStackTrace();
        }

        if (materiasList == null) {
            materiasList = new ArrayList<>();
        }

        for (Materia materia : materiasList) {
            agregarCard(
                    materia.getTitulo(),
                    materia.getMaestro(),
                    materia.getHorario(),
                    materia.getAula(),
                    materia.getColor()
            );
        }

        Log.d("cargar", "materias cargadas: " + materiasList.size());
    }

}
