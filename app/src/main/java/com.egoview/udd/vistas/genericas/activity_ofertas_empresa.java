package com.egoview.udd.vistas.genericas;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egoview.udd.R;
import com.egoview.udd.adapter.adapterPerfilEmpresa;
import com.egoview.udd.clases.campanias;
import com.egoview.udd.procesos.AnalyticsApplication;
import com.egoview.udd.procesos.Parseo_Json;
import com.egoview.udd.procesos.Porcentual;
import com.egoview.udd.procesos.RoundedTransformation;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.squareup.picasso.Picasso;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by Ernesto on 29/09/2016.
 */
public class activity_ofertas_empresa extends Activity {

    private ImageView img_empresa, img_casaMatriz, img_informacion, barra_separadora, val1, val2, val3, val4, val5, flecha_izq, flecha_der;
    private TextView txt_nombreEmpresa, txt_descripcionEmpresa, txt_infoCasa, txt_casaMatriz, txt_infoHorarios, txt_horarios,
        txt_infoTelefonos, txt_telefonos, txt_infoWeb, txt_web, txt_infoValoracion, txt_campActivas, txt_cambia;
    private RecyclerView recyclerView;
    private LinearLayout linear_valoracion, area_nombre, area_datos, area_casaMatriz, area_oculta;
    private Button valoracion;
    private int valoracionUser, valoracionUserActual, position;
    private long idUser, idEmpresa;
    private String token, userName;
    private double lat, lon;
    private campanias camp;
    private ArrayList<campanias> lista_camps;
    private Activity activity = this;
    private Fragment_activity_categorias_generic fragment;
    private PercentRelativeLayout volver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_ofertas_empresa);

        Porcentual porcentual = new Porcentual();
        final int width = porcentual.getScreenWidth(this);
        final int height = porcentual.getScreenHeight(this);
        double screenInches= new Porcentual().getScreenInches(this);
        Typeface type = Typeface.createFromAsset(getAssets(), "roboto_regular.ttf");
        Typeface bold = Typeface.createFromAsset(getAssets(), "roboto_bold.ttf");
        fragment = (Fragment_activity_categorias_generic) Fragment_activity_categorias_generic.fragment;

        camp = campanias.getCampanias();
        userName = getIntent().getExtras().getString("userName");
        token = getIntent().getExtras().getString("token");
        idUser = getIntent().getExtras().getLong("idUser");
        idEmpresa = getIntent().getExtras().getLong("idEmpresa");
        position = getIntent().getExtras().getInt("position");
        lat = getIntent().getExtras().getDouble("lat");
        lon = getIntent().getExtras().getDouble("lon");

        area_casaMatriz = (LinearLayout) findViewById(R.id.area_casaMatriz);
        area_oculta = (LinearLayout) findViewById(R.id.area_oculta);

        img_informacion = (ImageView) findViewById(R.id.informacion_extra);
        RelativeLayout.LayoutParams paramsInfo = (RelativeLayout.LayoutParams) img_informacion.getLayoutParams();
        paramsInfo.width = (int) (width*0.05);
        paramsInfo.height = paramsInfo.width;
        paramsInfo.rightMargin = paramsInfo.width;
        paramsInfo.topMargin = paramsInfo.width/2;
        img_informacion.setLayoutParams(paramsInfo);
        img_informacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(area_oculta.getVisibility()==View.VISIBLE){
                    area_oculta.setVisibility(View.GONE);
                }else{
                    area_oculta.setVisibility(View.VISIBLE);
                }
            }
        });

        flecha_izq = (ImageView) findViewById(R.id.flecha_izq);
        flecha_der = (ImageView) findViewById(R.id.flecha_der);
        flecha_izq.bringToFront();
        flecha_der.bringToFront();
        RelativeLayout.LayoutParams paramsFlechaIzq = (RelativeLayout.LayoutParams) flecha_izq.getLayoutParams();
        paramsFlechaIzq.width = (int) (width * 0.08);
        paramsFlechaIzq.height = (int) (paramsFlechaIzq.width*1.2);
        flecha_izq.setLayoutParams(paramsFlechaIzq);
        RelativeLayout.LayoutParams paramsFlechader = (RelativeLayout.LayoutParams) flecha_der.getLayoutParams();
        paramsFlechader.width = (int) (width * 0.08);
        paramsFlechader.height = (int) (paramsFlechaIzq.width*1.2);
        flecha_der.setLayoutParams(paramsFlechader);

        linear_valoracion = (LinearLayout) findViewById(R.id.area_valoracion);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_ofertas);
        LinearLayoutManager layoutManagerAll = new LinearLayoutManager(this);
        layoutManagerAll.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManagerAll);
        recyclerView.setHasFixedSize(true);

        barra_separadora = (ImageView) findViewById(R.id.barra_campanias);
        RelativeLayout.LayoutParams paramsBarra = (RelativeLayout.LayoutParams) barra_separadora.getLayoutParams();
        paramsBarra.width = (int) (width*0.9);
        paramsBarra.height = (int) (height*0.0025);
        barra_separadora.setLayoutParams(paramsBarra);

        valoracion = (Button) findViewById(R.id.valoracion_empresa);
        valoracion.setTextSize((float) (screenInches*1.5));
        valoracion.setTypeface(type);
        LinearLayout.LayoutParams paramsValoracion = (LinearLayout.LayoutParams) valoracion.getLayoutParams();
        paramsValoracion.width = (int) (width * 0.05);
        paramsValoracion.height = paramsValoracion.width;
        valoracion.setLayoutParams(paramsValoracion);
        valoracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mensaje = new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                        .setTitle("VALORACIÓN DE LA EMPRESA")
                        .setMessage(camp.getValoracion()+" "+camp.getNombre_empresa());
                AlertDialog mensajeReady = mensaje.show();
                TextView titleView = (TextView)mensajeReady.findViewById(activity.getResources().getIdentifier("alertTitle", "id", "android"));
                if (titleView != null) {
                    titleView.setGravity(Gravity.CENTER_HORIZONTAL);
                    titleView.setTextColor(getResources().getColor(R.color.gris));
                }
                TextView message = (TextView) mensajeReady.findViewById(android.R.id.message);
                message.setGravity(Gravity.CENTER_HORIZONTAL);
                message.setTextColor(getResources().getColor(R.color.gris));
                mensajeReady.show();
            }
        });

        area_nombre = (LinearLayout) findViewById(R.id.area_nombre);
        area_datos  = (LinearLayout) findViewById(R.id.area_datos);
        RelativeLayout.LayoutParams paramsMargin = (RelativeLayout.LayoutParams) area_nombre.getLayoutParams();
        paramsMargin.setMargins((int) (width * 0.05), (int) (height * 0.01), (int) (width * 0.05), 0);
        paramsMargin.height = (int) (paramsValoracion.height*1.5);
        area_nombre.setLayoutParams(paramsMargin);
        paramsMargin = (RelativeLayout.LayoutParams) area_datos.getLayoutParams();
        paramsMargin.setMargins((int) (width * 0.05), (int) (height * 0.01), (int) (width * 0.05), 0);
        area_datos.setLayoutParams(paramsMargin);

        img_empresa = (ImageView) findViewById(R.id.img_oferta);
        RelativeLayout.LayoutParams img_params = (RelativeLayout.LayoutParams) img_empresa.getLayoutParams();
        img_params.width = (int) (width*0.2);
        img_params.height = img_params.width;
        img_params.setMargins((int) (width * 0.05), (int) (height * 0.02), 0, 0);
        img_empresa.setLayoutParams(img_params);
        img_casaMatriz = (ImageView) findViewById(R.id.img_casaMatriz);
        LinearLayout.LayoutParams paramsCasaMatriz = (LinearLayout.LayoutParams) img_casaMatriz.getLayoutParams();
        paramsCasaMatriz.width = (int) (width*0.04);
        paramsCasaMatriz.height = (int) (paramsCasaMatriz.width*1.4);
        paramsCasaMatriz.rightMargin = (int) (width*0.01);
        img_casaMatriz.setLayoutParams(paramsCasaMatriz);

        img_informacion = (ImageView) findViewById(R.id.informacion_extra);
        val1 = (ImageView) findViewById(R.id.valoracion_1);
        val2 = (ImageView) findViewById(R.id.valoracion_2);
        val3 = (ImageView) findViewById(R.id.valoracion_3);
        val4 = (ImageView) findViewById(R.id.valoracion_4);
        val5 = (ImageView) findViewById(R.id.valoracion_5);

        LinearLayout.LayoutParams paramsVal = (LinearLayout.LayoutParams) val1.getLayoutParams();
        paramsVal.width = (int) (width*0.05);
        paramsVal.height = paramsVal.width;
        val1.setLayoutParams(paramsVal);
        paramsVal.leftMargin = (int) (width*0.01);
        val2.setLayoutParams(paramsVal);
        val3.setLayoutParams(paramsVal);
        val4.setLayoutParams(paramsVal);
        val5.setLayoutParams(paramsVal);

        val1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valoracionUserInt(1);
            }
        });
        val2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valoracionUserInt(2);
            }
        });
        val3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valoracionUserInt(3);
            }
        });
        val4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valoracionUserInt(4);
            }
        });
        val5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valoracionUserInt(5);
            }
        });

        valoracionUserActual = camp.getValoracionUser();
        valoracionUser = camp.getValoracionUser();
        valoracionUserInt(valoracionUser);

        txt_nombreEmpresa = (TextView) findViewById(R.id.txt_empresa);
        LinearLayout.LayoutParams paramsTxtEmpresa = (LinearLayout.LayoutParams) txt_nombreEmpresa.getLayoutParams();
        paramsTxtEmpresa.width = (int) (width*0.5);
        txt_nombreEmpresa.setLayoutParams(paramsTxtEmpresa);
        txt_descripcionEmpresa = (TextView) findViewById(R.id.txt_descripcion);
        txt_infoCasa = (TextView) findViewById(R.id.txt_infoCasa);
        txt_casaMatriz = (TextView) findViewById(R.id.txt_casaMatriz);
        txt_infoHorarios = (TextView) findViewById(R.id.txt_infoHorarios);
        txt_horarios = (TextView) findViewById(R.id.txt_horarios);
        txt_infoTelefonos = (TextView) findViewById(R.id.txt_infoTelefonos);
        txt_telefonos = (TextView) findViewById(R.id.txt_telefonos);
        txt_infoWeb = (TextView) findViewById(R.id.txt_infoWeb);
        txt_web = (TextView) findViewById(R.id.txt_web);
        txt_infoValoracion = (TextView) findViewById(R.id.txt_valoracion);
        txt_campActivas = (TextView) findViewById(R.id.txt_campaniaActiva);
        txt_cambia = (TextView) findViewById(R.id.txt_cambia);

        txt_nombreEmpresa.setTypeface(type);
        txt_nombreEmpresa.setTextSize((float) (screenInches*3));
        txt_descripcionEmpresa.setTypeface(type);
        txt_descripcionEmpresa.setTextSize((float) (screenInches*2.5));
        txt_infoCasa.setTypeface(type);
        txt_infoCasa.setTextSize((float) (screenInches*2.5));
        txt_casaMatriz.setTypeface(type);
        txt_casaMatriz.setTextSize((float) (screenInches*2.5));
        txt_infoHorarios.setTypeface(type);
        txt_infoHorarios.setTextSize((float) (screenInches*2.5));
        txt_horarios.setTypeface(type);
        txt_horarios.setTextSize((float) (screenInches*2.5));
        txt_infoTelefonos.setTypeface(type);
        txt_infoTelefonos.setTextSize((float) (screenInches*2.5));
        txt_telefonos.setTypeface(type);
        txt_telefonos.setTextSize((float) (screenInches*2.5));
        txt_infoWeb.setTypeface(type);
        txt_infoWeb.setTextSize((float) (screenInches*2.5));
        txt_web.setTypeface(type);
        txt_web.setTextSize((float) (screenInches*2.5));
        txt_infoValoracion.setTypeface(type);
        txt_infoValoracion.setTextSize((float) (screenInches*2.5));
        txt_campActivas.setTypeface(bold);
        txt_campActivas.setTextSize((float) (screenInches*3));
        txt_cambia.setTypeface(bold);
        txt_cambia.setTextSize((float) (screenInches*3));

        getPerfilEmpresa();
        volver = (PercentRelativeLayout) findViewById(R.id.volver);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                actualizarPromedio();
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

        if (idUser==0){
            linear_valoracion.setVisibility(View.GONE);
            txt_infoValoracion.setVisibility(View.GONE);
            valoracion.setVisibility(View.GONE);
        }
    }

    private void getPerfilEmpresa(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(userName)
                .setMessage("Cargando...")
                .setCancelable(false);
        final AlertDialog alert = builder.create();
        alert.show();

        if(idUser==0){
            ArrayList<Pair<String, String>> headers = new ArrayList<Pair<String, String>>();
            ArrayList<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
            params.add(new Pair<String, String>("idEmp",String.valueOf(idEmpresa)));
            params.add(new Pair<String, String>("latitud",String.valueOf(lat)));
            params.add(new Pair<String, String>("longitud",String.valueOf(lon)));
            byte[] aux =new byte[]{};
            try
            {
                MobileServiceClient mClient = new MobileServiceClient(getString(R.string.URL_Mobile_Service), this);

                try {
                    ListenableFuture<ServiceFilterResponse> result =  mClient.invokeApi("Empresas/PerfilEmpresaGuest",aux, "POST",headers,params);
                    Futures.addCallback(result, new FutureCallback<ServiceFilterResponse>() {
                                @Override
                                public void onSuccess(ServiceFilterResponse result) {
                                    try {
                                        JSONObject json = new JSONObject(result.getContent());
                                        txt_nombreEmpresa.setText(json.getString("razonSocialEmp"));
                                        if(!json.optString("descEmpresa").equals("null") && !json.optString("descEmpresa").equals("")){
                                            txt_descripcionEmpresa.setText(json.getString("descEmpresa"));
                                        }else{
                                            txt_descripcionEmpresa.setVisibility(View.GONE);
                                        }
                                        if(!json.optString("complemento_sucursal").equals("null") && !json.optString("complemento_sucursal").equals("")){
                                            txt_casaMatriz.setText(json.getString("direccion_sucursal")+" "+json.getString("numero_sucursal")+" "+json.optString("complemento_sucursal"));
                                        }else{
                                            txt_casaMatriz.setText(json.getString("direccion_sucursal")+" "+json.getString("numero_sucursal"));
                                        }
                                        if(!json.optString("horario_sucursal").equals("null") && !json.optString("horario_sucursal").equals("")){
                                            txt_horarios.setText(json.getString("horario_sucursal"));
                                        }else{
                                            txt_horarios.setVisibility(View.GONE);
                                            txt_infoHorarios.setVisibility(View.GONE);
                                        }
                                        if(!json.optString("telefono1").equals("null") && !json.optString("telefono1").equals("")){
                                            if(!json.optString("telefono2").equals("null") && !json.optString("telefono2").equals("")) {
                                                txt_telefonos.setText(json.optString("telefono1")+ json.optString("telefono2"));
                                            }else{
                                                txt_telefonos.setText(json.optString("telefono1"));
                                            }
                                        }else{
                                            txt_telefonos.setVisibility(View.GONE);
                                            txt_infoTelefonos.setVisibility(View.GONE);
                                        }
                                        if (!json.optString("sitio_web").equals("null") && !json.optString("sitio_web").equals("")){
                                            txt_web.setText(json.optString("sitio_web"));
                                        }else{
                                            txt_web.setVisibility(View.GONE);
                                            txt_infoWeb.setVisibility(View.GONE);
                                        }
                                        valoracion.setText(String.valueOf(camp.getValoracion()));

                                        try {
                                            Uri uri = Uri.parse(camp.getRuta_imagen());
                                            Picasso.with(activity)
                                                    .load(uri)
                                                    .placeholder(R.drawable.ajax_loader)
                                                    .transform(new RoundedTransformation(25,4))
                                                    .fit()
                                                    .error(R.drawable.sinfoto)
                                                    .into(img_empresa);
                                        } catch (Exception e) {
                                            Picasso.with(activity)
                                                    .load(R.drawable.sinfoto)
                                                    .placeholder(R.drawable.ajax_loader)
                                                    .transform(new RoundedTransformation(25,4))
                                                    .fit()
                                                    .into(img_empresa);
                                        }
                                        JSONArray jsonArray = json.getJSONArray("listaCampsEmpresa");
                                        lista_camps = Parseo_Json.parseoJsonCampain(jsonArray.toString());
                                        for (int i = 0; i<lista_camps.size();i++){
                                            lista_camps.get(i).setValoracionUser(valoracionUserActual);
                                            lista_camps.get(i).setValoracion(camp.getValoracion());
                                            lista_camps.get(i).setisPagada(camp.getIsPagada());
                                            lista_camps.get(i).setIsEmpresa(camp.getIsEmpresa());
                                        }

                                        adapterPerfilEmpresa adapter = new adapterPerfilEmpresa(camp.getFav(), token, idUser, activity, lat, lon, activity, lista_camps);
                                        recyclerView.setAdapter(adapter);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    alert.dismiss();
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    alert.dismiss();
                                    finish();
                                }
                            }
                    );
                }catch (Exception e){;
                }
            }
            catch(MalformedURLException e) {
            }
        }else{
            final String[] mensaje = {""};
            new AsyncTask<Object, Object, Object>() {

                @Override
                protected Object doInBackground(Object... params) {
                    mensaje[0] = ApiGetCampsUser(lat, lon);
                    while (mensaje[0] == "") {
                    }
                    return "Execute";
                }

                @Override
                protected void onPostExecute(Object result) {
                    try {
                        JSONObject json = new JSONObject(mensaje[0]);
                        txt_nombreEmpresa.setText(json.getString("razonSocialEmp"));
                        if(!json.optString("descEmpresa").equals("null") && !json.optString("descEmpresa").equals("")){
                            txt_descripcionEmpresa.setText(json.getString("descEmpresa"));
                        }else{
                            txt_descripcionEmpresa.setVisibility(View.GONE);
                        }
                        if(!json.optString("complemento_sucursal").equals("null") && !json.optString("complemento_sucursal").equals("")){
                            txt_casaMatriz.setText(json.getString("direccion_sucursal")+" "+json.getString("numero_sucursal")+" "+json.optString("complemento_sucursal"));
                        }else{
                            txt_casaMatriz.setText(json.getString("direccion_sucursal")+" "+json.getString("numero_sucursal"));
                        }
                        if(!json.optString("horario_sucursal").equals("null") && !json.optString("horario_sucursal").equals("")){
                            txt_horarios.setText(json.getString("horario_sucursal"));
                        }else{
                            txt_horarios.setVisibility(View.GONE);
                            txt_infoHorarios.setVisibility(View.GONE);
                        }
                        if(!json.optString("telefono1").equals("null") && !json.optString("telefono1").equals("")){
                            if(!json.optString("telefono2").equals("null") && !json.optString("telefono2").equals("")) {
                                txt_telefonos.setText(json.optString("telefono1")+ json.optString("telefono2"));
                            }else{
                                txt_telefonos.setText(json.optString("telefono1"));
                            }
                        }else{
                            txt_telefonos.setVisibility(View.GONE);
                            txt_infoTelefonos.setVisibility(View.GONE);
                        }
                        if (!json.optString("sitio_web").equals("null") && !json.optString("sitio_web").equals("")){
                            txt_web.setText(json.optString("sitio_web"));
                        }else{
                            txt_web.setVisibility(View.GONE);
                            txt_infoWeb.setVisibility(View.GONE);
                        }
                        valoracion.setText(String.valueOf(camp.getValoracion()));
                        try {
                            Uri uri = Uri.parse(camp.getRuta_imagen());
                            Picasso.with(activity)
                                    .load(uri)
                                    .placeholder(R.drawable.ajax_loader)
                                    .transform(new RoundedTransformation(25,4))
                                    .fit()
                                    .error(R.drawable.sinfoto)
                                    .into(img_empresa);
                        } catch (Exception e) {
                            Picasso.with(activity)
                                    .load(R.drawable.sinfoto)
                                    .placeholder(R.drawable.ajax_loader)
                                    .transform(new RoundedTransformation(25,4))
                                    .fit()
                                    .into(img_empresa);
                        }
                        JSONArray jsonArray = json.getJSONArray("listaCampsEmpresa");
                        lista_camps = Parseo_Json.parseoJsonCampain(jsonArray.toString());
                        for (int i = 0; i<lista_camps.size();i++){
                            lista_camps.get(i).setValoracionUser(valoracionUserActual);
                            lista_camps.get(i).setValoracion(camp.getValoracion());
                            lista_camps.get(i).setisPagada(camp.getIsPagada());
                            lista_camps.get(i).setIsEmpresa(camp.getIsEmpresa());
                        }
                        adapterPerfilEmpresa adapter = new adapterPerfilEmpresa(camp.getFav(), token, idUser, activity, lat, lon, activity, lista_camps);
                        recyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        finish();
                    }
                    alert.dismiss();
                }
            }.execute();
        }
    }

    private String ApiGetCampsUser(double lat_new, double lon_new){
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(getString(R.string.URL_Mobile_Service)+"/api/Empresas/PerfilEmpresaByUser");
        httpPost.addHeader("X-ZUMO-AUTH", token);
        httpPost.addHeader("ZUMO-API-VERSION","2.0.0");
        httpPost.addHeader("Content-type","application/json");
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            json.put("IdUser",idUser);
            json.put("idEmp", idEmpresa);
            json.put("latitud",lat_new);
            json.put("longitud",lon_new);
            httpPost.setEntity(new ByteArrayEntity(json.toString().getBytes("ISO-8859-1")));
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException e) {
        }
        try {
            response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String b;
            while ((b = reader.readLine()) != null) {
                stringBuilder.append(b+"\n");
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONArray jsonObject = new JSONArray();
        try {
            jsonObject = new JSONArray(stringBuilder.toString());
            return jsonObject.toString();

        } catch (JSONException e) {
            return stringBuilder.toString();
        }
    }

    private void valoracionUserInt(int valor){
        switch (valor) {
            case 0:
                val1.setBackground(getResources().getDrawable(R.drawable.valorno));
                val2.setBackground(getResources().getDrawable(R.drawable.valorno));
                val3.setBackground(getResources().getDrawable(R.drawable.valorno));
                val4.setBackground(getResources().getDrawable(R.drawable.valorno));
                val5.setBackground(getResources().getDrawable(R.drawable.valorno));
                break;
            case 1:
                val1.setBackground(getResources().getDrawable(R.drawable.valor5));
                val2.setBackground(getResources().getDrawable(R.drawable.valorno));
                val3.setBackground(getResources().getDrawable(R.drawable.valorno));
                val4.setBackground(getResources().getDrawable(R.drawable.valorno));
                val5.setBackground(getResources().getDrawable(R.drawable.valorno));
                break;
            case 2:
                val1.setBackground(getResources().getDrawable(R.drawable.valor5));
                val2.setBackground(getResources().getDrawable(R.drawable.valor5));
                val3.setBackground(getResources().getDrawable(R.drawable.valorno));
                val4.setBackground(getResources().getDrawable(R.drawable.valorno));
                val5.setBackground(getResources().getDrawable(R.drawable.valorno));
                break;
            case 3:
                val1.setBackground(getResources().getDrawable(R.drawable.valor5));
                val2.setBackground(getResources().getDrawable(R.drawable.valor5));
                val3.setBackground(getResources().getDrawable(R.drawable.valor5));
                val4.setBackground(getResources().getDrawable(R.drawable.valorno));
                val5.setBackground(getResources().getDrawable(R.drawable.valorno));
                break;
            case 4:
                val1.setBackground(getResources().getDrawable(R.drawable.valor5));
                val2.setBackground(getResources().getDrawable(R.drawable.valor5));
                val3.setBackground(getResources().getDrawable(R.drawable.valor5));
                val4.setBackground(getResources().getDrawable(R.drawable.valor5));
                val5.setBackground(getResources().getDrawable(R.drawable.valorno));
                break;
            case 5:
                val1.setBackground(getResources().getDrawable(R.drawable.valor5));
                val2.setBackground(getResources().getDrawable(R.drawable.valor5));
                val3.setBackground(getResources().getDrawable(R.drawable.valor5));
                val4.setBackground(getResources().getDrawable(R.drawable.valor5));
                val5.setBackground(getResources().getDrawable(R.drawable.valor5));
                break;
        }
        valoracionUser = valor;
    }

    private void actualizarPromedio(){
        if(valoracionUser!=valoracionUserActual){
            final String[] mensaje = {""};
            new AsyncTask<Object, Object, Object>() {

                @Override
                protected Object doInBackground(Object... params) {
                    mensaje[0] = ApiUpdateValoracion();
                    while (mensaje[0] == "") {
                    }
                    return "Execute";
                }

                @Override
                protected void onPostExecute(Object result) {
                    try{
                        double valoracionPromedio = Double.parseDouble(mensaje[0]);
                        fragment.setValoracion(valoracionPromedio,valoracionUser, position);
                    }catch(Exception e){}
                }
            }.execute();
        }
    }

    private String ApiUpdateValoracion(){
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(getString(R.string.URL_Mobile_Service)+"/api/Manage/ValoraEmpresaByUser");
        httpPost.addHeader("X-ZUMO-AUTH",token);
        httpPost.addHeader("ZUMO-API-VERSION","2.0.0");
        httpPost.addHeader("Content-type","application/json");
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        JSONObject json = new JSONObject();
        try {
            json.put("id_empresa",camp.getIdEmpresa());
            json.put("id_usuario",idUser);
            json.put("valoracion",valoracionUser);
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
