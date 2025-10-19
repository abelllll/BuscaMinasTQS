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
}
