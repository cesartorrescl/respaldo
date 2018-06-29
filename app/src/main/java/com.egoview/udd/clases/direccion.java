package com.egoview.udd.clases;

/**
 * Created by Ernesto on 19/04/2016.
 */
public class direccion {
    private String direccion;
    private double latitud;
    private double longitud;

    public direccion(String direccion, double latitud, double longitud) {
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public direccion() {
    }

    public void setDireccion(String direccion){
        this.direccion=direccion;
    }

    public String getDireccion() {
        return direccion;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}
