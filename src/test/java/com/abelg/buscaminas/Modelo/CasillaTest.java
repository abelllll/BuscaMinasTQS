package com.abelg.buscaminas.Modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CasillaTest {

    // Test de Caja Negra: Verifica los valores iniciales de la casilla.
    // En este caso, verificamos que la casilla se inicializa con valores predeterminados.
    @Test
    public void testInicializacionCasilla() {
        casilla c = new casilla();
        assertFalse(c.isMinada(), "La casilla no debe estar minada por defecto.");
        assertFalse(c.isDescubierta(), "La casilla no debe estar descubierta por defecto.");
        assertFalse(c.isMarcada(), "La casilla no debe estar marcada por defecto.");
        assertEquals(0, c.getMinasAdyacentes(), "El número de minas adyacentes debe ser 0 por defecto.");
    }

    // Test de Caja Blanca: Verifica la lógica interna de la casilla al ser descubierta.
    // Este test cubre una decisión interna, asegurando que se pueda cambiar el estado de descubierta.
    @Test
    public void testDescubrirCasilla() {
        casilla c = new casilla();
        c.descubrir();
        assertTrue(c.isDescubierta(), "La casilla debe estar descubierta después de llamar al método descubrir.");
    }

    // Test de Caja Blanca: Verifica la lógica interna de la casilla al ser marcada o desmarcada.
    // Este test cubre las condiciones en las que la casilla puede ser marcada y desmarcada.
    @Test
    public void testMarcarCasilla() {
        casilla c = new casilla();
        
        // Marca la casilla
        c.marcar();
        assertTrue(c.isMarcada(), "La casilla debe estar marcada después de llamar a marcar.");
        
        // Desmarca la casilla
        c.marcar();
        assertFalse(c.isMarcada(), "La casilla debe estar desmarcada después de llamar a marcar nuevamente.");
    }
}
