package com.example.models;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

/**
 * Clase que gestiona las apuestas de los jugadores en los diferentes juegos del casino.
 * 
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Almacenar apuestas de jugadores en mapas thread-safe</li>
 *   <li>Generar números aleatorios para juegos</li>
 *   <li>Sincronizar operaciones de apuesta con métodos {@code synchronized}</li>
 *   <li>Proporcionar barrera cíclica para testing multi-hilo</li>
 * </ul>
 * 
 * <p>Juegos Soportados:</p>
 * <ul>
 *   <li><strong>La Rule:</strong> Número exacto 1-36, premio 360€</li>
 *   <li><strong>Coin Flip:</strong> Paridad 1-36, premio 20€</li>
 *   <li><strong>Martingala:</strong> Número exacto con apuestas progresivas</li>
 * </ul>
 * 
 * <p>Patrones de Concurrencia:</p>
 * <ul>
 *   <li><strong>Monitor Synchronization:</strong> Métodos {@code apostarRule()} y {@code apostarCoinflip()}</li>
 *   <li><strong>CyclicBarrier:</strong> Para sincronización de tests (constructor alternativo)</li>
 *   <li><strong>HashMap:</strong> No thread-safe por sí solo, protegido por synchronized</li>
 *   <li><strong>ThreadLocalRandom:</strong> Generación de números sin contención</li>
 * </ul>
 * 
 * <p>Seguridad de Hilos:</p>
 * <ul>
 *   <li>Métodos de apuesta: Sincronizados (exclusión mutua garantizada)</li>
 *   <li>Métodos getter de apuestas: No sincronizados (riesgo de lectura inconsistente)</li>
 *   <li>Generadores de números: Sin sincronización (ThreadLocalRandom es thread-safe)</li>
 * </ul>
 * 
 * @author GAEL, CARLOS, GABRIEL y MARIANA
 * @version 2.0
 * @since 2024
 * @see Jugador
 * @see Banca
 */
public class Juego {

    /**
     * Mapa de apuestas para el juego "La Rule".
     * 
     * <p>Estructura: {@code Map<nombre_jugador, número_apostado>}</p>
     * <p>Protección: {@code synchronized} en método {@link #apostarRule(String, int)}</p>
     * <p>Rango de números: 1-36</p>
     * 
     * @see #apostarRule(String, int)
     * @see #getApuestasRule()
     */
    private Map<String, Integer> apuestasRule = new HashMap<>();

    /**
     * Mapa de apuestas para el juego "Coin Flip".
     * 
     * <p>Estructura: {@code Map<nombre_jugador, número_apostado>}</p>
     * <p>Protección: {@code synchronized} en método {@link #apostarCoinflip(String, int)}</p>
     * <p>Rango de números: 1-36</p>
     * <p>Criterio: Paridad (par/impar) del número</p>
     * 
     * @see #apostarCoinflip(String, int)
     */
    private Map<String, Integer> apuestasCoinflip = new HashMap<>();

    /**
     * Tipo de juego actual.
     * 
     * <p>Valores válidos:</p>
     * <ul>
     *   <li>"rule": Juego "La Rule"</li>
     *   <li>"coinflip": Juego "Coin Flip"</li>
     *   <li>"martingala": Sistema "Martingala"</li>
     * </ul>
     * 
     * @see #getTipoJuego()
     * @see #setTipoJuego(String)
     */
    private String tipoJuego = "";

    /**
     * Barrera cíclica para sincronización de tests.
     * 
     * <p>Permite que N hilos se esperen mutuamente en un punto específico.</p>
     * <p>Utilidad: Garantizar ejecución determinística en tests multi-hilo.</p>
     * <p>Inicialización: Solo si se usa constructor {@link #Juego(int)}.</p>
     * 
     * @see CyclicBarrier
     * @see #Juego(int)
     * @see #getBarrera()
     */
    private CyclicBarrier barrera;

    /**
     * Constructor por defecto.
     * 
     * <p>Inicializa:</p>
     * <ul>
     *   <li>Mapas de apuestas vacíos</li>
     *   <li>Tipo de juego en blanco</li>
     *   <li>Barrera como null</li>
     * </ul>
     * 
     * <p>Uso: Para ejecución normal del casino (sin testing).</p>
     */
    public Juego() {
    }

    /**
     * Constructor para testing con sincronización de múltiples hilos.
     * 
     * @param numPartes Número de hilos que participarán en la barrera
     * @throws IllegalArgumentException Si numPartes ≤ 0
     * 
     * <p>Crea un {@code CyclicBarrier} que sincroniza exactamente {@code numPartes} hilos.</p>
     * <p>Cada hilo que llama a {@code await()} en la barrera se bloquea hasta que
     * todos los hilos hayan llegado al mismo punto.</p>
     * 
     * <p>Ejemplo:</p>
     * <code>
     * Juego j = new Juego(4); // Barrera para 4 hilos<br>
     * // En cada hilo:<br>
     * j.getBarrera().await(); // Espera a que los otros 3 hilos lleguen
     * </code>
     * 
     * @see CyclicBarrier#await()
     * @see #getBarrera()
     */
    public Juego(int numPartes) {
        this.barrera = new CyclicBarrier(numPartes);
    }

    // ====================================== //
    // -------- GENERADORES DE NÚMEROS ------ //
    // ====================================== //

    /**
     * Genera un número aleatorio para el juego "La Rule".
     * 
     * <p>Rango: 0-36 (inclusivos)</p>
     * <p>Utiliza {@code ThreadLocalRandom} para máximo rendimiento sin contención.</p>
     * 
     * @return Número aleatorio entre 0 y 36 (inclusive)
     * 
     * @see ThreadLocalRandom
     * @see #coinFlipRule()
     */
    public int rule() {
        // Genera un número entre 0 y 36 (ambos incluidos)
        return ThreadLocalRandom.current().nextInt(0, 37);
    }

    /**
     * Genera un número aleatorio para el juego "Coin Flip".
     * 
     * <p>Rango: 0-36 (inclusivos)</p>
     * <p>Idéntico a {@link #rule()}.</p>
     * 
     * <p><strong>Nota:</strong> Podría refactorizarse para eliminar duplicidad.
     * Considerar método genérico {@code generarNumero()}</p>
     * 
     * @return Número aleatorio entre 0 y 36 (inclusive)
     * 
     * @see ThreadLocalRandom
     * @see #rule()
     */
    public int coinFlipRule() {
        return ThreadLocalRandom.current().nextInt(0, 37);
    }

    // ====================================== //
    // ------- MÉTODOS DE APUESTA ----------- //
    // ====================================== //

    /**
     * Registra una apuesta en el juego "La Rule" de forma sincronizada.
     * 
     * <p><strong>Sincronización: synchronized</strong></p>
     * <p>Garantiza que solo un hilo accede al mapa a la vez, evitando race conditions.</p>
     * 
     * <p>Procedimiento:</p>
     * <ol>
     *   <li>Imprime entrada en sección crítica con nombre del jugador</li>
     *   <li>Almacena apuesta en {@code apuestasRule}</li>
     *   <li>Imprime confirmación de apuesta registrada</li>
     * </ol>
     * 
     * @param nombre Nombre del jugador que apuesta
     * @param numeroApostado Número apostado (1-36)
     * 
     * <p>Ejemplo:</p>
     * <code>
     * juego.apostarRule("Maria", 17);<br>
     * // Output:<br>
     * // [HILO: Maria] ✓ ENTRA en la sección crítica de apostarRule<br>
     * // [HILO: Maria] → Apuesta registrada: 17
     * </code>
     * 
     * @see #getApuestasRule()
     * @see #apostarCoinflip(String, int)
     */
    public synchronized void apostarRule(String nombre, int numeroApostado) {
        System.out.println("[HILO: " + nombre + "] ✓ ENTRA en la sección crítica de apostarRule");
        apuestasRule.put(nombre, numeroApostado);
        System.out.println("[HILO: " + nombre + "] → Apuesta registrada: " + numeroApostado);
    }

    /**
     * Obtiene el mapa de apuestas del juego "La Rule".
     * 
     * <p><strong>Riesgo de concurrencia:</strong> Lectura sin sincronización.
     * Múltiples hilos podrían leer el mapa mientras se está modificando,
     * causando {@code ConcurrentModificationException} o datos inconsistentes.</p>
     * 
     * <p><strong>Recomendación:</strong> Sincronizar acceso o usar {@code ConcurrentHashMap}.</p>
     * 
     * @return {@code Map<nombre_jugador, número_apostado>}
     * 
     * @see #apostarRule(String, int)
     * @see java.util.concurrent.ConcurrentHashMap
     */
    public Map<String, Integer> getApuestasRule() {
        return apuestasRule;
    }

    /**
     * Registra una apuesta en el juego "Coin Flip" de forma sincronizada.
     * 
     * <p><strong>Sincronización: synchronized</strong></p>
     * <p>Garantiza exclusión mutua en acceso al mapa {@code apuestasCoinflip}.</p>
     * 
     * <p>Operación: Almacena la apuesta sin logs adicionales (similar a {@link #apostarRule(String, int)}
     * pero sin mensajes de debug).</p>
     * 
     * @param nombre Nombre del jugador que apuesta
     * @param numeroApostado Número apostado (1-36)
     * 
     * @see #apostarRule(String, int)
     */
    public synchronized void apostarCoinflip(String nombre, int numeroApostado) {
        apuestasCoinflip.put(nombre, numeroApostado);
    }

    // ====================================== //
    // --------- ACCESORES GENERALES ------- //
    // ====================================== //

    /**
     * Obtiene la barrera cíclica de sincronización.
     * 
     * <p><strong>Disponibilidad:</strong> Solo si se inicializó con
     * {@link #Juego(int)} (constructor parametrizado).</p>
     * 
     * <p>Uso:</p>
     * <ul>
     *   <li>Testing multi-hilo determinístico</li>
     *   <li>Sincronización de N hilos en un punto específico</li>
     * </ul>
     * 
     * @return {@code CyclicBarrier} para sincronización, o {@code null} si no se inicializó
     * 
     * @throws BrokenBarrierException Si la barrera se rompe durante wait
     * @throws InterruptedException Si el hilo se interrumpe durante wait
     * 
     * @see CyclicBarrier#await()
     */
    public CyclicBarrier getBarrera() {
        return barrera;
    }

    /**
     * Obtiene el tipo de juego actual.
     * 
     * @return Tipo de juego: "rule", "coinflip", "martingala", o blanco si no se ha establecido
     * 
     * @see #setTipoJuego(String)
     */
    public String getTipoJuego() {
        return tipoJuego;
    }

    /**
     * Establece el tipo de juego actual.
     * 
     * @param tipoJuego Tipo de juego a establecer
     * 
     * @see #getTipoJuego()
     */
    public void setTipoJuego(String tipoJuego) {
        this.tipoJuego = tipoJuego;
    }
}
