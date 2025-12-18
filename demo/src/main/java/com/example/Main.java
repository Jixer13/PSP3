package com.example;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import com.example.models.*;


/**
 * Clase principal que gestiona la ejecución del Casino GCGM.
 * 
 * <p>Responsable de:</p>
 * <ul>
 *   <li>Inicializar todos los hilos del sistema (banca y jugadores)</li>
 *   <li>Presentar la interfaz de usuario interactiva</li>
 *   <li>Gestionar los tres juegos disponibles: La Rule, Coin Flip y Martingala</li>
 *   <li>Controlar la sincronización entre el jugador humano y los bots</li>
 * </ul>
 * 
 * <p>Estructura de datos:</p>
 * <ul>
 *   <li>{@code sc}: Scanner para entrada de usuario</li>
 *   <li>{@code juego}: Instancia de Juego para gestionar apuestas</li>
 *   <li>{@code jugadorHumano}: Jugador humano con control manual</li>
 *   <li>{@code banca}: Instancia de Banca que genera números ganadores</li>
 *   <li>{@code jugadores}: Array de Jugadores bots (Maria, Jose, Ana)</li>
 * </ul>
 * 
 * @author GAEL, CARLOS, GABRIEL y MARIANA
 * @version 2.0
 * @since 2024
 */
public class Main {

    private static Scanner sc = new Scanner(System.in);
    static Juego juego = new Juego();
    static Jugador jugadorHumano = new Jugador();
    static Banca banca = new Banca();
    static String nombre;

    // Array de jugadores bots
    static Jugador[] jugadores = {
            new Jugador("Maria", juego),
            new Jugador("Jose", juego),
            new Jugador("Ana", juego),
    };

    public static void main(String[] args) {
        registro();
        inicioHilosJugadores();
        menu();
    }

    private static void inicioHilosJugadores() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  INICIANDO HILOS DE JUGADORES Y DE LA BANCA                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");

        // Iniciamos el hilo de la banca
        banca.iniciarHiloBanca();

        // Iniciamos los hilos de los bots
        for (Jugador j : jugadores) {
            j.iniciarHiloJugador();
        }

        // Iniciamos el hilo del jugador humano
        jugadorHumano.iniciarHiloJugador();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void registro() {
        System.out.println("-------------------------------------------------");
        System.out.println("                    Bienvenidos al Casino GCGM                    ");
        System.out.println("-------------------------------------------------\n");

        System.out.println(" REGLAS GENERALES:");
        System.out.println("──────────────────────────────────────────────────────────────");
        System.out.println("• La Banca genera números del 0 al 36");
        System.out.println("• Los jugadores apuestan números del 1 al 36");
        System.out.println("• Si sale 0, todos los jugadores pierden");
        System.out.println("• Cada apuesta cuesta 10€");
        System.out.println("• Los hilos generan números continuamente en segundo plano\n");

        System.out.println("──────────────────────────────────────────────────────────────\n");

        System.out.println(" REGISTRO:");
        System.out.println("Introduce tu nombre para recibir 1000€ de saldo inicial\n");

        do {
            System.out.print("Nombre: ");
            nombre = sc.nextLine();

            if (nombre.length() <= 3) {
                System.out.println(" El nombre debe tener más de 3 caracteres\n");
            }
        } while (nombre.length() <= 3);

        System.out.println("\n * NOMBRE ACEPTADO - ¡Bienvenido " + nombre + "! *\n");

        jugadorHumano = new Jugador(nombre, juego);
        jugadorHumano.setEsHumano(true);
    }

    private static void menu() {
        int opcion = 0;

        do {
            System.out.println("-------------------------------------------------");
            System.out.println("                    MENÚ PRINCIPAL                     ");
            System.out.println("-------------------------------------------------");
            System.out.println("1. 🎲 La Rule");
            System.out.println("2. 🪙 Coin Flip");
            System.out.println("3. 📈 Martingala");
            System.out.println("4. 📊 Info jugadores");
            System.out.println("5. 🚪 Salir");
            System.out.print("\nSelecciona una opción: ");

            opcion = sc.nextInt();
            sc.nextLine();

            // CONTROL DE CONCURRENCIA: Si el jugador elige un juego
            if (opcion >= 1 && opcion <= 3) {
                if (EstadoJuego.botsApostando) {
                    System.out.println("\nLos Jugadores están ya apostando en este momento");
                    System.out.println("ESTÁS EN COLA PARA LA SIGUIENTE RONDA\n");

                    synchronized (banca) {
                        try {
                            banca.wait();
                        } catch (InterruptedException eHilo) {
                            Thread.currentThread().interrupt();
                        }
                    }

                    System.out.println("\nLa ronda ha terminado. Ahora puedes entrar al juego...\n");
                }
            }

            switch (opcion) {
                case 1:
                    System.out.println("\nHas seleccionado [ La Rule ]");
                    System.out.println("Premio por acierto: 360€\n");
                    rule();
                    break;

                case 2:
                    System.out.println("\nHas seleccionado [ Coin Flip ]");
                    System.out.println("Ganas si tu número y el de la banca son ambos pares o ambos impares\n");
                    coinflip();
                    break;

                case 3:
                    System.out.println("\nHas seleccionado [ Martingala ]");
                    System.out.println("Si pierdes, tu próxima apuesta se duplica\n");
                    martingala();
                    break;

                case 4:
                    infoJugadores();
                    break;

                case 5:
                    System.out.println("\n¿Tan pronto te vas? Tú te lo pierdes ...");
                    break;

                default:
                    System.out.println("\nOpción no válida");
            }

        } while (opcion != 5);
    }

    // ═══════════════════════════════════════════════════════════════
    // LA RULE - OPCIÓN (1)
    // ═══════════════════════════════════════════════════════════════
    private static void rule() {
        // VARIABLES DE ESTADO //
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

    // ═══════════════════════════════════════════════════════════════
    // COIN FLIP - OPCIÓN (2)
    // ═══════════════════════════════════════════════════════════════
    private static void coinflip() {
        boolean continuarJugando = true;
        
        // Inicializamos con el valor actual. Si es 0, el bucle forzará a esperar el cambio.
        int contadorGeneracionesAnterior = EstadoJuego.contadorGeneraciones;

        while (continuarJugando) {

            // Verificamos si la banca puede pagar (20€ * 4 jugadores = 80€ min)
            if (banca.getSaldo() < 80) {
                System.out.println("\nLa banca no tiene suficiente saldo para pagar los premios");
                System.out.println("   Saldo actual de la banca: " + banca.getSaldo() + "€");
                break;
            }

            // Verificamos si el jugador tiene saldo
            if (jugadorHumano.getSaldo() < 10) {
                System.out.println("\nNo tienes suficiente saldo para apostar (necesitas 10€)");
                break;
            }

            // Esperamos a que se generen nuevos numeros.
            // Eliminamos el 'if > 0' para obligar a esperar siempre una nueva generación fresca,
            // evitando leer 0s iniciales.
            
            System.out.println("\nEsperando a que los jugadores generen nuevos numeros...");
            System.out.print("Generando");

            // Bucle de espera con feedback visual
            while (EstadoJuego.contadorGeneraciones == contadorGeneracionesAnterior) {
                try {
                    Thread.sleep(1000);
                    System.out.print("."); // Feedback visual para que no parezca colgado
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println(" ¡Listos!\n");
            
            // Pausa pequeña para asegurar que todos los hilos (incluida banca) terminen de actualizar
            try { Thread.sleep(500); } catch (InterruptedException e) {}

            System.out.println("-------------------------------------------------");
            System.out.println("                    COIN FLIP                    ");
            System.out.println("-------------------------------------------------\n");

            // Capturamos las apuestas
            System.out.println("Guardando las apuestas actuales...\n");

            int[] numerosApostados = new int[jugadores.length];
            for (int i = 0; i < jugadores.length; i++) {
                numerosApostados[i] = jugadores[i].getNumeroApostado();
            }
            int numeroJugadorHumano = jugadorHumano.getNumeroApostado();

            System.out.println(" APUESTAS (Pares/Impares):");
            System.out.println("──────────────────────────────────────────────────────────────");
            
            // Helper local para mostrar par/impar
            for (int i = 0; i < jugadores.length; i++) {
                String tipo = (numerosApostados[i] % 2 == 0) ? "PAR" : "IMPAR";
                System.out.println("   • " + jugadores[i].getNombre() + " tiene el " + numerosApostados[i] + " [" + tipo + "]");
            }
            String tipoHumano = (numeroJugadorHumano % 2 == 0) ? "PAR" : "IMPAR";
            System.out.println("   • " + jugadorHumano.getNombre() + " tiene el " + numeroJugadorHumano + " [" + tipoHumano + "]");
            System.out.println("──────────────────────────────────────────────────────────────\n");

            System.out.println("🪙 La banca muestra su número...\n");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // CORRECCIÓN: Usamos el número generado por el Hilo de la Banca
            int numeroGanador = banca.getNumeroApostado();

            System.out.println("--------------------------------");
            System.out.println("     NÚMERO BANCA: " + String.format("%2d", numeroGanador));
            System.out.println("--------------------------------\n");

            boolean haHabidoGanador = false;

            System.out.println("RESULTADOS:");
            System.out.println("──────────────────────────────────────────────────────────────");

            // LÓGICA DE PREMIOS
            // Si sale 0 (la banca gana todo en CoinFlip también según reglas generales)
            if (numeroGanador == 0) {
                System.out.println("¡Ha salido 0! La banca gana todas las apuestas.");
                 // Procesar pérdidas para todos
                for (int i = 0; i < jugadores.length; i++) {
                    jugadores[i].setSaldo(jugadores[i].getSaldo() - 10);
                    banca.setSaldo(banca.getSaldo() + 10);
                }
                jugadorHumano.setSaldo(jugadorHumano.getSaldo() - 10);
                banca.setSaldo(banca.getSaldo() + 10);
                
                System.out.println(jugadorHumano.getNombre() + ", pierdes 10€.");

            } else {
                boolean bancaEsPar = (numeroGanador % 2 == 0);

                // Bots
                for (int i = 0; i < jugadores.length; i++) {
                    boolean jugadorEsPar = (numerosApostados[i] % 2 == 0);
                    
                    if (bancaEsPar == jugadorEsPar) {
                        // Gana
                        jugadores[i].setSaldo(jugadores[i].getSaldo() + 20);
                        banca.setSaldo(banca.getSaldo() - 20);
                        System.out.println("¡" + jugadores[i].getNombre() + " ACERTÓ! Gana 20€. Nuevo saldo: " + jugadores[i].getSaldo() + "€");
                        haHabidoGanador = true;
                    } else {
                        // Pierde
                        jugadores[i].setSaldo(jugadores[i].getSaldo() - 10);
                        banca.setSaldo(banca.getSaldo() + 10);
                    }
                }

                // Humano
                boolean humanoEsPar = (numeroJugadorHumano % 2 == 0);
                if (bancaEsPar == humanoEsPar) {
                    jugadorHumano.setSaldo(jugadorHumano.getSaldo() + 20);
                    banca.setSaldo(banca.getSaldo() - 20);
                    System.out.println(jugadorHumano.getNombre() + " ¡HAS GANADO! (+20€)");
                    System.out.println("Tu nuevo saldo: " + jugadorHumano.getSaldo() + "€");
                    haHabidoGanador = true;
                } else {
                    jugadorHumano.setSaldo(jugadorHumano.getSaldo() - 10);
                    banca.setSaldo(banca.getSaldo() + 10);
                    System.out.println(jugadorHumano.getNombre() + ", has fallado.");
                    System.out.println(" Tu saldo: " + jugadorHumano.getSaldo() + "€");
                }
            }

            if (!haHabidoGanador && numeroGanador != 0) {
                System.out.println("\nNadie coincidió con la banca.");
            }

            System.out.println("──────────────────────────────────────────────────────────────");
            System.out.println("Saldo final de la Banca: " + banca.getSaldo() + "€");
            System.out.println("══════════════════════════════════════════════════════════════\n");

            // Actualizamos el contador para esperar al SIGUIENTE cambio en la próxima vuelta
            contadorGeneracionesAnterior = EstadoJuego.contadorGeneraciones;

            System.out.print("¿Quieres jugar otra ronda? (s/n): ");
            String respuesta = sc.nextLine();
            continuarJugando = respuesta.equalsIgnoreCase("s");
        }
        System.out.println("\nVolviendo al menú...\n");
    }

    // ═══════════════════════════════════════════════════════════════
    // MARTINGALA - OPCIÓN (3)
    // ═══════════════════════════════════════════════════════════════
    private static void martingala() {

    boolean continuarJugando = true;

    // Apuesta actual de cada jugador
    Map<Jugador, Integer> apuestas = new HashMap<>();

    // Inicializamos apuestas a 10€
    for (Jugador j : jugadores) {
        apuestas.put(j, 10);
    }
    apuestas.put(jugadorHumano, 10);

    int contadorGeneracionesAnterior = EstadoJuego.contadorGeneraciones;

    while (continuarJugando) {

        // Comprobación de banca (360€ * nº jugadores potenciales)
        if (banca.getSaldo() < 360 * (jugadores.length + 1)) {
            System.out.println("\nLa banca no puede pagar los premios.");
            break;
        }

        // Comprobación de saldo del humano
        if (jugadorHumano.getSaldo() < apuestas.get(jugadorHumano)) {
            System.out.println("\nNo tienes saldo suficiente para continuar en Martingala.");
            break;
        }

        // Esperar nueva generación de números
        System.out.println("\nEsperando a que los jugadores generen nuevos números...");
        System.out.print("Generando");

        while (EstadoJuego.contadorGeneraciones == contadorGeneracionesAnterior) {
            try {
                Thread.sleep(1000);
                System.out.print(".");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println(" ¡Listos!\n");

        try { Thread.sleep(500); } catch (InterruptedException e) {}

        System.out.println("-------------------------------------------------");
        System.out.println("                   MARTINGALA                   ");
        System.out.println("-------------------------------------------------\n");

        // Capturamos apuestas
        int[] numerosBots = new int[jugadores.length];
        for (int i = 0; i < jugadores.length; i++) {
            numerosBots[i] = jugadores[i].getNumeroApostado();
        }
        int numeroHumano = jugadorHumano.getNumeroApostado();

        System.out.println("APUESTAS ACTUALES:");
        System.out.println("──────────────────────────────────────────────────────────────");
        for (int i = 0; i < jugadores.length; i++) {
            System.out.println("   • " + jugadores[i].getNombre() +
                    " apuesta " + apuestas.get(jugadores[i]) + "€ al " + numerosBots[i]);
        }
        System.out.println("   • " + jugadorHumano.getNombre() +
                " apuesta " + apuestas.get(jugadorHumano) + "€ al " + numeroHumano);
        System.out.println("──────────────────────────────────────────────────────────────\n");

        System.out.println("🎰 La banca gira la ruleta...\n");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int numeroGanador = banca.getNumeroApostado();

        System.out.println("--------------------------------");
        System.out.println("     NÚMERO GANADOR: " + numeroGanador);
        System.out.println("--------------------------------\n");

        boolean huboGanador = false;

        // Bots
        for (int i = 0; i < jugadores.length; i++) {
            Jugador j = jugadores[i];
            int apuestaActual = apuestas.get(j);

            if (numerosBots[i] == numeroGanador) {
                j.setSaldo(j.getSaldo() + 360);
                banca.setSaldo(banca.getSaldo() - 360);
                apuestas.put(j, 10);
                System.out.println("¡" + j.getNombre() + " GANA 360€! (reinicia apuesta)");
                huboGanador = true;
            } else {
                j.setSaldo(j.getSaldo() - apuestaActual);
                banca.setSaldo(banca.getSaldo() + apuestaActual);

                int siguiente = apuestaActual * 2;
                if (siguiente > 320 || j.getSaldo() < siguiente) {
                    apuestas.put(j, 10);
                    System.out.println(j.getNombre() + " se retira de Martingala");
                } else {
                    apuestas.put(j, siguiente);
                    System.out.println(j.getNombre() + " pierde y dobla a " + siguiente + "€");
                }
            }
        }

        // Jugador humano
        int apuestaHumano = apuestas.get(jugadorHumano);
        if (numeroHumano == numeroGanador) {
            jugadorHumano.setSaldo(jugadorHumano.getSaldo() + 360);
            banca.setSaldo(banca.getSaldo() - 360);
            apuestas.put(jugadorHumano, 10);
            System.out.println("\n🎉 HAS GANADO 360€");
            huboGanador = true;
        } else {
            jugadorHumano.setSaldo(jugadorHumano.getSaldo() - apuestaHumano);
            banca.setSaldo(banca.getSaldo() + apuestaHumano);

            int siguiente = apuestaHumano * 2;
            if (siguiente > 320 || jugadorHumano.getSaldo() < siguiente) {
                apuestas.put(jugadorHumano, 10);
                System.out.println("\nHas perdido y te retiras de Martingala");
            } else {
                apuestas.put(jugadorHumano, siguiente);
                System.out.println("\nHas perdido. Tu próxima apuesta será de " + siguiente + "€");
            }
        }

        if (!huboGanador) {
            System.out.println("\nLa banca gana la ronda");
        }

        System.out.println("\nSaldo banca: " + banca.getSaldo() + "€");
        System.out.println("Tu saldo: " + jugadorHumano.getSaldo() + "€");
        System.out.println("══════════════════════════════════════════════════════════════\n");

        contadorGeneracionesAnterior = EstadoJuego.contadorGeneraciones;

        System.out.print("¿Quieres jugar otra ronda de Martingala? (s/n): ");
        String respuesta = sc.nextLine();
        continuarJugando = respuesta.equalsIgnoreCase("s");
    }

    System.out.println("\nVolviendo al menú...\n");
}


    // ═══════════════════════════════════════════════════════════════
    // INFO JUGADORES - OPCIÓN (4)
    // ═══════════════════════════════════════════════════════════════
    private static void infoJugadores() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                   INFORMACIÓN DE JUGADORES                    ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");

        System.out.println("🤖 BOTS:");
        System.out.println("──────────────────────────────────────────────────────────────");
        for (Jugador j : jugadores) {
            System.out.println("   • " + j.getNombre() + ": " + j.getSaldo() + "€ (último número: " + j.getNumeroApostado() + ")");
        }

        System.out.println("\n👤 JUGADOR HUMANO:");
        System.out.println("──────────────────────────────────────────────────────────────");
        System.out.println("   • " + jugadorHumano.getNombre() + ": " + jugadorHumano.getSaldo() + "€");

        System.out.println("\n🏦 BANCA:");
        System.out.println("──────────────────────────────────────────────────────────────");
        System.out.println("   • Saldo: " + banca.getSaldo() + "€");

        System.out.println("\n📊 ESTADO DEL SISTEMA:");
        System.out.println("──────────────────────────────────────────────────────────────");
        System.out.println("   • Bots apostando: " + (EstadoJuego.botsApostando));
        System.out.println("══════════════════════════════════════════════════════════════\n");
    }
}
