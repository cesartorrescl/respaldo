package com.egoview.udd.clases;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Serial on 14/08/2015 modified on 10/09/2015.
 */
public class categorias implements Serializable{
    private static categorias categoria01 = null;
    public categorias() {}

    public ArrayList<categorias> list_categorias;
    {
        list_categorias = new ArrayList<categorias>();
    }

    @com.google.gson.annotations.SerializedName("$id")
    private long mId;

    @com.google.gson.annotations.SerializedName("id_tipoCampania")
    private int mId_TipoCampania;

    @com.google.gson.annotations.SerializedName("id_tipoSubCampania")
    private int mId_TipoSubCampania;

    @com.google.gson.annotations.SerializedName("desc_tipoCampania")
    private String mDesc_TipoCampania;

    @com.google.gson.annotations.SerializedName("totalByCateg")
    private int mTotalByCateg;

    private boolean estado;

    public ArrayList<campanias> camps = new ArrayList<campanias>();

    //methods Azure
    public boolean getEstado(){ return estado;}

    public void setEstado(boolean estado){this.estado=estado;}

    public long getId() {
        return mId;
    }

    public void setId(long id){
        mId = id;
    }

    public int getId_categorias() {
        return mId_TipoCampania;
    }

    public void setId_categorias(int id_categorias){
        mId_TipoCampania=id_categorias;
    }

    public int getId_sub_categorias() {
        return mId_TipoSubCampania;
    }

    public void setId_sub_categorias(int id_sub_categorias){
        mId_TipoSubCampania=id_sub_categorias;
    }

    public String getDescripcion_campania() {
        return mDesc_TipoCampania;
    }

    public void setDescripcion_campania(String descripcion_campania){
        mDesc_TipoCampania=descripcion_campania;
    }

    public int getTotal_por_categorias() {
        return mTotalByCateg;
    }

    public void setTotal_por_categorias(int total_por_categorias){
        mTotalByCateg=total_por_categorias;
    }

    public static void setCategorias(categorias categorias01) {
        categoria01=categorias01;
    }
    public static categorias getCategorias() {
        return categoria01;
    }
    public ArrayList<campanias> getCamps() {
        return camps;
    }

    public void setCamps(ArrayList<campanias> camps0) {
        camps = camps0;
    }


}