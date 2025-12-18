package com.example.models;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Clase que representa la banca del casino.
 * 
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Gestionar el saldo de la banca (inicial: 50000€)</li>
 *   <li>Generar números ganadores (0-36) periódicamente</li>
 *   <li>Ejecutarse en un hilo daemon independiente</li>
 *   <li>Notificar a otros hilos cuando una ronda termina</li>
 * </ul>
 * 
 * <p>Ciclo de ejecución del hilo:</p>
 * <ol>
 *   <li>Sleep 5 segundos (tiempo de apuesta)</li>
 *   <li>Genera número aleatorio 0-36</li>
 *   <li>Establece {@code botsApostando = false}</li>
 *   <li>Notifica con {@code notifyAll()}</li>
 *   <li>Sleep 8 segundos (intervalo entre rondas)</li>
 *   <li>Repite si {@code estadoBanca = true}</li>
 * </ol>
 * 
 * <p>Patrones de Concurrencia:</p>
 * <ul>
 *   <li><strong>Daemon Thread:</strong> Hilo ejecutado en background</li>
 *   <li><strong>Monitor Synchronization:</strong> {@code synchronized} + {@code notifyAll()}</li>
 *   <li><strong>ThreadLocalRandom:</strong> Generación sin contención</li>
 * </ul>
 * 
 * @author GAEL, CARLOS, GABRIEL y MARIANA
 * @version 2.0
 * @see Thread
 */
public class Banca {

    /**
     * Saldo actual de la banca.
     * 
     * <p>Inicial: 50000€</p>
     * <p><strong>Nota:</strong> No sincronizado. Acceso concurrente sin protección
     * puede causar inconsistencias. Considerar {@code AtomicInteger} para futuras versiones.</p>
     */
    private int saldo = 50000;

    /**
     * Número generado en la última ronda.
     * 
     * <p>Rango: 0-36 (incluidos)</p>
     * <p>Se actualiza cada 5 segundos en el hilo de la banca</p>
     */
    private int numeroApostado;

    /**
     * Referencia al hilo de ejecución de la banca.
     * 
     * <p>Inicialmente null hasta que se llama a {@link #iniciarHiloBanca()}</p>
     */
    private Thread hilo;

    /**
     * Bandera que controla el ciclo de ejecución del hilo.
     * 
     * <p>true: Hilo ejecutándose</p>
     * <p>false: Hilo debe detenerse</p>
     */
    private boolean estadoBanca = false;

    /**
     * Constructor por defecto.
     * 
     * <p>Inicializa la banca con saldo por defecto de 50000€</p>
     */
    public Banca() {
    }

    /**
     * Constructor parametrizado.
     * 
     * @param saldo Saldo inicial de la banca
     */
    public Banca(int saldo) {
        this.saldo = saldo;
    }

    /**
     * Obtiene el saldo actual de la banca.
     * 
     * <p><strong>Riesgo de concurrencia:</strong> Sin sincronización.
     * Múltiples hilos pueden leer valores inconsistentes.</p>
     * 
     * @return Saldo actual en euros
     * @see #setSaldo(int)
     */
    public int getSaldo() {
        return saldo;
    }

    /**
     * Establece el saldo de la banca.
     * 
     * <p><strong>Riesgo de concurrencia:</strong> Sin sincronización.
     * Múltiples escrituras simultáneas pueden causar pérdida de actualizaciones.</p>
     * 
     * @param saldo Nuevo saldo en euros
     * @see #getSaldo()
     */
    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    /**
     * Obtiene el número apostado (generado) en la última ronda.
     * 
     * @return Número generado 0-36
     */
    public int getNumeroApostado() {
        return numeroApostado;
    }

    /**
     * Representación en texto del estado de la banca.
     * 
     * @return String con formato "La banca tiene un saldo de {saldo}€"
     */
    @Override
    public String toString() {
        return "La banca tiene un saldo de " + saldo + "€";
    }

    // ----------------------------------------------- //
    // ----------- MÉTODOS DE HILOS ----------- //
    // ----------------------------------------------- //

    /**
     * Inicia el hilo independiente de la banca.
     * 
     * <p>Procedimiento:</p>
     * <ol>
     *   <li>Verifica si el hilo es nulo (solo una inicialización)</li>
     *   <li>Crea nuevo hilo con lambda que ejecuta {@link #ejecutarBanca()}</li>
     *   <li>Establece como daemon con {@code setDaemon(true)}</li>
     *   <li>Establece {@code estadoBanca = true}</li>
     *   <li>Inicia el hilo con {@code start()}</li>
     *   <li>Imprime mensaje de confirmación</li>
     * </ol>
     * 
     * <p>Efectos:</p>
     * <ul>
     *   <li>El hilo comienza a generar números automáticamente</li>
     *   <li>El hilo no impide que el programa termine</li>
     *   <li>Se ejecuta en paralelo al hilo principal</li>
     * </ul>
     * 
     * @see Thread#setDaemon(boolean)
     * @see Thread#start()
     * @see #ejecutarBanca()
     */
    public void iniciarHiloBanca() {
        if (hilo == null) {
            hilo = new Thread(() -> ejecutarBanca());
            // Ponemos el hilo en segundo plano para que no cierre el programa,
            // dejando así a la máquina virtual de Java terminar aunque siga el hilo activo
            hilo.setDaemon(true);

            estadoBanca = true;
            System.out.println(" * [ HILO BANCA INICIADO ] * \n");
            hilo.start();
        }
    }

    /**
     * Cuerpo de ejecución del hilo de la banca (método privado).
     * 
     * <p>Bucle principal (13 segundos por ciclo):</p>
     * <ol>
     *   <li><strong>Sleep 5s:</strong> Tiempo para que los jugadores apuesten</li>
     *   <li><strong>Generar número:</strong> {@code ThreadLocalRandom.nextInt(0, 37)}</li>
     *   <li><strong>Notificar:</strong> Establece {@code botsApostando = false} y llama {@code notifyAll()}</li>
     *   <li><strong>Sleep 8s:</strong> Intervalo antes de la siguiente ronda</li>
     *   <li><strong>Repetir:</strong> Mientras {@code estadoBanca == true}</li>
     * </ol>
     * 
     * <p>Sincronización:</p>
     * <ul>
     *   <li>Usa {@code synchronized (this)} para proteger notificación</li>
     *   <li>{@code notifyAll()} despierta todos los hilos esperadores</li>
     *   <li>Si se interrumpe el hilo, detiene la ejecución limpiamente</li>
     * </ul>
     * 
     * <p>Manejo de Excepciones:</p>
     * <ul>
     *   <li>{@code InterruptedException}: Hilo interrumpido externamente</li>
     *   <li>Establece {@code interrupted()} para preservar estado</li>
     * </ul>
     * 
     * @see EstadoJuego#botsApostando
     * @see ThreadLocalRandom
     */
    private void ejecutarBanca() {
        while (estadoBanca) {
            try {
                // Esperamos 5 segundos para que los jugadores apuesten
                Thread.sleep(5000);

            } catch (InterruptedException eHilo) {
                Thread.currentThread().interrupt();
                break;
            }

            // Generación de número ganador en el hilo Banca [0-36]
            numeroApostado = ThreadLocalRandom.current().nextInt(0, 37);

            EstadoJuego.botsApostando = false;

            // Notificamos al resto de hilos que la ronda ha terminado
            synchronized (this) {
                notifyAll();
            }

            try {
                // Esperamos 8 segundos para volver a empezar
                Thread.sleep(8000);

            } catch (InterruptedException eHilo) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Detiene la ejecución del hilo de la banca.
     * 
     * <p>Procedimiento:</p>
     * <ol>
     *   <li>Establece {@code estadoBanca = false}</li>
     *   <li>Interrumpe el hilo si existe</li>
     * </ol>
     * 
     * <p>Efecto:</p>
     * <ul>
     *   <li>El bucle {@code while (estadoBanca)} terminará</li>
     *   <li>El hilo dejará de generar números</li>
     *   <li>Se ejecuta de forma limpia sin causar corrupción</li>
     * </ul>
     * 
     * @see #estadoBanca
     * @see Thread#interrupt()
     */
    public void detenerHilo() {
        estadoBanca = false;
        if (hilo != null) {
            hilo.interrupt();
        }
    }
}
