package com.egoview.udd.clases;

/**
 * Created by Serial on 01/09/2015.
 */
public class imagenCamp {
        //variable local
        private boolean estado;
        private String messageError;
        private String respuestaJson;
        private String mensajePersonalizado;

        public imagenCamp(){}

        @com.google.gson.annotations.SerializedName("$id")
        String $id;

        @com.google.gson.annotations.SerializedName("id_imagen")
        String id_imagen;

        @com.google.gson.annotations.SerializedName("nombre_imagen")
        String nombre_imagen;

        @com.google.gson.annotations.SerializedName("ruta_imagen")
        String ruta_imagen;


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

        public String getId_imagen()
        {
            return id_imagen;
        }

        public void setId_imagen(String _id_imagen)
        {
            id_imagen =_id_imagen;
        }

        public String getNombre_imagen()
        {
            return nombre_imagen;
        }

        public void setNombre_imagen(String _nombre_imagen)
        {
            nombre_imagen = _nombre_imagen;
        }

        public String getRuta_imagen()
    {
        return ruta_imagen;
    }

        public void setRuta_imagen(String _ruta_imagen)
    {
        ruta_imagen = _ruta_imagen;
    }
}//end class

