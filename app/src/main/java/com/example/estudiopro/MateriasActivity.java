package com.example.estudiopro;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.icu.text.ListFormatter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MateriasActivity extends AppCompatActivity {

    // Lista que contiene las materias agregadas ( Es un Array).
    private List<Materia> materiasList = new ArrayList<>();

    // Preferencias para guardar las materias de forma persistente ( Aqui es donde el Json gurdara todos los datos ingresados).
    private SharedPreferences sharedPreferences;

    // Clave usada para guardar y recuperar las materias ( Es como un nombre único que sirve para identificar un dato almacenado)
    private static final String PREF_KEY = "materias_guardadas";

    // Contenedor donde se agregan las tarjetas de materias dinámicamente ( Declarado como LineraLayout ya que ahi es donde agregaremos los cardView)
    private LinearLayout cardContainer;

    // Inflador para crear vistas desde archivos XML ( Sirve para ayudarnos a abrir xml como Alerts Dialog)
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materias);

        // Inicializa SharedPreferences ( Guardado de datos en Json)
        sharedPreferences = getSharedPreferences("MisMasterias", MODE_PRIVATE);

        // Obtiene el contenedor de las tarjetas ( Layout donde se agregaran los cardView)
        cardContainer = findViewById(R.id.card_container);

        // Inicializa el inflater
        inflater = LayoutInflater.from(this);

        // Carga las materias previamente guardadas en memoria ( En caso de cerrar la aplicacion todo se guarda aqui y se carga aqui)
        cargarMateriasGuardadas();

        // Botón que abre el formulario para agregar nueva materia
        CardView btnAgregar1 = findViewById(R.id.card_agregar_materia);
        btnAgregar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoAgregar(); // Muestra el diálogo de agregar materia
            }
        });
    }

    // Método que muestra el formulario para una nueva materia
    private void mostrarDialogoAgregar() {
        View dialogView = inflater.inflate(R.layout.agregar_materia, null);

        // Referencias a los campos del formulario ( Aqui es donde se enlazan al xml los parametros de entrada del formulario)
        EditText editTitulo = dialogView.findViewById(R.id.editTitulo);
        EditText editMaestro = dialogView.findViewById(R.id.editMaestro);
        EditText editHorario = dialogView.findViewById(R.id.editHorario);
        EditText editAula = dialogView.findViewById(R.id.editAula);
        Button btnAgregar = dialogView.findViewById(R.id.btnAgregar);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelar);

        // Crea el AlertDialog con el formulario
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        // Elementos para la selección de color ( Aqui se define un color por defecto el cual siempre sera seleccionado en caso de no haberlo selecciona el ususario)
        View viewColor = dialogView.findViewById(R.id.previewColor);
        Button btnElegirColor = dialogView.findViewById(R.id.btnElegirCOlor);
        final int[] colorSeleccionado = {Color.parseColor("#FF0000")}; // Color por defecto

        // Muestra el selector de colores cuando se presiona el botón
        btnElegirColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View colorDialogView = inflater.inflate(R.layout.colores_circulo, null);
                AlertDialog colorDialog = new AlertDialog.Builder(MateriasActivity.this)
                        .setView(colorDialogView)
                        .create();

                // IDs de los botones circulares de colores ( Los colores son ids ya que se encuentran en el xml y se seleccionan dinamicamente)
                int[] viewIds = {R.id.color1, R.id.color2, R.id.color3, R.id.color4, R.id.color5};

                for (int id : viewIds) {
                    View colorView = colorDialogView.findViewById(id);
                    String colorHex = (String) colorView.getTag(); // El color está en el atributo tag
                    int parsedColor = Color.parseColor(colorHex);

                    // Cambia visualmente el color del círculo ( Cada circulo adopta el color definido en el Xml)
                    GradientDrawable drawable = (GradientDrawable) colorView.getBackground();
                    drawable.setColor(parsedColor);

                    // Selecciona el color cuando se toca un círculo ( El evento click listener nos ayuda a definir el nuevo color)
                    colorView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            colorSeleccionado[0] = parsedColor;
                            viewColor.getBackground().setColorFilter(parsedColor, PorterDuff.Mode.SRC_IN);
                            colorDialog.dismiss(); // Cierra el selector
                        }
                    });
                }

                // Muestra el diálogo sin fondo blanco ( transparente)
                colorDialog.show();
                colorDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
        });

        // Agrega la nueva materia al presionar el botón
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo = editTitulo.getText().toString().trim();
                String maestro = editMaestro.getText().toString().trim();
                String horario = editHorario.getText().toString().trim();
                String aula = editAula.getText().toString().trim();
                int color = colorSeleccionado[0];

                if (!titulo.isEmpty() && !maestro.isEmpty() && !horario.isEmpty() && !aula.isEmpty()) {
                    // Agrega a la lista ( Aqui es donde el Json guarda la informacion requerida para la ventana detallesMateria)
                    materiasList.add(new Materia(titulo, maestro, horario, aula, color));
                    guardarMaterias(); // Guarda la lista actualizada ( Actualiza la informacon en caso de ser requerido)
                    agregarCard(titulo, maestro, horario, aula, color); // Crea la tarjeta ( Crea la cardVIew visible en la ventana Materias, agregada de forma dinamica)
                    dialog.dismiss(); // Cierra el formulario
                } else {
                    Toast.makeText(MateriasActivity.this, "Campos vacíos", Toast.LENGTH_SHORT).show();
                    // Toast para avisar al usuario que hay campos faltantes por rellenar
                }
            }
        });

        // Cancela el formulario ( Cierra el formulario)
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); // Quita fondo blanco ( transparente)
    }

    // Agrega visualmente una tarjeta con los datos de una materia ( Se agrega de forma dinamica desde su xml individual)
    private void agregarCard(String titulo, String maestro, String hoario, String aula, int color) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View card = inflater.inflate(R.layout.ventana_materias, null);

        // Referencias a los campos de texto ( Campos los cuales son rellenados)
        // Sin embargo en la tarjeta solo son visibles  campos: Materia y Maestro con la finalidad de no amontonar informacion)
        // Los demas campos, son ocultos y solo fueron agregados para evitar el mal funcionamiento del guardado de informacion en el Json)
        TextView tituloView = card.findViewById(R.id.titulo_card);
        TextView maestroView = card.findViewById(R.id.maestro_card);
        TextView horarioView = card.findViewById(R.id.horario_card);
        TextView aulaView = card.findViewById(R.id.aula_card);
        View colorCircle = card.findViewById(R.id.circuloColor);

        // Aplica el color seleccionado al círculo (El color seleccionado se Agregara al shape cirluo del cardView)
        GradientDrawable background = (GradientDrawable) colorCircle.getBackground();
        background.setColor(color);

        // Asigna los datos a los TextView ( Aquei solo seran visibles Maestro y Materia(Titulo))
        tituloView.setText(titulo);
        maestroView.setText(maestro);
        horarioView.setText(hoario);
        aulaView.setText(aula);

        // Cuando se toca la tarjeta, se abre la pantalla de detalles ( Aqui si se muestran todos los datos guardados)
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Todos estos parametros son transportados a la ventana DetallesActivity en donde son recibidos y colocados en su lugar)
                Intent intent = new Intent(MateriasActivity.this, DetallesMatActivity.class);
                intent.putExtra("Titulo", titulo);
                intent.putExtra("Maestro", maestro);
                intent.putExtra("Horario", hoario);
                intent.putExtra("Aula", aula);
                // sirve para iniciar una nueva actividad esperando una respuesta de vuelta
                // No solo abre otra pantalla, sino que también está preparada para recibir información cuando esa pantalla termine
                startActivityForResult(intent, 1);
                // Animacion realizada en la pestaña anim por medio de 2 xml translate
                overridePendingTransition(R.anim.movimiento_derecha, R.anim.movimiento_izquierda);
            }
        });

        cardContainer.addView(card); // Agrega la tarjeta al contenedor ( MateriasActivity)
    }

    // Guarda la lista de materias en SharedPreferences como JSON
    // Aqui es donde se guardan los datos anteriormente almacenados
    private void guardarMaterias() {
        SharedPreferences.Editor editor = sharedPreferences.edit(); // Aqui se edita en caso de cualquier cambio o borron de informacion
        Gson gson = new Gson(); // Se crea un nuevo Json en caso de no existir Uno.
        String json = gson.toJson(materiasList); // confirma la lista
        editor.putString(PREF_KEY, json);
        editor.apply(); // Se aplican los cambios
        Log.d("Guardar", "Materias guardadas: " + json); // Solo agregado para ver su funcionalidad en LogCat
    }

    // Carga la lista de materias desde SharedPreferences
    private void cargarMateriasGuardadas() {
        // Se crea una instancia de Gson para convertir entre JSON y objetos Java
        Gson gson = new Gson();

        // Se obtiene el string JSON guardado en SharedPreferences bajo la clave PREF_KEY
        String json = sharedPreferences.getString(PREF_KEY, null);

        // Se define el tipo de dato que se espera deserializar (una lista de objetos Materia)
        Type type = new TypeToken<ArrayList<Materia>>(){}.getType();

        try {
            // Se convierte (deserializa) el JSON en una lista de objetos Materia
            materiasList = gson.fromJson(json, type);
        } catch (Exception e) {
            // Si hay un error durante la deserialización, se crea una lista vacía y se imprime el error
            materiasList = new ArrayList<>();
            e.printStackTrace();
        }

        // En caso de que la lista aún sea null por alguna razón, se asegura que no cause errores
        if (materiasList == null) {
            materiasList = new ArrayList<>();
        }

        // Por cada materia en la lista cargada, se crea visualmente una tarjeta en la pantalla
        for (Materia materia : materiasList) {
            agregarCard(
                    materia.getTitulo(),
                    materia.getMaestro(),
                    materia.getHorario(),
                    materia.getAula(),
                    materia.getColor()
            );
        }

        // Se imprime en la consola la cantidad de materias que fueron cargadas
        Log.d("cargar", "materias cargadas: " + materiasList.size());
    }

    // Maneja el resultado al volver de la actividad de detalles
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getBooleanExtra("materiaEliminada", false)) {
            cardContainer.removeAllViews(); // Borra todas las tarjetas
            cargarMateriasGuardadas(); // Recarga la lista actualizada
        }
    }
}

