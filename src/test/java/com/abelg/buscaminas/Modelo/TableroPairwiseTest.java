package com.abelg.buscaminas.Modelo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test data-driven + pairwise para el constructor de Tablero.
 *
 * Valores posibles:
 *  filas:   {1, 3, 5}
 *  columnas:{1, 4}
 *  minas:   {0, 1, 4}
 *  semilla: {1, 7}
 *
 * El conjunto de casos se ha construido manualmente siguiendo un
 * esquema pairwise para que, para cualquier pareja de parámetros,
 * exista al menos una combinación donde aparecen juntos.
 */
public class TableroPairwiseTest {

    static Stream<org.junit.jupiter.params.provider.Arguments> parametrosTablero() {
        return Stream.of(
                // filas, columnas, minas, semilla
                org.junit.jupiter.params.provider.Arguments.of(1, 1, 0, 1),
                org.junit.jupiter.params.provider.Arguments.of(1, 4, 1, 7),
                org.junit.jupiter.params.provider.Arguments.of(3, 1, 1, 7),
                org.junit.jupiter.params.provider.Arguments.of(3, 4, 4, 1),
                org.junit.jupiter.params.provider.Arguments.of(5, 1, 4, 1),
                org.junit.jupiter.params.provider.Arguments.of(5, 4, 0, 7),
                org.junit.jupiter.params.provider.Arguments.of(1, 1, 1, 7),
                org.junit.jupiter.params.provider.Arguments.of(5, 4, 1, 1)
        );
    }

    @ParameterizedTest(name = "filas={0}, columnas={1}, minas={2}, seed={3}")
    @MethodSource("parametrosTablero")
    void testConstructorTableroPairwise(int filas, int columnas, int minas, int seed) {
        Tablero tablero = new Tablero(filas, columnas, minas, new Random(seed));

        // Comprobación básica de dimensiones y número de minas
        assertEquals(filas, tablero.getFilas());
        assertEquals(columnas, tablero.getColumnas());
        assertEquals(minas, tablero.getNumMinas());

        // Caja negra: número real de minas colocadas
        int minasEncontradas = 0;
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (tablero.getCasilla(f, c).isMinada()) {
                    minasEncontradas++;
                }
            }
        }
        assertEquals(minas, minasEncontradas,
                "El número de minas colocadas debe coincidir con el solicitado");
    }
}
