package com.abelg.buscaminas.Modelo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test data-driven para la clase casilla.
 * Cada fila representa una secuencia de operaciones:
 *
 *  marcar1, descubrir, marcar2, marcadaFinal, descubiertaFinal
 */
public class CasillaDataDrivenTest {

    @ParameterizedTest(name = "m1={0}, d={1}, m2={2} -> marcada={3}, descubierta={4}")
    @CsvSource({
        // Partición: casilla nunca descubierta
        // 1) marcar y desmarcar -> acaba sin marcar ni descubrir
        //   M: false -> true -> false, D: false
        "true,false,true,false,false",

        // 2) marcar una sola vez -> acaba marcada, no descubierta
        //   M: false -> true, D: false
        "true,false,false,true,false",

        // Partición: casilla marcada antes de intentar descubrir
        // 3) marcar, descubrir -> la marca impide descubrir
        //   M: false -> true, D: false
        "true,true,false,true,false",

        // 4) marcar, descubrir, marcar -> la marca impide descubrir,
        //    y la segunda llamada a marcar desmarca porque sigue sin estar descubierta
        //   M: false -> true -> false, D: false
        "true,true,true,false,false",

        // Partición: casilla descubierta antes de intentar marcar
        // 5) descubrir y luego marcar -> al estar descubierta ya no se puede marcar
        //   M: false, D: false -> true
        "false,true,true,false,true"
})

    void testSecuenciasMarcadoDescubierto(boolean marcar1,
                                          boolean descubrir,
                                          boolean marcar2,
                                          boolean marcadaFinal,
                                          boolean descubiertaFinal) {

        casilla c = new casilla();

        if (marcar1) {
            c.marcar();
        }
        if (descubrir) {
            c.descubrir();
        }
        if (marcar2) {
            c.marcar();
        }

        assertEquals(marcadaFinal, c.isMarcada(),
                "Estado final de marcada incorrecto para la secuencia probada");
        assertEquals(descubiertaFinal, c.isDescubierta(),
                "Estado final de descubierta incorrecto para la secuencia probada");
    }
}
