package com.example.estudiopro;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DetallesTarActivity extends AppCompatActivity {
    private String titulo, fecha, prioridad, descripcion, nota;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detallestarea);

        // Obtener los parámetros
        String titulo = getIntent().getStringExtra("titulo");
        String fecha = getIntent().getStringExtra("fecha");
        String prioridad = getIntent().getStringExtra("prioridad");
        String descripcion = getIntent().getStringExtra("descripcion");
        String nota = getIntent().getStringExtra("nota");
        String materia = getIntent().getStringExtra("materia");

        // Asignar a los TextViews
        ((TextView) findViewById(R.id.tituloDetalle)).setText(titulo);
        ((TextView) findViewById(R.id.fechaDetalle)).setText("Fecha: " + fecha);
        ((TextView) findViewById(R.id.prioridadDetalle)).setText("Prioridad: " + prioridad);
        ((TextView) findViewById(R.id.descripcionDetalle)).setText("Descripción: " + descripcion);
        ((TextView) findViewById(R.id.notaDetalle)).setText("Nota: " + nota);
        ((TextView) findViewById(R.id.textMateriaNombre)).setText("Materia: " + materia);

        Button btnEliminar = findViewById(R.id.btnEliminarTarea);
        btnEliminar.setOnClickListener(v -> eliminarTarea());

        LinearLayout btnRegresar = findViewById(R.id.regresar);
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Cierra la actividad
                overridePendingTransition(R.anim.movimiento_derecha, R.anim.movimiento_izquierda); // Aplica animación al salir
            }
        });


        boolean estaCompleta = getIntent().getBooleanExtra("completa", false);
        TextView btnEstado = findViewById(R.id.estadoTarea);
        if (estaCompleta) {
            btnEstado.setText("Completada");
            btnEstado.setEnabled(false);
            btnEstado.setTextColor(getResources().getColor(android.R.color.black));
            btnEstado.setBackgroundResource(R.drawable.bordes_redondos_4);
        } else {
            btnEstado.setText("Pendiente");
            btnEstado.setEnabled(true);
            btnEstado.setTextColor(getResources().getColor(android.R.color.black)); // o el color que quieras para pendiente
        }
        btnEstado.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Marcar como completado")
                    .setMessage("¿Estás seguro de marcar como completado?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        btnEstado.setText("Completada");
                        btnEstado.setTextColor(getResources().getColor(android.R.color.black));
                        btnEstado.setBackgroundResource(R.drawable.bordes_redondos_4);
                        marcarTareaComoCompletada(titulo);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }
    private void eliminarTarea() {
        SharedPreferences prefs = getSharedPreferences("TAREAS_PREF", MODE_PRIVATE);
        Gson gson = new Gson();

        String clave = getIntent().getStringExtra("claveTareas");
        String tituloEliminar = getIntent().getStringExtra("titulo");

        String json = prefs.getString(clave, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<Tareas>>(){}.getType();
            ArrayList<Tareas> tareasList = gson.fromJson(json, type);

            for (int i = 0; i < tareasList.size(); i++) {
                if (tareasList.get(i).getTitulo().equals(tituloEliminar)) {
                    tareasList.remove(i);
                    break;
                }
            }

            prefs.edit().putString(clave, gson.toJson(tareasList)).apply();

            // Indicamos que se eliminó la tarea para que DetallesMatActivity lo sepa
            Intent resultIntent = new Intent();
            resultIntent.putExtra("tareaEliminada", tituloEliminar);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    private void marcarTareaComoCompletada (String titulo) {
        SharedPreferences prefs = getSharedPreferences("TAREAS_PREF", MODE_PRIVATE);
        Gson gson = new Gson();
        String clave = getIntent().getStringExtra("claveTareas");
        String json = prefs.getString(clave, null);

        if (json != null) {
            Type type = new TypeToken<ArrayList<Tareas>>(){}.getType();
            ArrayList<Tareas> tareasArrayList = gson.fromJson(json, type);

            for (Tareas tarea : tareasArrayList) {
                if (tarea.getTitulo().equals(titulo)) {
                    tarea.setCompleta(true);
                    break;
                }
            }
            prefs.edit().putString(clave, gson.toJson(tareasArrayList)).apply();
        }
        Intent resultIntent = new Intent();
        resultIntent.putExtra("tareaActualizada", titulo);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

}

