package com.egoview.udd.vistas.personalizadas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.egoview.udd.R;
import com.egoview.udd.procesos.AnalyticsApplication;
import com.egoview.udd.procesos.Porcentual;
import com.egoview.udd.procesos.providers;
import com.google.android.gms.analytics.Tracker;

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
import java.util.Calendar;


/**
 * Created by Ernesto on 29/04/2016.
 */
public class activity_solicitar_vacaciones extends Activity {


    private LinearLayout actualizar;
    private Activity activity = this;
    private Calendar calendar, min_calendar;
    private EditText correo, numero_dir,  fecha_nac;
    private TextView txt_actualizar_datos, txt_datos_personales, txt_aviso;
    private PercentRelativeLayout volver;
    private String fecha_nac_envio;
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
        setContentView(R.layout.activity_solicitar_vacaciones);
        //Inicializar Variables
        calendar = Calendar.getInstance();
        min_calendar = Calendar.getInstance();
        min_calendar.add(Calendar.DAY_OF_MONTH, 1);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        correo = (EditText) findViewById(R.id.correo);
        numero_dir = (EditText) findViewById(R.id.numero_direccion);
        fecha_nac = (EditText) findViewById(R.id.fecha_nac);
        volver = (PercentRelativeLayout) findViewById(R.id.volver);
        actualizar = (LinearLayout) findViewById(R.id.actualizar);
        txt_datos_personales = (TextView) findViewById(R.id.txt_datos_personales);
        txt_actualizar_datos = (TextView) findViewById(R.id.txt_actualizar_datos);
        txt_aviso = (TextView) findViewById(R.id.txt_aviso);

        Typeface type = Typeface.createFromAsset(getAssets(),"roboto_regular.ttf");
        Typeface bold= Typeface.createFromAsset(getAssets(),"roboto_regular.ttf");
        //Fin
        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        // [END shared_tracker]
        //TextSize Ajust
        Porcentual porcentual = new Porcentual();
        txt_datos_personales.setTextSize((float) (porcentual.getScreenInches(this)*3.5));
        txt_actualizar_datos.setTextSize((float) (porcentual.getScreenInches(this)*3.5));
        txt_aviso.setTextSize((float) (porcentual.getScreenInches(this)*3.0));
        //Fin
        //Porcentual Ajust
        LinearLayout.LayoutParams actualizar_layout = (LinearLayout.LayoutParams) actualizar.getLayoutParams();
        actualizar_layout.width = (int) (porcentual.getScreenWidth(this)*0.6);
        actualizar_layout.height = (int) (porcentual.getScreenHeight(this)*0.06);
        actualizar.setLayoutParams(actualizar_layout);

        ViewGroup.LayoutParams params_txt = correo.getLayoutParams();
        params_txt.width = (int) (porcentual.getScreenWidth(this)*0.8);
        correo.setLayoutParams(params_txt);
        numero_dir.setLayoutParams(params_txt);
        fecha_nac.setLayoutParams(params_txt);
        //Fin
        //TypeFace
        correo.setTypeface(type);
        fecha_nac.setTypeface(type);
        numero_dir.setTypeface(type);
        txt_aviso.setTypeface(type);
        txt_datos_personales.setTypeface(bold);
        txt_actualizar_datos.setTypeface(bold);
        //Fin
        try {
            Token = getIntent().getExtras().getString("Token");
            idUser=getIntent().getExtras().getLong("idUser");
            userName=getIntent().getExtras().getString("userName");
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
                if(validarFecha() && validarNumero() && validarEmailLogin()) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                            .setTitle(userName)
                            .setMessage("Enviando solicitud, espere...")
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
                            if(statusCode==200){
                                finish();
                            }
                        }
                    }.execute();
                }else{
                    mesage = "Ingrese los siguientes campos: \n";
                    validarEmailLogin();
                    validarFecha();
                    validarNumero();
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
            dialog.getDatePicker().setMinDate(min_calendar.getTime().getTime());
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
    public boolean validarNumero(){
        try {
            int number = Integer.parseInt(numero_dir.getText().toString());
            if(number>0){
                return true;
            }
            mesage = mesage + "-Debe ingresar un número valido.\n";
            return false;
        }catch (Exception e) {
            mesage = mesage + "-Debe ingresar un número valido.\n";
            return false;
        }
    }

    public boolean validarEmailLogin(){
        if(correo.getText() != null && !correo.getText().toString().equals("")){
            if(correo.getText().toString().matches("^[\\w-]+(\\.[\\w-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
                return true;
            }else{
                mesage = mesage + "-Debe ingresar un correo valido.\n";
                return false;
            }
        }else{
            mesage = mesage + "-Debe ingresar un correo valido.\n";
            return false;
        }
    }

    private String ApiUpdateInfouser(){
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(getString(R.string.URL_Mobile_Service)+"/api/Usuarios/IneedVacation");
        httpPost.addHeader("X-ZUMO-AUTH",Token);
        httpPost.addHeader("ZUMO-API-VERSION","2.0.0");
        httpPost.addHeader("Content-type","application/json");
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        JSONObject json = new JSONObject();
        try {
            json.put("idUsuario",idUser);
            json.put("cant_dias",Integer.parseInt(numero_dir.getText().toString()));
            json.put("fecha_inicio",fecha_nac_envio);
            json.put("email_supervisor",correo.getText().toString());
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
