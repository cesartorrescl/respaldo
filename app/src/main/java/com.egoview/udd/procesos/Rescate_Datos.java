package com.egoview.udd.procesos;

import android.content.Intent;
import android.os.Bundle;

import com.egoview.udd.clases.categorias;
import com.egoview.udd.clases.subcategorias;

import java.util.ArrayList;

/**
 * Created by Ernesto_Ruiz_2015 on 06/10/2015.
 */
public class Rescate_Datos {

    public static String rescatar_direccion(Intent intent){
        String direccion=null;
        try{
            Bundle bundle = intent.getExtras();
            direccion = bundle.getString("direccion");
        }catch (Exception e){
        }
        return direccion;
    }


    // para traer las categorias de preferencia del usuario
   public static ArrayList<categorias> rescatar_categorias(Intent intent) {
        ArrayList<categorias> list_categorias;
        list_categorias = new ArrayList<categorias>();
        try {
            Bundle bundle = intent.getExtras();
            list_categorias = (ArrayList<categorias>) bundle.getSerializable("list_categorias");
        } catch (Exception e) {
        }
        return list_categorias;
    }//end method

    // para traer las categorias de preferencia del usuario
    public static ArrayList<subcategorias> rescatar_sub_categorias(Intent intent) {
        ArrayList<subcategorias> list_subcategorias;
        list_subcategorias = new ArrayList<subcategorias>();
        try {
            Bundle bundle = intent.getExtras();
            list_subcategorias = (ArrayList<subcategorias>) bundle.getSerializable("list_sub_categorias");
        } catch (Exception e) {
        }
        return list_subcategorias;
    }//end method
    
    public static double rescatar_latitud(Intent intent){
        double latitud;
        Bundle bundle = intent.getExtras();
        latitud=bundle.getDouble("latitud");
        return latitud;
    }

    public static double rescatar_longitud(Intent intent){
        double longitud;
        Bundle bundle = intent.getExtras();
            longitud=bundle.getDouble("longitud");
        return longitud;
    }

}// end class