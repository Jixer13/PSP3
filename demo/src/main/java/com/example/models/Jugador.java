package com.example.models;

import com.example.models.Juego;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Jugador implements Runnable {

    // VARIABLES //
    private String nombre;
    private int saldo;
    private int numeroApostado;
    private Juego juego;
    private boolean esHumano = false;

    // CREACIÓN DEL HILO //
    private Thread hilo;

    // ESTADO INICIAL DEL HILO //
    private boolean estadoHilo = false;

    // CONSTRUCTOR //
    public Jugador() {
    }

    public Jugador(String nombre, Juego juego) {
        this.nombre = nombre;
        this.saldo = 1000;
        this.juego = juego;
    }

    // GETTER Y SETTERS //
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    public int getNumeroApostado() {
        return numeroApostado;
    }

    public void setNumeroApostado(int numeroApostado) {
        this.numeroApostado = numeroApostado;
    }

    public Juego getJuego() {
        return juego;
    }

    public void setJuego(Juego juego) {
        this.juego = juego;
    }

    public boolean isEsHumano() {
        return esHumano;
    }

    public void setEsHumano(boolean esHumano) {
        this.esHumano = esHumano;
    }

    // ----------------------------------------------- //
    // -------------        HILOS         ------------ //
    // ----------------------------------------------- //

    public void iniciarHiloJugador() {
        try {
            if (hilo == null) {
                hilo = new Thread(() -> iniciarJugador());
                hilo.setDaemon(true);

                System.out.println("[ INICIO HILO ] - Jugador " + nombre + " ha entrado en el Casino");

                // CAMBIAMOS ESTADO AL CREAR EL HILO CORRECTAMENTE //
                estadoHilo = true;

                hilo.start();
            }
        } catch (IllegalThreadStateException eHilo) {
            System.out.println(" [ HILO NO INICIADO * ERROR * ] - No se pudo iniciar el hilo del jugador [ " + nombre + " ]");
        } catch (Exception e) {
            System.out.println(" [ ERROR INESPERADO ] \n" + e.getMessage());
        }
    }

    private void iniciarJugador() {
        while (estadoHilo) {
            try {
                // Esperamos 5 segundos antes de generar nuevo número
                Thread.sleep(5000);

            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }

            // GENERAMOS UN NUMERO ALEATORIO DEL 1 al 36 //
            this.numeroApostado = (int)(Math.random() * 36) + 1;

            // Incrementamos el contador de generaciones
            synchronized (EstadoJuego.class) {
                EstadoJuego.contadorGeneraciones++;
                EstadoJuego.ultimaGeneracion = System.currentTimeMillis();
            }

            try {
                // Esperamos 35 segundos antes del siguiente ciclo
                Thread.sleep(35000);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void detenerHilo() {
        estadoHilo = false;
        if (hilo != null) {
            hilo.interrupt();
        }
    }


    private int numGenerador(){
        return ThreadLocalRandom.current().nextInt(1, 37); // 1 incluido, 37 excluido

    }

    public void run() {
        this.setSaldo(this.getSaldo() - 10);

        if (!juego.equals(juego.rule())) {
            this.setNumeroApostado(numGenerador());
            juego.apostarCoinflip(this.getNombre(), this.getNumeroApostado());
        } else {
            this.setNumeroApostado(numGenerador());
            juego.apostarRule(this.getNombre(), this.getNumeroApostado());
        }
    }

}
