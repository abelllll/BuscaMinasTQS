package com.abelg.buscaminas.Modelo;

import java.util.Random;

public class Tablero {
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

        // Paso 2B: colocar minas (determinista con la semilla de rng)
        colocarMinas(rng);
        // El cálculo de adyacencias se hará en el siguiente paso (Etapa 3)
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

    // --- Nuevo en Paso 2B ---
    private void colocarMinas(Random rng) {
        int colocadas = 0;
        while (colocadas < numMinas) {
            int f = rng.nextInt(filas);
            int c = rng.nextInt(columnas);
            if (!celdas[f][c].isMinada()) {
                celdas[f][c].setMinada(true);
                colocadas++;
            }
        }
    }
}
