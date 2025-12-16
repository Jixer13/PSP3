package com.example.models;

public class EstadoJuego {
    // Control de si los bots están apostando
    public static boolean botsApostando = false;


    // Control de ciclo de generacion de numeros
    public static volatile long ultimaGeneracion = 0;
    public static volatile int contadorGeneraciones = 0;
}