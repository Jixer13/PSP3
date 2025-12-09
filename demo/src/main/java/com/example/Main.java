package com.example;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import com.example.models.*;


public class Main {

    private static Scanner sc = new Scanner(System.in);

    static Juego juego = new Juego();
    static Banca banca = new Banca();

    static Jugador[] jugadores = {
            new Jugador("Maria", juego),
            new Jugador("Juan", juego),
            new Jugador("Pedro", juego),
            new Jugador("Daniela", juego)
    };


    public static void main(String[] args) {

        menu(sc);

    }

    private static void menu(Scanner sc) {

        int opcion = 0;

        do {
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
            System.out.println("4. Info Jugadores");
            System.out.println("5. Salir");

            opcion = sc.nextInt();
            sc.nextLine(); // Consume the leftover newline

            switch (opcion) {
                case 1:
                    System.out.println("Has seleccionado La Rule\n" +
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
                    System.out.println("Información Dinero Jugadores\n" +
                            "Aquí tienes el nombre de los jugadores y el dinero que les queda");
                    for (int i = 0; i < jugadores.length; i++) {
                        System.out.println(jugadores[i].getNombre() + ": " + jugadores[i].getSaldo() + "€");
                    }


                    break;
                case 5:
                    System.out.println("Has dejado que otro gane");
                    break;
            }

        } while (opcion != 5);
    }

    private static void martingala() {
        boolean continuar = true;
        Map<Jugador, Integer> apuestas = new HashMap<>();
        List<Jugador> activos = new ArrayList<>(Arrays.asList(jugadores));

        for (Jugador j : jugadores) {
            apuestas.put(j, 10);
        }

        // NO crear un nuevo Scanner aquí, usar uno global o pasar como parámetro
        Scanner sc = new Scanner(System.in);

        do {
            if (banca.getSaldo() < 360 * 4) {
                System.out.println("La banca no se puede permitir pagar");
                continuar = false;
            } else if (activos.isEmpty()) {
                System.out.println("Todos se han retirado");
                continuar = false;
            } else {
                System.out.println("\n=== Iniciando nueva ronda: Martingala ===");

                System.out.println("Hagan sus apuestas...");

                for (Jugador j : activos) {
                    j.setNumeroApostado(ThreadLocalRandom.current().nextInt(0, 37));
                }

                System.out.println("No va más! La bola está girando...");

                System.out.println("Apuestas realizadas:");
                for (Jugador j : activos) {
                    int apuestaActual = apuestas.get(j);
                    System.out.println("- " + j.getNombre() + " apuesta " +
                            apuestaActual + "€ al número " + j.getNumeroApostado());

                    j.setSaldo(j.getSaldo() - apuestaActual);
                }

                int ganador = ThreadLocalRandom.current().nextInt(0, 37);
                System.out.println("Número ganador: " + ganador);

                List<Jugador> retirados = new ArrayList<>();
                boolean huboGanador = false;

                for (Jugador j : activos) {
                    int apuestaActual = apuestas.get(j);

                    if (j.getNumeroApostado() == ganador) {
                        j.setSaldo(j.getSaldo() + 360);
                        banca.setSaldo(banca.getSaldo() - 360);
                        apuestas.put(j, 10);
                        System.out.println(j.getNombre() + " gana 360€ (acertó el " + j.getNumeroApostado() + ")");
                        huboGanador = true;
                    } else {
                        int siguienteApuesta = apuestaActual * 2;

                        if (siguienteApuesta > 320) {
                            System.out.println(j.getNombre() + " se retira (límite 320€)");
                            retirados.add(j);
                        } else if (j.getSaldo() < siguienteApuesta) {
                            System.out.println(j.getNombre() + " se retira (sin saldo)");
                            retirados.add(j);
                        } else {
                            apuestas.put(j, siguienteApuesta);
                            System.out.println(j.getNombre() + " dobla a " + siguienteApuesta + "€");
                        }

                        banca.setSaldo(banca.getSaldo() + apuestaActual);
                    }
                }

                activos.removeAll(retirados);

                if (!huboGanador) {
                    System.out.println("No hubo ganadores");
                }

                System.out.println("Banca: " + banca.getSaldo() + "€");
                System.out.println("Jugadores activos: " + activos.size());

                if (!activos.isEmpty()) {
                    System.out.print("\n¿Otra ronda? (s/n): ");
                    String respuesta = sc.nextLine().trim();

                    if (respuesta.equalsIgnoreCase("n")) {
                        continuar = false;
                    } else if (!respuesta.equalsIgnoreCase("s")) {
                        System.out.println("Respuesta no reconocida, continuando...");
                    }
                } else {
                    continuar = false;
                }
            }
        } while (continuar);

        // NO cerrar el Scanner aquí
        // sc.close();

        System.out.println("\n=== Juego terminado ===");
        System.out.println("Resumen final:");
        System.out.println("Banca: " + banca.getSaldo() + "€");
        for (Jugador j : jugadores) {
            System.out.println(j.getNombre() + ": " + j.getSaldo() + "€");
        }
    }

    private static void coinflip() {
        boolean bool = true;

        do {
            if (banca.getSaldo() < (20 * 4)) {
                System.out.println("La banca no se puede permitir pagar");
                bool = false;
            } else {
                System.out.println("Iniciando juego: Par o Impar");

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
                    // Aquí podrías añadir también si el jugador apostó a par o impar para verlo claro
                    String ap = (jugador.getNumeroApostado() % 2 == 0) ? "PAR" : "IMPAR";
                    System.out.println("- " + jugador.getNombre() + " apostó al " + jugador.getNumeroApostado() + " (" + ap + ")");
                }

                // --- GENERACIÓN DEL NÚMERO ---
                int numeroGanador = ThreadLocalRandom.current().nextInt(0, 37);
                System.out.println("-----------------------------------");
                System.out.println("El número ganador es: " + numeroGanador);

                // --- MOSTRAR SI ES PAR O IMPAR (NUEVO) ---
                if (numeroGanador == 0) {
                    System.out.println("¡Ha salido el CERO! (La banca gana)");
                } else if (numeroGanador % 2 == 0) {
                    System.out.println("El resultado es: PAR");
                } else {
                    System.out.println("El resultado es: IMPAR");
                }
                System.out.println("-----------------------------------");

                boolean haHabidoGanador = false;

                // --- COMPROBACIÓN DE GANADORES ---
                for (Jugador jugador : jugadores) {
                    if (numeroGanador != 0) {
                        // Si el número y la apuesta tienen el mismo resto al dividir por 2, coinciden en paridad
                        if (numeroGanador % 2 == jugador.getNumeroApostado() % 2) {

                            jugador.setSaldo(jugador.getSaldo() + 20);
                            banca.setSaldo(banca.getSaldo() - 20);
                            System.out.println("¡El jugador " + jugador.getNombre() + " ha ganado! Su nuevo saldo es: " + jugador.getSaldo() + "€");
                            haHabidoGanador = true;

                        } else {
                            // Perdió
                            banca.setSaldo(banca.getSaldo() + 10);
                            // Opcional: mostrar que perdió
                            // System.out.println("El jugador " + jugador.getNombre() + " pierde.");
                        }
                    } else {
                        // Si sale 0, todos pierden
                        banca.setSaldo(banca.getSaldo() + 10);
                    }
                }

                if (!haHabidoGanador && numeroGanador != 0) {
                    System.out.println("No ha habido ganadores en esta ronda.");
                } else if (numeroGanador == 0) {
                    System.out.println("Al salir 0, todos los jugadores pierden esta ronda.");
                }

                System.out.println("Saldo final de la Banca en esta ronda: " + banca.getSaldo() + "€");
                bool = false;
            }

        } while (bool);
    }

private static void rule ()
        { // meter el metodo dentro de un do while, que funcione mientras la banca se pueda permitir pagar
            boolean bool = true;
            do {
                if (banca.getSaldo() < (360 * 4)) {
                    System.out.println("La banca no se puede permitir pagar");
                    bool = false;
                } else {
                    System.out.println("Iniciando juego: Ruleta");


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

                    int numeroGanador = ThreadLocalRandom.current().nextInt(0, 37);
                    System.out.println("El número ganador es: " + numeroGanador);

                    boolean haHabidoGanador = false;
                    for (Jugador jugador : jugadores) {
                        if (jugador.getNumeroApostado() == numeroGanador) {
                            jugador.setSaldo(jugador.getSaldo() + 360);
                            banca.setSaldo(banca.getSaldo() - 360); // La banca paga el premio
                            System.out.println("¡El jugador " + jugador.getNombre() + " ha ganado! Su nuevo saldo es: " + jugador.getSaldo() + "€");
                              haHabidoGanador = true;
                          } else {
                              banca.setSaldo(banca.getSaldo() + 10); // La banca se queda con la apuesta
                          }
                      }

                      if (!haHabidoGanador) {
                          System.out.println("No ha habido ganadores en esta ronda.");
                      }

                      System.out.println("Saldo final de la Banca en esta ronda: " + banca.getSaldo() + "€");
                      bool = false;
                  }
              } while (bool);
          }
        }
