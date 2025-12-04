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
        if (terminado) {
            return;
        }

        tablero.descubrir(f, c);

        // Si la casilla descubierta es una mina -> derrota
        if (tablero.getCasilla(f, c).isMinada()) {
            this.estado = EstadoPartida.PERDIDA;
            this.terminado = true;
            vista.mostrarMensaje("Has perdido");
            vista.mostrarTablero(tablero);
            return;
        }

        // Actualizar vista tras descubrir una casilla segura
        vista.mostrarTablero(tablero);

        // Preguntar al modelo si ya se cumple la condición de victoria
        if (tablero.todasNoMinadasDescubiertas()) {
            this.estado = EstadoPartida.GANADA;
            this.terminado = true;
            vista.mostrarMensaje("¡Has ganado!");
            vista.mostrarTablero(tablero);
        }
    }


    public void procesarMarcar(int f, int c) {
        if (terminado) return;
        tablero.marcar(f, c);
        vista.mostrarTablero(tablero);
    }

    
}
