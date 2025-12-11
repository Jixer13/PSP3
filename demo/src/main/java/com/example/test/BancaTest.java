package com.example.test;

import com.example.models.Banca;

public class BancaTest {

    public static void main(String[] args) {
        System.out.println("=== PRUEBAS DE LA CLASE BANCA ===\n");

        int pruebasPasadas = 0;
        int pruebasTotales = 3;

        // Prueba Banca-001: Verificar la inicialización por defecto
        System.out.println("Banca-001 - Inicialización por defecto:");
        try {
            Banca banca1 = new Banca();
            int saldo = banca1.getSaldo();

            if (saldo == 50000) {
                System.out.println("  OK - Saldo: " + saldo + " (esperado: 50000)");
                pruebasPasadas++;
            } else {
                System.out.println("  FALLO - Saldo obtenido: " + saldo + " (esperado: 50000)");
            }
        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
        }

        // Prueba Banca-002: Verificar la inicialización con un saldo específico
        System.out.println("\nBanca-002 - Inicialización con saldo específico (100):");
        try {
            Banca banca2 = new Banca(100);
            int saldo = banca2.getSaldo();

            if (saldo == 100) {
                System.out.println("  OK - Saldo: " + saldo + " (esperado: 100)");
                pruebasPasadas++;
            } else {
                System.out.println("  FALLO - Saldo obtenido: " + saldo + " (esperado: 100)");
            }
        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
        }

        // Prueba Banca-003: Verificar la modificación del saldo
        System.out.println("\nBanca-003 - Modificación del saldo:");
        try {
            Banca banca3 = new Banca();
            banca3.setSaldo(500);  // Cambiamos el saldo
            int saldo = banca3.getSaldo();

            if (saldo == 500) {
                System.out.println("  OK - Saldo: " + saldo + " (esperado: 500)");
                pruebasPasadas++;
            } else {
                System.out.println("  FALLO - Saldo obtenido: " + saldo + " (esperado: 500)");
            }
        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
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
