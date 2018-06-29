package com.egoview.udd.procesos;

/**
 * Created by Ernesto on 13-11-2015.
 */
public class Fechas {

    public static String modificar_formato_fecha(String fecha_nacimiento) {
        String fecha_nacimiento_formateada=null;
        String[] fecha_nacimiento_array = new String[3];
        fecha_nacimiento_array = fecha_nacimiento.split("-");
        fecha_nacimiento_formateada = fecha_nacimiento_array[2] + "-" + fecha_nacimiento_array[1] + "-" + fecha_nacimiento_array[0];
        return fecha_nacimiento_formateada;
    }
}