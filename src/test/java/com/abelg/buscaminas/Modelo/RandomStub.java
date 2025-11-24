package com.abelg.buscaminas.Modelo;

import java.util.Random;

/**
 * Stub de Random para controlar la posición de las minas en los tests.
 * Se usa como mock object adicional para eliminar el azar.
 */
public class RandomStub extends Random {

    private final int[] valores;
    private int index = 0;

    public RandomStub(int... valores) {
        this.valores = valores.clone();
    }

    @Override
    public int nextInt(int bound) {
        if (valores.length == 0) {
            return 0;
        }
        int val = valores[index % valores.length] % bound;
        index++;
        return val;
    }
}
