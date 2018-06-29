package com.egoview.udd.clases;

import java.io.Serializable;
import java.util.ArrayList;

public class subcategorias implements Serializable {

    private static subcategorias subcategoria01 = null;
    public subcategorias() {}

    public ArrayList<subcategorias> list_subcategorias;
    {
        list_subcategorias = new ArrayList<subcategorias>();
    }

    @com.google.gson.annotations.SerializedName("$id")
    private long mId;

    @com.google.gson.annotations.SerializedName("desc_subCategoria")
    private String mDesc_subCategoria;

    @com.google.gson.annotations.SerializedName("totalBySubCateg")
    private int mTotalBySubCateg;

    public ArrayList<campanias> camps = new ArrayList<campanias>();

    //methods Azure

    public long getId() {
        return mId;
    }

    public void setId(long id){
        mId = id;
    }


    public String getDescripcion_campania() {
        return mDesc_subCategoria;
    }

    public void setDescripcion_campania(String desc_subcat){
        mDesc_subCategoria=desc_subcat;
    }

    public int getTotal_por_subcategorias() {
        return mTotalBySubCateg;
    }

    public void setTotal_por_subcategorias(int total_por_subcategorias){
        mTotalBySubCateg=total_por_subcategorias;
    }

    public static void setSubCategorias(subcategorias subcategorias01) {
        subcategoria01=subcategorias01;
    }
    public static subcategorias getSubbCategorias() {
        return subcategoria01;
    }
    public ArrayList<campanias> getCamps() {
        return camps;
    }

    public void setCamps(ArrayList<campanias> camps0) {
        camps = camps0;
    }


}
