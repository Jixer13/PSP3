package com.example.models;

import com.example.models.Juego;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Clase que representa un jugador del casino.
 * 
 * <p>Puede ser:</p>
 * <ul>
 *   <li><strong>Bot:</strong> Automático, genera apuestas en su hilo propio</li>
 *   <li><strong>Humano:</strong> Controlado por el usuario, interactúa mediante menú</li>
 * </ul>
 * 
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Gestionar saldo del jugador (inicial: 1000€)</li>
 *   <li>Ejecutar en un hilo daemon independiente</li>
 *   <li>Generar números aleatorios periódicamente (cada 10 segundos)</li>
 *   <li>Sincronizar generaciones con el contador global</li>
 *   <li>Implementar {@code Runnable} para ejecución en threads</li>
 * </ul>
 * 
 * <p>Ciclo de Ejecución (para Bots):</p>
 * <ol>
 *   <li>Sleep 2 segundos</li>
 *   <li>Genera número 1-36 con {@code ThreadLocalRandom}</li>
 *   <li>Incrementa {@code EstadoJuego.contadorGeneraciones} en sección crítica</li>
 *   <li>Registra timestamp en {@code EstadoJuego.ultimaGeneracion}</li>
 *   <li>Sleep 8 segundos</li>
 *   <li>Repite mientras {@code estadoHilo = true}</li>
 * </ol>
 * 
 * <p>Patrones de Concurrencia:</p>
 * <ul>
 *   <li><strong>Daemon Thread:</strong> No impide cierre de JVM</li>
 *   <li><strong>Synchronization Block:</strong> {@code synchronized (EstadoJuego.class)}</li>
 *   <li><strong>Volatile Variables:</strong> {@code EstadoJuego.contadorGeneraciones}</li>
 *   <li><strong>ThreadLocalRandom:</strong> Generación sin contención</li>
 *   <li><strong>Runnable:</strong> Implementación de interfaz para tests</li>
 * </ul>
 * 
 * @author GAEL, CARLOS, GABRIEL y MARIANA
 * @version 2.0
 * @since 2024
 * @see Thread
 * @see Runnable
 * @see Juego
 * @see EstadoJuego
 */
public class Jugador implements Runnable {

    /**
     * Nombre identificativo del jugador.
     * 
     * <p>Ejemplos:</p>
     * <ul>
     *   <li>Bots: "Maria", "Jose", "Ana"</li>\n     *   <li>Humano: Nombre ingresado por el usuario</li>
     * </ul>
     */
    private String nombre;

    /**
     * Saldo actual del jugador en euros.
     * 
     * <p>Inicial: 1000€</p>
     * <p><strong>Riesgo:</strong> No sincronizado. Acceso concurrente desde múltiples hilos
     * (Main actualiza saldo mientras Bot lo lee) puede causar inconsistencias.</p>
     * <p><strong>Mejora futuro:</strong> Usar {@code AtomicInteger} o sincronizar accesos.</p>
     * 
     * @see #getSaldo()
     * @see #setSaldo(int)
     */
    private int saldo;

    /**
     * Número apostado en la última ronda de generación.
     * 
     * <p>Rango: 1-36 (para bots) o 0-36 (para banca)</p>
     * <p>Se actualiza cada 10 segundos en el hilo del jugador.</p>
     * 
     * @see #getNumeroApostado()
     * @see #setNumeroApostado(int)
     */
    private int numeroApostado;

    /**
     * Referencia a la instancia de Juego compartida.
     * 
     * <p>Utilizada para:</p>
     * <ul>
     *   <li>Registrar apuestas en métodos sincronizados</li>
     *   <li>Obtener información sobre el juego actual</li>
     *   <li>Acceder a la barrera de sincronización (en tests)</li>
     * </ul>
     * 
     * @see Juego
     */
    private Juego juego;

    /**
     * Bandera para identificar tipo de jugador.
     * 
     * <p>Valores:</p>
     * <ul>
     *   <li>true: Jugador humano (controlado por usuario)</li>
     *   <li>false: Bot (automático)</li>
     * </ul>
     * 
     * <p>Usado en Main.java para diferencias comportamiento en menú.</p>
     * 
     * @see #isEsHumano()
     * @see #setEsHumano(boolean)
     */
    private boolean esHumano = false;

    /**
     * Referencia al hilo de ejecución del jugador.
     * 
     * <p>Inicialmente null. Se asigna en {@link #iniciarHiloJugador()}.</p>
     * <p>Se utiliza para detener el hilo con {@link #detenerHilo()}.</p>
     * 
     * @see Thread
     */
    private Thread hilo;

    /**
     * Bandera de control del ciclo de ejecución del hilo.
     * 
     * <p>Valores:</p>
     * <ul>
     *   <li>true: Hilo ejecutándose</li>
     *   <li>false: Hilo debe detenerse</li>
     * </ul>
     * 
     * <p>Se verifica en el bucle {@code while (estadoHilo)}</p>
     * 
     * @see #iniciarJugador()
     * @see #detenerHilo()
     */
    private boolean estadoHilo = false;

    /**
     * Constructor por defecto.
     * 
     * <p>Inicialización parcial (usualmente para construcción manual).</p>
     */
    public Jugador() {
    }

    /**
     * Constructor principal para crear un jugador con nombre e instancia de Juego.
     * 
     * @param nombre Nombre del jugador
     * @param juego Instancia de Juego (compartida entre jugadores)
     * 
     * <p>Inicializa:</p>
     * <ul>
     *   <li>{@code nombre}: Nombre proporcionado</li>
     *   <li>{@code saldo}: 1000€</li>
     *   <li>{@code juego}: Referencia al Juego</li>
     *   <li>{@code esHumano}: false (es un bot por defecto)</li>
     * </ul>
     * 
     * @see #Jugador()
     */
    public Jugador(String nombre, Juego juego) {
        this.nombre = nombre;
        this.saldo = 1000;
        this.juego = juego;
    }

    // ====================================== //
    // --------- ACCESORES (GETTERS) ------- //
    // ====================================== //

    /**
     * Obtiene el nombre del jugador.
     * 
     * @return Nombre del jugador
     * 
     * @see #setNombre(String)
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del jugador.
     * 
     * @param nombre Nuevo nombre
     * 
     * @see #getNombre()
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el saldo actual.
     * 
     * <p><strong>Riesgo de concurrencia:</strong> Sin sincronización.
     * Puede leer valores inconsistentes si otro hilo está escribiendo.</p>
     * 
     * @return Saldo en euros
     * 
     * @see #setSaldo(int)
     */
    public int getSaldo() {
        return saldo;
    }

    /**
     * Establece el saldo del jugador.
     * 
     * <p><strong>Riesgo de concurrencia:</strong> Sin sincronización.
     * Múltiples escrituras simultáneas pueden causar pérdida de actualizaciones.</p>
     * 
     * @param saldo Nuevo saldo en euros
     * 
     * @see #getSaldo()
     */
    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    /**
     * Obtiene el número apostado en la última generación.
     * 
     * @return Número 1-36
     * 
     * @see #setNumeroApostado(int)
     */
    public int getNumeroApostado() {
        return numeroApostado;
    }

    /**
     * Establece el número apostado.
     * 
     * @param numeroApostado Número a apostar
     * 
     * @see #getNumeroApostado()
     */
    public void setNumeroApostado(int numeroApostado) {
        this.numeroApostado = numeroApostado;
    }

    /**
     * Obtiene la instancia de Juego.
     * 
     * @return Referencia a Juego compartida
     * 
     * @see #setJuego(Juego)
     */
    public Juego getJuego() {
        return juego;
    }

    /**
     * Establece la instancia de Juego.
     * 
     * @param juego Nueva referencia a Juego
     * 
     * @see #getJuego()
     */
    public void setJuego(Juego juego) {
        this.juego = juego;
    }

    /**
     * Verifica si el jugador es humano.
     * 
     * @return true si es humano, false si es bot
     * 
     * @see #setEsHumano(boolean)
     */
    public boolean isEsHumano() {
        return esHumano;
    }

    /**
     * Establece si el jugador es humano.
     * 
     * @param esHumano true para humano, false para bot
     * 
     * @see #isEsHumano()
     */
    public void setEsHumano(boolean esHumano) {
        this.esHumano = esHumano;
    }

    // ====================================== //
    // --------- MÉTODOS DE HILOS --------- //
    // ====================================== //

    /**
     * Inicia el hilo independiente del jugador.
     * 
     * <p>Procedimiento:</p>
     * <ol>
     *   <li>Verifica si el hilo es null (solo una inicialización)</li>
     *   <li>Crea nuevo hilo daemon con lambda que ejecuta {@link #iniciarJugador()}</li>
     *   <li>Imprime mensaje de confirmación con nombre del jugador</li>
     *   <li>Establece {@code estadoHilo = true}</li>
     *   <li>Inicia el hilo con {@code start()}</li>
     * </ol>
     * 
     * <p>Manejo de Excepciones:</p>
     * <ul>
     *   <li>{@code IllegalThreadStateException}: Hilo ya iniciado</li>
     *   <li>{@code Exception}: Error inesperado (imprime mensaje)</li>
     * </ul>
     * 
     * <p>Efectos:</p>
     * <ul>
     *   <li>El jugador comienza a generar números cada 10 segundos</li>
     *   <li>Se ejecuta en paralelo al hilo principal</li>
     *   <li>No bloquea finalización del programa (daemon)</li>
     * </ul>
     * 
     * @see #iniciarJugador()
     * @see #detenerHilo()
     * @see Thread#setDaemon(boolean)
     * @throws IllegalThreadStateException Si el hilo ya fue iniciado
     */
    public void iniciarHiloJugador() {
        try {
            if (hilo == null) {
                hilo = new Thread(() -> iniciarJugador());
                // Ponemos el hilo en segundo plano para que no cierre el programa,
                // dejando así a la máquina virtual de Java terminar aunque siga el hilo activo
                hilo.setDaemon(true);

                System.out.println("[ INICIO HILO ] - Jugador " + nombre + " ha entrado en el Casino");

                // Cambiamos estado al crear el hilo correctamente
                estadoHilo = true;

                // Iniciamos el hilo
                hilo.start();
            }
        } catch (IllegalThreadStateException eHilo) {
            System.out.println(
                    " [ HILO NO INICIADO * ERROR * ] - No se pudo iniciar el hilo del jugador [ " + nombre + " ]");
        } catch (Exception e) {
            System.out.println(" [ ERROR INESPERADO ] \n" + e.getMessage());
        }
    }

    /**
     * Cuerpo de ejecución del hilo del jugador (método privado).
     * 
     * <p>Bucle principal (10 segundos por ciclo):</p>
     * <ol>
     *   <li><strong>Sleep 2s:</strong> Pausa antes de generar</li>
     *   <li><strong>Generar número:</strong> {@code ThreadLocalRandom.nextInt(1, 37)}</li>
     *   <li><strong>Sección Crítica:</strong> Incrementa {@code contadorGeneraciones} con sync</li>
     *   <li><strong>Registrar timestamp:</strong> {@code ultimaGeneracion = currentTimeMillis()}</li>
     *   <li><strong>Sleep 8s:</strong> Intervalo antes de próxima generación</li>
     *   <li><strong>Repetir:</strong> Mientras {@code estadoHilo == true}</li>
     * </ol>
     * 
     * <p>Sincronización:</p>
     * <ul>
     *   <li>Usa {@code synchronized (EstadoJuego.class)} para proteger variables volátiles</li>
     *   <li>Minimiza tiempo en sección crítica (solo 2 líneas)</li>
     *   <li>Permite que Main.java detecte cambios mediante {@code contadorGeneraciones}</li>
     * </ul>
     * 
     * <p>Manejo de Excepciones:</p>
     * <ul>
     *   <li>{@code InterruptedException}: Hilo interrumpido</li>
     *   <li>Establece {@code interrupted()} para preservar estado</li>
     *   <li>Termina el bucle de forma limpia</li>
     * </ul>
     * 
     * <p>Ejemplo de Secuencia:</p>
     * <code>
     * T=0s: Jugador iniciado, entra en bucle<br>
     * T=2s: Genera número (ej: 17)<br>
     * T=2s+ms: Incrementa contador (ej: 1 → 2)<br>
     * T=2s+ms: Registra timestamp<br>
     * T=10s: Repite<br>
     * T=12s: Genera nuevo número (ej: 23)<br>
     * T=12s+ms: Incrementa contador (ej: 2 → 3)
     * </code>
     *
     */
    private void iniciarJugador() {
        while (estadoHilo) {
            try {
                // Esperamos 2 segundos
                Thread.sleep(2000);

            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }

            // Generamos un número aleatorio del 1 al 36
            this.numeroApostado = ThreadLocalRandom.current().nextInt(1, 37);

            // Incrementamos el contador de generaciones para determinar el estado del jugador
            // SECCIÓN CRÍTICA: Garantiza visibilidad de cambios
            synchronized (EstadoJuego.class) {
                EstadoJuego.contadorGeneraciones++;
                EstadoJuego.ultimaGeneracion = System.currentTimeMillis();
            }

            try {
                // Esperamos 8 segundos para que se vuelva a generar un número
                Thread.sleep(8000);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Detiene la ejecución del hilo del jugador.
     * 
     * <p>Procedimiento:</p>
     * <ol>
     *   <li>Establece {@code estadoHilo = false}</li>
     *   <li>Interrumpe el hilo si existe</li>
     * </ol>
     * 
     * <p>Efecto:</p>
     * <ul>
     *   <li>El bucle {@code while (estadoHilo)} terminará</li>
     *   <li>El hilo dejará de generar números</li>
     *   <li>Se ejecuta de forma limpia</li>
     * </ul>
     *
     */
    public void detenerHilo() {
        estadoHilo = false;
        if (hilo != null) {
            hilo.interrupt();
        }
    }

    // ====================================== //
    // ------- MÉTODOS AUXILIARES --------- //
    // ====================================== //

    /**
     * Genera un número aleatorio para apuestas.
     * 
     * <p>Utilizado en tests e inicializaciones.</p>
     * 
     * @return Número aleatorio entre 1 y 36 (inclusive)
     * 

     */
    private int numGenerador() {
        return ThreadLocalRandom.current().nextInt(1, 37); // 1 incluido, 37 excluido
    }

    /**
     * Implementa {@code Runnable} para ejecución en tests.
     * 
     * <p>Procedimiento (simplificado para testing):</p>
     * <ol>
     *   <li>Resta 10€ del saldo (costo de apuesta)</li>
     *   <li>Genera número 1-36</li>
     *   <li>Registra apuesta según tipo de juego actual</li>
     * </ol>
     * 
     * <p>Utilidad:</p>
     * <ul>
     *   <li>Permite usar Jugador en tests multi-hilo determinísticos</li>
     *   <li>Simula una ronda de apuesta completa</li>
     *   <li>Se usa con {@code Juego(int numPartes)} y {@code CyclicBarrier}</li>
     * </ul>
     * 
     * <p>Ejemplo:</p>
     * <code>
     * Juego juego = new Juego(2);<br>
     * Jugador j1 = new Jugador("Bot1", juego);<br>
     * Thread t1 = new Thread(j1);<br>
     * t1.start(); // Ejecuta run()
     * </code>
     *
     */
    @Override
    public void run() {
        this.setSaldo(this.getSaldo() - 10);

        if (!juego.getTipoJuego().equalsIgnoreCase("rule")) {
            this.setNumeroApostado(numGenerador());
            juego.apostarCoinflip(this.getNombre(), this.getNumeroApostado());
        } else {
            this.setNumeroApostado(numGenerador());
            juego.apostarRule(this.getNombre(), this.getNumeroApostado());
        }
    }
}
