package com.example.models;

import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.ThreadLocalRandom;

public class Juego {

    private Map<String, Integer> apuestasRule=new HashMap<>();
    private Map<String, Integer> apuestasCoinflip=new HashMap<>();

    public int rule() {
        // Genera un número entre 0 y 36 (ambos incluidos)
        return ThreadLocalRandom.current().nextInt(0, 37);
    }
    public int coinFlipRule(){
        return ThreadLocalRandom.current().nextInt(0,37);
    }

    public synchronized void apostarRule(String nombre, int numeroApostado){
        apuestasRule.put(nombre, numeroApostado);
    }

    public Map<String, Integer> getApuestasRule() {
        return apuestasRule;
    }


    public synchronized void apostarCoinflip(String nombre, int numeroApostado) {
        apuestasCoinflip.put(nombre,numeroApostado);

    }
}
