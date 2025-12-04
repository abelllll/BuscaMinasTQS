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
// Caja negra – valores límite y frontera para el constructor de Tablero
@Test
void testConstructorDimensionesInvalidasLanzaExcepcion() {
    // filas o columnas <= 0
    assertThrows(IllegalArgumentException.class,
            () -> new Tablero(0, 1, 0, new Random(1)));
    assertThrows(IllegalArgumentException.class,
            () -> new Tablero(1, 0, 0, new Random(1)));
    assertThrows(IllegalArgumentException.class,
            () -> new Tablero(-1, 3, 0, new Random(1)));
    assertThrows(IllegalArgumentException.class,
            () -> new Tablero(3, -1, 0, new Random(1)));
}

@Test
void testConstructorNumMinasInvalidoLanzaExcepcion() {
    // numMinas negativo
    assertThrows(IllegalArgumentException.class,
            () -> new Tablero(2, 2, -1, new Random(1)));
    // numMinas mayor que filas*columnas
    assertThrows(IllegalArgumentException.class,
            () -> new Tablero(2, 2, 5, new Random(1))); // 2*2 = 4
}

// Caja negra – valores límite: tablero 1x1 con 0 y 1 mina
@Test
void testConstructorTablero1x1ValoresLimite() {
    Tablero sinMinas = new Tablero(1, 1, 0, new Random(1));
    assertEquals(1, sinMinas.getFilas());
    assertEquals(1, sinMinas.getColumnas());
    assertEquals(0, sinMinas.getNumMinas());
    assertFalse(sinMinas.getCasilla(0,0).isMinada());

    Tablero conMina = new Tablero(1, 1, 1, new Random(1));
    assertEquals(1, conMina.getFilas());
    assertEquals(1, conMina.getColumnas());
    assertEquals(1, conMina.getNumMinas());
    assertTrue(conMina.getCasilla(0,0).isMinada());
}
// Loop testing – bucle while de colocarMinas con 0, 1 y N iteraciones
@Test
void testColocarMinasLoop_0_1_N() {
    // 0 minas: el bucle while no debería colocar ninguna
    Tablero sinMinas = new Tablero(3, 3, 0, new Random(1));
    int minas0 = contarMinasEnTablero(sinMinas);
    assertEquals(0, minas0, "Con 0 minas no debería haber ninguna casilla minada");

    // 1 mina: el bucle itera al menos una vez
    Tablero unaMina = new Tablero(3, 3, 1, new Random(1));
    int minas1 = contarMinasEnTablero(unaMina);
    assertEquals(1, minas1, "Debe colocarse exactamente 1 mina");

    // N minas: el bucle itera varias veces hasta colocar N minas
    Tablero variasMinas = new Tablero(3, 3, 5, new Random(1));
    int minasN = contarMinasEnTablero(variasMinas);
    assertEquals(5, minasN, "Debe colocarse exactamente 5 minas");
}

// Método auxiliar privado para contar minas en un tablero (solo para estos tests)
private int contarMinasEnTablero(Tablero tablero) {
    int count = 0;
    for (int f = 0; f < tablero.getFilas(); f++) {
        for (int c = 0; c < tablero.getColumnas(); c++) {
            if (tablero.getCasilla(f, c).isMinada()) {
                count++;
            }
        }
    }
    return count;
}
// Path coverage – casilla marcada: descubrir no hace nada
@Test
void testDescubrirCasillaMarcadaNoLaDescubre() {
    Tablero tablero = new Tablero(3, 3, 1, new Random(10));
    tablero.marcar(0, 0);
    assertTrue(tablero.getCasilla(0,0).isMarcada());

    tablero.descubrir(0, 0);

    assertFalse(tablero.getCasilla(0,0).isDescubierta(),
            "Una casilla marcada no debe descubrirse");
}

// Path coverage – casilla ya descubierta: segunda llamada no cambia el estado
@Test
void testDescubrirCasillaYaDescubiertaNoCambiaEstado() {
    Tablero tablero = new Tablero(3, 3, 0, new Random(1)); // sin minas
    tablero.descubrir(1, 1);
    assertTrue(tablero.getCasilla(1,1).isDescubierta());

    boolean[][] estadoAntes = new boolean[tablero.getFilas()][tablero.getColumnas()];
    for (int f = 0; f < tablero.getFilas(); f++) {
        for (int c = 0; c < tablero.getColumnas(); c++) {
            estadoAntes[f][c] = tablero.getCasilla(f, c).isDescubierta();
        }
    }

    tablero.descubrir(1, 1); // segunda llamada

    for (int f = 0; f < tablero.getFilas(); f++) {
        for (int c = 0; c < tablero.getColumnas(); c++) {
            assertEquals(estadoAntes[f][c],
                    tablero.getCasilla(f, c).isDescubierta(),
                    "Descubrir una casilla ya descubierta no debe cambiar nada");
        }
    }
}

// Path coverage – descubrir una mina: solo esa casilla se descubre, sin cascada
@Test
void testDescubrirMinaNoProvocaCascada() {
    Tablero tablero = new Tablero(4, 4, 3, new Random(2));

    int mf = -1, mc = -1;
    outer:
    for (int f = 0; f < tablero.getFilas(); f++) {
        for (int c = 0; c < tablero.getColumnas(); c++) {
            if (tablero.getCasilla(f,c).isMinada()) {
                mf = f;
                mc = c;
                break outer;
            }
        }
    }
    assertTrue(mf >= 0 && mc >= 0, "Debe existir una mina en el tablero");

    tablero.descubrir(mf, mc);

    for (int f = 0; f < tablero.getFilas(); f++) {
        for (int c = 0; c < tablero.getColumnas(); c++) {
            if (f == mf && c == mc) {
                assertTrue(tablero.getCasilla(f,c).isDescubierta(),
                        "La mina descubierta debe estar marcada como descubierta");
            } else {
                assertFalse(tablero.getCasilla(f,c).isDescubierta(),
                        "Descubrir una mina no debe descubrir las demás casillas");
            }
        }
    }
}

// Path coverage – casilla segura con minas adyacentes > 0: no hay cascada
@Test
void testDescubrirConMinasAdyacentesNoHaceCascada() {
    Tablero tablero = new Tablero(4, 4, 3, new Random(3));

    int sf = -1, sc = -1;
    outer:
    for (int f = 0; f < tablero.getFilas(); f++) {
        for (int c = 0; c < tablero.getColumnas(); c++) {
            casilla celda = tablero.getCasilla(f,c);
            if (!celda.isMinada() && celda.getMinasAdyacentes() > 0) {
                sf = f;
                sc = c;
                break outer;
            }
        }
    }
    assertTrue(sf >= 0 && sc >= 0, "Debe existir una casilla segura con minas adyacentes > 0");

    tablero.descubrir(sf, sc);

    for (int f = 0; f < tablero.getFilas(); f++) {
        for (int c = 0; c < tablero.getColumnas(); c++) {
            if (f == sf && c == sc) {
                assertTrue(tablero.getCasilla(f,c).isDescubierta());
            } else {
                assertFalse(tablero.getCasilla(f,c).isDescubierta(),
                        "No debe haber cascada cuando hay minas adyacentes > 0");
            }
        }
    }
}

// Path coverage – casilla segura con 0 minas adyacentes: sí hay cascada
@Test
void testDescubrirSinMinasAdyacentesHaceCascada() {
    Tablero tablero = new Tablero(4, 4, 1, new Random(23));

    int sf = -1, sc = -1;
    outer:
    for (int f = 0; f < tablero.getFilas(); f++) {
        for (int c = 0; c < tablero.getColumnas(); c++) {
            casilla celda = tablero.getCasilla(f,c);
            if (!celda.isMinada() && celda.getMinasAdyacentes() == 0) {
                sf = f;
                sc = c;
                break outer;
            }
        }
    }
    assertTrue(sf >= 0 && sc >= 0, "Debe existir una casilla segura con 0 minas adyacentes");

    tablero.descubrir(sf, sc);

    int descubiertas = 0;
    for (int f = 0; f < tablero.getFilas(); f++) {
        for (int c = 0; c < tablero.getColumnas(); c++) {
            if (tablero.getCasilla(f,c).isDescubierta()) {
                descubiertas++;
            }
        }
    }

    assertTrue(descubiertas > 1,
            "La cascada debe descubrir varias casillas cuando no hay minas adyacentes");
}
    // Caja negra – condición de victoria en el modelo:
    // true solo cuando TODAS las casillas no minadas están descubiertas
    @Test
    void testTodasNoMinadasDescubiertasSoloCuandoTodasSegurasDescubiertas() {
        Tablero tablero = new Tablero(2, 2, 1, new Random(1));

        // Al inicio no debería haber victoria
        assertFalse(tablero.todasNoMinadasDescubiertas(),
                "Al inicio no debería considerarse victoria");

        // Descubrimos SOLO una casilla segura
        boolean descubiertaUna = false;
        outer:
        for (int f = 0; f < tablero.getFilas(); f++) {
            for (int c = 0; c < tablero.getColumnas(); c++) {
                if (!tablero.getCasilla(f, c).isMinada()) {
                    tablero.descubrir(f, c);
                    descubiertaUna = true;
                    break outer;
                }
            }
        }
        assertTrue(descubiertaUna, "Debe haberse descubierto al menos una casilla segura");
        assertFalse(tablero.todasNoMinadasDescubiertas(),
                "Con casillas seguras sin descubrir aún no debe haber victoria");

        // Descubrimos el RESTO de casillas no minadas
        for (int f = 0; f < tablero.getFilas(); f++) {
            for (int c = 0; c < tablero.getColumnas(); c++) {
                if (!tablero.getCasilla(f, c).isMinada()
                        && !tablero.getCasilla(f, c).isDescubierta()) {
                    tablero.descubrir(f, c);
                }
            }
        }

        // Ahora sí: todas las no minadas están descubiertas
        assertTrue(tablero.todasNoMinadasDescubiertas(),
                "Cuando todas las casillas seguras están descubiertas debe devolver true");
    }






}
