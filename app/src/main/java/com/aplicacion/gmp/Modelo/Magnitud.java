package com.aplicacion.gmp.Modelo;

public class Magnitud {
    private String Prefijo;
    private Double valor;

    public Magnitud() {
    }

    public String getPrefijo() {
        return Prefijo;
    }

    public void setPrefijo(String prefijo) {
        Prefijo = prefijo;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}
