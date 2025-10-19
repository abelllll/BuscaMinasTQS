package com.abelg.buscaminas.Modelo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Random;

public class TableroTest {

    @Test
    void testInicializacionDimensionesYCeldas() {
        int filas = 5, columnas = 6, minas = 0;
        Random rng = new Random(42); // Semilla fija para pruebas
        Tablero t = new Tablero(filas, columnas, minas, rng);

        assertEquals(filas, t.getFilas(), "Filas incorrectas");
        assertEquals(columnas, t.getColumnas(), "Columnas incorrectas");
        assertNotNull(t.getCasilla(0,0), "Debe existir casilla (0,0)");
        assertNotNull(t.getCasilla(filas-1, columnas-1), "Debe existir casilla (última)");
        // Aún no comprobamos minas ni adyacencias
    }

    @Test
    void testColocacionDeMinasCuentaExacta() {
    int filas = 8, columnas = 8, minas = 10;
    Random rng = new Random(7); // semilla fija para reproducibilidad
    Tablero t = new Tablero(filas, columnas, minas, rng);

    int totalMinas = 0;
    for (int f = 0; f < filas; f++) {
        for (int c = 0; c < columnas; c++) {
            if (t.getCasilla(f,c).isMinada()) totalMinas++;
        }
    }
    assertEquals(minas, totalMinas, "Debe haber exactamente N minas");
    }

    @Test
    void testMinasAdyacentesCalculadas() {
    int filas = 3, columnas = 3, minas = 1;
    Random rng = new Random(1); // Semilla fija para reproducibilidad
    Tablero t = new Tablero(filas, columnas, minas, rng);

    // Ubicamos la casilla que es mina
    int mf = -1, mc = -1;
    for (int f = 0; f < filas; f++) {
        for (int c = 0; c < columnas; c++) {
            if (t.getCasilla(f, c).isMinada()) {
                mf = f; mc = c;
                break;
            }
        }
        if (mf != -1) break;
    }
    assertTrue(mf >= 0, "Debe haberse colocado una mina");

    // Todos los vecinos no minados deben tener al menos 1 adyacente
    int[] df = {-1,-1,-1, 0,0, 1,1,1};
    int[] dc = {-1, 0, 1,-1,1,-1,0,1};

    for (int i = 0; i < 8; i++) {
        int nf = mf + df[i], nc = mc + dc[i];
        if (nf >= 0 && nf < filas && nc >= 0 && nc < columnas && !t.getCasilla(nf, nc).isMinada()) {
            assertTrue(t.getCasilla(nf, nc).getMinasAdyacentes() >= 1,
                "Vecino de mina debe tener al menos 1 adyacente");
        }
    }
    }


}
