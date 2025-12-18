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
        {  // VARIABLES DE ESTADO //
        boolean continuarJugando = true;
        boolean haHabidoGanador = false;
        int contadorGeneracionesAnterior = EstadoJuego.contadorGeneraciones;

        while (continuarJugando) {

            // VERIFICAMOS QUE LA BANCA PUEDA PAGAR A TODOS LOS JUGADORES
            if (banca.getSaldo() < (360 * 4)) {
                System.out.println("\nLa banca no tiene suficiente saldo para pagar los premios");
                System.out.println("   Saldo actual de la banca: " + banca.getSaldo() + "€");
                break;
            }

            // VERIFICAMOS SI EL JUGADOR TIENE SALDO
            if (jugadorHumano.getSaldo() < 10) {
                System.out.println("\nNo tienes suficiente saldo para apostar (necesitas 10€)");
                break;
            }

            // ESPERAMOS A QUE SE GENEREN NUEVOS NÚMEROS SI YA SE HA JUGADO UNA RONDA
            if (contadorGeneracionesAnterior > 0) {
                System.out.println("\nEsperando a que los jugadores generen nuevos números...");

                // ESPERAMOS HASTA QUE TENGAMOS UNA GENERACIÓN DE NÚMEROS //
                while (EstadoJuego.contadorGeneraciones == contadorGeneracionesAnterior) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                System.out.println("Nuevos números generados. Iniciando ronda...\n");
            }

            System.out.println("-------------------------------------------------");
            System.out.println("                     LA RULE                    ");
            System.out.println("-------------------------------------------------\n");

            // GUARDAMOS LOS NÚMEROS QUE HAN SIDO GENERADOS POR LOS HILOS
            System.out.println("Guardando las apuestas actuales...\n");

            // GUARDAMOS LOS NÚMEROS APOSTADOS
            int[] numerosApostados = new int[jugadores.length];
            for (int i = 0; i < jugadores.length; i++) {
                numerosApostados[i] = jugadores[i].getNumeroApostado();
            }

            int numeroJugadorHumano = jugadorHumano.getNumeroApostado();

            System.out.println(" APUESTAS GUARDADAS :");
            System.out.println("──────────────────────────────────────────────────────────────");
            for (int i = 0; i < jugadores.length; i++) {
                System.out.println("   • " + jugadores[i].getNombre() + " apostó al número " + numerosApostados[i]);
            }
            System.out.println("   • " + jugadorHumano.getNombre() + " apostó al número " + numeroJugadorHumano);
            System.out.println("──────────────────────────────────────────────────────────────\n");

            System.out.println("🎰 La banca está girando la ruleta...\n");

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // LA BANCA GENERA UN NÚMERO GANADOR
            int numeroGanador = ThreadLocalRandom.current().nextInt(0, 37); // 0 al 36

            System.out.println("--------------------------------");
            System.out.println("     NÚMERO GANADOR: " + String.format("%2d", numeroGanador));
            System.out.println("--------------------------------\n");


            System.out.println("RESULTADOS:");
            System.out.println("──────────────────────────────────────────────────────────────");

            // SI SALE 0 LA BANCA GANA
            if (numeroGanador == 0) {
                System.out.println("\nHA SALIDO EL 0 - LA BANCA GANA \n");

                // TODOS LOS JUGADORES PIERDEN SU DINERO (BOTS)
                for (int i = 0; i < jugadores.length; i++) {
                    jugadores[i].setSaldo(jugadores[i].getSaldo() - 10);
                    banca.setSaldo(banca.getSaldo() + 10);
                }

                // EL JUGADOR HUMANO TAMBIÉN PIERDE //
                jugadorHumano.setSaldo(jugadorHumano.getSaldo() - 10);
                banca.setSaldo(banca.getSaldo() + 10);

                System.out.println("Todos los jugadores pierden sus apuestas");
                System.out.println(jugadorHumano.getNombre() + ", has perdido 10€");
                System.out.println(" Tu saldo: " + jugadorHumano.getSaldo() + "€");

            } else {
                // MIRAMOS QUE HAN SACADO LOS BOTS
                for (int i = 0; i < jugadores.length; i++) {
                    // SI EL NÚMERO GANADOR AGREGAMOS EL SALDO
                    if (numerosApostados[i] == numeroGanador) {
                        jugadores[i].setSaldo(jugadores[i].getSaldo() + 360);
                        // Y SE LO QUITAMOS A LA BANCA //
                        banca.setSaldo(banca.getSaldo() - 360);
                        System.out.println("¡" + jugadores[i].getNombre() + " ha ganado 360€! Nuevo saldo: " + jugadores[i].getSaldo() + "€");
                        // INDICAMOS QUE HAY GANADOR
                        haHabidoGanador = true;
                    } else {
                        jugadores[i].setSaldo(jugadores[i].getSaldo() - 10);
                        banca.setSaldo(banca.getSaldo() + 10);
                    }
                }

                // COMPROBAMOS EL NÚMERO DEL JUGADOR (IGUAL QUE LOS BOTS PERO SOLO SE COMPRUEBA 1 VEZ)
                if (numeroJugadorHumano == numeroGanador) {
                    // LE AGREGAMOS EL DINERO AL JUGADOR
                    jugadorHumano.setSaldo(jugadorHumano.getSaldo() + 360);
                    // SE LO QUITAMOS A LA BANCA
                    banca.setSaldo(banca.getSaldo() - 360);
                    System.out.println(jugadorHumano.getNombre() + " HAS GANADO 360€");
                    System.out.println("Tu nuevo saldo: " + jugadorHumano.getSaldo() + "€");
                    // INDICAMOS QUE HA HABIDO GANADOR
                    haHabidoGanador = true;
                } else {
                    // SI PIERDE SE LE RESTA LO QUE CUESTA LA PARTIDA
                    jugadorHumano.setSaldo(jugadorHumano.getSaldo() - 10);
                    // Y AGREGAMOS A LA BANCA EL DINERO QUE HA PERDIDO EL JUGADOR
                    banca.setSaldo(banca.getSaldo() + 10);
                    System.out.println(jugadorHumano.getNombre() + ", no has ganado esta vez");
                    System.out.println(" Tu saldo: " + jugadorHumano.getSaldo() + "€");
                }

                // SI NO HAY GANADORES SACAMOS POR PANTALLA ESTÉ MENSAJE
                if (!haHabidoGanador) {
                    System.out.println("\nLa banca se queda con todas las apuestas");
                }

            }
            System.out.println("──────────────────────────────────────────────────────────────");
            System.out.println("Saldo final de la Banca: " + banca.getSaldo() + "€");
            System.out.println("══════════════════════════════════════════════════════════════\n");

            // ACTUALIZAMOS EL CONTADOR PARA LA PRÓXIMA RONDA
            contadorGeneracionesAnterior = EstadoJuego.contadorGeneraciones;

            // PREGUNTAMOS AL JUGADOR SI QUIERE VOLVER A JUGAR
            System.out.print("¿Quieres jugar otra ronda? (s/n): ");
            String respuesta = sc.nextLine();
            continuarJugando = respuesta.equalsIgnoreCase("s");
        }

        System.out.println("\nHas dejado que otro gane... \n");
        }
