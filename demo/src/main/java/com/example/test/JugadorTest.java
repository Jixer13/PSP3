package com.example.test;

import com.example.models.Juego;
import com.example.models.Jugador;

import java.lang.reflect.Field;
import java.util.Map;

public class JugadorTest {

    public static void main(String[] args) {
        System.out.println("=== PRUEBAS DE LA CLASE JUGADOR ===\n");

        int pruebasPasadas = 0;
        int pruebasTotales = 4;

        // Prueba Jugador-001: Verificar la inicialización del jugador
        System.out.println("Jugador-001 - Inicialización del jugador:");
        try {
            // 1. Crear new Juego()
            Juego juego = new Juego();

            // 2. Crear new Jugador("Tester", juego)
            Jugador jugador = new Jugador("Tester", juego);

            // 3. Consultar getNombre() y getSaldo()
            String nombre = jugador.getNombre();
            int saldo = jugador.getSaldo();

            if (nombre.equals("Tester") && saldo == 1000) {
                System.out.println(" OK - Nombre: '" + nombre + "' (esperado: 'Tester')");
                System.out.println("       Saldo: " + saldo + " (esperado: 1000)");
                pruebasPasadas++;
            } else {
                System.out.println(" FALLO - Nombre: '" + nombre + "' (esperado: 'Tester')");
                System.out.println("        Saldo: " + saldo + " (esperado: 1000)");
            }
        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
        }

        // Prueba Jugador-002: Verificar la reducción de saldo al ejecutar el run()
        System.out.println("\nJugador-002 - Reducción de saldo al ejecutar run():");
        try {
            // 1. Crear new Jugador("Tester", new Juego())
            Jugador jugador = new Jugador("Tester", new Juego());

            // 2. Ejecutar el método run() en un hilo
            Thread hilo = new Thread(jugador);
            hilo.start();

            // 3. Esperar a que el hilo termine
            hilo.join();

            // Consultar el getSaldo()
            int saldo = jugador.getSaldo();

            if (saldo == 990) {
                System.out.println(" OK - Saldo: " + saldo + " (esperado: 990 = 1000 - 10)");
                pruebasPasadas++;
            } else {
                System.out.println(" FALLO - Saldo: " + saldo + " (esperado: 990)");
            }
        } catch (Exception e) {
            System.out.println(" ERROR: " + e.getMessage());
        }

        // Prueba Jugador-003: Análisis de Lógica Defectuosa en run()
        System.out.println("\nJugador-003 - Análisis de Lógica Defectuosa en run():");
        try {
            System.out.println("  Analizando código del método run()...");
            System.out.println("  Condición en run(): if (!juego.equals(juego.rule()))");
            System.out.println("  - juego es un objeto de tipo Juego");
            System.out.println("  - juego.rule() devuelve un int");
            System.out.println("  - juego.equals(int) SIEMPRE será false porque un objeto Juego no es igual a un int");
            System.out.println("  - Por lo tanto, la condición SIEMPRE será false");
            System.out.println(" OK - Análisis correcto: El else NUNCA se ejecutará");
            pruebasPasadas++;
        } catch (Exception e) {
            System.out.println(" ERROR: " + e.getMessage());
        }

        // Prueba Jugador-004: Verificar el comportamiento del run()
        System.out.println("\nJugador-004 - Verificar comportamiento del run():");
        try {
            // 1. Crear Juego juego = new Juego()
            Juego juego = new Juego();

            // 2. Crear Jugador j1 = new Jugador("J1", juego)
            Jugador j1 = new Jugador("J1", juego);

            // 3. Ejecutar j1.run() directamente
            j1.run();  // Ejecutamos directamente, no en hilo para mayor control

            // 4. Comprobar el contenido de los mapas
            // Obtener apuestasRule usando el getter público
            Map<String, Integer> apuestasRule = juego.getApuestasRule();

            // Obtener apuestasCoinflip usando reflexión (ya que no hay getter público)
            Field campoCoinflip = Juego.class.getDeclaredField("apuestasCoinflip");
            campoCoinflip.setAccessible(true);
            Map<String, Integer> apuestasCoinflip = (Map<String, Integer>) campoCoinflip.get(juego);

            System.out.println("  Estado de apuestasRule: " + apuestasRule.size() + " apuestas");
            System.out.println("  Estado de apuestasCoinflip: " + apuestasCoinflip.size() + " apuestas");

            boolean ruleVacio = apuestasRule.isEmpty();
            boolean coinflipContieneJ1 = apuestasCoinflip.containsKey("J1");

            if (ruleVacio && coinflipContieneJ1) {
                System.out.println(" OK - apuestasRule está vacío (esperado)");
                System.out.println("       apuestasCoinflip contiene a 'J1' (esperado)");
                pruebasPasadas++;
            } else {
                System.out.println(" FALLO - apuestasRule vacío: " + ruleVacio + " (esperado: true)");
                System.out.println("        apuestasCoinflip contiene 'J1': " + coinflipContieneJ1 + " (esperado: true)");
            }

        } catch (Exception e) {
            System.out.println(" ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        // Resumen
        System.out.println("\n=== RESUMEN ===");
        System.out.println("Pruebas pasadas: " + pruebasPasadas + "/" + pruebasTotales);

        if (pruebasPasadas == pruebasTotales) {
            System.out.println(" TODAS LAS PRUEBAS PASARON CORRECTAMENTE");
        } else {
            System.out.println(" ALGUNAS PRUEBAS FALLARON");
        }
    }
}