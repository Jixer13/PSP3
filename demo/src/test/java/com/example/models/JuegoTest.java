package com.example.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;

class JuegoTest {

    // Juego-001: Verificar que rule() genera números en el rango [0, 36].
    @Test
    void testRuleGeneraNumeosEnRango() {
        System.out.println("\nComprobar si en el método RULE se sale del rango [0, 36] - [ 100 pruebas ]");
        // 1. Crear new Juego()
        Juego juego = new Juego();

        // 2. Llamar a rule() 15 veces en un bucle
        for (int i = 0; i < 100; i++) {
            int numero = juego.rule();

            // 3. Todos los números deben estar entre 0 y 36 (inclusive)
            assertTrue(numero >= 0 && numero <= 36,
                    "El método RULE generó un número fuera del rango [0, 36]: " + numero);
        }
        System.out.println("Test Juego-001 = OK");
    }

    // Juego-002: Verificar que coinFlipRule() genera números en el rango [0, 36].
    @Test
    void testCoinFlipRuleGeneraNumerosEnRango() {
        System.out.println("\nComprobar si en el método COINFLIP se sale del rango [0 ,36] - [ 100 pruebas ]");
        // 1. Crear new Juego()
        Juego juego = new Juego();

        // 2. Llamar a coinFlipRule() 15 veces
        for (int i = 0; i < 100; i++) {
            int numero = juego.coinFlipRule();

            // 3. Todos los números deben estar entre 0 y 36 (inclusive)
            assertTrue(numero >= 0 && numero <= 36,
                    "El método COINFLIP generó un número fuera del rango [0, 36]: " + numero);
        }
        System.out.println("Test Juego-002 = OK");
    }

    // Juego-003: Verificar que apostarRule() registra una apuesta.
    @Test
    void testApostarRuleRegistraApuesta() {
        System.out.println("Comprobación de registro de apuesta en el método ApostarRule");
        // 1. Crear new Juego()
        Juego juego = new Juego();

        // 2. Llamar a apostarRule("Jugador1", 25)
        System.out.println("Registrando apuesta: apostarRule('Jugador1', 25)");
        juego.apostarRule("Jugador1", 25);

        // 3. Obtener el mapa con getApuestasRule()
        Map<String, Integer> apuestas = juego.getApuestasRule();
        System.out.println("Mapa de apuestas obtenido: " + apuestas);

        // El mapa debe contener la clave "Jugador1" con el valor 25
        assertTrue(apuestas.containsKey("Jugador1"),
                "El mapa debe contener la clave 'Jugador1'");
        assertEquals(25, apuestas.get("Jugador1"),
                "El valor debe ser 25");

        System.out.println("Test Juego-003 = OK");
    }

    // Juego-004: Verificar que apostarCoinflip() registra una apuesta.
    @Test
    void testApostarCoinflipRegistraApuesta() throws NoSuchFieldException, IllegalAccessException {
        System.out.println("Comprobación de registro de apuesta en el método ApostarCoinflip");
        // 1. Crear new Juego()
        Juego juego = new Juego();

        // 2. Llamar a apostarCoinflip("Jugador1", 10)
        System.out.println("Registrando apuesta: apostarCoinflip('Jugador1', 10)");
        juego.apostarCoinflip("Jugador1", 10);

        // 3. Acceder al mapa apuestasCoinflip (mediante reflexión si es necesario)
        Field field = Juego.class.getDeclaredField("apuestasCoinflip");
        field.setAccessible(true);
        Map<String, Integer> apuestasCoinflip = (Map<String, Integer>) field.get(juego);
        System.out.println("Mapa de apuestas coinflip obtenido: " + apuestasCoinflip);

        // El mapa debe contener la clave "Jugador1" con el valor 10
        assertTrue(apuestasCoinflip.containsKey("Jugador1"),
                "El mapa debe contener la clave 'Jugador1'");
        assertEquals(10, apuestasCoinflip.get("Jugador1"),
                "El valor debe ser 10");
        System.out.println("Test Juego-004 = OK");
    }

    // Juego-005: Verificar que el constructor inicializa la CyclicBarrier.
    @Test
    void testConstructorInicializaCyclicBarrier() {
        System.out.println("\nVerificación que el constructor inicializa la CyclicBarrier");
        // 1. Crear new Juego(3)
        System.out.println("Creando Juego(3)...");
        Juego juego = new Juego(3);

        // 2. Obtener la barrera con getBarrera()
        CyclicBarrier barrera = juego.getBarrera();
        System.out.println("Barrera obtenida: " + barrera);

        // La barrera no debe ser NULL
        assertNotNull(barrera, "La barrera no puede ser NULL");
        System.out.println("Barrera no es nula = OK");

        // 3. La barrera debe tener 3 partes (getParties())
        int parties = barrera.getParties();
        System.out.println("Número de partes en la barrera: " + parties);
        assertEquals(3, parties,
                "La barrera debe tener 3 partes");
        System.out.println("Test Juego-005 = OK");
    }
}
