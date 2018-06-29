package com.egoview.udd.clases;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Serial on 27/07/2015 modified 17/11/2015.*/
public class campanias implements Serializable {
    //variable local
    private boolean estado, visto;
    private String nombre_empresa, nombre_categoria;
    private long idEmpresa;
    private boolean personalizado, Fav, MG, NMG, isPagada, isEmpresa;
    private double valoracion;
    private int cantMG, cantNMG, valoracionUser;
    private String messageError;
    private String respuestaJson;
    private String mensajePersonalizado;
    private ArrayList<htmlCamp> htmlCampArrayList = new ArrayList<htmlCamp>();
    private ArrayList<imagenCamp> imagenCampArrayList = new ArrayList<imagenCamp>();
    private static campanias campania01 = null;

    public campanias(){}

    @com.google.gson.annotations.SerializedName("$id")
    long mid;



    @com.google.gson.annotations.SerializedName("id_campania")
    String mid_campania;

    @com.google.gson.annotations.SerializedName("id_sub_campania")
    String mid_sub_campania;

    @com.google.gson.annotations.SerializedName("nombre_camp")
    String mnombre_camp;

    @com.google.gson.annotations.SerializedName("desc_campania")
    String mdesc_campania;

    @com.google.gson.annotations.SerializedName("inicio_camp")
    String minicio_camp;

    @com.google.gson.annotations.SerializedName("fin_camp")
    String mfin_camp;

    @com.google.gson.annotations.SerializedName("distancia")
    double mdistancia;

    @com.google.gson.annotations.SerializedName("nombre_variable")
    String mid_empresa;

    @com.google.gson.annotations.SerializedName("nombre_variable")
    String mid_tipoCampania;

    @com.google.gson.annotations.SerializedName("nombre_variable")
    String mestaVigente;

    @com.google.gson.annotations.SerializedName("nombre_variable")
    String mid_prioridadCamp;

    @com.google.gson.annotations.SerializedName("url_sitio_externo")
    String murl_sitio_externo;

    @com.google.gson.annotations.SerializedName("condiciones_uso")
    String mcondiciones_uso;

    @com.google.gson.annotations.SerializedName("nombre_variable")
    String mesEstatico;

    @com.google.gson.annotations.SerializedName("ruta_imagen")
    String Ruta_imagen;

    @com.google.gson.annotations.SerializedName("nombre_variable")
    String mvar_2;

    @com.google.gson.annotations.SerializedName("nombre_variable")
    String mvar_3;

    @com.google.gson.annotations.SerializedName("nombre_variable")
    String mvar_4;

    @com.google.gson.annotations.SerializedName("nombre_variable")
    String mvar_5;

    //locales
    public boolean getPersonalizado()
    {
        return personalizado;
    }

    public void setPersonalizado(boolean _pessonalizado)
    {
        personalizado=_pessonalizado;
    }

    public boolean getEstado()
    {
        return estado;
    }

    public void setEstado(boolean _estado)
    {
        estado =_estado;
    }

    public String getMessageError()
    {
        return messageError;
    }

    public void setMessageError(String _messageError)
    {
        messageError = _messageError;
    }

    public String getNombre_empresa()
    {
        return nombre_empresa;
    }

    public void setNombre_categoria(String nombre)
    {
        nombre_categoria = nombre;
    }

    public String getNombre_categoria()
    {
        return nombre_categoria;
    }

    public void setNombre_empresa(String _nombre_empresa)
    {
        nombre_empresa = _nombre_empresa;
    }

    public String getRespuestaJson()
    {
        return respuestaJson;
    }

    public void setRespuestaJson(String _respuestaJson)
    {
        respuestaJson = _respuestaJson;
    }

    public String getMensajePersonalizado()
    {
        return mensajePersonalizado;
    }
    public void setMensajePersonalizado(String _mensajePersonalizado)
    {
        mensajePersonalizado=_mensajePersonalizado;
    }

    public ArrayList <htmlCamp> getHtmlCampArrayList()
    {
        return  htmlCampArrayList;
    }

    public void setHtmlCampArrayList(htmlCamp htmlCamp01)
    {
        if (htmlCampArrayList.size()==0) {
            htmlCampArrayList.add(htmlCamp01);
        }else{
            htmlCampArrayList.set(0, htmlCamp01);
        }
    }

    public ArrayList <imagenCamp> getImagenCampArrayList()
    {
        return imagenCampArrayList;
    }

    public void setImagenCampArrayList(imagenCamp imagenCamp01) {
        imagenCampArrayList.add(imagenCamp01);}


    //metodos Azure
    public String getRuta_imagen()
    {
        return Ruta_imagen;
    }

    public void setRuta_imagen(String _ruta)
    {
        Ruta_imagen = _ruta;
    }

    public long getId()
    {
        return mid;
    }

    public void setId(long _id)
    {
        mid = _id;
    }

    public String getIdCampania()
    {
        return mid_campania;
    }

    public void setIdCampania(String _id_campania)
    {
        mid_campania =_id_campania;
    }

    public String getIdSubCampania()
    {
        return mid_sub_campania;
    }

    public void setIdSubCampania(String _id_campania)
    {
        mid_sub_campania =_id_campania;
    }

    public String getNombre_camp()
    {
        return mnombre_camp;
    }

    public void setNombre_camp(String _nombre_camp)
    {
        mnombre_camp = _nombre_camp;
    }

    public String getDesc_campania()
    {
        return mdesc_campania;
    }

    public void setDesc_campania(String _desc_campania)
    {
        mdesc_campania =_desc_campania;
    }

    public String getInicio_camp()
    {
        return minicio_camp;
    }

    public void setInicio_camp(String _inicio_camp)
    {
        minicio_camp = _inicio_camp;
    }

    public String getFin_camp()
    {
        return mfin_camp;
    }

    public void setFin_camp(String _fin_camp)
    {
        mfin_camp = _fin_camp;
    }

    public double getMdistancia() { return mdistancia;}
    public void setMdistancia(double _distancia){mdistancia=_distancia;}

    public String getId_empresa()
    {
        return mid_empresa;
    }

    public void setId_empresa(String _id_empresa)
    {
        mid_empresa = _id_empresa;
    }

    public String getId_tipoCampania()
    {
        return mid_tipoCampania;
    }

    public void setId_tipoCampania(String _id_tipoCampania)
    {
        mid_tipoCampania =_id_tipoCampania;
    }

    public String getEstaVigente()
    {
        return mestaVigente;
    }

    public void setEstaVigente(String _estaVigente)
    {
        mestaVigente= _estaVigente;
    }

    public String getId_prioridadCamp()
    {
        return  mid_prioridadCamp;
    }

    public void setId_prioridadCamp(String _id_prioridadCamp)
    {
        mid_prioridadCamp = _id_prioridadCamp;
    }

    public String getUrl_sitio_externo()
    {
        return murl_sitio_externo;
    }

    public void setUrl_sitio_externo(String _url_sitio_externo)
    {
        murl_sitio_externo=_url_sitio_externo;
    }

    public  String getCondiciones_uso()
    {
        return mcondiciones_uso;
    }

    public void setCondiciones_uso(String _condiciones_uso)
    {
        mcondiciones_uso = _condiciones_uso;
    }

    public String getEsEstatico()
    {
        return mesEstatico;
    }

    public void setEsEstatico(String _mesEstatico)
    {
        mesEstatico =_mesEstatico;
    }

    public String getVar_2()
    {
        return mvar_2;
    }

    public void setVar_2(String _var_2)
    {
        mvar_2 = _var_2;
    }

    public String getVar_3()
    {
        return mvar_3;
    }

    public void setVar_3(String _var_3)
    {
        mvar_3 = _var_3;
    }

    public String getVar_4()
    {
        return mvar_4;
    }

    public void setVar_4(String _var_4)
    {
        mvar_4 = _var_4;
    }

    public String getVar_5()
    {
        return mvar_5;
    }

    public void setVar_5(String _var_5)
    {
        mvar_5 = _var_5;
    }

    public static void setCampanias(campanias campanias01) {
        campania01= campanias01;
    }

    public static campanias getCampanias() {
        return campania01;
    }

    public void setFav(boolean fav) {
        Fav= fav;
    }

    public boolean getFav() {
        return Fav;
    }

    public void setMG(boolean mg) {
        MG= mg;
    }

    public boolean getMG() {
        return MG;
    }

    public void setNMG(boolean nmg) {
        NMG= nmg;
    }

    public boolean getNMG() {
        return NMG;
    }

    public void setCantMG(int cmg) {
        cantMG= cmg;
    }

    public int getCantMG() {
        return cantMG;
    }

    public void setCantNMG(int cnmg) {
        cantNMG= cnmg;
    }

    public int getCantNMG() {
        return cantNMG;
    }

    public boolean getIsPagada()
    {
        return isPagada;
    }

    public void setisPagada(boolean _pagada)
    {
        isPagada =_pagada;
    }

    public boolean getIsEmpresa()
    {
        return isEmpresa;
    }

    public void setIsEmpresa(boolean _empresa)
    {
        isEmpresa =_empresa;
    }

    public double getValoracion()
    {
        return valoracion;
    }

    public void setValoracion(double _valoracion)
    {
        valoracion=_valoracion;
    }

    public long getIdEmpresa()
    {
        return idEmpresa;
    }

    public void setIdEmpresa(long _idEmpresa)
    {
        idEmpresa=_idEmpresa;
    }

    public int getValoracionUser()
    {
        return valoracionUser;
    }

    public void setValoracionUser(int _valoracionUser)
    {
        valoracionUser =_valoracionUser;
    }

    public boolean getVisto()
    {
        return visto;
    }

    public void setVisto(boolean _visto)
    {
        visto =_visto;
    }
}//end class
