package com.abelg.buscaminas.Controlador;

import com.abelg.buscaminas.Modelo.RandomStub;
import com.abelg.buscaminas.Modelo.Tablero;
import com.abelg.buscaminas.Vista.Vista;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test que usa RandomStub como mock object adicional para controlar
 * dónde se colocan las minas y poder probar el Juego sin azar.
 */
public class JuegoRandomStubTest {

    // Vista mínima para este test (mock necesario)
    static class VistaFake implements Vista {
        @Override public void mostrarTablero(Tablero t) {}
        @Override public void mostrarMensaje(String msg) {}
    }

    @Test
    void testJuegoConRandomStubColocaMinaEnPosicionControlada() {
        // RandomStub fuerza que la primera mina se coloque en (0,0)
        RandomStub randomStub = new RandomStub(0, 0);
        Tablero tablero = new Tablero(2, 2, 1, randomStub);
        Vista vista = new VistaFake();
        Juego juego = new Juego(tablero, vista);

        // Comprobamos que hay una mina en (0,0)
        assertTrue(tablero.getCasilla(0,0).isMinada(),
                "Con RandomStub(0,0) la primera mina debería estar en (0,0)");

        // Descubrir otra casilla segura no debe terminar la partida
        juego.procesarDescubrir(1, 1);
        assertFalse(juego.isTerminado(),
                "Descubrir una casilla segura no debe terminar la partida");
    }
}
