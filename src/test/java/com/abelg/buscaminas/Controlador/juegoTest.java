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
    // Vista "spy" para verificar las interacciones entre Juego y Vista
static class VistaSpy implements Vista {
    Tablero ultimoTableroMostrado;
    String ultimoMensaje;
    int numVecesMostrarTablero = 0;
    int numVecesMostrarMensaje = 0;

    @Override
    public void mostrarTablero(Tablero t) {
        ultimoTableroMostrado = t;
        numVecesMostrarTablero++;
    }

    @Override
    public void mostrarMensaje(String msg) {
        ultimoMensaje = msg;
        numVecesMostrarMensaje++;
    }
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
        // Caja negra – descubrir una casilla segura NO debe terminar la partida
    @Test
    void testDescubrirCasillaSeguraMantienePartidaEnCurso() {
        Tablero tablero = new Tablero(2, 2, 1, new Random(3)); // 1 mina
        Vista vista = new VistaFake();
        Juego juego = new Juego(tablero, vista);

        // Buscamos una casilla NO minada y la descubrimos
        boolean descubierta = false;
        outer:
        for (int f = 0; f < tablero.getFilas(); f++) {
            for (int c = 0; c < tablero.getColumnas(); c++) {
                if (!tablero.getCasilla(f, c).isMinada()) {
                    juego.procesarDescubrir(f, c);
                    descubierta = true;
                    break outer;
                }
            }
        }

        assertTrue(descubierta, "Debe haberse descubierto al menos una casilla segura");
        assertFalse(juego.isTerminado(),
                "Tras descubrir una casilla segura sin completar el tablero la partida debe seguir en curso");
        assertEquals(EstadoPartida.EN_CURSO, juego.getEstado(),
                "El estado debe seguir siendo EN_CURSO tras descubrir solo una casilla segura");
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
// Mock object + caja blanca – comprobar mensaje y tablero al perder
@Test
void testVistaRecibeMensajePerdidaYTablero() {
    Tablero tablero = new Tablero(3, 3, 2, new Random(7));
    VistaSpy vista = new VistaSpy();
    Juego juego = new Juego(tablero, vista);

    // Buscar una mina para forzar la derrota
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

    juego.procesarDescubrir(mf, mc);

    assertEquals(EstadoPartida.PERDIDA, juego.getEstado());
    assertEquals("Has perdido", vista.ultimoMensaje,
            "Al perder se debe mostrar el mensaje de derrota");
    assertEquals(1, vista.numVecesMostrarMensaje,
            "Al perder debe mostrarse exactamente un mensaje");
    assertEquals(1, vista.numVecesMostrarTablero,
            "Al perder debe mostrarse el tablero una vez");
    assertNotNull(vista.ultimoTableroMostrado,
            "El tablero mostrado no debe ser null");
}

// Mock object + caja blanca – comprobar mensaje y tablero al ganar
@Test
void testVistaRecibeMensajeVictoriaYTablero() {
    Tablero tablero = new Tablero(2, 2, 1, new Random(1));
    VistaSpy vista = new VistaSpy();
    Juego juego = new Juego(tablero, vista);

    // Descubrir todas las casillas no minadas
    for (int f = 0; f < tablero.getFilas(); f++) {
        for (int c = 0; c < tablero.getColumnas(); c++) {
            if (!tablero.getCasilla(f,c).isMinada()) {
                juego.procesarDescubrir(f, c);
            }
        }
    }

    assertEquals(EstadoPartida.GANADA, juego.getEstado(),
            "Al descubrir todas las casillas seguras la partida debe estar GANADA");
    assertEquals("¡Has ganado!", vista.ultimoMensaje,
            "Al ganar se debe mostrar el mensaje de victoria");
    assertTrue(vista.numVecesMostrarTablero >= 1,
            "Durante la partida y al ganar se debe mostrar el tablero al menos una vez");
    assertTrue(vista.numVecesMostrarMensaje >= 1,
            "Debe haberse mostrado al menos el mensaje de victoria");
}

// Caja blanca – rama if(terminado) en procesarMarcar no llama a la vista
@Test
void testProcesarMarcarNoLlamaVistaSiPartidaTerminada() {
    Tablero tablero = new Tablero(2, 2, 4, new Random(42)); // todas minadas
    VistaSpy vista = new VistaSpy();
    Juego juego = new Juego(tablero, vista);

    // Primera jugada: descubrir una mina -> termina en PERDIDA
    juego.procesarDescubrir(0, 0);
    assertTrue(juego.isTerminado(), "La partida debe estar terminada");

    // Reseteamos contadores del spy para medir solo lo que pasa después
    vista.numVecesMostrarTablero = 0;
    vista.numVecesMostrarMensaje = 0;

    // Ahora procesarMarcar debe entrar en la rama if(terminado) y no tocar la vista
    juego.procesarMarcar(0, 1);

    assertEquals(0, vista.numVecesMostrarTablero,
            "No se debe mostrar el tablero al marcar tras terminar la partida");
    assertEquals(0, vista.numVecesMostrarMensaje,
            "No se deben mostrar mensajes al marcar tras terminar la partida");
}


}
