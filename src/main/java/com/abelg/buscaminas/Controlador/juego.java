package com.abelg.buscaminas.Controlador;

import com.abelg.buscaminas.Modelo.Tablero;
import com.abelg.buscaminas.Vista.Vista;

public class Juego {
    private final Tablero tablero;
    private final Vista vista;
    private EstadoPartida estado;
    private boolean terminado;

    public Juego(Tablero tablero, Vista vista) {
        if (tablero == null || vista == null) throw new IllegalArgumentException("Argumentos nulos");
        this.tablero = tablero;
        this.vista = vista;
        this.estado = EstadoPartida.EN_CURSO;
        this.terminado = false;
    }

    public boolean isTerminado() { return terminado; }
    public EstadoPartida getEstado() { return estado; }

    public void procesarDescubrir(int f, int c) {
        if (terminado) return;

        tablero.descubrir(f, c);

        if (tablero.getCasilla(f, c).isMinada()) {
            this.estado = EstadoPartida.PERDIDA;
            this.terminado = true;
            vista.mostrarMensaje("Has perdido");
            vista.mostrarTablero(tablero);
            return;
        }

        // Actualiza vista tras descubrir sin mina
        vista.mostrarTablero(tablero);

        // Comprobar si todas las no minadas están descubiertas -> victoria
        comprobarVictoria();
    }

    public void procesarMarcar(int f, int c) {
        if (terminado) return;
        tablero.marcar(f, c);
        vista.mostrarTablero(tablero);
    }

    // --- Comprobación de victoria ---
    private void comprobarVictoria() {
        for (int fila = 0; fila < tablero.getFilas(); fila++) {
            for (int col = 0; col < tablero.getColumnas(); col++) {
                // Si existe alguna casilla NO minada y NO descubierta -> no hay victoria aún
                if (!tablero.getCasilla(fila, col).isMinada()
                        && !tablero.getCasilla(fila, col).isDescubierta()) {
                    return;
                }
            }
        }
        // Si llegamos aquí, todas las no minadas están descubiertas
        this.estado = EstadoPartida.GANADA;
        this.terminado = true;
        vista.mostrarMensaje("¡Has ganado!");
        vista.mostrarTablero(tablero);
    }
}
