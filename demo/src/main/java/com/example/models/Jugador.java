package com.example.models;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Jugador implements Runnable{

    private String nombre;
    private int saldo;

    private int numero;

    public Jugador() {

    }

    public Jugador(String nombre, int saldo) {
        this.nombre = nombre;
        this.saldo = saldo;
        numeroApostado();
    }


    private void numeroApostado(){
        numero = ThreadLocalRandom.current().nextInt(1, 37);
    }
    @Override
    public void run() {

    }
}
