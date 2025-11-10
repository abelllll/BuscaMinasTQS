package com.abelg.buscaminas.Controlador;

import com.abelg.buscaminas.Modelo.Tablero;
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
        for (int f = 0; f < tablero.getFilas(); f++) {
            for (int c = 0; c < tablero.getColumnas(); c++) {
                if (tablero.getCasilla(f, c).isMinada()) { mf = f; mc = c; break outer; }
            }
        }
        assertTrue(mf >= 0, "Debe existir una mina en el tablero de prueba");

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
        for (int f = 0; f < tablero.getFilas(); f++) {
            for (int c = 0; c < tablero.getColumnas(); c++) {
                if (!tablero.getCasilla(f, c).isMinada()) {
                    juego.procesarDescubrir(f, c);
                }
            }
        }

        assertTrue(juego.isTerminado(), "Debe terminar cuando todas las no minadas estén descubiertas");
        assertEquals(EstadoPartida.GANADA, juego.getEstado(), "Estado debe ser GANADA");
    }

    @Test
    void testDescubrirNoFinaliza_partidaSigueEnCurso() {
        // Caso EN_CURSO: descubrir una casilla no minada sin completar la victoria
        Tablero tablero = new Tablero(3, 3, 1, new Random(123));
        Vista vista = new VistaFake();
        Juego juego = new Juego(tablero, vista);

        // Buscar una casilla NO minada para no terminar la partida
        int rNoMina = -1, cNoMina = -1;
        outer:
        for (int r = 0; r < tablero.getFilas(); r++) {
            for (int c = 0; c < tablero.getColumnas(); c++) {
                if (!tablero.getCasilla(r, c).isMinada()) { rNoMina = r; cNoMina = c; break outer; }
            }
        }
        assertTrue(rNoMina >= 0, "Debe existir alguna casilla no minada");

        juego.procesarDescubrir(rNoMina, cNoMina);

        assertFalse(juego.isTerminado(), "No debe terminar la partida");
        assertEquals(EstadoPartida.EN_CURSO, juego.getEstado(), "Estado debe ser EN_CURSO");
    }
}
