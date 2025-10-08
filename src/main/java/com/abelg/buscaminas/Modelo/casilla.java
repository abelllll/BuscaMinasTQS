package com.abelg.buscaminas.Modelo;

public class casilla {
    private boolean minada;
    private boolean descubierta;
    private boolean marcada;
    private int minasAdyacentes;

    // Constructor por defecto
    public casilla() {
        this.minada = false;
        this.descubierta = false;
        this.marcada = false;
        this.minasAdyacentes = 0;
    }

    // Getters y setters
    public boolean isMinada() {
        return minada;
    }

    public void setMinada(boolean minada) {
        this.minada = minada;
    }

    public boolean isDescubierta() {
        return descubierta;
    }

    public void setDescubierta(boolean descubierta) {
        this.descubierta = descubierta;
    }

    public boolean isMarcada() {
        return marcada;
    }

    public void setMarcada(boolean marcada) {
        this.marcada = marcada;
    }

    public int getMinasAdyacentes() {
        return minasAdyacentes;
    }

    public void setMinasAdyacentes(int minasAdyacentes) {
        this.minasAdyacentes = minasAdyacentes;
    }

    // Método para descubrir la casilla
    public void descubrir() {
        if (!marcada) {
            this.descubierta = true;
        }
    }

    // Método para marcar la casilla
    public void marcar() {
        if (!descubierta) {
            this.marcada = !this.marcada;
        }
    }
}
