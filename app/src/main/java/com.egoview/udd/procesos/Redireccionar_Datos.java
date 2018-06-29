package com.egoview.udd.procesos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.egoview.udd.R;
import com.egoview.udd.clases.campanias;
import com.egoview.udd.clases.categorias;
import com.egoview.udd.clases.subcategorias;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/** Created by Serial on 06/10/2015 modified on 09/12/2015.*/
public class Redireccionar_Datos extends Activity {

    public static Intent Redireccionar_Datos_Buscar_Categorias_Generic(String mi_ubicacion, String ambiente, double lat, double lon) {
        Intent intent;
        intent=new Intent("android.intent.action.BuscarCategoriasGeneric"+ambiente);
        intent.putExtra("latitud",lat);
        intent.putExtra("longitud",lon);
        intent.putExtra("direccion",mi_ubicacion);
        return intent;
    }


    public static Intent Redireccionar_Datos_Contenedor_Home_Generic(Boolean redSocial, ArrayList<categorias> list_categorias, ArrayList<subcategorias> list_sub_categorias, String direccion, String ambiente, double latitud, double longitud, String token, long user) {
        try {
            //Asignacion de datos hacia el otro activity
            Intent intent = new Intent("android.intent.action.ContenedorHomeGenericdesa"+ambiente);
            intent.putExtra("list_categorias", list_categorias);
            intent.putExtra("list_sub_categorias", list_sub_categorias);
            AnalyticsApplication.setCat(list_categorias);
            intent.putExtra("direccion", direccion);
            intent.putExtra("latitud",latitud);
            intent.putExtra("longitud", longitud);
            intent.putExtra("idUser",user);
            intent.putExtra("Token",token);
            intent.putExtra("redSocial",redSocial);
            if (list_categorias == null) {
                intent.putExtra("cantidad_categorias", 0);
            } else {
                intent.putExtra("cantidad_categorias", list_categorias.size()); // lista de categorias
            }
            return intent;
        } catch (Exception e) {
            return null;
        }
    }//end of method

    public static void Cargar_Datos_Contenedor_Campania(int position, final String token, final long idUser, final campanias campania01, Context context, final String ambiente, double latitud, double longitud, final Activity activity, boolean flag) {
        final String[] mensaje = {""};
        if(!campania01.getVisto()){
            new AsyncTask<Object, Object, Object>() {

                @Override
                protected Object doInBackground(Object... params) {
                    mensaje[0] = ApiVisto(token, idUser, campania01.getIdCampania(), activity);

                    return "Execute";
                }

                @Override
                protected void onPostExecute(Object result) {

                }
            }.execute();
        }
        while (mensaje[0] == "" && !campania01.getVisto()) {
        }
        if(mensaje[0]!=null) {
            if (mensaje[0].equals("200")) {
                campania01.setVisto(true);
            }
        }
        Apigetsucursales(position, token, idUser, campania01, latitud, longitud, context, ambiente, activity, flag);
    }

    private static String ApiVisto(String token, long idUser, String idCamp, Context context){
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(context.getString(R.string.URL_Mobile_Service)+"api/Camps/VistoCampByUser?idUser="+String.valueOf(idUser)+"&idCamp="+String.valueOf(idCamp));
        httpPost.addHeader("X-ZUMO-AUTH", token);
        httpPost.addHeader("ZUMO-API-VERSION","2.0.0");
        httpPost.addHeader("Content-type","application/json");
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
            return String.valueOf(response.getStatusLine().getStatusCode());
        } catch (ClientProtocolException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public static void Apigetsucursales(final int position, final String token, final long idUser, final campanias campania01, final double lat, final double lon, final Context context, final String ambiente, final Activity activity, final boolean flag) {
        final ProgressDialog progressDialog;
        progressDialog = DialogEgoView.crear_progres_dialog_campanias(context);
        List<Pair<String, String>> queryParams;
        MobileServiceClient mClient;
        ListenableFuture<JsonElement> result;
        queryParams = new ArrayList<Pair<String, String>>();
        queryParams.add(new Pair<String, String>("idCamp", campania01.getIdCampania()));
        queryParams.add(new Pair<String, String>("latitud", String.valueOf(lat)));
        queryParams.add(new Pair<String, String>("longitud", String.valueOf(lon)));
        Log.i("Posicion Linkeo", campania01.getIdCampania()+"-"+String.valueOf(lat)+"-"+String.valueOf(lon));
        try {
            mClient = new MobileServiceClient(context.getString(R.string.URL_Mobile_Service), context);
            try {
                result = mClient.invokeApi("Camps/DetalleCampGenericaByidCamp", "POST", queryParams);
                Futures.addCallback
                        (result, new FutureCallback<JsonElement>() {
                                    @Override
                                    public void onSuccess(JsonElement result) {
                                        campanias campania02 = Parseo_Json.parseoJsonCampainFinal(result.toString(), campania01);
                                        Redireccionar_Contenedor_Campania(position, token, idUser, campania02, context, ambiente, lat,lon, activity, flag);
                                        DialogEgoView.terminar_progrest(progressDialog);
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        DialogEgoView.terminar_progrest(progressDialog);
                                    }
                                }
                        );
            } catch (Exception e) {

                DialogEgoView.terminar_progrest(progressDialog);
            }
        } catch (MalformedURLException e) {
            DialogEgoView.terminar_progrest(progressDialog);
        }
    }

    private static void Redireccionar_Contenedor_Campania(int position, String token, long idUser, campanias campania01, Context context,final String ambiente,double lat, double lon, Activity activity, boolean flag){
        Intent intent = new Intent("android.intent.action.ContenedorCampaniadesa"+ambiente);
        intent.putExtra("latitud",lat);
        intent.putExtra("longitud",lon);
        intent.putExtra("flag", flag);
        intent.putExtra("token", token);
        intent.putExtra("idUser", idUser);
        intent.putExtra("position", position);
        campanias.setCampanias(campania01);
        context.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


}
