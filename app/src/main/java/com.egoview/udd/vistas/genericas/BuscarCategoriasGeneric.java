package com.egoview.udd.vistas.genericas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.SystemUpdatePolicy;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.egoview.udd.R;
import com.egoview.udd.clases.campanias;
import com.egoview.udd.clases.categorias;
import com.egoview.udd.clases.subcategorias;
import com.egoview.udd.clases.htmlCamp;
import com.egoview.udd.clases.volleyy;
import com.egoview.udd.procesos.AnalyticsApplication;
import com.egoview.udd.procesos.Conexion;
import com.egoview.udd.procesos.ObjectSerializer;
import com.egoview.udd.procesos.Parseo_Json;
import com.egoview.udd.procesos.Redireccionar_Datos;
import com.egoview.udd.procesos.Rescate_Datos;
import com.egoview.udd.procesos.footer;
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
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Serial on 09/09/2015 modified 15/12/2015
 */
public class BuscarCategoriasGeneric extends Activity {
    private ArrayList<categorias> lista_categorias01;
    private ArrayList<subcategorias> lista_subcategorias01;
    private  String direccion;
    private  double latitud;
    private  double longitud;
    private String token, userName;
    private int radioGPS;
    private long idUser=0;
    private Boolean redSocial=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        footer foot = new footer();
        foot.footer(this);
        Boolean con_wifi = Conexion.Conexion_Wifi(getApplicationContext());
        Boolean con_datos = Conexion.Conexion_Datos(getApplicationContext());
        direccion=Rescate_Datos.rescatar_direccion(getIntent());
        try {
            token = getIntent().getExtras().getString("Token");
            idUser=getIntent().getExtras().getLong("idUser");
            userName=getIntent().getExtras().getString("userName");
            radioGPS=getIntent().getExtras().getInt("radioGPS");
            redSocial=getIntent().getExtras().getBoolean("redSocial");
        }catch(Exception e){        }
        try {
            latitud = Rescate_Datos.rescatar_latitud(getIntent());
            longitud = Rescate_Datos.rescatar_longitud(getIntent());
        }catch(Exception e){
        }

        if (con_datos || con_wifi) {
            if(idUser==0) {
                invokeApicategorias();
            }else{
                invokeApiSubcategoriasUser(2);
                invokeApicategoriasUser();
            }
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Internet Desactivado");
            builder.setMessage("No se ha encontrado conexion a internet, activelo para poder continuar.");
            builder.setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(Settings.ACTION_SETTINGS),0);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
            builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(getIntent());
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
            });
            builder.setCancelable(false);
            builder.create().show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        startActivity(getIntent());
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void invokeApicategorias() {
        ListenableFuture<JsonElement> result;
        try {
            MobileServiceClient mClient = new MobileServiceClient(getString(R.string.URL_Mobile_Service), this);
            try {
                result = mClient.invokeApi("Camps/CategoriasGenericas");
                Futures.addCallback(result, new FutureCallback<JsonElement>() {
                    @Override
                    public void onSuccess(JsonElement result) {
                        lista_categorias01 = Parseo_Json.parseoJsonCategorygeneric(result.toString());
                        if(lista_categorias01.size()!=0){
                            startActivity(Redireccionar_Datos.Redireccionar_Datos_Contenedor_Home_Generic(redSocial,lista_categorias01,lista_subcategorias01,direccion,getString(R.string.ambiente_ego),latitud,longitud, token, idUser));
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        }
                    }
                    @Override
                    public void onFailure(Throwable t){
                            startActivity(getIntent());
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        }
                });
            }catch (Exception e){}
        }catch(MalformedURLException e) {
        }
    }// end_method


    private void invokeApicategoriasUser() {
        ListenableFuture<ServiceFilterResponse> result;

        try {
            ArrayList<Pair<String, String>> headers = new ArrayList<Pair<String, String>>();
            headers.add(new Pair<String, String>("X-ZUMO-AUTH", token));
            ArrayList<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
            byte[] aux =new byte[]{};
            MobileServiceClient mClient= new MobileServiceClient(getString(R.string.URL_Mobile_Service), this);//m�todo de conexion a azure (this = contexto)
            try {
                result = mClient.invokeApi("Camps/CategoriasByUser/"+idUser,aux, "POST", headers,params);
                Futures.addCallback(result, new FutureCallback<ServiceFilterResponse>() {
                    @Override
                    public void onSuccess(ServiceFilterResponse result) {
                        lista_categorias01 = Parseo_Json.parseoJsonCategorypersonalizado(result.getContent());
                        SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        try {
                            editor.putString("categorias", ObjectSerializer.serialize(lista_categorias01));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        invokeApiGetCampUser();
                    }
                    @Override
                    public void onFailure(Throwable t){
                        startActivity(getIntent());
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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


    private void invokeApiSubcategoriasUser(int idCategoria) {

    ListenableFuture<ServiceFilterResponse> result;

        try {
            ArrayList<Pair<String, String>> headers = new ArrayList<Pair<String, String>>();
            headers.add(new Pair<String, String>("ZUMO-API-VERSION", "2.0.0"));
            headers.add(new Pair<String, String>("X-ZUMO-AUTH", token));
            ArrayList<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
            idUser=3;
            idCategoria=2;
            params.add(new Pair<String, String>("idUsuario",String.valueOf(idUser)));
            params.add(new Pair<String, String>("idCategoria",String.valueOf(idCategoria)));
            byte[] aux =new byte[]{};
            MobileServiceClient mClient= new MobileServiceClient(getString(R.string.URL_Mobile_Service), this);//m�todo de conexion a azure (this = contexto)
            try {
                result = mClient.invokeApi("api/Camps/SubCategoriasByUser",aux, "POST", headers,params);
                Futures.addCallback(result, new FutureCallback<ServiceFilterResponse>() {
                    @Override
                    public void onSuccess(ServiceFilterResponse result) {
                        lista_subcategorias01 = Parseo_Json.parseoJsonSubCategory(result.getContent());
                        SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        try {
                            editor.putString("subcategorias", ObjectSerializer.serialize(lista_subcategorias01));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        invokeApiGetCampUser();
                    }
                    @Override
                    public void onFailure(Throwable t){
                        System.out.println("error al traer subcategorias");
                        startActivity(getIntent());
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

    private void invokeApiGetCampUser() {
        ListenableFuture<ServiceFilterResponse> result;

        try {
            ArrayList<Pair<String, String>> headers = new ArrayList<Pair<String, String>>();
            headers.add(new Pair<String, String>("X-ZUMO-AUTH", token));
            ArrayList<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
            params.add(new Pair<String, String>("idUser",String.valueOf(idUser)));
            byte[] aux =new byte[]{};
            MobileServiceClient mClient= new MobileServiceClient(getString(R.string.URL_Mobile_Service), this);
            try {
                result = mClient.invokeApi("Usuarios/HasCampUser",aux, "POST",headers, params);
                Futures.addCallback(result, new FutureCallback<ServiceFilterResponse>() {
                    @Override
                    public void onSuccess(ServiceFilterResponse result) {

                        try {
                            JSONObject json = new JSONObject(result.getContent());
                            long idCamp = json.getLong("idCamp");
                            if(lista_categorias01.size()!=0){
                                Intent intent = Redireccionar_Datos.Redireccionar_Datos_Contenedor_Home_Generic(redSocial, lista_categorias01, lista_subcategorias01, direccion, getString(R.string.ambiente_ego), latitud, longitud, token, idUser);
                                intent.putExtra("userName",userName);
                                intent.putExtra("radioGPS",radioGPS);
                                intent.putExtra("idCamp",idCamp);
                                intent.putExtra("isPublicador", json.getBoolean("isPublicador"));
                                if (idCamp!=0){
                                    campanias campania = new campanias();
                                    campania.setNombre_camp(json.getString("nombre_camp"));
                                    campania.setCondiciones_uso(json.getString("condiciones_uso"));
                                    campania.setRuta_imagen(json.getString("ruta_img"));
                                    htmlCamp cod = new htmlCamp();
                                    cod.setCod_html(json.getString("cod_html"));
                                    campania.setHtmlCampArrayList(cod);
                                    campania.setNombre_empresa(json.getString("nombreUser"));
                                    intent.putExtra("campanias", campania);
                                }
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onFailure(Throwable t){
                        startActivity(getIntent());
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
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