package com.abelg.buscaminas.Modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CasillaTest {

    // Caja negra: valores iniciales por defecto
    @Test
    public void testInicializacionCasilla() {
        casilla c = new casilla();
        assertFalse(c.isMinada(), "La casilla no debe estar minada por defecto.");
        assertFalse(c.isDescubierta(), "La casilla no debe estar descubierta por defecto.");
        assertFalse(c.isMarcada(), "La casilla no debe estar marcada por defecto.");
        assertEquals(0, c.getMinasAdyacentes(), "El número de minas adyacentes debe ser 0 por defecto.");
    }

    // Caja blanca: descubrir cambia el estado a descubierta
    @Test
    public void testDescubrirCasilla() {
        casilla c = new casilla();
        c.descubrir();
        assertTrue(c.isDescubierta(), "La casilla debe estar descubierta tras descubrir.");
    }

    // Caja blanca: marcar alterna y no afecta al estado de 'descubierta'
    @Test
    public void testMarcarCasilla() {
        casilla c = new casilla();

        // Marca
        c.marcar();
        assertTrue(c.isMarcada(), "La casilla debe estar marcada después de marcar.");

        // Desmarca
        c.marcar();
        assertFalse(c.isMarcada(), "La casilla debe estar desmarcada después de marcar de nuevo.");
    }

    // Cobertura de getters/setters de minas adyacentes
    @Test
    public void testSetGetMinasAdyacentes() {
        casilla c = new casilla();
        c.setMinasAdyacentes(3);
        assertEquals(3, c.getMinasAdyacentes());
    }

    // Marcar cuando ya está descubierta NO debe cambiar el marcado
    @Test
    public void testMarcarCuandoDescubiertaNoCambia() {
        casilla c = new casilla();
        c.descubrir();
        boolean antes = c.isMarcada();
        c.marcar();
        assertEquals(antes, c.isMarcada(), "No debe cambiar el marcado si la casilla está descubierta");
        assertTrue(c.isDescubierta(), "Descubierta debe permanecer true");
    }
}
