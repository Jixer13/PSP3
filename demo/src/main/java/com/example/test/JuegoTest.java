package com.example.test;

import com.example.models.Juego;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;

public class JuegoTest {

    public static void main(String[] args) {
        System.out.println("=== PRUEBAS DE LA CLASE JUEGO ===\n");

        int pruebasPasadas = 0;
        int pruebasTotales = 5;

        // Juego-001: Verificar que rule() genera números en el rango [0, 36]
        System.out.println("Juego-001 - Verificar que rule() genera números en el rango [0, 36]:");
        try {
            // 1. Crear new Juego()
            Juego juego = new Juego();

            // 2. Llamar a rule() 15 veces en un bucle
            boolean todosEnRango = true;
            for (int i = 0; i < 15; i++) {
                int numero = juego.rule();
                if (numero < 0 || numero > 36) {
                    todosEnRango = false;
                    System.out.println("  ERROR: Número fuera de rango: " + numero);
                    break;
                }
            }

            // 3. Todos los números deben estar entre 0 y 36 (inclusive)
            if (todosEnRango) {
                System.out.println("  OK - Todos los números generados están en el rango [0, 36]");
                pruebasPasadas++;
            } else {
                System.out.println("  FALLO - Algunos números están fuera del rango [0, 36]");
            }
        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
        }

        // Juego-002: Verificar que coinFlipRule() genera números en el rango [0, 36]
        System.out.println("\nJuego-002 - Verificar que coinFlipRule() genera números en el rango [0, 36]:");
        try {
            // 1. Crear new Juego()
            Juego juego = new Juego();

            // 2. Llamar a coinFlipRule() 15 veces
            boolean todosEnRango = true;
            for (int i = 0; i < 15; i++) {
                int numero = juego.coinFlipRule();
                if (numero < 0 || numero > 36) {
                    todosEnRango = false;
                    System.out.println("  ERROR: Número fuera de rango: " + numero);
                    break;
                }
            }

            // 3. Todos los números deben estar entre 0 y 36 (inclusive)
            if (todosEnRango) {
                System.out.println("  OK - Todos los números generados están en el rango [0, 36]");
                pruebasPasadas++;
            } else {
                System.out.println("  FALLO - Algunos números están fuera del rango [0, 36]");
            }
        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
        }

        // Juego-003: Verificar que apostarRule() registra una apuesta
        System.out.println("\nJuego-003 - Verificar que apostarRule() registra una apuesta:");
        try {
            // 1. Crear new Juego()
            Juego juego = new Juego();

            // 2. Llamar a apostarRule("Jugador1", 25)
            juego.apostarRule("Jugador1", 25);

            // 3. Obtener el mapa con getApuestasRule()
            Map<String, Integer> apuestas = juego.getApuestasRule();

            // 4. El mapa debe contener la clave "Jugador1" con el valor 25
            if (apuestas.containsKey("Jugador1") && apuestas.get("Jugador1") == 25) {
                System.out.println("  OK - Apuesta registrada correctamente");
                System.out.println("       'Jugador1' → " + apuestas.get("Jugador1") + " (esperado: 25)");
                pruebasPasadas++;
            } else {
                System.out.println("  FALLO - La apuesta no se registró correctamente");
                System.out.println("       Contenido del mapa: " + apuestas);
            }
        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
        }

        // Juego-004: Verificar que apostarCoinflip() registra una apuesta
        System.out.println("\nJuego-004 - Verificar que apostarCoinflip() registra una apuesta:");
        try {
            // 1. Crear new Juego()
            Juego juego = new Juego();

            // 2. Llamar a apostarCoinflip("Jugador1", 10)
            juego.apostarCoinflip("Jugador1", 10);

            // 3. Acceder al mapa apuestasCoinflip (mediante reflexión si es necesario)
            Field campoApuestasCoinflip = Juego.class.getDeclaredField("apuestasCoinflip");
            campoApuestasCoinflip.setAccessible(true);
            Map<String, Integer> apuestasCoinflip = (Map<String, Integer>) campoApuestasCoinflip.get(juego);

            // 4. El mapa debe contener la clave "Jugador1" con el valor 10
            if (apuestasCoinflip.containsKey("Jugador1") && apuestasCoinflip.get("Jugador1") == 10) {
                System.out.println("  OK - Apuesta de coinflip registrada correctamente");
                System.out.println("       'Jugador1' → " + apuestasCoinflip.get("Jugador1") + " (esperado: 10)");
                pruebasPasadas++;
            } else {
                System.out.println("  FALLO - La apuesta de coinflip no se registró correctamente");
                System.out.println("       Contenido del mapa: " + apuestasCoinflip);
            }
        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
        }

        // Juego-005: Verificar que el constructor inicializa la CyclicBarrier
        System.out.println("\nJuego-005 - Verificar que el constructor inicializa la CyclicBarrier:");
        try {
            // 1. Crear new Juego(3)
            Juego juego = new Juego(3);

            // 2. Obtener la barrera con getBarrera()
            CyclicBarrier barrera = juego.getBarrera();

            // 3. La barrera no debe ser nula y debe tener 3 partes (getParties())
            if (barrera != null && barrera.getParties() == 3) {
                System.out.println("  OK - CyclicBarrier inicializada correctamente");
                System.out.println("       Barrera no es nula: true");
                System.out.println("       Número de partes: " + barrera.getParties() + " (esperado: 3)");
                pruebasPasadas++;
            } else {
                System.out.println("  FALLO - CyclicBarrier no se inicializó correctamente");
                if (barrera == null) {
                    System.out.println("       La barrera es nula (esperado: no nula)");
                } else {
                    System.out.println("       Número de partes: " + barrera.getParties() + " (esperado: 3)");
                }
            }
        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
        }

        // Resumen
        System.out.println("\n=== RESUMEN ===");
        System.out.println("Pruebas pasadas: " + pruebasPasadas + "/" + pruebasTotales);

        if (pruebasPasadas == pruebasTotales) {
            System.out.println("✓ TODAS LAS PRUEBAS PASARON CORRECTAMENTE");
        } else {
            System.out.println("✗ " + (pruebasTotales - pruebasPasadas) + " PRUEBA(S) FALLARON");
        }
    }
}
