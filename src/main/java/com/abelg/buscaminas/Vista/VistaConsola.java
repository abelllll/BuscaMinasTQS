package com.abelg.buscaminas.Vista;

import com.abelg.buscaminas.Modelo.Tablero;

public class VistaConsola implements Vista {

    @Override
    public void mostrarTablero(Tablero t) {
        StringBuilder sb = new StringBuilder();

        // Cabecera de columnas
        sb.append("   ");
        for (int c = 0; c < t.getColumnas(); c++) {
            sb.append(String.format("%2d ", c));
        }
        sb.append("\n");

        for (int f = 0; f < t.getFilas(); f++) {
            sb.append(String.format("%2d ", f)); // índice de fila
            for (int c = 0; c < t.getColumnas(); c++) {
                var celda = t.getCasilla(f, c);
                char ch;
                if (!celda.isDescubierta()) {
                    ch = celda.isMarcada() ? 'F' : '■'; // Oculta: marcada o no
                } else if (celda.isMinada()) {
                    ch = '*'; // Minas descubiertas (solo al final de la partida)
                } else {
                    int n = celda.getMinasAdyacentes();
                    ch = (n == 0) ? ' ' : Character.forDigit(n, 10);
                }
                sb.append(' ').append(ch).append(' ');
            }
            sb.append('\n');
        }

        System.out.print(sb.toString());
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }
}
