package com.abelg.buscaminas.Controlador;

import com.abelg.buscaminas.Modelo.Tablero;
import com.abelg.buscaminas.Modelo.casilla;
import com.abelg.buscaminas.Vista.Vista;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class juegoTest {

    // Vista “fake” para testear sin I/O real
    static class VistaFake implements Vista {
        @Override public void mostrarTablero(Tablero t) { /* no-op */ }
        @Override public void mostrarMensaje(String msg) { /* no-op */ }
    }

    @Test
    void testDescubrirMinaTerminaEnPerdida() {
        Tablero tablero = new Tablero(4, 4, 3, new Random(9)); // distribución reproducible
        Vista vista = new VistaFake();
        Juego juego = new Juego(tablero, vista);

        // Buscar una casilla minada para forzar la pérdida
        int mf = -1, mc = -1;
        outer:
        for (int f=0; f<tablero.getFilas(); f++) {
            for (int c=0; c<tablero.getColumnas(); c++) {
                if (tablero.getCasilla(f,c).isMinada()) { mf=f; mc=c; break outer; }
            }
        }
        assertTrue(mf>=0, "Debe existir una mina en el tablero de prueba");

        juego.procesarDescubrir(mf, mc);

        assertTrue(juego.isTerminado(), "La partida debe terminar al descubrir mina");
        assertEquals(EstadoPartida.PERDIDA, juego.getEstado(), "Estado debe ser PERDIDA");
    }
    @Test
void testVictoriaCuandoTodasNoMinadasDescubiertas() {
    // Tablero pequeño con 1 mina
    Tablero tablero = new Tablero(3, 3, 1, new Random(5));
    Vista vista = new VistaFake();
    Juego juego = new Juego(tablero, vista);

    // Descubre todas las casillas NO minadas
    for (int f=0; f<tablero.getFilas(); f++) {
        for (int c=0; c<tablero.getColumnas(); c++) {
            if (!tablero.getCasilla(f,c).isMinada()) {
                juego.procesarDescubrir(f, c);
            }
        }
    }

    assertTrue(juego.isTerminado(), "Debe terminar cuando todas las no minadas estén descubiertas");
    assertEquals(EstadoPartida.GANADA, juego.getEstado(), "Estado debe ser GANADA");
}
// Caja negra + mock object – comprobar que marcar alterna el estado
@Test
void testMarcarAlternaMarcaYDesmarca() {
    Tablero tablero = new Tablero(2, 2, 0, new Random(1));
    Vista vista = new VistaFake();
    Juego juego = new Juego(tablero, vista);

    // Primera marca -> casilla marcada
    juego.procesarMarcar(0, 0);
    assertTrue(tablero.getCasilla(0,0).isMarcada(),
            "La casilla debe quedar marcada tras la primera llamada");

    // Segunda marca -> desmarca
    juego.procesarMarcar(0, 0);
    assertFalse(tablero.getCasilla(0,0).isMarcada(),
            "La casilla debe quedar desmarcada tras la segunda llamada");
}

// Caja negra – no se debe poder marcar una casilla descubierta
@Test
void testNoMarcaCasillaDescubierta() {
    Tablero tablero = new Tablero(2, 2, 0, new Random(1));
    Vista vista = new VistaFake();
    Juego juego = new Juego(tablero, vista);

    juego.procesarDescubrir(0, 0);
    assertTrue(tablero.getCasilla(0,0).isDescubierta(),
            "La casilla debe estar descubierta antes de intentar marcar");

    juego.procesarMarcar(0, 0);
    assertFalse(tablero.getCasilla(0,0).isMarcada(),
            "No se debe marcar una casilla ya descubierta");
}

// Caja blanca – decisión/condición en procesarDescubrir (terminado = true)
@Test
void testProcesarDescubrirNoHaceNadaSiPartidaTerminada() {
    // Tablero pequeño con todas las casillas minadas -> cualquier descubrimiento pierde
    Tablero tablero = new Tablero(2, 2, 4, new Random(1));
    Vista vista = new VistaFake();
    Juego juego = new Juego(tablero, vista);

    // Primera jugada: descubrir una mina -> termina en PERDIDA
    juego.procesarDescubrir(0, 0);
    assertTrue(juego.isTerminado(), "La partida debe estar terminada");
    EstadoPartida estadoAntes = juego.getEstado();

    // Segunda llamada: la rama if(terminado) debe ejecutarse y no cambiar nada
    assertDoesNotThrow(() -> juego.procesarDescubrir(1, 1),
            "No debe lanzar excepción al llamar después de terminar");
    assertEquals(estadoAntes, juego.getEstado(),
            "El estado no debe cambiar al intentar descubrir con la partida terminada");
}

}
