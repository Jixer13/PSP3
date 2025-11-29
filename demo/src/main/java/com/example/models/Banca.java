package com.example.models;

import java.util.Objects;

public class Banca {
    private int saldo=50000;

    public Banca() {
    }

    public Banca(int saldo) {
        this.saldo = saldo;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    @Override
    public String toString() {
        return "Banca{" +
                "saldo=" + saldo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Banca banca = (Banca) o;
        return saldo == banca.saldo;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(saldo);
    }
}
