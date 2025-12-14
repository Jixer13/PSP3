package com.example.models;

public class EstadoJuego {
    // Control de si los bots están apostando
    public static boolean botsApostando = false;
    public static int vecesJugadas = 0;

    // Control de rondas
    public static volatile boolean rondaActiva = false;
    public static volatile boolean esperandoRonda = false;

    // Número ganador de la última ronda
    public static volatile int numeroGanador = -1;

    // Tipo de juego activo (1=Rule, 2=CoinFlip, 3=Martingala)
    public static volatile int juegoActivo = 0;

    // Control de jugador humano
    public static volatile boolean jugadorHumanoEnJuego = false;
    public static volatile int apuestaJugadorHumano = -1;

    // Control de ciclo de generacion de numeros
    public static volatile long ultimaGeneracion = 0;
    public static volatile int contadorGeneraciones = 0;
}