package com.egoview.udd.vistas.personalizadas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.egoview.udd.R;
import com.egoview.udd.clases.comunas;
import com.egoview.udd.procesos.AnalyticsApplication;
import com.egoview.udd.procesos.Fechas;
import com.egoview.udd.procesos.Parseo_Json;
import com.egoview.udd.procesos.Porcentual;
import com.egoview.udd.procesos.footer;
import com.egoview.udd.procesos.providers;
import com.google.android.gms.analytics.Tracker;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by Ernesto on 29/04/2016.
 */
public class activity_datos_personales extends Activity {


    private MobileServiceClient mClient;
    private ListenableFuture<ServiceFilterResponse> result;
    private ListenableFuture<JsonElement> result_comunas;
    private ListenableFuture<JsonElement> result_actualizar;
    private LinearLayout actualizar;
    private Activity activity = this;
    private Calendar calendar, max_calendar;
    private EditText nombre, apellido, correo, fecha_nac, direccion, numero_dir, complemento, telefono;
    private TextView txt_actualizar_datos, txt_datos_personales;
    private AutoCompleteTextView comuna;
    private PercentRelativeLayout volver;
    private String fecha_nac_envio;
    private RadioButton m,f;
    private int idComuna=0;
    private ArrayList<comunas> lista_comunas;
    private int year, month, day, statusCode=0;

    private String Token;
    private long idUser;
    private String userName;
    private AnalyticsApplication application;
    private Tracker mTracker;

    private String mesage;
    private AlertDialog.Builder alertDialog;
    private providers mProviders;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_datos_personales);
        footer foot = new footer();
        foot.footer(this);
        //Inicializar Variables
        calendar = Calendar.getInstance();
        max_calendar = Calendar.getInstance();
        max_calendar.add(Calendar.YEAR, -12);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        nombre = (EditText) findViewById(R.id.nombre);
        apellido = (EditText) findViewById(R.id.apellido);
        correo = (EditText) findViewById(R.id.correo);
        correo.setEnabled(false);
        telefono = (EditText) findViewById(R.id.telefono);
        fecha_nac = (EditText) findViewById(R.id.fecha_nac);
        comuna = (AutoCompleteTextView) findViewById(R.id.comuna);
        m = (RadioButton) findViewById(R.id.hombre);
        f = (RadioButton) findViewById(R.id.mujer);
        direccion = (EditText) findViewById(R.id.direccion);
        numero_dir = (EditText) findViewById(R.id.numero_direccion);
        complemento = (EditText) findViewById(R.id.complemento_direccion);
        volver = (PercentRelativeLayout) findViewById(R.id.volver);
        actualizar = (LinearLayout) findViewById(R.id.actualizar);
        txt_datos_personales = (TextView) findViewById(R.id.txt_datos_personales);
        txt_actualizar_datos = (TextView) findViewById(R.id.txt_actualizar_datos);

        Typeface type = Typeface.createFromAsset(getAssets(),"roboto_regular.ttf");
        Typeface bold= Typeface.createFromAsset(getAssets(),"roboto_bold.ttf");
        //Fin
        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        application.sendScreen("DatosPersonales");
        // [END shared_tracker]
        //TextSize Ajust
        Porcentual porcentual = new Porcentual();
        txt_datos_personales.setTextSize((float) (porcentual.getScreenInches(this)*3.5));
        txt_actualizar_datos.setTextSize((float) (porcentual.getScreenInches(this)*3.5));
        m.setTextSize((float) (porcentual.getScreenInches(this)*3));
        f.setTextSize((float) (porcentual.getScreenInches(this)*3));
        //Fin
        //Porcentual Ajust
        LinearLayout.LayoutParams actualizar_layout = (LinearLayout.LayoutParams) actualizar.getLayoutParams();
        actualizar_layout.width = (int) (porcentual.getScreenWidth(this)*0.6);
        actualizar_layout.height = (int) (porcentual.getScreenHeight(this)*0.06);
        actualizar_layout.bottomMargin = (int) (porcentual.getScreenHeight(this)*0.06);
        actualizar.setLayoutParams(actualizar_layout);

        ViewGroup.LayoutParams params_txt = nombre.getLayoutParams();
        params_txt.width = (int) (porcentual.getScreenWidth(this)*0.8);
        nombre.setLayoutParams(params_txt);
        apellido.setLayoutParams(params_txt);
        correo.setLayoutParams(params_txt);
        direccion.setLayoutParams(params_txt);
        numero_dir.setLayoutParams(params_txt);
        complemento.setLayoutParams(params_txt);
        comuna.setLayoutParams(params_txt);
        telefono.setLayoutParams(params_txt);
        fecha_nac.setLayoutParams(params_txt);
        //Fin
        //TypeFace
        nombre.setTypeface(type);
        apellido.setTypeface(type);
        correo.setTypeface(type);
        fecha_nac.setTypeface(type);
        m.setTypeface(type);
        f.setTypeface(type);
        txt_datos_personales.setTypeface(bold);
        txt_actualizar_datos.setTypeface(bold);
        telefono.setTypeface(type);
        direccion.setTypeface(type);
        numero_dir.setTypeface(type);
        complemento.setTypeface(type);
        comuna.setTypeface(type);
        //Fin
        try {
            Token = getIntent().getExtras().getString("Token");
            idUser=getIntent().getExtras().getLong("idUser");
            userName=getIntent().getExtras().getString("userName");
            ApiGetInfoUsuario();
        }catch(Exception e){
            AlertDialog.Builder error = new AlertDialog.Builder(this).setTitle("Error")
                    .setMessage("Usted no esta Logeado en la aplicacion, inicie sesion para acceder a esta seccion.")
                    .setCancelable(false)
                    .setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.finish();
                        }
                    });
            error.create().show();
        }
        //Net Listener
        alertDialog = new AlertDialog.Builder(this);
        mProviders = new providers();
        mProviders.providers(Token, userName, this, this, null, null, idUser);
        registerReceiver(mProviders.getBroadcastReceiverNET(alertDialog), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        //Fin
        //Listener
        comuna.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(lista_comunas==null) {
                    apigetcomunas();
                }
                return false;
            }
        });
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        volver.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v == volver) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        v.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    }
                }
                return false;
            }
        });
        actualizar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(validarNombre() && validarApellido() && validarFecha() && validarTelefono() && validarDireccion() && validarComuna()) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                            .setTitle(userName)
                            .setMessage("Actualizando datos personales...")
                            .setCancelable(false);
                    final AlertDialog alert = builder.create();
                    alert.show();
                    final String[] mensaje = {""};
                    new AsyncTask<Object, Object, Object>() {

                        @Override
                        protected Object doInBackground(Object... params) {
                            mensaje[0] = ApiUpdateInfouser();
                            while (mensaje[0] == "") {
                            }
                            return "Execute";
                        }

                        @Override
                        protected void onPostExecute(Object result) {
                            application.sendEvent("datosPersonales", "actualizarDatos", "actualizar");
                            AlertDialog.Builder alerta = new AlertDialog.Builder(activity)
                                    .setTitle(userName)
                                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            dialog.dismiss();
                                            if (statusCode==200) {
                                                finish();
                                            }
                                        }
                                    })
                                    .setMessage(mensaje[0]);
                            alerta.create().show();
                            alert.dismiss();
                        }
                    }.execute();
                }else{
                    mesage = "Ingrese los siguientes campos: \n";
                    validarNombre();
                    validarApellido();
                    validarFecha();
                    validarTelefono();
                    validarDireccion();
                    validarComuna();
                    AlertDialog.Builder alerta = new AlertDialog.Builder(activity)
                            .setTitle(userName)
                            .setMessage(mesage);
                    alerta.create().show();
                }
            }
        });
        fecha_nac.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setDate(activity.getCurrentFocus());
                    v.clearFocus();
                }
            }
        });
        //Fin
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            DatePickerDialog dialog = new DatePickerDialog(this, myDateListener, year, month, day);
            dialog.getDatePicker().setMaxDate(max_calendar.getTime().getTime());
            return dialog;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            fecha_nac.setText(arg3 + "-" + (arg2 + 1) + "-" + arg1);
            fecha_nac_envio = arg1 + "-" + (arg2 + 1) + "-" + arg3;
        }
    };

    public boolean validarFecha(){
        if(fecha_nac.getText() != null && !fecha_nac.getText().toString().equals("")){
            return true;
        }else{
            mesage = mesage + "-Ingrese una fecha valida.\n";
            return false;
        }
    }

    public boolean validarNombre(){
        if(nombre.getText() != null && !nombre.getText().toString().equals("") && nombre.length()<51){
            return true;
        }else{
            mesage = mesage + "-Ingrese un nombre de maximo 30 caracteres.\n";
            return false;
        }
    }

    public boolean validarApellido(){
        if(apellido.getText() != null && !apellido.getText().toString().equals("") && apellido.length()<51){
            return true;
        }else{
            mesage = mesage + "-Ingrese un apellido de maximo 30 caracteres.\n";
            return false;
        }
    }public boolean validarTelefono(){
        if(telefono.getText() != null && !telefono.getText().toString().equals("") && telefono.length()>8 && telefono.length()<13){
            return true;
        }else{
            mesage = mesage + "-Ingrese un telefono de minimo 9 y maximo 12 caracteres.\n";
            return false;
        }
    }

    public boolean validarComuna(){
        boolean state = false;
        if(lista_comunas!=null) {
            for (int i = 0; i < lista_comunas.size(); i++) {
                if (lista_comunas.get(i).getmDescomuna().equals(comuna.getText().toString())) {
                    idComuna = lista_comunas.get(i).getmIdcomuna();
                    state = true;
                }
            }
            if(comuna.getText().toString().equals("")){
                state = true;
            }
        }else{
            state = true;
        }
        if(!state){
            mesage = mesage + "-Ingrese una comuna valida.\n";
        }
        return  state;
    }
    public boolean validarDireccion(){
        boolean state = false;
        if(direccion.getText().toString().equals("") && numero_dir.getText().toString().equals("") && comuna.getText().toString().equals("")){
            state = true;
        }else{
            if(!direccion.getText().toString().equals("") && !numero_dir.getText().toString().equals("") && !comuna.getText().toString().equals("")){
                state = true;
            }else {
                state = false;
            }
        }
        if(!state){
            mesage = mesage + "-Ingrese todos los campos para la dirección.\n";
        }
        return state;
    }

    public void ApiGetInfoUsuario()
    {
        final ProgressDialog dialog = ProgressDialog.show(this,"","Cargando. Por favor espere...", true);
        ArrayList<Pair<String, String>> headers = new ArrayList<Pair<String, String>>();
        headers.add(new Pair<String, String>("X-ZUMO-AUTH", Token));
        ArrayList<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
        params.add(new Pair<String, String>("idUser", String.valueOf(idUser)));

        byte[] aux =new byte[]{};
        try
        {
            mClient = new MobileServiceClient(getString(R.string.URL_Mobile_Service), this);

            try {
                result =  mClient.invokeApi("Usuarios/GetInfoUser",aux, "GET",headers,params);
                Futures.addCallback(result, new FutureCallback<ServiceFilterResponse>() {
                    @Override
                    public void onSuccess(ServiceFilterResponse result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result.getContent());
                            nombre.setText(jsonObject.getString("nombre")!="null" ? jsonObject.getString("nombre"):"");
                            apellido.setText(jsonObject.getString("apellido")!="null" ? jsonObject.getString("apellido"):"");
                            correo.setText(jsonObject.getString("email") != "null" ? jsonObject.getString("email") : "");
                            fecha_nac.setText(jsonObject.getString("fecha_nacimiento")!="null" ? Fechas.modificar_formato_fecha(jsonObject.getString("fecha_nacimiento").substring(0, 10)) : "");
                            fecha_nac_envio = jsonObject.getString("fecha_nacimiento")!="null" ? jsonObject.getString("fecha_nacimiento").substring(0, 10) : "";
                            telefono.setText(jsonObject.getString("phoneNumber")!="null" ? jsonObject.getString("phoneNumber"):"");
                            direccion.setText(jsonObject.getString("direccion")!="null" ? jsonObject.getString("direccion"): "");
                            numero_dir.setText(jsonObject.getString("numeroDireccion")!="null" ? jsonObject.getString("numeroDireccion"): "");
                            complemento.setText(jsonObject.getString("complementoDireccion")!="null" ? jsonObject.getString("complementoDireccion"): "");
                            comuna.setText(jsonObject.getString("comuna")!="null" ? jsonObject.getString("comuna"):"");
                            String sexo =jsonObject.getString("sexo");
                            if(sexo.equals("M")){
                                m.setChecked(true);
                            }else{
                                f.setChecked(true);
                            }

                        } catch (JSONException e) {
                            Log.d("Error",e.getMessage());
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d("Prueba",t.toString());
                        dialog.dismiss();
                    }
                }
            );
            }catch (Exception e){dialog.dismiss();
            }
        }
        catch(MalformedURLException e)
        {dialog.dismiss();
        }
    }

    private void apigetcomunas(){
        final ProgressDialog dialog = ProgressDialog.show(this,"","Cargando Comunas. Por favor espere...", true);
        ArrayList<Pair<String,String>> queryParams = new ArrayList<Pair<String, String>>();
        queryParams.add(new Pair<String, String>("idCiudad", ""));
        queryParams.add(new Pair<String, String>("todasComunas", "true"));
        try
        {
            mClient = new MobileServiceClient(getString(R.string.URL_Mobile_Service), this);
            try {
                result_comunas =  mClient.invokeApi("Manage/ListaComunas","POST", queryParams);
                Futures.addCallback(result_comunas, new FutureCallback<JsonElement>() {
                    @Override
                    public void onSuccess(JsonElement result) {
                        lista_comunas = Parseo_Json.parseoJsonComunas(result.toString());
                        String[] comunas = new String[lista_comunas.size()];
                        for(int i=0; i<lista_comunas.size(); i++){
                            comunas[i]=lista_comunas.get(i).getmDescomuna();
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,R.layout.list_comunas_view,comunas);
                        comuna.setAdapter(adapter);
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        dialog.dismiss();
                    }
                });
            }
            catch (Exception e)
            {dialog.dismiss();
            }
        }
        catch(MalformedURLException e)
        {dialog.dismiss();
        }
    }

    private String ApiUpdateInfouser(){
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(getString(R.string.URL_Mobile_Service)+"/api/Manage/UpdateDatosUser");
        httpPost.addHeader("X-ZUMO-AUTH",Token);
        httpPost.addHeader("ZUMO-API-VERSION","2.0.0");
        httpPost.addHeader("Content-type","application/json");
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        JSONObject json = new JSONObject();
        try {
            json.put("idUser",idUser);
            json.put("Nombre",nombre.getText().toString());
            json.put("Apellido",apellido.getText().toString());
            json.put("Sexo",(m.isChecked() ? "M":"F"));
            json.put("fecha_nacimiento",fecha_nac_envio);
            json.put("PhoneNumber",telefono.getText().toString());
            json.put("Direccion",direccion.getText().toString());
            json.put("NumeroDireccion",numero_dir.getText().toString());
            json.put("ComplementoDireccion",complemento.getText().toString());
            json.put("id_comuna", idComuna!=0 ? idComuna : null );
            httpPost.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException e) {
        }
        try {
            response = client.execute(httpPost);
            statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
            return jsonObject.toString();

        } catch (JSONException e) {
            return stringBuilder.toString();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AnalyticsApplication.activityResumed(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AnalyticsApplication.activityPaused();
    }

}
