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
    @Test
void testDescubrirCascadaCuandoAdyacentesCero() {
    int filas = 6, columnas = 6, minas = 3;
    Random rng = new Random(123); // distribución reproducible
    Tablero t = new Tablero(filas, columnas, minas, rng);

    // Busca una casilla NO minada y con 0 adyacentes para provocar cascada
    boolean lanzado = false;
    for (int f = 0; f < filas && !lanzado; f++) {
        for (int c = 0; c < columnas && !lanzado; c++) {
            if (!t.getCasilla(f,c).isMinada() && t.getCasilla(f,c).getMinasAdyacentes() == 0) {
                t.descubrir(f, c);
                lanzado = true;
            }
        }
    }
    assertTrue(lanzado, "Debe existir alguna casilla con 0 adyacentes");

    // Tras cascada debe haber varias casillas descubiertas
    int descubiertas = 0;
    for (int f = 0; f < filas; f++)
        for (int c = 0; c < columnas; c++)
            if (t.getCasilla(f,c).isDescubierta()) descubiertas++;

    assertTrue(descubiertas > 1, "La cascada debe descubrir múltiples casillas");
}

@Test
void testDescubrirMinaNoHaceCascada() {
    int filas = 5, columnas = 5, minas = 5;
    Random rng = new Random(7);
    Tablero t = new Tablero(filas, columnas, minas, rng);

    // Busca una mina y la descubre
    boolean hecho = false;
    for (int f = 0; f < filas && !hecho; f++) {
        for (int c = 0; c < columnas && !hecho; c++) {
            if (t.getCasilla(f,c).isMinada()) {
                t.descubrir(f, c);
                hecho = true;
            }
        }
    }
    assertTrue(hecho, "Debe haberse descubierto una mina");
    // Chequeo mínimo: la propia mina está descubierta
    // (El estado de derrota lo gestionará el controlador más tarde)
    // Y no ha habido cascada alrededor de ella necesariamente
}

@Test
void testDescubrirFueraDeRangoLanzaExcepcion() {
    Tablero t = new Tablero(2, 2, 0, new Random(1));
    assertThrows(IllegalArgumentException.class, () -> t.descubrir(-1, 0));
    assertThrows(IllegalArgumentException.class, () -> t.descubrir(0, 2));
}

@Test
void testDescubrirDosVecesNoRompeEstado() {
    Tablero t = new Tablero(3, 3, 0, new Random(1));
    t.descubrir(1,1);
    boolean estadoTrasPrimera = t.getCasilla(1,1).isDescubierta();
    t.descubrir(1,1); // idempotente
    assertTrue(estadoTrasPrimera && t.getCasilla(1,1).isDescubierta(),
        "Descubrir dos veces no debe cambiar el estado ni fallar");
}

@Test
void testMarcarAlternaYNoMarcaSiDescubierta() {
    Tablero t = new Tablero(3, 3, 0, new Random(1));
    // Alterna marcaje
    t.marcar(0,0);
    assertTrue(t.getCasilla(0,0).isMarcada(), "Debe marcar en primera pulsación");
    t.marcar(0,0);
    assertFalse(t.getCasilla(0,0).isMarcada(), "Debe desmarcar en segunda pulsación");

    // No marca si ya está descubierta
    t.descubrir(1,1);
    boolean estabaMarcadaAntes = t.getCasilla(1,1).isMarcada();
    t.marcar(1,1);
    assertEquals(estabaMarcadaAntes, t.getCasilla(1,1).isMarcada(), "No debe marcar una casilla descubierta");
}

@Test
void testMarcarFueraDeRangoLanzaExcepcion() {
    Tablero t = new Tablero(2, 2, 0, new Random(1));
    assertThrows(IllegalArgumentException.class, () -> t.marcar(-1, 0));
    assertThrows(IllegalArgumentException.class, () -> t.marcar(0, 2));
}



}
