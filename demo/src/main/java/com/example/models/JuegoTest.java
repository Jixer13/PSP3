package com.example.models;

import java.util.Map;
import java.lang.reflect.Field;

public class JuegoTest {

    public static void main(String[] args) {
        System.out.println("=== PRUEBAS DEL JUEGO ===\n");

        // Prueba 1: rule()
        System.out.println("Prueba 1 - rule():");
        Juego j1 = new Juego();
        boolean ok1 = true;
        for (int i = 0; i < 100; i++) {
            int n = j1.rule();
            if (n < 0 || n > 36) {
                System.out.println("  ERROR: " + n + " fuera de rango");
                ok1 = false;
                break;
            }
        }
        System.out.println(ok1 ? "  ✓ OK" : "  ✗ FALLO");

        // Prueba 2: coinFlipRule()
        System.out.println("\nPrueba 2 - coinFlipRule():");
        Juego j2 = new Juego();
        boolean ok2 = true;
        for (int i = 0; i < 100; i++) {
            int n = j2.coinFlipRule();
            if (n < 0 || n > 36) {
                System.out.println("  ERROR: " + n + " fuera de rango");
                ok2 = false;
                break;
            }
        }
        System.out.println(ok2 ? "  ✓ OK" : "  ✗ FALLO");

        // Prueba 3: apostarRule()
        System.out.println("\nPrueba 3 - apostarRule():");
        Juego j3 = new Juego();
        j3.apostarRule("Jugador1", 25);
        Map<String, Integer> map3 = j3.getApuestasRule();
        boolean ok3 = map3.containsKey("Jugador1") && map3.get("Jugador1") == 25;
        System.out.println(ok3 ? "  ✓ OK" : "  ✗ FALLO");

        // Prueba 4: apostarCoinflip()
        System.out.println("\nPrueba 4 - apostarCoinflip():");
        Juego j4 = new Juego();
        j4.apostarCoinflip("Jugador1", 10);
        boolean ok4 = false;
        try {
            Field f = Juego.class.getDeclaredField("apuestasCoinflip");
            f.setAccessible(true);
            Map<String, Integer> map4 = (Map<String, Integer>) f.get(j4);
            ok4 = map4.containsKey("Jugador1") && map4.get("Jugador1") == 10;
        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
        System.out.println(ok4 ? "  ✓ OK" : "  ✗ FALLO");

        System.out.println("\n=== FIN PRUEBAS ===");
    }
}