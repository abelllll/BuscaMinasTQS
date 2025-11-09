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

        // Paso 3B: calcular minas adyacentes tras colocar las minas
        calcularMinasAdyacentes();
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

    // --- Paso 2B ---
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

    // --- Paso 3B ---
    private void calcularMinasAdyacentes() {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (!celdas[f][c].isMinada()) {
                    celdas[f][c].setMinasAdyacentes(contarMinasAdyacentes(f, c));
                }
            }
        }
    }

    private int contarMinasAdyacentes(int f, int c) {
        int count = 0;
        for (int df = -1; df <= 1; df++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (df == 0 && dc == 0) continue;
                int nf = f + df, nc = c + dc;
                if (esValida(nf, nc) && celdas[nf][nc].isMinada()) {
                    count++;
                }
            }
        }
        return count;
    }
    public void descubrir(int f, int c) {
    if (!esValida(f, c)) throw new IllegalArgumentException("Coordenada fuera de rango");
    casilla celda = celdas[f][c];

    // No hacemos nada si ya está descubierta o si está marcada
    if (celda.isDescubierta() || celda.isMarcada()) return;

    // Descubrir esta casilla
    celda.descubrir();

    // Si es mina, no hay cascada (el controlador decidirá derrota)
    if (celda.isMinada()) return;

    // Si tiene 0 adyacentes, cascada en 8 direcciones
    if (celda.getMinasAdyacentes() == 0) {
        for (int df = -1; df <= 1; df++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (df == 0 && dc == 0) continue;
                int nf = f + df, nc = c + dc;
                if (esValida(nf, nc) && !celdas[nf][nc].isDescubierta() && !celdas[nf][nc].isMinada()) {
                    descubrir(nf, nc);
                }
            }
        }
    }
}

public void marcar(int f, int c) {
    if (!esValida(f, c)) throw new IllegalArgumentException("Coordenada fuera de rango");
    casilla celda = celdas[f][c];
    // No permitir marcar si ya está descubierta
    if (celda.isDescubierta()) return;
    // Alterna marcado con tu método existente
    celda.marcar();
}
}
