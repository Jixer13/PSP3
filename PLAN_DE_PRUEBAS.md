# Plan de Pruebas Manual - Aplicación de Apuestas

Este documento describe los casos de prueba manuales para verificar la funcionalidad de las clases `Banca`, `Juego` y `Jugador`.

## 1. Pruebas para la Clase `Banca`

| ID de Prueba | Descripción | Pasos a Seguir | Resultado Esperado |
| :--- | :--- | :--- | :--- |
| **Banca-001** | Verificar la inicialización por defecto. | 1. Crear una instancia `new Banca()`.<br>2. Consultar el saldo con `getSaldo()`. | El saldo debe ser `50000`. |
| **Banca-002** | Verificar la inicialización con un saldo específico. | 1. Crear una instancia `new Banca(100)`.<br>2. Consultar el saldo con `getSaldo()`. | El saldo debe ser `100`. |
| **Banca-003** | Verificar la modificación del saldo. | 1. Crear una instancia `new Banca()`.<br>2. Llamar a `setSaldo(500)`.<br>3. Consultar el saldo con `getSaldo()`. | El saldo debe ser `500`. |

---

## 2. Pruebas para la Clase `Juego`

| ID de Prueba | Descripción | Pasos a Seguir | Resultado Esperado |
| :--- | :--- | :--- | :--- |
| **Juego-001** | Verificar el rango de números de `rule()`. | 1. Crear una instancia `new Juego()`.<br>2. Llamar al método `rule()` 100 veces.<br>3. Registrar los resultados. | Todos los números generados deben estar en el rango `[0, 36]`. |
| **Juego-002** | Verificar el rango de números de `coinFlipRule()`. | 1. Crear una instancia `new Juego()`.<br>2. Llamar al método `coinFlipRule()` 100 veces.<br>3. Registrar los resultados. | Todos los números generados deben estar en el rango `[0, 36]`. |
| **Juego-003** | Verificar que una apuesta se añade correctamente a `apuestasRule`. | 1. Crear `new Juego()`.<br>2. Llamar a `apostarRule("Jugador1", 25)`.<br>3. Obtener el mapa con `getApuestasRule()`. | El mapa debe contener la clave `"Jugador1"` con el valor `25`. |
| **Juego-004** | Verificar que una apuesta se añade correctamente a `apuestasCoinflip`. | 1. Crear `new Juego()`.<br>2. Llamar a `apostarCoinflip("Jugador1", 10)`.<br>3. Obtener el mapa `apuestasCoinflip` (requiere añadir un getter temporalmente o usar un debugger). | El mapa debe contener la clave `"Jugador1"` con el valor `10`. |

---

## 3. Pruebas para la Clase `Jugador`

Estas pruebas son más complejas debido a que `Jugador` es un `Runnable` y depende de `Juego`.

| ID de Prueba | Descripción | Pasos a Seguir | Resultado Esperado |
| :--- | :--- | :--- | :--- |
| **Jugador-001** | Verificar la inicialización del jugador. | 1. Crear `new Juego()`.<br>2. Crear `new Jugador("Tester", juego)`.<br>3. Consultar `getNombre()` y `getSaldo()`. | El nombre debe ser `"Tester"` y el saldo `1000`. |
| **Jugador-002** | Verificar la reducción de saldo al ejecutar el `run()`. | 1. Crear `new Jugador("Tester", new Juego())`.<br>2. Ejecutar el método `run()` en un hilo (`new Thread(jugador).start()`).<br>3. Esperar a que el hilo termine y consultar el `getSaldo()`. | El saldo debe ser `990` (1000 - 10). |
| **Jugador-003** | **Análisis de Lógica Defectuosa en `run()`**. | 1. Revisar la condición `if (!juego.equals(juego.rule()))`.<br>2. `juego` es un objeto de tipo `Juego`. `juego.rule()` devuelve un `int`. <br>3. La comparación `juego.equals(int)` siempre será `false`. | La condición `if` siempre será `false`, por lo que el código dentro del `else` (`juego.apostarRule()`) **nunca se ejecutará**. El método `juego.apostarCoinflip()` será llamado en cada ejecución del `run()` del jugador. |
| **Jugador-004** | Verificar el comportamiento del `run()` (basado en el análisis anterior). | 1. Crear `Juego juego = new Juego()`<br>2. Crear `Jugador j1 = new Jugador("J1", juego)`.<br>3. Ejecutar `j1.run()` (directamente o en un hilo).<br>4. Comprobar el contenido de `juego.getApuestasRule()` y `juego.apuestasCoinflip`. | El mapa `apuestasRule` debe estar vacío. El mapa `apuestasCoinflip` debe contener la apuesta de "J1". |

Este plan de pruebas te servirá como guía para validar tu código manualmente y te ayudará a identificar el comportamiento anómalo en la clase `Jugador`.
