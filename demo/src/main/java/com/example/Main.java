package com.example;

import java.util.Scanner;
import com.example.models.*;

import static com.example.models.Juego.*;


public class Main {

    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        menu(sc);

    }

    private static void menu(Scanner sc) {

        int opcion;

        System.out.println("\n Bienvenidos al Casino GCGM \n");
        System.out.println("En nuestros juegos la Banca va a generar un número del 0 al 36 , y los jugadores del 1 al 36\n" +
                "   - Si sale 0 todos los jugadores pierden.\n" +
                "   - Todos los jugadores apuestan 10.\n" +
                "   - Si los jugadores tiene el mismo número que la banca habrán ganado esa ronda." +
                "Estas son las normas generales, las normas más específicas se explicaran en cada juego.");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Seleccione el juego al que le interese jugar :");
        System.out.println("1. La Rule");
        System.out.println("2. Coin flip");
        System.out.println("3. Martingala");
        System.out.println("4. Salir");

        opcion = sc.nextInt();

        do {
            switch (opcion) {
                case 1:
                    System.out.println("Has seleccionado La Rule\n"+
                            "   - Los jugadores que ganen recibirán 360€ por cada ronda ganada.");
                    rule();
                    break;
                case 2:
                    System.out.println("Has seleccionado Coin flip\n" +
                            "   - Se comprueban los números generados para saber si son pares o impares.\n" +
                            "   - Si la banca saca un número par y el jugador también, el jugador gana.\n");
                    coinflip();
                    break;
                case 3:
                    System.out.println("Has seleccionado Martingala\n" +
                            "   - Los jugadores que ganen recibirán 360€ por cada ronda ganada.\n" +
                            "   - En el caso en el que el jugador pierda, su próxima apuesta sera el doble de la anterior.\n");
                    martingala();
                    break;
                case 4:
                    System.out.println("Has dejado que otro gane");
                    break;
            }
        } while (opcion != 4);
    }

    private static void martingala() {
        // IMPLEMENTAR LÓGICA DEL JUEGO AQUÍ
    }

    private static void coinflip() {
        // IMPLEMENTAR LÓGICA DEL JUEGO AQUÍ
    }

    private static void rule() {
        System.out.println("Iniciando juego: Ruleta");
        Juego juego = new Juego();
        Banca banca = new Banca();

        Jugador[] jugadores = {
                new Jugador("Maria", juego),
                new Jugador("Juan", juego),
                new Jugador("Pedro", juego),
                new Jugador("Daniela", juego)
        };

        Thread[] threads = new Thread[jugadores.length];
        for (int i = 0; i < jugadores.length; i++) {
            threads[i] = new Thread(jugadores[i]);
        }

        System.out.println("Hagan sus apuestas...");
        for (Thread thread : threads) {
            thread.start();
        }

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            System.err.println("Uno de los hilos fue interrumpido.");
            Thread.currentThread().interrupt();
        }

        System.out.println("No va más! La bola está girando...");

        System.out.println("Apuestas realizadas:");
        for (Jugador jugador : jugadores) {
            System.out.println("- " + jugador.getNombre() + " apostó al " + jugador.getNumeroApostado());
        }

        int numeroGanador = juego.rule();
        System.out.println("El número ganador es: " + numeroGanador);

        boolean haHabidoGanador = false;
        for (Jugador jugador : jugadores) {
            if (jugador.getNumeroApostado() == numeroGanador) {
                jugador.setSaldo(jugador.getSaldo() + 360);
                banca.setSaldo(banca.getSaldo() - 360); // La banca paga el premio
                System.out.println("¡El jugador " + jugador.getNombre() + " ha ganado! Su nuevo saldo es: " + jugador.getSaldo());
                haHabidoGanador = true;
            } else {
                banca.setSaldo(banca.getSaldo() + 10); // La banca se queda con la apuesta
            }
        }

        if (!haHabidoGanador) {
            System.out.println("No ha habido ganadores en esta ronda.");
        }

        System.out.println("Saldo final de la Banca en esta ronda: " + banca.getSaldo());
    }


}