package com.aplicacion.gmp.Modelo;

import java.util.List;

public class Servicio {

    private String uid;
    private String codigo;
    private String matricula;
    private int repeticion;
    private int punto;
    private int prueba;
    private List<Magnitud> magnitudes;
    private Long dateTime;

    public Servicio() {
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public int getRepeticion() {
        return repeticion;
    }

    public void setRepeticion(int repeticion) {
        this.repeticion = repeticion;
    }

    public int getPunto() {
        return punto;
    }

    public void setPunto(int punto) {
        this.punto = punto;
    }

    public int getPrueba() {
        return prueba;
    }

    public void setPrueba(int prueba) {
        this.prueba = prueba;
    }

    public List<Magnitud> getMagnitudes() {
        return magnitudes;
    }

    public void setMagnitudes(List<Magnitud> magnitudes) {
        this.magnitudes = magnitudes;
    }
}
