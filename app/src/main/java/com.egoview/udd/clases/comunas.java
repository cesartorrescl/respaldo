package com.egoview.udd.clases;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Serial on 29/10/2015.
 */
public class comunas implements Serializable {

    public comunas(){}

    public ArrayList<comunas> list_comunas;
    {
        list_comunas = new ArrayList<comunas>();
    }

    @com.google.gson.annotations.SerializedName("$id")
    private String mId;

    @com.google.gson.annotations.SerializedName("idComuna")
    private int mIdcomuna;

    @com.google.gson.annotations.SerializedName("descComuna")
    private String mDescomuna;

    @com.google.gson.annotations.SerializedName("idCiudad")
    private int mIdciudad;

    @com.google.gson.annotations.SerializedName("descRegion")
    private String mDesregion;

    //methods Azure
    public void  setmId(String _id){mId=_id;}
    public String getmId(){return mId;}

    public void  setmIdcomuna(int _idcomuna){mIdcomuna=_idcomuna;}
    public int getmIdcomuna(){
        return mIdcomuna;
    }

    public void  setmDescomuna(String _descomuna){mDescomuna=_descomuna;}
    public String getmDescomuna(){
        return mDescomuna;
    }

    public void  setmIdciudad(int _idciudad){
        mIdciudad=_idciudad;
    }
    public int getmIdciudad(){
        return mIdciudad;
    }

    public void  setmDesregion(String _desregion){mDesregion=_desregion;}
    public String getmDesregion(){
        return mDesregion;
    }

}
