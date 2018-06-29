package com.egoview.udd.vistas.personalizadas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.egoview.udd.R;
import com.egoview.udd.adapter.ConfPrefAdapter;
import com.egoview.udd.clases.categorias;

import com.egoview.udd.contenedores.generico.ContenedorHomeGeneric;
import com.egoview.udd.procesos.AnalyticsApplication;
import com.egoview.udd.procesos.ObjectSerializer;
import com.egoview.udd.procesos.Parseo_Json;
import com.egoview.udd.procesos.Porcentual;
import com.egoview.udd.procesos.providers;
import com.egoview.udd.vistas.genericas.Fragment_activity_categorias_generic;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Ernesto on 04/05/2016.
 */
public class activity_cambiar_preferencias extends Activity {

    private RecyclerView vista_lista;
    private ArrayList<categorias> lista_categorias01, categorias_user, cat_actualizadas;
    protected ArrayList<categorias> lista_subcat;
    private String Token, userName;
    private Activity activity=this;
    private long idUser;
    private int radio_actual;
    private PercentRelativeLayout volver;
    private TextView txt_preferencias, txt_seleccion;
    private ConfPrefAdapter adapter;
    private AnalyticsApplication application;
    private Tracker mTracker;
    private SwipeRefreshLayout swipe;


    private AlertDialog.Builder alertDialog;
    private providers mProviders;

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_preferencias);


        //Iniciar Campos
        vista_lista = (RecyclerView) findViewById(R.id.listado_categorias);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        vista_lista.setLayoutManager(layoutManager);
        vista_lista.setHasFixedSize(true);
        vista_lista.setDrawingCacheEnabled(true);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setEnabled(false);
        volver = (PercentRelativeLayout) findViewById(R.id.volver);
        txt_preferencias = (TextView) findViewById(R.id.txt_preferencias);
        txt_seleccion= (TextView) findViewById(R.id.txt_selecciona_preferencias);
        Typeface type = Typeface.createFromAsset(getAssets(),"roboto_regular.ttf");
        Typeface bold= Typeface.createFromAsset(getAssets(),"roboto_bold.ttf");
        //Fin
        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        application.sendScreen("CambiarPreferencias");
        // [END shared_tracker]
        //TextSize Ajust
        Porcentual porcentual = new Porcentual();
        txt_preferencias.setTextSize((float) (porcentual.getScreenInches(this) * 3.5));
        txt_seleccion.setTextSize((float) (porcentual.getScreenInches(this) * 2.5));
        //Fin
        //TypeFace
        txt_preferencias.setTypeface(bold);
        txt_seleccion.setTypeface(type);
        //Fin
        //Tomar Datos
        try {
            Token = getIntent().getExtras().getString("Token");
            idUser=getIntent().getExtras().getLong("idUser");
            radio_actual=getIntent().getExtras().getInt("radioGPS");
            userName=getIntent().getExtras().getString("userName");
            categorias_user = (ArrayList<categorias>) getIntent().getSerializableExtra("categorias");
            invokeApicategorias();

        }catch(Exception e){
            AlertDialog.Builder error = new AlertDialog.Builder(this).setTitle("Error")
                    .setMessage("Usted no esta Logeado en la aplicacion, inicie sesión para acceder a esta seccion.")
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
        //Listener
        volver.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                ArrayList<categorias> compare = adapter.getLista();
                if(compare.size()!=0) {
                    boolean iguales = true;
                    if (compare.size() == categorias_user.size()) {
                        for (int i = 0; i < compare.size(); i++) {
                            if (compare.get(i).getId_categorias() != categorias_user.get(i).getId_categorias()) {
                                iguales = false;
                            }
                        }
                    } else {
                        iguales = false;
                    }

                    if (!iguales) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                                .setTitle(userName)
                                .setMessage("Actualizando preferencias...")
                                .setCancelable(true);
                        builder.create().show();
                        final String[] mensaje = {""};
                        final AsyncTask<Object, Object, Object> execute = new AsyncTask<Object, Object, Object>() {

                            @Override
                            protected Object doInBackground(Object... params) {
                                mensaje[0] = ApiUpdatePrefuser();
                                while (mensaje[0] == "") {
                                }
                                return "Execute";
                            }

                            @Override
                            protected void onPostExecute(Object result) {
                                application.sendEvent("cambiarPreferencias", "cambiarPreferencias", "cambiar");
                                if (mensaje[0].equals("\"Preferencias guardadas correctamente\"")) {
                                    Fragment_activity_categorias_generic fragment = (Fragment_activity_categorias_generic) Fragment_activity_categorias_generic.fragment;
                                    fragment.setCategorias(cat_actualizadas, 1);
                                    SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    try {
                                        editor.putString("categorias", ObjectSerializer.serialize(lista_categorias01));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    editor.apply();
                                    finish();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(activity_cambiar_preferencias.this)
                                            .setTitle("Error")
                                            .setCancelable(true)
                                            .setMessage("Ha ocurrido un error inesperado, intentelo nuevamente.");
                                    builder.create().show();
                                }
                            }
                        }.execute();
                    } else {
                        finish();
                    }
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity_cambiar_preferencias.this)
                            .setTitle("Error")
                            .setCancelable(true)
                            .setMessage("La cantidad mínima es de una preferencia.");
                    builder.create().show();
                }

            }
        });
        volver.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v == volver) {
                    if (MotionEvent.ACTION_DOWN == event.getAction()) {
                        v.setBackgroundColor(getResources().getColor(R.color.azul_cupon));
                    }
                    if (MotionEvent.ACTION_UP == event.getAction()) {
                        v.setBackgroundColor(getResources().getColor(R.color.azulclaro_college));
                    }
                }
                return false;
            }
        });
        //Fin
    }

    private void invokeApicategorias() {
        final ProgressDialog dialog = ProgressDialog.show(this,"","Cargando preferencias. Por favor espere...", true);
        ListenableFuture<JsonElement> result;
        lista_subcat = new  ArrayList<categorias> ();
        try {
            MobileServiceClient mClient = new MobileServiceClient(getString(R.string.URL_Mobile_Service), this);
            try {
                result = mClient.invokeApi("Camps/CategoriasGenericas");
                Futures.addCallback(result, new FutureCallback<JsonElement>() {
                    @Override
                    public void onSuccess(JsonElement result) {
                        lista_categorias01 = Parseo_Json.parseoJsonCategorygenericPref(result.toString());
                        for(int i = 0; i<lista_categorias01.size(); i++){
                            lista_categorias01.get(i).setEstado(false);
                            invokeApiSubcategoriasUser(lista_categorias01.get(i).getId_categorias());
                            for (int j=0; j<categorias_user.size(); j++){
                                if(categorias_user.get(j).getId_categorias()==lista_categorias01.get(i).getId_categorias()){
                                    lista_categorias01.get(i).setEstado(true);
                                }
                            }
                        }
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        adapter = new ConfPrefAdapter(activity, lista_categorias01, lista_subcat, activity.getApplicationContext());
                                        vista_lista.setSelected(true);
                                        vista_lista.setAdapter(adapter);
                                        dialog.dismiss();
                                    }
                                }, 2000);

                    }


                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(activity, "Hubo un error, intentelo nuevamente por favor.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
                    }
                });

            }catch (Exception e){dialog.dismiss();}
        }catch(MalformedURLException e) {
            dialog.dismiss();
        }

    }// end_method

    private void invokeApiSubcategoriasUser(final int idCategoria) {

        ListenableFuture<ServiceFilterResponse> result;
        try {
            ArrayList<Pair<String, String>> headers = new ArrayList<Pair<String, String>>();
            headers.add(new Pair<String, String>("X-ZUMO-AUTH", Token));
            ArrayList<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
            params.add(new Pair<String, String>("idUsuario",String.valueOf(idUser)));
            params.add(new Pair<String, String>("idCategoria",String.valueOf(idCategoria)));
            byte[] aux =new byte[]{};
            URL url= new URL("https://uddappservice.azurewebsites.net/");
            MobileServiceClient mClient= new MobileServiceClient(url, getApplicationContext());//metodo de conexion a azure (this = contexto)
            try {
                result = mClient.invokeApi("Camps/SubCategoriasByUser",aux, "POST", headers,params);
                Futures.addCallback(result, new FutureCallback<ServiceFilterResponse>() {
                    @Override
                    public void onSuccess(ServiceFilterResponse result) {
                        String listaAux = result.getContent().toString();
                         try {
                             JSONArray jsonArray = new JSONArray(listaAux);
                                for (int i=0; i<jsonArray.length();i++){
                                    categorias cat = new categorias();
                                    cat.setId(jsonArray.getJSONObject(i).getLong("id_subcategoria"));
                                    cat.setDescripcion_campania("        " + jsonArray.getJSONObject(i).getString("desc_subcategoria"));
                                    cat.setId_sub_categorias(jsonArray.getJSONObject(i).getInt("id_subcategoria"));
                                    cat.setId_categorias(idCategoria);
                                    cat.setEstado(false);
                                    lista_subcat.add(cat);
                                    for (int j=0; j<categorias_user.size(); j++){
                                        if(categorias_user.get(j).getId_categorias()==lista_subcat.get(i).getId_sub_categorias()){
                                            lista_subcat.get(i).setEstado(true);
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d("resultado222",t.toString());
                        finish();
                    }
                });

            }catch (Exception e){
                Log.d("error",e.getMessage());
            }
        }catch(MalformedURLException e){
            Log.d("error",e.getMessage());
        }
    }

    private String ApiUpdatePrefuser(){
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(getString(R.string.URL_Mobile_Service)+"api/Manage/UpdateCategoriasUser");
        httpPost.addHeader("X-ZUMO-AUTH", Token);
        httpPost.addHeader("ZUMO-API-VERSION","2.0.0");
        httpPost.addHeader("Content-type","application/json");
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            json.put("IdUser",idUser);
            cat_actualizadas = adapter.getLista();
            for(int i =0; i<cat_actualizadas.size(); i++) {
                JSONObject jsonInterior = new JSONObject();
                jsonInterior.put("id_categoria",cat_actualizadas.get(i).getId_categorias());
                jsonInterior.put("selectTipo",true);
                array.put(i,jsonInterior);
            }
            json.put("ListTipoCamp",array);
            httpPost.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException e) {
        }
        try {
            response = client.execute(httpPost);
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
            return new String(jsonObject.toString().getBytes("ISO-8859-1"), "UTF-8");

        } catch (JSONException e) {
            return stringBuilder.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
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
