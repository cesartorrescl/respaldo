package com.egoview.udd.vistas.genericas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.egoview.udd.Push.CreateUpdateService;
import com.egoview.udd.R;
import com.egoview.udd.contenedores.generico.ContenedorHomeGeneric;
import com.egoview.udd.procesos.AnalyticsApplication;
import com.egoview.udd.procesos.Porcentual;
import com.egoview.udd.procesos.footer;
import com.egoview.udd.procesos.providers;
import com.google.android.gms.analytics.Tracker;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by Ernesto on 27/04/2016.
 */
public class activity_inicio extends Activity {

    //Login
    private EditText correo;
    private EditText pass;
    private CheckBox mantener;
    private TextView forget_pass, txt_inicio_sesion,txt_inicia_sesion2;
    private MobileServiceClient mClient;
    private ListenableFuture<JsonElement> result;
    private Activity activity = this;
    private PercentRelativeLayout area_sesion,iniciar_sesion;
    //Registro
    private PercentRelativeLayout volver;
    private AnalyticsApplication application;
    private Tracker mTracker;

    public static Boolean isVisible = true;
    public long idUser = 0;
    private static final String TAG = "MainActivity";
    public static activity_inicio mActivity_inicio;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;

    private providers mProviders;
    private AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_inicio);
        footer foot = new footer();
        foot.footer(this);

        //Shared
        sharedpreferences = getSharedPreferences("info", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        //Fin
        //Push Notification
        mActivity_inicio = this;
        //Fin
        // Iniciar variables
        correo = (EditText) findViewById(R.id.correo);
        pass = (EditText) findViewById(R.id.contrasena);
        mantener = (CheckBox) findViewById(R.id.mantener);
        forget_pass = (TextView) findViewById(R.id.txt_forget_pass);
        iniciar_sesion = (PercentRelativeLayout) findViewById(R.id.inicio_sesion);
        area_sesion = (PercentRelativeLayout) findViewById(R.id.area_sesion);
        volver = (PercentRelativeLayout) findViewById(R.id.volver);
        txt_inicia_sesion2 = (TextView) findViewById(R.id.txt_inicia_sesion2);
        txt_inicio_sesion = (TextView) findViewById(R.id.txt_inicio_sesion);

        Typeface type = Typeface.createFromAsset(getAssets(),"roboto_regular.ttf");
        Typeface bold= Typeface.createFromAsset(getAssets(),"roboto_bold.ttf");

        // fin iniciar variables
        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        application.sendScreen("Inicio");
        // [END shared_tracker]
        //Net Listener
        alertDialog = new AlertDialog.Builder(this);
        mProviders = new providers();
        mProviders.providers(null, null, this, this, null, null, 0);
        registerReceiver(mProviders.getBroadcastReceiverNET(alertDialog), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        //Fin
        //TextSize Ajust
        Porcentual porcentual = new Porcentual();
        txt_inicia_sesion2.setTextSize((float) (porcentual.getScreenInches(this)*2.75));
        txt_inicio_sesion.setTextSize((float) (porcentual.getScreenInches(this)*3.5));
        //Fin
        //TypeFace
        txt_inicia_sesion2.setTypeface(bold);
        txt_inicio_sesion.setTypeface(bold);
        mantener.setTypeface(type);
        correo.setTypeface(type);
        pass.setTypeface(type);
        forget_pass.setTypeface(type);
        //Fin
        //OnClickListeners
        forget_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                application.sendEvent("inicio", "forgetpass", "redirectForget");
                Uri uri = Uri.parse(getResources().getString(R.string.URL_Mobile_Web)+"Account/ForgotPassword");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        iniciar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(correo.getText()!=null && !correo.getText().toString().equals("")) {
                    if(validarEmailLogin()) {
                        ApiGetUsuario();
                    }else{
                        AlertDialog.Builder alert = new AlertDialog.Builder(activity)
                                .setMessage("Formato de correo invalido")
                                .setPositiveButton("Aceptar", null);
                        alert.create().show();
                    }
                }else{
                    AlertDialog.Builder alert = new AlertDialog.Builder(activity)
                            .setMessage("Ingrese los datos")
                            .setPositiveButton("Aceptar", null);
                    alert.create().show();
                }
            }
        });
        pass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    if(correo.getText()!=null && !correo.getText().toString().equals("")) {
                        if(validarEmailLogin()) {
                            ApiGetUsuario();
                        }else{
                            AlertDialog.Builder alert = new AlertDialog.Builder(activity)
                                    .setMessage("Formato de correo invalido")
                                    .setPositiveButton("Aceptar", null);
                            alert.create().show();
                        }
                    }else{
                        AlertDialog.Builder alert = new AlertDialog.Builder(activity)
                                .setMessage("Ingrese los datos")
                                .setPositiveButton("Aceptar", null);
                        alert.create().show();
                    }
                    return true;
                }
                return false;
            }
        });
        //Fin OnCLickListeners
    }

    private void iniciar_push(){
        CreateUpdateService service = new CreateUpdateService();
        service.CreateUpdateService(idUser, this);
    }

    public void ApiGetUsuario()
    {
        final ProgressDialog dialog = ProgressDialog.show(this,"","Cargando. Por favor espere...", true);
        JsonObject json = new JsonObject();
        try {
            json.addProperty("Email", correo.getText().toString());
            json.addProperty("Password", pass.getText().toString());
        } catch (JsonIOException e) {
        }
        ArrayList<Pair<String, String>> headers = new ArrayList<Pair<String, String>>();
        headers.add(new Pair<String, String>("Content-Type", "application/json"));
        try
        {
            mClient = new MobileServiceClient(getString(R.string.URL_Mobile_Service), this);

            try {
                result =  mClient.invokeApi("Auth/Login",json, "POST", headers);
                Futures.addCallback(result, new FutureCallback<JsonElement>() {
                    @Override
                    public void onSuccess(JsonElement result) {
                        Log.d("JsonElement", result.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());
                            String userName = jsonObject.getJSONObject("user").getString("name");
                            ContenedorHomeGeneric chg = new ContenedorHomeGeneric();
                            if (chg.achg != null) {
                                chg.achg.finish();
                            }
                            Intent intent = new Intent("android.intent.action.BuscarCategoriasGenericdesa" + getString(R.string.ambiente_ego));
                            intent.putExtra("Token",jsonObject.getString("authenticationToken"));
                            idUser = Long.parseLong(jsonObject.getJSONObject("user").getString("userId"));
                            iniciar_push();
                            AnalyticsApplication.setIdUser(idUser);
                            AnalyticsApplication.setToken(jsonObject.getString("authenticationToken"));
                            AnalyticsApplication.setUserName(userName);
                            intent.putExtra("idUser", idUser);
                            intent.putExtra("userName",userName);
                            intent.putExtra("radioGPS", jsonObject.getJSONObject("user").getInt("rango"));
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            if(mantener.isChecked()){
                                //Guardar datos en shared
                                Log.d("JsonElement", "Checked fijado");
                                application.sendEvent("inicio", "login", "conectado");
                                editor.clear();
                                editor.putString("correo", correo.getText().toString());
                                editor.putString("info", pass.getText().toString());
                                editor.commit();
                            }else{
                                application.sendEvent("inicio", "login", "noConectado");
                            }
                            finish();
                        } catch (JSONException e) {

                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        application.sendEvent("inicio", "login", "fallido");
                        if (t.getMessage().equals("Error while processing request.")) {
                            Toast.makeText(activity, "Ha ocurrido un error inesperado, intentelo nuevamente.", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(activity, t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        if(!sharedpreferences.getString("correo","").equals("")){
                            ApiGetUsuario();
                        }
                        dialog.dismiss();
                    }
                });
            }
            catch (Exception e){
            }
        }
        catch(MalformedURLException e)
        {
        }
    }

    public boolean validarEmailLogin(){
        if(correo.getText() != null && !correo.getText().toString().equals("")){
            if(correo.getText().toString().matches("^[\\w-]+(\\.[\\w-]+)*@[A-Za-z0-9\\-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        AnalyticsApplication.activityPaused();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        AnalyticsApplication.activityResumed(this);
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }
}
