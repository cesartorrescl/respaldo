package com.egoview.udd.vistas.personalizadas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.egoview.udd.R;
import com.egoview.udd.procesos.AnalyticsApplication;
import com.egoview.udd.procesos.Porcentual;
import com.egoview.udd.procesos.footer;

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


/**
 * Created by Ernesto on 29/04/2016.
 */
public class activity_contacto extends Activity {

    private PercentRelativeLayout volver;
    private TextView titulo, opinion, opinion2, enviar;
    private EditText asunto, mensaje;
    private LinearLayout btn_enviar;
    private String Token, userName;
    private long idUser;
    private Activity activity=this;
    private int statusCode=0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_contacto);
        footer foot = new footer();
        foot.footer(this);
        //Inicializar Variables
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
                            finish();
                        }
                    });
            error.create().show();
        }

        titulo = (TextView) findViewById(R.id.txt_titulo);
        opinion = (TextView) findViewById(R.id.txt_opinion);
        opinion.setText("¡Hola "+userName+"!\nTu opinión nos importa");
        opinion2 = (TextView) findViewById(R.id.txt_opinion2);
        enviar= (TextView) findViewById(R.id.txt_enviar);

        asunto = (EditText) findViewById(R.id.asunto);
        mensaje = (EditText) findViewById(R.id.mensaje);

        btn_enviar = (LinearLayout) findViewById(R.id.enviar);

        Typeface type = Typeface.createFromAsset(getAssets(),"roboto_regular.ttf");
        Typeface bold= Typeface.createFromAsset(getAssets(),"roboto_regular.ttf");
        //Fin
        //TextSize Ajust
        Porcentual porcentual = new Porcentual();

        titulo.setTextSize((float) (porcentual.getScreenInches(this)*3.5));
        opinion.setTextSize((float) (porcentual.getScreenInches(this)*3.0));
        opinion2.setTextSize((float) (porcentual.getScreenInches(this)*2.8));
        enviar.setTextSize((float) (porcentual.getScreenInches(this)*3.5));
        //Fin

        //TypeFace
        titulo.setTypeface(bold);
        opinion.setTypeface(type);
        opinion2.setTypeface(type);
        enviar.setTypeface(type);
        //Fin

        //Net Listener
        volver = (PercentRelativeLayout) findViewById(R.id.volver);
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

        btn_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(asunto.getText()!=null && !asunto.getText().toString().equals("") && mensaje.getText()!=null && !mensaje.getText().toString().equals("")){
                    if(mensaje.getText().length()<501 && asunto.getText().length()<51) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                                .setTitle(userName)
                                .setMessage("Enviando")
                                .setCancelable(false);
                        final AlertDialog alert = builder.create();
                        alert.show();
                        final String[] mensaje = {""};
                        new AsyncTask<Object, Object, Object>() {

                            @Override
                            protected Object doInBackground(Object... params) {
                                mensaje[0] = ApiContacto();
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
                                                if (statusCode == 200) {
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
                        AlertDialog.Builder error = new AlertDialog.Builder(activity).setTitle("Error")
                                .setMessage("El largo máximo del asunto es de 50 y el de la descripción es de 500.")
                                .setCancelable(false)
                                .setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        error.create().show();
                    }
                }else{
                    AlertDialog.Builder error = new AlertDialog.Builder(activity).setTitle("Error")
                            .setMessage("Favor ingresar todos los campos.")
                            .setCancelable(false)
                            .setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    error.create().show();
                }
            }
        });
        //Fin
    }

    private String ApiContacto(){
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(getString(R.string.URL_Mobile_Service)+"/api/Usuarios/MyFeedback");
        httpPost.addHeader("X-ZUMO-AUTH",Token);
        httpPost.addHeader("ZUMO-API-VERSION","2.0.0");
        httpPost.addHeader("Content-type","application/json");
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        JSONObject json = new JSONObject();
        try {
            json.put("idUsuario",idUser);
            json.put("asunto",asunto.getText().toString());
            json.put("descripcion",mensaje.getText().toString());
            httpPost.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException e) {
        }
        try {
            response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            statusCode = response.getStatusLine().getStatusCode();
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
