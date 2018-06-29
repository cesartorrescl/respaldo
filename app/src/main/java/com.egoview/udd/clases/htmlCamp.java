package com.egoview.udd.clases;

import java.io.Serializable;

/**
 * Created by Serial on 27/07/2015 modified on 14/10/2015.
 */
public class htmlCamp implements Serializable{
    //variable local
    private boolean estado;
    private String messageError;
    private String respuestaJson;
    private String mensajePersonalizado;

    public htmlCamp(){}

    @com.google.gson.annotations.SerializedName("$id")
    String $id;

    @com.google.gson.annotations.SerializedName("id_html")
    String id_html;

    @com.google.gson.annotations.SerializedName("cod_html")
    String cod_html;

    //locales
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

    //Metodos Azure
    public String getId()
    {
        return $id;
    }

    public void setId(String _id)
    {
        $id = _id;
    }

    public String getId_html()
    {
        return id_html;
    }

    public void setId_html(String _id_html)
    {
        id_html =_id_html;
    }

    public String getCod_html()
    {
        return cod_html;
    }

    public void setCod_html(String _cod_html)
    {
        cod_html = _cod_html;
    }

}//end class
