package com.egoview.udd.clases;
/**
 * Created by Serial on 15/07/2015 modified 28/10/2015.
 */
public class usuarios {

    //variable local
    private boolean estado;
    private String messageError;
    private String respuestaJson;
    private String mensajePersonalizado;
    private static usuarios usuario01 = null;

    //constructor
    public usuarios(){}


    //variable azure
    @com.google.gson.annotations.SerializedName("Id")
    private String mId;

    @com.google.gson.annotations.SerializedName("UserName")
    private String mUserName;

    @com.google.gson.annotations.SerializedName("nombre")
    private String mnombre;

    @com.google.gson.annotations.SerializedName("apellido")
    private String mapellido;

    @com.google.gson.annotations.SerializedName("email")
    private String memail;

    @com.google.gson.annotations.SerializedName("PasswordHash")
    private String mPasswordHash;

    @com.google.gson.annotations.SerializedName("sexo")
    private String msexo;

    @com.google.gson.annotations.SerializedName("celular")
    private String mcelular;

    @com.google.gson.annotations.SerializedName("direccion")
    private String mdireccion;

    @com.google.gson.annotations.SerializedName("numeroDireccion")
    private String mnumeroDireccion;

    @com.google.gson.annotations.SerializedName("complementoDireccion")
    private String mcomplementoDireccion;

    @com.google.gson.annotations.SerializedName("CodigoPostal")
    private String mcodigoPostal;

    @com.google.gson.annotations.SerializedName("id_comuna")
    private String midComuna;

    @com.google.gson.annotations.SerializedName("fecha_nacimiento")
    private String mfechaNacimiento;

    @com.google.gson.annotations.SerializedName("estaVigente")
    private String mestaVigente;

    @com.google.gson.annotations.SerializedName("SecurityStamp")
    private String msecurityStamp;

    @com.google.gson.annotations.SerializedName("EmailConfirmed")
    private String memailConfirmed;

    @com.google.gson.annotations.SerializedName("phoneNumber")
    private String mphoneNumber;

    @com.google.gson.annotations.SerializedName("PhoneNumberConfirmed")
    private String mPhoneNumberConfirmed;

    @com.google.gson.annotations.SerializedName("TwoFactorEnabled")
    private String mTwoFactorEnabled;

    @com.google.gson.annotations.SerializedName("LockoutEndDateUtc")
    private String mLockoutEndDateUtc;

    @com.google.gson.annotations.SerializedName("LockoutEnabled")
    private String mLockoutEnabled;

    @com.google.gson.annotations.SerializedName("AccessFailedCount")
    private String mAccessFailedCount;

    //local method
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
    //methods Azure
    public String getId()
    {
        return mId;
    }

    public void setId(String _id)
    {
        mId = _id;
    }

    public String getUserName()
    {
        return mUserName;
    }

    public void setUserName(String _username)
    {
        mUserName =_username;
    }

    public String getApellido()
    {
        return mapellido;
    }

    public void setApellido(String _apellido)
    {
        mapellido =_apellido;
    }

    public String getEmail()
    {
        return memail;
    }

    public void setEmail(String _email)
    {
        memail =_email;
    }

    public String getNombre() {
        return mnombre;
    }

    public final void setNombre(String _nombre) {
        mnombre = _nombre;
    }

    public String getPasswordHash()
    {
        return mPasswordHash;
    }

    public void setPasswordHash(String _mpasswordhash)
    {
        mPasswordHash = _mpasswordhash;
    }

    public String getSexo()
    {
        return msexo;
    }

    public final void setSexo(String _sexo)
    {
        msexo = _sexo;
    }

    public String getCelular()
    {
        return mcelular;
    }

    public final void setCelular(String _celular)
    {
        mcelular = _celular;
    }

    public String getDireccion()
    {
        return mdireccion;
    }

    public final void setDireccion(String _direccion)
    {
        mdireccion = _direccion;
    }

    public String getNumeroDireccion()
    {
        return mnumeroDireccion;
    }

    public final void setNumeroDireccion(String _numerodireccion)
    {
        mnumeroDireccion = _numerodireccion;
    }

    public String getComplentoDireccion()
    {
        return mcomplementoDireccion;
    }

    public final void setComplentoDireccion(String _complementodireccion)
    {
        mcomplementoDireccion = _complementodireccion;
    }

    public String getCodigoPostal()
    {
        return mcodigoPostal;
    }

    public final void setCodigoPostal(String _codigopostal)
    {
        mcodigoPostal = _codigopostal;
    }

    public String getId_comuna()
    {
        return midComuna;
    }

    public final void setId_comuna(String _comuna)
    {
        midComuna = _comuna;
    }

    public String getFecha_nacimiento()
    {
        return mfechaNacimiento;
    }

    public final void setFecha_nacimiento(String _fecha_nacimiento)
    {
        mfechaNacimiento = _fecha_nacimiento;
    }

    public String getEstaVigente()
    {
        return mestaVigente;
    }

    public final void setEstaVigente(String _estavigente)
    {
        mestaVigente = _estavigente;
    }

    public String getSecurityStamp()
    {
        return msecurityStamp;
    }

    public final void setSecurityStamp(String _securitystamp)
    {
        msecurityStamp = _securitystamp;
    }

    public String getEmailConfirmed()
    {
        return memailConfirmed;
    }

    public final void setEmailConfirmed(String _emailconfirmed)
    {
        memailConfirmed = _emailconfirmed;
    }

    public String getPhoneNumber()
    {
        return mphoneNumber;
    }

    public final void setPhoneNumber(String _phonenumber)
    {
        mphoneNumber = _phonenumber;
    }

    public String getPhoneNumberConfirmed()
    {
        return mPhoneNumberConfirmed;
    }

    public final void setPhoneNumberConfirmed(String _phonenumberconfirmed)
    {
        mPhoneNumberConfirmed = _phonenumberconfirmed;
    }

    public String getTwoFactorEnabled()
    {
        return mTwoFactorEnabled;
    }

    public final void setTwoFactorEnabled(String _twofactorenabled)
    {
        mTwoFactorEnabled = _twofactorenabled;
    }

    public String getLockoutEndDateUtc()
    {
        return mLockoutEndDateUtc;
    }

    public final void setLockoutEndDateUtc(String _lockoutenddateutc)
    {
        mLockoutEndDateUtc = _lockoutenddateutc;
    }

    public String getLockoutEnabled()
    {
        return mLockoutEnabled;
    }

    public final void setLockoutEnabled(String _lockoutenabled)
    {
        mLockoutEnabled = _lockoutenabled;
    }

    public String getAccessFailedCount()
    {
        return mAccessFailedCount;
    }

    public final void setAccessFailedCount(String _accessfailedcount)
    {
        mAccessFailedCount = _accessfailedcount;
    }

    public static void setUsuarios(usuarios usuarios01) {
        usuario01= usuarios01;
    }
    public static usuarios getUsuarios() {
        return usuario01;
    }

}//end class
