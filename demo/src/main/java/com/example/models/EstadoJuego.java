package com.example.models;

/**
 * Clase utilitaria que centraliza el estado global del sistema de juego.
 * 
 * <p>Propósito:</p>
 * <p>Almacenar variables compartidas entre múltiples hilos que definen el estado
 * del casino: si los bots están apostando, contador de generaciones de números, etc.</p>
 * 
 * <p>Atributos Estáticos (Compartidos):</p>
 * <ul>
 *   <li>{@code botsApostando}: Bandera de sincronización de estado</li>
 *   <li>{@code ultimaGeneracion}: Timestamp de último cambio (volatile)</li>
 *   <li>{@code contadorGeneraciones}: Contador incremental (volatile)</li>
 * </ul>
 * 
 * <p><strong>¿Por qué volatile?</strong></p>
 * <ul>
 *   <li><strong>Visibilidad:</strong> Cambios son inmediatamente visibles en todos los hilos</li>
 *   <li><strong>Sin Caché:</strong> Impide que el procesador almacene en caché valores locales</li>
 *   <li><strong>Orden:</strong> Evita reordenamiento de instrucciones por el compilador</li>
 *   <li><strong>Prevención:</strong> De bucles infinitos y deadlocks causados por datos obsoletos</li>
 * </ul>
 * 
 * <p>Arquitectura Multi-Core:</p>
 * <ul>
 *   <li>Sin volatile: Cada núcleo podría tener su propia copia de la variable</li>
 *   <li>Con volatile: Todos los núcleos ven la versión actual en memoria</li>
 * </ul>
 * 
 * <p>Casos de Uso en el Código:</p>
 * <code>
 * // En Jugador.java - Incremento con sincronización mínima<br>
 * synchronized (EstadoJuego.class) {<br>
 *     EstadoJuego.contadorGeneraciones++;<br>
 *     EstadoJuego.ultimaGeneracion = System.currentTimeMillis();<br>
 * }<br>
 * <br>
 * // En Main.java - Espera a cambio de valor<br>
 * while (EstadoJuego.contadorGeneraciones == contadorAnterior) {<br>
 *     Thread.sleep(500);<br>
 * }
 * </code>
 * 
 * @author GAEL, CARLOS, GABRIEL y MARIANA
 * @version 2.0
 */
public class EstadoJuego {
    
    /**
     * Indica si los bots están en fase de apuesta.
     * 
     * <p>Valores:</p>
     * <ul>
     *   <li>true: Bots están generando y apostando números</li>
     *   <li>false: Ronda ha terminado, esperando siguiente inicio</li>
     * </ul>
     * 
     * <p>Usado por:</p>
     * <ul>
     *   <li>Main.java: Para sincronizar entrada del jugador humano</li>
     *   <li>Banca.java: Para indicar fin de ronda</li>
     * </ul>
     *
     */
    public static boolean botsApostando = false;

    /**
     * Timestamp del último cambio de generación de números.
     * 
     * <p><strong>Modificador volatile:</strong> Garantiza visibilidad inmediata</p>
     * 
     * <p>Propósito:</p>
     * <ul>
     *   <li>Registrar cuándo fue la última generación</li>
     *   <li>Permitir análisis de cadencia de generación</li>
     *   <li>Debugging de problemas de sincronización</li>
     * </ul>
     * 
     * <p>Actualización:</p>
     * <ul>
     *   <li>Se establece simultáneamente con {@code contadorGeneraciones}</li>
     *   <li>Se incrementa cada 10 segundos aproximadamente (ciclo Jugador)</li>
     * </ul>
     *
     */
    public static volatile long ultimaGeneracion = 0;

    /**
     * Contador incremental de ciclos de generación de números.
     * 
     * <p><strong>Modificador volatile:</strong> Garantiza visibilidad inmediata</p>
     * 
     * <p>Propósito:</p>
     * <ul>
     *   <li>Determinar cuándo se han generado nuevos números</li>
     *   <li>Sincronizar inicio de nuevas rondas de juego</li>
     *   <li>Evitar que el jugador humano use números obsoletos</li>
     * </ul>
     * 
     * <p>Comportamiento:</p>
     * <ul>
     *   <li>Se incrementa en cada ciclo de generación (~cada 10 segundos)</li>
     *   <li>Nunca se reinicia a 0 durante la ejecución</li>
     *   <li>Permite detección simple de cambios: comparar con valor anterior</li>
     * </ul>
     * 
     * <p>Ejemplo de uso:</p>
     * <code>
     * int contadorAnterior = EstadoJuego.contadorGeneraciones;<br>
     * <br>
     * // Esperar a que cambie<br>
     * while (EstadoJuego.contadorGeneraciones == contadorAnterior) {<br>
     *     Thread.sleep(500);<br>
     * }<br>
     * <br>
     * // Nuevos números disponibles, proceder
     * </code>
     * 
     * <p>Ventajas vs alternativas:</p>
     * <ul>
     *   <li><strong>vs Locks:</strong> No requiere sincronización explícita para lectura</li>
     *   <li><strong>vs Wait/Notify:</strong> No causa overhead de monitores en lectura continua</li>
     *   <li><strong>vs AtomicInteger:</strong> Menor overhead para casos simples</li>
     * </ul>
     *
     */
    public static volatile int contadorGeneraciones = 0;
}
