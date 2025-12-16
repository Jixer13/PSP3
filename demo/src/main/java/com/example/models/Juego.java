package com.example.models;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

public class Juego {

    private Map<String, Integer> apuestasRule=new HashMap<>();
    private Map<String, Integer> apuestasCoinflip=new HashMap<>();

    private String tipoJuego = ""; // "rule" o "coinflip"

    public Juego() {
    }



    public int rule() {
        // Genera un número entre 0 y 36 (ambos incluidos)
        return ThreadLocalRandom.current().nextInt(0, 37);
    }
    
    public int coinFlipRule(){
        return ThreadLocalRandom.current().nextInt(0,37);
    }

    public synchronized void apostarRule(String nombre, int numeroApostado){
        System.out.println("[HILO: " + nombre + "] ✓ ENTRA en la sección crítica de apostarRule");
        apuestasRule.put(nombre, numeroApostado);
        System.out.println("[HILO: " + nombre + "] → Apuesta registrada: " + numeroApostado);
    }

    public Map<String, Integer> getApuestasRule() {
        return apuestasRule;
    }

    public synchronized void apostarCoinflip(String nombre, int numeroApostado) {
        apuestasCoinflip.put(nombre,numeroApostado);
    }





    public String getTipoJuego() {
        return tipoJuego;
    }

    public void setTipoJuego(String tipoJuego) {
        this.tipoJuego = tipoJuego;
    }
}
