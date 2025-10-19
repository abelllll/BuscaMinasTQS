package com.abelg.buscaminas.Modelo;

import java.util.Random;

class Tablero {
    private final int filas;
    private final int columnas;
    private final int numMinas;
    private final casilla[][] celdas; // Usa tu clase existente 'casilla' (minúsculas)

    // Inyectamos Random para testabilidad (semilla fija en tests)
    public Tablero(int filas, int columnas, int numMinas, Random rng) {
        if (filas <= 0 || columnas <= 0) throw new IllegalArgumentException("Dimensiones inválidas");
        if (numMinas < 0 || numMinas > filas * columnas) throw new IllegalArgumentException("Num minas inválido");

        this.filas = filas;
        this.columnas = columnas;
        this.numMinas = numMinas;

        this.celdas = new casilla[filas][columnas];
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                this.celdas[f][c] = new casilla();
            }
        }
        // Colocación de minas y cálculo de adyacencias se harán en iteraciones posteriores
    }

    public int getFilas() { return filas; }
    public int getColumnas() { return columnas; }
    public int getNumMinas() { return numMinas; }

    public casilla getCasilla(int fila, int col) {
        if (!esValida(fila, col)) throw new IllegalArgumentException("Coordenada fuera de rango");
        return celdas[fila][col];
    }

    private boolean esValida(int f, int c) {
        return f >= 0 && f < filas && c >= 0 && c < columnas;
    }
}
