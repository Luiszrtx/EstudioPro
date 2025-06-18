package com.example.estudiopro;

public class Tareas {
    private String titulo;
    private String fecha;
    private String prioridad;
    private String descripcion;
    private String nota;

    public Tareas(String titulo, String fecha, String prioridad, String descripcion, String nota) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.prioridad = prioridad;
        this.descripcion = descripcion;
        this.nota = nota;
    }

    // Getters
    public String getTitulo() { return titulo; }
    public String getFecha() { return fecha; }
    public String getPrioridad() { return prioridad; }
    public String getDescripcion() { return descripcion; }
    public String getNota() { return nota; }
}
