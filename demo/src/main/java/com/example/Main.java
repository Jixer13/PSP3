package com.example;

import java.util.Scanner;
import com.example.models.*;

import static com.example.models.Juego.*;


public class Main {

    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        menu(sc);

    }

    private static void menu(Scanner sc) {

        int opcion;

        System.out.println("\n Bienvenidos al Casino GCGM \n");
        System.out.println("En nuestros juegos la Banca va a generar un número del 0 al 36 , y los jugadores del 1 al 36\n" +
                "   - Si sale 0 todos los jugadores pierden.\n" +
                "   - Todos los jugadores apuestan 10.\n" +
                "   - Si los jugadores tiene el mismo número que la banca habrán ganado esa ronda." +
                "Estas son las normas generales, las normas más específicas se explicaran en cada juego.");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Seleccione el juego al que le interese jugar :");
        System.out.println("1. La Rule");
        System.out.println("2. Coin flip");
        System.out.println("3. Martingala");
        System.out.println("4. Salir");

        opcion = sc.nextInt();

        do {
            switch (opcion) {
                case 1:
                    System.out.println("Has seleccionado La Rule\n"+
                            "   - Los jugadores que ganen recibirán 360€ por cada ronda ganada.");
                    rule();
                    break;
                case 2:
                    System.out.println("Has seleccionado Coin flip\n" +
                            "   - Se comprueban los números generados para saber si son pares o impares.\n" +
                            "   - Si la banca saca un número par y el jugador también, el jugador gana.\n");
                    coinflip();
                    break;
                case 3:
                    System.out.println("Has seleccionado Martingala\n" +
                            "   - Los jugadores que ganen recibirán 360€ por cada ronda ganada.\n" +
                            "   - En el caso en el que el jugador pierda, su próxima apuesta sera el doble de la anterior.\n");
                    martingala();
                    break;
                case 4:
                    System.out.println("Has dejado que otro gane");
                    break;
            }
        } while (opcion != 4);
    }


}