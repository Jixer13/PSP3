# Diagrama de Proceso del "Casino GCGM"

A continuación se describe el flujo de ejecución de la aplicación del casino, basado en el análisis del código fuente.

## 1. INICIO DEL PROGRAMA

```
1. INICIO DEL PROGRAMA
   │
   ├─> 1.1. Inicialización de Componentes:
   │   ├─ Se crea una 'Banca' con 50,000€ de saldo.
   │   ├─ Se crea un objeto 'Juego' para gestionar las apuestas.
   │   └─ Se crean 4 'Jugadores' ("Maria", "Juan", "Pedro", "Daniela"), cada uno con 1,000€ de saldo.
   │
   └─> 1.2. Mostrar Menú Principal:
       │
       ├─ 1. La Rule
       ├─ 2. Coin flip
       ├─ 3. Martingala
       ├─ 4. Info Jugadores
       └─ 5. Salir
```

## 2. SELECCIÓN DEL USUARIO

```
2. SELECCIÓN DEL USUARIO
   │
   ├─> Opción 1 ("La Rule") o 2 ("Coin flip"): [Ejecución con Hilos]
   │   │
   │   ├─ 2.1. Comprobar Saldo Banca: Si no puede pagar los premios, el juego no inicia.
   │   │
   │   ├─ 2.2. Iniciar Apuestas (en paralelo):
   │   │   └─ Para cada jugador, se inicia un hilo (Thread) que ejecuta:
   │   │      ├─ a. El jugador paga 10€ de su saldo.
   │   │      ├─ b. El jugador elige un número aleatorio del 1 al 36.
   │   │      └─ c. El jugador registra su apuesta en el objeto 'Juego'.
   │   │
   │   ├─ 2.3. Esperar a Jugadores: El programa principal espera a que todos los hilos de los jugadores terminen.
   │   │
   │   ├─ 2.4. Generar Número Ganador: La banca genera un número aleatorio del 0 al 36.
   │   │
   │   └─ 2.5. Calcular Resultados:
   │       │
   │       ├─ Para "La Rule": Si un jugador acierta el número, gana 360€. Si no, la banca se queda la apuesta.
   │       │
   │       └─ Para "Coin flip": Si la paridad (par/impar) del número del jugador y el de la banca coinciden, gana 20€. Si no, la banca se queda la apuesta.
   │
   ├─> Opción 3 ("Martingala"): [Ejecución Secuencial]
   │   │
   │   └─ 2.1. Bucle de Rondas (mientras el usuario quiera seguir y haya jugadores con saldo):
   │       │
   │       ├─ a. Los jugadores realizan su apuesta (la apuesta inicial es de 10€).
   │       ├─ b. La banca genera un número ganador.
   │       ├─ c. Si un jugador GANA: Recibe 360€ y su siguiente apuesta se reinicia a 10€.
   │       └─ d. Si un jugador PIERDE: La banca cobra la apuesta y el jugador debe DUPLICAR su siguiente apuesta. Es eliminado si no puede pagar.
   │
   ├─> Opción 4 ("Info Jugadores"):
   │   │
   │   └─ 2.1. Se muestra por pantalla el nombre y el saldo actual de cada jugador.
   │
   └─> Opción 5 ("Salir"):
       │
       └─ 3. FIN DEL PROGRAMA
```
