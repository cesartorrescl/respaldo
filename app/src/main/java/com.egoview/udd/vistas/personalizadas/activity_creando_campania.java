package com.egoview.udd.vistas.personalizadas;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Window;
import android.widget.TextView;

import com.egoview.udd.R;
import com.egoview.udd.clases.campanias;
import com.egoview.udd.clases.direccion;
import com.egoview.udd.clases.htmlCamp;
import com.egoview.udd.contenedores.generico.ContenedorHomeGeneric;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
import java.net.URI;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

/**
 * Created by meste on 21-08-2016.
 */
public class activity_creando_campania extends Activity {

    private String token, nombreImagen, inicio_direccion, numero_direccion, rutaImagen, comuna_direccion, colorFondo;
    private long idUser;
    private int idComuna;
    private campanias camp;
    private direccion dir;
    private ContenedorHomeGeneric homeGeneric;
    private URI blobUri;
    private StorageCredentials credencial;
    private Uri uri_envio;
    private Activity activity = this;
    public static Activity achg;
    private TextView generic;

    protected void onCreate(Bundle savedInstanceState) {
        achg=this;
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_creando_campania);

        try{
            idUser = getIntent().getExtras().getLong("idUser");
            token = getIntent().getExtras().getString("token");
            inicio_direccion = getIntent().getExtras().getString("inicio_dir");
            numero_direccion = getIntent().getExtras().getString("numero_dir");
            comuna_direccion = getIntent().getExtras().getString("comuna_dir");
            idComuna = getIntent().getExtras().getInt("idComuna");
            camp = (campanias) getIntent().getExtras().get("camp");
            homeGeneric = new ContenedorHomeGeneric();
            nombreImagen = getIntent().getExtras().getString("nombreImagen");
            uri_envio =(Uri) getIntent().getExtras().get("uri");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void onStart(){
        super.onStart();
        iniciar();
    }

    private void iniciar(){
        String direccion_completa = inicio_direccion+"+"+numero_direccion+"+"+comuna_direccion;
        AsyncTask<String, String, direccion> ubicacion_async = new GeocodeAPI().execute(String.valueOf(direccion_completa).replaceAll(" ", "+"));
        try {
            dir = ubicacion_async.get();
            if (dir == null) {
                activity_sucursal_form back = new activity_sucursal_form();
                ((activity_sucursal_form) back.achg).errorDireccion();
                finish();
            } else {
                if (uri_envio != null) {
                    final boolean[] aux = {false};
                    final String[] serviceToken = {""};
                    new AsyncTask<Object, Object, Object>() {

                        @Override
                        protected Object doInBackground(Object... params) {
                            serviceToken[0] = GetToken(uri_envio);
                            return null;
                        }
                    }.execute();

                    while (serviceToken[0] == "") {
                    }
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(serviceToken[0]);
                        String token = jsonObject.getString("token");
                        final String uri = jsonObject.getString("uri");
                        blobUri = URI.create(uri);
                        credencial = StorageCredentials.tryParseCredentials(token);
                        final CloudBlockBlob blobFromSASCredential = new CloudBlockBlob(blobUri, credencial);
                        new AsyncTask<Object, Object, Object>() {

                            @Override
                            protected Object doInBackground(Object[] params) {
                                try {
                                    blobFromSASCredential.uploadFromFile(uri_envio.getPath());
                                    rutaImagen = createUrlEnvio(uri);
                                    aux[0] = true;
                                } catch (StorageException e) {
                                    activity_sucursal_form back = new activity_sucursal_form();
                                    ((activity_sucursal_form) back.achg).errorImagen();
                                    finish();
                                } catch (IOException e) {
                                    activity_sucursal_form back = new activity_sucursal_form();
                                    ((activity_sucursal_form) back.achg).errorImagen();
                                    finish();
                                } catch (Exception e) {
                                    activity_sucursal_form back = new activity_sucursal_form();
                                    ((activity_sucursal_form) back.achg).errorImagen();
                                    finish();
                                }
                                return null;
                            }
                        }.execute();

                    } catch (JSONException e) {
                        activity_sucursal_form back = new activity_sucursal_form();
                        ((activity_sucursal_form) back.achg).errorImagen();
                        finish();
                    } catch (StorageException e) {
                        activity_sucursal_form back = new activity_sucursal_form();
                        ((activity_sucursal_form) back.achg).errorImagen();
                        finish();
                    } catch (InvalidKeyException e) {
                        activity_sucursal_form back = new activity_sucursal_form();
                        ((activity_sucursal_form) back.achg).errorImagen();
                        finish();
                    }
                    while (!aux[0]){}
                    final String[] respuesta = new String[1];
                    respuesta[0] = "";
                    new AsyncTask<Object, Object, Object>() {

                        @Override
                        protected Object doInBackground(Object... params) {
                            respuesta[0] = CreateCamp();
                            return null;
                        }
                    }.execute();
                    while (respuesta[0] == "") {
                    }
                    try {
                        final long idCamp = Integer.parseInt(respuesta[0]);
                        activity_sucursal_form back = new activity_sucursal_form();
                        ((activity_sucursal_form) back.achg).exito(idCamp);
                        finish();
                    } catch (Exception e) {
                        activity_sucursal_form back = new activity_sucursal_form();
                        ((activity_sucursal_form) back.achg).errorServicio();
                        finish();
                    }
                }else {
                    final String[] respuesta = new String[1];
                    respuesta[0] = "";
                    new AsyncTask<Object, Object, Object>() {

                        @Override
                        protected Object doInBackground(Object... params) {
                            respuesta[0] = CreateCamp();
                            return null;
                        }
                    }.execute();
                    while (respuesta[0] == "") {
                    }
                    try {
                        final long idCamp = Integer.parseInt(respuesta[0]);
                        activity_sucursal_form back = new activity_sucursal_form();
                        ((activity_sucursal_form) back.achg).exito(idCamp);
                        finish();
                    } catch (Exception e) {
                        activity_sucursal_form back = new activity_sucursal_form();
                        ((activity_sucursal_form) back.achg).errorServicio();
                        finish();
                    }
                }
            }
        }catch (ExecutionException e){
            activity_sucursal_form back = new activity_sucursal_form();
            ((activity_sucursal_form) back.achg).errorServicio();
            finish();
        }catch (InterruptedException e) {
            activity_sucursal_form back = new activity_sucursal_form();
            ((activity_sucursal_form) back.achg).errorServicio();
            finish();
        }
    }

    public String createUrlEnvio(String uri){
        return uri.substring(0, uri.indexOf("?"));
    }

    private String GetToken(final Uri resultURI){
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(getString(R.string.URL_Mobile_Service)+"/api/Storage/GetTokenForUpload");
        httpPost.addHeader("X-ZUMO-AUTH",token);
        httpPost.addHeader("ZUMO-API-VERSION","2.0.0");
        httpPost.addHeader("Content-type","application/json");
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        JSONObject json = new JSONObject();
        try {
            json.put("idUser",idUser);
            nombreImagen = resultURI.getLastPathSegment();
            json.put("nombreImagen",nombreImagen);
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
            Log.d("","");
        } catch (IOException e) {
            Log.d("","");
        } catch (Exception e) {
            Log.d("","");
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
            return jsonObject.toString();

        } catch (JSONException e) {
            return stringBuilder.toString();
        }
    }

    private String CreateCamp(){
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(getString(R.string.URL_Mobile_Service)+"/api/Camps/UploadCampByUser");
        httpPost.addHeader("X-ZUMO-AUTH",token);
        httpPost.addHeader("ZUMO-API-VERSION","2.0.0");
        httpPost.addHeader("Content-type","application/json");
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        JSONObject json = new JSONObject();
        try {
            json.put("idUser",idUser);
            json.put("nombre_camp",camp.getNombre_camp());
            json.put("desc_campania", camp.getDesc_campania());
            Calendar inicio = Calendar.getInstance();
            json.put("inicio_camp",inicio.get(Calendar.YEAR)+"-"+(inicio.get(Calendar.MONTH)+1)+"-"+inicio.get(Calendar.DAY_OF_MONTH));
            inicio.add(Calendar.MONTH,+1);
            json.put("fin_camp",inicio.get(Calendar.YEAR)+"-"+(inicio.get(Calendar.MONTH)+1)+"-"+inicio.get(Calendar.DAY_OF_MONTH));
            json.put("id_categoria",0);
            json.put("condiciones_uso", camp.getCondiciones_uso());
            json.put("colorFont", "#FFFFFF");
            colorCamp();
            json.put("colorFondo",colorFondo);
            json.put("direccion_camp", inicio_direccion);
            json.put("numero", numero_direccion);
            json.put("id_comuna", idComuna);
            json.put("latitud_campuser", dir.getLatitud());
            json.put("longitud_campuser", dir.getLongitud());
            json.put("nombreImagen", nombreImagen);
            json.put("rutaImagen", rutaImagen);
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
            return jsonObject.toString();

        } catch (JSONException e) {
            return stringBuilder.toString();
        }
    }

    protected class GeocodeAPI extends AsyncTask<String, String, direccion> {

        @Override
        protected direccion doInBackground(String... params) {
            HttpGet httpGet = new HttpGet("https://maps.googleapis.com/maps/api/geocode/json?address="+params[0]+"+Chile");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            StringBuilder stringBuilder = new StringBuilder();

            try {
                response = client.execute(httpGet);
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
                String status = jsonObject.getString("status");

                if(status.equals("OK")) {
                    JSONArray array = jsonObject.getJSONArray("results");
                    for(int i = 0; i < array.length(); i++){
                        JSONObject obj = array.getJSONObject(i);
                        String tipo = obj.getJSONObject("geometry").getString("location_type");
                        if(!tipo.equals("APPROXIMATE") && !tipo.equals("GEOMETRIC_CENTER")) {
                            direccion dir = new direccion();
                            JSONObject location = obj.getJSONObject("geometry").getJSONObject("location");
                            dir.setLatitud(location.getDouble("lat"));
                            dir.setLongitud(location.getDouble("lng"));
                            return dir;
                        }
                    }
                    return null;
                }else {
                    return null;
                }

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    private void invokeApiGetCampUser(final long idCamp) {
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
                            campanias campania = new campanias();
                            campania.setNombre_camp(json.getString("nombre_camp"));
                            campania.setCondiciones_uso(json.getString("condiciones_uso"));
                            campania.setRuta_imagen(json.getString("ruta_img"));
                            htmlCamp cod = new htmlCamp();
                            cod.setCod_html(json.getString("cod_html"));
                            campania.setHtmlCampArrayList(cod);
                            campania.setNombre_empresa(json.getString("nombreUser"));
                            ((ContenedorHomeGeneric) homeGeneric.achg).setPublicacion(true,idCamp, campania);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onFailure(Throwable t){
                        invokeApiGetCampUser(idCamp);
                    }
                });
            }catch (Exception e){
                Log.d("error",e.getMessage());
            }
        }catch(MalformedURLException e){
            Log.d("error",e.getMessage());
        }
    }

    private void colorCamp(){
            colorFondo = "#fd4703";
    }
}
