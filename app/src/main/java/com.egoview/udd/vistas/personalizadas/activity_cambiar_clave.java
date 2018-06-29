package com.egoview.udd.vistas.personalizadas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.egoview.udd.R;
import com.egoview.udd.procesos.AnalyticsApplication;
import com.egoview.udd.procesos.Porcentual;
import com.egoview.udd.procesos.footer;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Ernesto on 03/05/2016.
 */
public class activity_cambiar_clave extends Activity {

    private EditText old_pass, new_pass, repeat_pass;
    private LinearLayout cambiar_clave;
    private TextView forget_pass, txt_cambio_clave, txt_actualizar_datos;
    private PercentRelativeLayout volver;
    private Activity activity=this;
    private String Token;
    private long idUser;
    private int statusCode=0;
    private String userName;
    private AnalyticsApplication application;
    private Tracker mTracker;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;

    private AlertDialog.Builder alertDialog;
    private providers mProviders;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cambio_contrasena);
        footer foot = new footer();
        foot.footer(this);

        //Shared
        sharedpreferences = getSharedPreferences("info", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        //Fin
        //Inicializar Campos
        old_pass = (EditText) findViewById(R.id.prev_pass);
        new_pass = (EditText) findViewById(R.id.new_pass);
        repeat_pass = (EditText) findViewById(R.id.rep_pass);
        cambiar_clave = (LinearLayout) findViewById(R.id.cambiar_clave);
        txt_actualizar_datos = (TextView) findViewById(R.id.txt_actualizar_datos);
        forget_pass = (TextView) findViewById(R.id.txt_forget_pass);
        volver = (PercentRelativeLayout) findViewById(R.id.volver);
        txt_cambio_clave = (TextView) findViewById(R.id.txt_cambio_contrasena);

        Typeface type = Typeface.createFromAsset(getAssets(),"roboto_regular.ttf");
        Typeface bold= Typeface.createFromAsset(getAssets(),"roboto_bold.ttf");
        //Fin
        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        application.sendScreen("cambiarContrasena");
        // [END shared_tracker]
        //TextSize Ajust
        Porcentual porcentual = new Porcentual();
        txt_cambio_clave.setTextSize((float) (porcentual.getScreenInches(this)*3.5));
        txt_actualizar_datos.setTextSize((float) (porcentual.getScreenInches(this)*3.5));
        //Fin
        //Porcentual Ajust
        LinearLayout.LayoutParams actualizar_layout = (LinearLayout.LayoutParams) cambiar_clave.getLayoutParams();
        actualizar_layout.width = (int) (porcentual.getScreenWidth(this)*0.6);
        actualizar_layout.height = (int) (porcentual.getScreenHeight(this)*0.08);
        cambiar_clave.setLayoutParams(actualizar_layout);

        ViewGroup.LayoutParams params_txt = old_pass.getLayoutParams();
        params_txt.width = (int) (porcentual.getScreenWidth(this)*0.8);
        old_pass.setLayoutParams(params_txt);
        new_pass.setLayoutParams(params_txt);
        repeat_pass.setLayoutParams(params_txt);
        //Fin
        //TypeFace
        old_pass.setTypeface(type);
        new_pass.setTypeface(type);
        repeat_pass.setTypeface(type);
        txt_cambio_clave.setTypeface(bold);
        txt_actualizar_datos.setTypeface(bold);
        //Fin
        //Toma Datos
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
        //Fin
        //Net Listener
        alertDialog = new AlertDialog.Builder(this);
        mProviders = new providers();
        mProviders.providers(Token, userName, this, this, null, null, idUser);
        registerReceiver(mProviders.getBroadcastReceiverNET(alertDialog), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        //Fin
        //Listeners
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
        forget_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                application.sendEvent("cambiarContrasena", "forgetpass", "redirectForget");
                Uri uri = Uri.parse(getResources().getString(R.string.URL_Mobile_Web)+"Account/ForgotPassword");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        cambiar_clave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarClave();
            }
        });
        repeat_pass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    cambiarClave();
                    return true;
                }
                return false;
            }
        });
        //Fin
    }

    private void cambiarClave(){
        if(new_pass.getText().toString().equals(repeat_pass.getText().toString())){
            if(!old_pass.getText().toString().equals("") && !new_pass.getText().toString().equals("")){
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                        .setTitle(userName)
                        .setMessage("Actualizando contraseña...")
                        .setCancelable(false);
                final AlertDialog alert = builder.create();
                alert.show();
                final String[] mensaje = {""};
                new AsyncTask<Object, Object, Object>() {

                    @Override
                    protected Object doInBackground(Object... params) {
                        mensaje[0] = ApiUpdatePassuser();
                        while (mensaje[0] == "") {
                        }
                        return "Execute";
                    }

                    @Override
                    protected void onPostExecute(Object result) {
                        application.sendEvent("cambiarContrasena", "cambiarContrasena", "cambiar");
                        alert.dismiss();
                        AlertDialog.Builder alerta = new AlertDialog.Builder(activity)
                                .setTitle(userName)
                                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        dialog.dismiss();
                                        if(statusCode==200){
                                            if (sharedpreferences.getString("info", null) != null) {
                                                editor.putString("info", new_pass.getText().toString());
                                                editor.commit();
                                            }
                                            finish();
                                        }
                                    }
                                })
                                .setMessage(mensaje[0]);
                        alerta.create().show();
                    }
                }.execute();
            }else{
                AlertDialog.Builder alerta = new AlertDialog.Builder(activity)
                        .setTitle("Error")
                        .setMessage("Por favor, ingrese todos los campos");
                alerta.create().show();
            }
        }else{
            AlertDialog.Builder alerta = new AlertDialog.Builder(activity)
                    .setTitle("Error")
                    .setMessage("Las contraseñas deben ser iguales.");
            alerta.create().show();
        }
    }
    private String ApiUpdatePassuser(){
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(getString(R.string.URL_Mobile_Service)+"/api/Manage/ChangePassword");
        httpPost.addHeader("X-ZUMO-AUTH",Token);
        httpPost.addHeader("ZUMO-API-VERSION","2.0.0");
        httpPost.addHeader("Content-type","application/json");
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        JSONObject json = new JSONObject();
        try {
            json.put("idUser",idUser);
            json.put("OldPassword",old_pass.getText().toString());
            json.put("NewPassword",new_pass.getText().toString());
            httpPost.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException e) {
        }
        try {
            response = client.execute(httpPost);
            statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String b;
            while ((b = reader.readLine()) != null) {
                stringBuilder.append(b);
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
