package com.example.models;

import java.util.Objects;

public class Banca {

    private int saldo = 50000;
    private int numeroApostado;
    private Thread hilo;
    private boolean estadoBanca = false;

    // CONSTRUCTOR //
    public Banca() {
    }

    public Banca(int saldo) {
        this.saldo = saldo;
    }

    // GETTER SETTERS //
    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    public int getNumeroApostado() {
        return numeroApostado;
    }

    // TO STRING //
    @Override
    public String toString() {
        return "La banca tiene un saldo de " + saldo + "€";
    }

    // ----------------------------------------------- //
    // -------------        HILOS         ------------ //
    // ----------------------------------------------- //

    public void iniciarHiloBanca() {
        if (hilo == null) {
            hilo = new Thread(() -> ejecutarBanca());
            hilo.setDaemon(true);

            estadoBanca = true;
            System.out.println(" * [ HILO BANCA INICIADO ] * \n");
            hilo.start();
        }
    }

    private void ejecutarBanca() {
        while (estadoBanca) {
            try {
                // Esperamos 5 segundos
                Thread.sleep(5000);

            } catch (InterruptedException eHilo) {
                Thread.currentThread().interrupt();
                break;
            }

            // GENERACIÓN DE NÚMERO GANADOR EN EL HILO BANCA //
            numeroApostado = (int) (Math.random() * 37); // 0 al 36

            System.out.println("\n🏦 [ BANCA ] Ha generado el número " + numeroApostado);
            EstadoJuego.botsApostando = false;

            // NOTIFICAMOS que la ronda de generación ha terminado
            synchronized (this) {
                notifyAll();
            }

            try {
                // Esperamos 35 segundos antes del siguiente ciclo
                Thread.sleep(35000);

            } catch (InterruptedException eHilo) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void detenerHilo() {
        estadoBanca = false;
        if (hilo != null) {
            hilo.interrupt();
        }
    }}
