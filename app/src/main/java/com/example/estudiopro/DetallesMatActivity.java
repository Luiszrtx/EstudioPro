package com.example.estudiopro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.estudiopro.fragments.InicioFragment;
import com.example.estudiopro.fragments.MateriasFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import android.widget.Button;

public class DetallesMatActivity extends AppCompatActivity {

    private LayoutInflater inflater; // Inflador para cargar vistas personalizadas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detallesmateria); // Establece el layout de la actividad

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
    }

    // Método para eliminar una materia específica de SharedPreferences
    private void eliminarMateria(String titulo, String maestro, String horario, String aula) {
        SharedPreferences preferences = getSharedPreferences("MisMasterias", MODE_PRIVATE);
        String json = preferences.getString("materias_guardadas", null); // Obtiene el JSON de la lista

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Materia>>(){}.getType();
            List<Materia> materias = gson.fromJson(json, type); // Deserializa la lista

            // Busca la materia exacta por sus campos y la elimina
            for (int i = 0; i < materias.size(); i++) {
                Materia materia = materias.get(i);
                if (materia.getTitulo().equals(titulo) &&
                        materia.getMaestro().equals(maestro) &&
                        materia.getHorario().equals(horario) &&
                        materia.getAula().equals(aula)) {

                    materias.remove(i); // Elimina la materia de la lista
                    break;
                }
            }

            // Guarda la lista actualizada en SharedPreferences
            String nuevoJson = gson.toJson(materias);
            preferences.edit().putString("materias_guardadas", nuevoJson).apply();

            // Regresa a la actividad anterior informando que se eliminó la materia
            Intent intent = new Intent(DetallesMatActivity.this, HomeActivity.class); // Reemplaza con el nombre real de tu activity principal
            intent.putExtra("ir_a_fragment", "materias");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
}

