package com.example.estudiopro;

// Clase modelo que representa una materia o asignatura
public class Materia {

    // Atributos privados de la clase
    private String titulo;   // Nombre de la materia
    private String maestro;  // Nombre del profesor
    private String horario;  // Horario de la clase
    private String aula;     // Aula donde se imparte
    private int color;       // Color representativo de la materia (en formato int, por ejemplo Color.RED)

    // Constructor vacío necesario para algunas operaciones como deserialización con Gson
    public Materia() {}

    // Constructor con todos los campos necesarios para crear una materia
    public Materia(String titulo, String maestro, String horario, String aula, int color) {
        this.titulo = titulo;
        this.maestro = maestro;
        this.horario = horario;
        this.aula = aula;
        this.color = color;
    }

    // Métodos getters: permiten acceder al valor de los atributos

    public String getTitulo() {
        return titulo;
    }

    public String getMaestro() {
        return maestro;
    }

    public String getHorario() {
        return horario;
    }

    public String getAula() {
        return aula;
    }

    public int getColor() {
        return color;
    }

    // Método setter: permite modificar el color después de creada la instancia
    public void setColor(int color) {
        this.color = color;
    }
}

