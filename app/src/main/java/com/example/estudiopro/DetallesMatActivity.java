package com.example.estudiopro;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DetallesMatActivity extends AppCompatActivity {

    private LayoutInflater inflater; // Inflador para cargar vistas personalizadas

    private String tituloMateria ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detallesmateria); // Establece el layout de la actividad
        tituloMateria = getIntent().getStringExtra("Titulo").trim();
        cargarTareas();

        // Referencias a los TextViews que mostrarán los datos de la materia
        TextView titulo = findViewById(R.id.textTituloDetalle);
        TextView maestro = findViewById(R.id.textMaestroDetalle);
        TextView horario = findViewById(R.id.textHorarioDetalle);
        TextView aula = findViewById(R.id.textAulaDetalle);

        // Recupera los datos enviados desde la actividad anterior mediante el Intent
        String t = getIntent().getStringExtra("Titulo");
        String m = getIntent().getStringExtra("Maestro");
        String h = getIntent().getStringExtra("Horario");
        String a = getIntent().getStringExtra("Aula");
        tituloMateria = t;

        // Asigna los valores a los TextViews
        titulo.setText(t);
        maestro.setText(m);
        horario.setText(h);
        aula.setText(a);

        // Botón para eliminar la materia
        CardView btnEliminar = findViewById(R.id.card_borrar_materia);
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Infla el diseño del diálogo de confirmación personalizado
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.eliminar_materias, null);

                // Crea el AlertDialog con la vista inflada
                AlertDialog dialog = new AlertDialog.Builder(DetallesMatActivity.this)
                        .setView(dialogView)
                        .setCancelable(false) // Evita que se cierre al tocar fuera
                        .create();

                // Muestra el diálogo
                dialog.show();
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); // Fondo transparente

                // Opcional: Ajusta el tamaño del diálogo a toda la anchura
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                }

                // Referencias a los botones y al texto del diálogo
                Button btnCancelar = dialogView.findViewById(R.id.btnCancelar);
                Button btnConfirmar = dialogView.findViewById(R.id.btnConfirmar);
                TextView tvMensaje = dialogView.findViewById(R.id.tvMensaje);

                // Personaliza el mensaje con el nombre de la materia
                tvMensaje.setText("¿Estás seguro de que deseas eliminar “" + t + "”?");

                // Botón de cancelar cierra el diálogo
                btnCancelar.setOnClickListener(view -> dialog.dismiss());

                // Botón de confirmar llama al método para eliminar la materia
                btnConfirmar.setOnClickListener(view -> {
                    eliminarMateria(t, m, h, a); // Se envían los datos para identificar la materia
                    dialog.dismiss(); // Cierra el diálogo

                });
            }

        });

        // Botón de regresar (parte superior o inferior de la pantalla)
        LinearLayout btnRegresar = findViewById(R.id.regresar);
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Cierra la actividad
                overridePendingTransition(R.anim.movimiento_derecha, R.anim.movimiento_izquierda); // Aplica animación al salir
            }
        });


        CardView cardAgregarTarea = findViewById(R.id.card_agregar_tarea);
        cardAgregarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoAgregarTarea();
            }
        });
    }

    // Método para eliminar una materia específica de SharedPreferences
    private void eliminarMateria(String titulo, String maestro, String horario, String aula) {
        SharedPreferences preferences = getSharedPreferences("MisMasterias", MODE_PRIVATE);
        String json = preferences.getString("materias_guardadas", null);

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Materia>>(){}.getType();
        List<Materia> materias = (json != null) ? gson.fromJson(json, type) : new ArrayList<>();

        for (int i = 0; i < materias.size(); i++) {
            Materia materia = materias.get(i);
            if (materia.getTitulo().equals(titulo) &&
                    materia.getMaestro().equals(maestro) &&
                    materia.getHorario().equals(horario) &&
                    materia.getAula().equals(aula)) {

                materias.remove(i);
                break;
            }
        }

        // Guardamos nuevamente las materias
        String nuevoJson = gson.toJson(materias);
        preferences.edit().putString("materias_guardadas", nuevoJson).apply();

        // 🔥 ELIMINAMOS TAMBIÉN LAS TAREAS ASOCIADAS
        SharedPreferences tareasPrefs = getSharedPreferences("TAREAS_PREF", MODE_PRIVATE);
        String claveTareas = "TAREAS_" + titulo;  // igual que en obtenerClaveTareas()
        tareasPrefs.edit().remove(claveTareas).apply();

        // Regresamos a la pantalla principal
        Intent intent = new Intent(DetallesMatActivity.this, HomeActivity.class);
        intent.putExtra("ir_a_fragment", "materias");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private void mostrarDialogoAgregarTarea() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.agregar_tarea, null);

        EditText editTitulo = dialogView.findViewById(R.id.editTituloTarea);
        EditText editDescripcion = dialogView.findViewById(R.id.editDescripcionTarea);
        EditText editNota = dialogView.findViewById(R.id.editNotaTarea);
        Button btnSeleccionarFecha = dialogView.findViewById(R.id.btnSeleccionarFecha);
        TextView textFechaSeleccionada = dialogView.findViewById(R.id.textFechaSeleccionada);
        RadioGroup radioGroupPrioridad = dialogView.findViewById(R.id.radioGroupPrioridad);

        // Nuevos botones que ahora están en el XML
        Button btnAgregar = dialogView.findViewById(R.id.btnAgregar);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelar);

        final String[] fechaSeleccionada = {""};  // Variable auxiliar

        btnSeleccionarFecha.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int anio = calendar.get(Calendar.YEAR);
            int mes = calendar.get(Calendar.MONTH);
            int dia = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        fechaSeleccionada[0] = dayOfMonth + "/" + (month + 1) + "/" + year;
                        textFechaSeleccionada.setText(fechaSeleccionada[0]);
                    }, anio, mes, dia);
            datePickerDialog.show();
        });

        // Creamos el diálogo sin título
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnAgregar.setOnClickListener(v -> {
            String titulo = editTitulo.getText().toString();
            String descripcion = editDescripcion.getText().toString();
            String nota = editNota.getText().toString();
            String fecha = fechaSeleccionada[0];

            int radioId = radioGroupPrioridad.getCheckedRadioButtonId();
            RadioButton radioSeleccionado = dialogView.findViewById(radioId);
            String prioridad = (radioSeleccionado != null) ? radioSeleccionado.getText().toString() : "";

            Tareas nuevaTarea = new Tareas(titulo, fecha, prioridad, descripcion, nota);
            agregarCardTarea(nuevaTarea);
            guardarTarea(nuevaTarea);
            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }


    private void agregarCardTarea(Tareas tarea) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.ventana_tareas, null);

        TextView tituloCard = cardView.findViewById(R.id.titulo_card);
        TextView fechaCard = cardView.findViewById(R.id.fecha_card);
        ImageView icono = cardView.findViewById(R.id.circuloColor);
        androidx.cardview.widget.CardView card = cardView.findViewById(R.id.cardTreas);

        tituloCard.setText(tarea.getTitulo());
        fechaCard.setText(tarea.getFecha());
        if (tarea.isCompleta()) {
            icono.setImageResource(R.drawable.baseline_playlist_add_check_24);
            icono.setColorFilter(getResources().getColor(android.R.color.holo_green_light));
        } else {
            icono.setImageResource(R.drawable.baseline_menu_book_24);
            String colorHex = "#00B8D4";
            int color = Color.parseColor(colorHex);
            icono.setColorFilter(color);
        }

        // Al hacer clic en el CardView mostramos los detalles
        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(DetallesMatActivity.this, DetallesTarActivity.class);
            intent.putExtra("titulo", tarea.getTitulo());
            intent.putExtra("fecha", tarea.getFecha());
            intent.putExtra("prioridad", tarea.getPrioridad());
            intent.putExtra("descripcion", tarea.getDescripcion());
            intent.putExtra("nota", tarea.getNota());

            // También pasamos el nombre de la materia para saber a qué clave pertenece:
            intent.putExtra("claveTareas", obtenerClaveTareas());
            intent.putExtra("materia", tituloMateria);
            intent.putExtra("completa", tarea.isCompleta());
            startActivityForResult(intent, 100); // Para que podamos actualizar al volver si hace falta
        });

        LinearLayout contenedor = findViewById(R.id.contenedor_tareas);
        contenedor.addView(cardView);
    }

    private String obtenerClaveTareas() {
        return "TAREAS_" + tituloMateria;  // Variable de la materia actual
    }

    private void guardarTarea(Tareas tarea) {
        SharedPreferences prefs = getSharedPreferences("TAREAS_PREF", MODE_PRIVATE);
        Gson gson = new Gson();

        // Leer las tareas existentes
        String json = prefs.getString(obtenerClaveTareas(), null);
        Type type = new TypeToken<ArrayList<Tareas>>(){}.getType();
        ArrayList<Tareas> tareasList = (json != null) ? gson.fromJson(json, type) : new ArrayList<>();

        tareasList.add(tarea);

        // Guardar de nuevo
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(obtenerClaveTareas(), gson.toJson(tareasList));
        editor.apply();
    }
    private void cargarTareas() {
        SharedPreferences prefs = getSharedPreferences("TAREAS_PREF", MODE_PRIVATE);
        String json = prefs.getString(obtenerClaveTareas(), null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Tareas>>(){}.getType();
            ArrayList<Tareas> tareasList = gson.fromJson(json, type);

            for (Tareas tarea : tareasList) {
                agregarCardTarea(tarea);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Limpiamos los cards existentes:
            LinearLayout contenedor = findViewById(R.id.contenedor_tareas);
            contenedor.removeAllViews();

            // Volvemos a cargar las tareas desde SharedPreferences
            cargarTareas();
        }
    }

}

