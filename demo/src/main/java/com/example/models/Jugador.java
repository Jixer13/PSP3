package com.example.models;

import com.example.models.Juego;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Jugador implements Runnable{

    private String nombre;
    private int saldo;
    private int numeroApostado;
    private Juego juego;

    public Jugador() {
    }

    public Jugador(String nombre, Juego juego) {
        this.nombre = nombre;
        this.saldo = 1000;
        this.juego = juego;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Jugador jugador = (Jugador) o;
        return Objects.equals(nombre, jugador.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nombre);
    }

    @Override
    public String toString() {
        return "Jugador{" +
                "nombre='" + nombre + '\'' +
                ", saldo=" + saldo +
                '}';
    }

    private int numGenerador(){
        return ThreadLocalRandom.current().nextInt(1, 37); // 1 incluido, 37 excluido

    }

    @Override
    public void run() {

        if (!juego.equals(juego.rule())) {
            this.setSaldo(this.getSaldo() - 10);
            this.setNumeroApostado(this.getNumeroApostado());
            juego.apostarCoinflip(this.getNombre(),this.getNumeroApostado());


        } else {
            // Deduce 10 euros al apostar
            this.setSaldo(this.getSaldo() - 10);
            this.setNumeroApostado(numGenerador());
            juego.apostarRule(this.getNombre(), this.getNumeroApostado());
        }
    }
}
