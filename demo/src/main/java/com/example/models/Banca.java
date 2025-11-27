package com.example.models;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Banca {
    private int saldo;

    private int numero;

    private void numeroGanador(){
        numero = ThreadLocalRandom.current().nextInt(0, 37);
    }



}
