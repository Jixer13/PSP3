package com.example.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;
import java.util.Map;

class JugadorTest {

    // Jugador-001: Verificar la inicialización del constructor.
    @Test
    void testInicializacionConstructor() {
        Juego juego = new Juego();
        Jugador jugador = new Jugador("Tester", juego);

        assertEquals("Tester", jugador.getNombre(), "El nombre debe ser 'Tester'");
        assertEquals(1000, jugador.getSaldo(), "El saldo debe ser 1000");
    }

    // Jugador-002: Verificar que set/getNumeroApostado funciona.
    @Test
    void testSetGetNumeroApostado() {
        Juego juego = new Juego();
        Jugador jugador = new Jugador("Tester", juego);

        jugador.setNumeroApostado(15);
        assertEquals(15, jugador.getNumeroApostado(), "El valor devuelto debe ser 15");
    }

    // Jugador-003: Verificar que el hilo se inicia y genera un número.
    @Test
    void testHiloIniciaYGeneraNumero() throws InterruptedException {
        Juego juego = new Juego();
        Jugador jugador = new Jugador("Tester", juego);

        jugador.iniciarHiloJugador();

        // Esperamos 3 segundos (el hilo espera 2s antes de generar)
        Thread.sleep(3000);

        int numero = jugador.getNumeroApostado();
        assertTrue(numero >= 1 && numero <= 36, "El número debe estar en el rango [1, 36], fue: " + numero);

        jugador.detenerHilo();
    }

    // Jugador-004: Verificar que detenerHilo funciona.
    @Test
    void testDetenerHilo() throws InterruptedException {
        Juego juego = new Juego();
        Jugador jugador = new Jugador("Tester", juego);

        jugador.iniciarHiloJugador();
        // Esperar 3 seg para que genere el primer número
        Thread.sleep(3000);

        int valorGuardado = jugador.getNumeroApostado();
        assertTrue(valorGuardado >= 1 && valorGuardado <= 36, "Debe haber generado un número");

        jugador.detenerHilo();

        // Esperar 12 segundos (suficiente para que hubiese generado otro si siguiera
        // vivo, loop es ~10s)
        Thread.sleep(12000);

        int valorDespues = jugador.getNumeroApostado();
        assertEquals(valorGuardado, valorDespues,
                "El valor debe ser el mismo que el guardado, el hilo debería haberse detenido");
    }

    // Jugador-005: Verificar la reducción de saldo al ejecutar run().
    @Test
    void testReduccionSaldoRun() {
        Juego juego = new Juego();
        Jugador jugador = new Jugador("Tester", juego);
        // Saldo inicial 1000

        // Ejecutamos run() directamente (simulando la acción del hilo sin esperas)
        // Nota: run() llama a apostar, que sincroniza.
        jugador.run();

        assertEquals(990, jugador.getSaldo(), "El saldo debe ser 990 (1000 - 10)");
    }

    // Jugador-006: Análisis de la lógica en run() - FIXED
    @Test
    void testLogicaRunCondition() throws NoSuchFieldException, IllegalAccessException {
        Juego juego = new Juego();
        Jugador jugador = new Jugador("Tester", juego);

        // Caso 1: Juego es "rule"
        juego.setTipoJuego("rule");
        jugador.run();

        // Verificar que entró en apostarRule
        assertTrue(juego.getApuestasRule().containsKey("Tester"),
                "Debe haber apostado en Rule cuando tipoJuego='rule'");

        // Reflection for coinflip map
        Field field = Juego.class.getDeclaredField("apuestasCoinflip");
        field.setAccessible(true);
        Map<?, ?> mapCoinflip = (Map<?, ?>) field.get(juego);
        assertTrue(mapCoinflip.isEmpty(), "No debe haber apostado en Coinflip cuando tipoJuego='rule'");

        // Limpieza para Caso 2
        juego.getApuestasRule().clear();

        // Caso 2: Juego NO es "rule" (e.g. "coinflip" o vacío)
        juego.setTipoJuego("coinflip");
        jugador.run();

        assertTrue(mapCoinflip.containsKey("Tester"), "Debe haber apostado en Coinflip cuando tipoJuego!='rule'");
        assertTrue(juego.getApuestasRule().isEmpty(), "No debe haber apostado en Rule cuando tipoJuego!='rule'");
    }
}
