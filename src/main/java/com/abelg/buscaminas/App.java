package com.abelg.buscaminas;

import com.abelg.buscaminas.Controlador.EstadoPartida;
import com.abelg.buscaminas.Controlador.Juego;
import com.abelg.buscaminas.Modelo.Tablero;
import com.abelg.buscaminas.Vista.Vista;
import com.abelg.buscaminas.Vista.VistaConsola;

import java.util.Random;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        int filas = 9, columnas = 9, minas = 10;
        Tablero t = new Tablero(filas, columnas, minas, new Random());
        Vista v = new VistaConsola();
        Juego juego = new Juego(t, v);

        v.mostrarMensaje("Bienvenido a BuscaMinas (consola).");
        v.mostrarTablero(t);

        try (Scanner sc = new Scanner(System.in)) {
            while (!juego.isTerminado()) {
                System.out.print("Acción (d=descubrir, m=marcar) y coordenadas [f c]: ");
                String accion = sc.next();
                int f = sc.nextInt();
                int c = sc.nextInt();

                if (accion.equalsIgnoreCase("d")) {
                    try { juego.procesarDescubrir(f, c); }
                    catch (IllegalArgumentException e) { v.mostrarMensaje("Coordenadas inválidas."); }
                } else if (accion.equalsIgnoreCase("m")) {
                    try { juego.procesarMarcar(f, c); }
                    catch (IllegalArgumentException e) { v.mostrarMensaje("Coordenadas inválidas."); }
                } else {
                    v.mostrarMensaje("Acción no reconocida. Usa 'd' o 'm'.");
                }
            }
        }

        if (juego.getEstado() == EstadoPartida.GANADA) {
            v.mostrarMensaje("¡Partida GANADA!");
        } else if (juego.getEstado() == EstadoPartida.PERDIDA) {
            v.mostrarMensaje("Partida PERDIDA.");
        }
    }
}
