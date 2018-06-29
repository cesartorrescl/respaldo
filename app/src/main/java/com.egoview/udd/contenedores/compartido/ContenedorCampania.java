package com.egoview.udd.contenedores.compartido;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egoview.udd.R;
import com.egoview.udd.clases.campanias;
import com.egoview.udd.procesos.AnalyticsApplication;
import com.egoview.udd.procesos.Porcentual;
import com.egoview.udd.procesos.footer;
import com.egoview.udd.procesos.providers;
import com.egoview.udd.vistas.compartidas.Fragment_activity_ver_campania;
import com.egoview.udd.vistas.genericas.Fragment_activity_categorias_generic;
import com.google.android.gms.analytics.Tracker;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by Serial on 02/09/2015.
 */
public class ContenedorCampania extends AppCompatActivity {
    private boolean flag;
    private PercentRelativeLayout volver;
    Tracker mTracker;
    AnalyticsApplication application;
    private TextView txt_oferta;

    private AlertDialog.Builder alertDialog;
    private providers mProviders;
    private RelativeLayout contenedor_social;

    private String token, idCamp;
    private long idUser;
    private int position;

    private ImageView mg, nmg, fav, comp, visto;
    private TextView txt_mg, txt_nmg, txt_fav, txt_comp, txt_visto;
    private Fragment_activity_categorias_generic fragment;
    private Fragment_activity_ver_campania detalle;
    private MediaPlayer smg;
    private MediaPlayer sfav;

    campanias camp = campanias.getCampanias();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_contenedor_campania);
            footer foot = new footer();
            foot.footer(this);
            System.runFinalization();
            Runtime.getRuntime().gc();
            System.gc();
            smg = MediaPlayer.create(this, R.raw.mg);
            sfav = MediaPlayer.create(this, R.raw.favorito);

            fragment = (Fragment_activity_categorias_generic) Fragment_activity_categorias_generic.fragment;
            position = getIntent().getExtras().getInt("position");
            if(fragment!=null && camp.getVisto()) {
                fragment.setVisto(position);
            }

            // [START shared_tracker]
            // Obtain the shared Tracker instance.
            application = (AnalyticsApplication) getApplication();
            mTracker = application.getDefaultTracker();
            // [END shared_tracker]
            application.sendScreen("Detalle");
            //Net Listener
            alertDialog = new AlertDialog.Builder(this);
            mProviders = new providers();
            mProviders.providers(token, null, this, this, null, null, 0);
            registerReceiver(mProviders.getBroadcastReceiverNET(alertDialog), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            //Fin

            socialMedia();
            contenedor_social = (RelativeLayout) findViewById(R.id.contenedor_social);
            contenedor_social.bringToFront();

            double screenInches=  new Porcentual().getScreenInches(this);
            Typeface bold= Typeface.createFromAsset(getAssets(), "roboto_regular.ttf");
            volver = (PercentRelativeLayout) findViewById(R.id.volver);
            txt_oferta = (TextView) findViewById(R.id.txt_oferta);

            txt_oferta.setTypeface(bold);

            txt_oferta.setTextSize((float) (screenInches * 3.5));

            onLowMemory();
            flag = getIntent().getBooleanExtra("flag", false);
            dibujar_tabs();
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
        }
    }

    private void actualizarPromedio(){
        if(detalle.valoracionUser!=detalle.valoracionUserInicial){
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
                        if(fragment!=null) {
                            fragment.setValoracion(valoracionPromedio, detalle.valoracionUser, position);
                        }
                    }catch(Exception e){

                    }
                }
            }.execute();
        }
    }

    public void setFragment(Fragment_activity_ver_campania fr){
        detalle = fr;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressLint("ResourceAsColor")
    private void dibujar_tabs(){
        final RelativeLayout left = (RelativeLayout) findViewById(R.id.cont_detalle);

        txt_mg = (TextView) findViewById(R.id.txt_cantMG);
        txt_nmg = (TextView) findViewById(R.id.txt_cantNMG);
        txt_fav = (TextView) findViewById(R.id.txt_fav);
        //txt_visto = (TextView) findViewById(R.id.txt_visto);
        //txt_comp = (TextView) findViewById(R.id.txt_comp);
        Typeface type= Typeface.createFromAsset(getAssets(), "roboto_regular.ttf");

        txt_mg.setText(String.valueOf(camp.getCantMG()));
        txt_nmg.setText(String.valueOf(camp.getCantNMG()));

        txt_mg.setTypeface(type);
        txt_nmg.setTypeface(type);
        txt_fav.setTypeface(type);
        //txt_visto.setTypeface(type);
        //txt_comp.setTypeface(type);

        Porcentual porcentual = new Porcentual();
        int height = porcentual.getScreenHeight(this);
        double size = porcentual.getScreenInches(this);

        txt_mg.setTextSize((float) (size * 1.5));
        txt_nmg.setTextSize((float) (size * 1.5));
        txt_fav.setTextSize((float) (size * 1.5));
        //txt_visto.setTextSize((float) (size * 1.5));
        //txt_comp.setTextSize((float) (size * 1.5));

        RelativeLayout.LayoutParams margen = (RelativeLayout.LayoutParams) contenedor_social.getLayoutParams();
        margen.height = (int) (height*0.12);
        contenedor_social.setLayoutParams(margen);

        LinearLayout.LayoutParams params_detalle = (LinearLayout.LayoutParams) left.getLayoutParams();
        params_detalle.height = (int) (height * 0.6);
        left.setLayoutParams(params_detalle);
    }

    private void socialMedia(){
        idUser = getIntent().getExtras().getLong("idUser");
        token = getIntent().getExtras().getString("token");
        idCamp = campanias.getCampanias().getIdCampania();
        if(idCamp==null){
            idCamp = getIntent().getExtras().getString("idTipo");
        }

        mg = (ImageView) findViewById(R.id.img_mg);
        nmg = (ImageView) findViewById(R.id.img_nmg);
        fav = (ImageView) findViewById(R.id.img_fav);
        visto = (ImageView) findViewById(R.id.img_visto);
        comp = (ImageView) findViewById(R.id.img_comp);

        if(camp.getVisto()){
            visto.setBackground(getResources().getDrawable(R.drawable.visto_hvr));
        }
        if(camp.getFav()){
            fav.setBackground(getResources().getDrawable(R.drawable.favoritos_hvr));
        }
        if(camp.getMG()){
            mg.setBackground(getResources().getDrawable(R.drawable.social_mg_hvr));
        }
        if(camp.getNMG()){
            nmg.setBackground(getResources().getDrawable(R.drawable.social_nmg_hvr));
        }

        txt_mg = (TextView) findViewById(R.id.txt_cantMG);
        txt_nmg = (TextView) findViewById(R.id.txt_cantNMG);
        txt_fav= (TextView) findViewById(R.id.txt_fav);
        //txt_comp = (TextView) findViewById(R.id.txt_comp);

        Porcentual porcentual = new Porcentual();
        double screen = porcentual.getScreenInches(this);

        txt_mg.setTextSize((float) (screen*2));
        txt_nmg.setTextSize((float) (screen*2));
        txt_fav.setTextSize((float) (screen*2));
        //txt_comp.setTextSize((float) (screen*2));

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sfav.start();
                camp.setFav(!camp.getFav());
                favCamp(String.valueOf(camp.getFav()));
                if(camp.getFav()){
                    fav.setBackground(getResources().getDrawable(R.drawable.favoritos_hvr));
                }else{
                    fav.setBackground(getResources().getDrawable(R.drawable.favoritos));
                }
            }
        });
        mg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smg.start();
                if (camp.getMG()){
                    camp.setMG(false);
                    camp.setCantMG(camp.getCantMG()-1);
                    txt_mg.setText(String.valueOf((Integer.parseInt(txt_mg.getText().toString())-1)));
                    valoracionCamp("null");
                    mg.setBackground(getResources().getDrawable(R.drawable.mg));
                }else {
                    if(camp.getNMG()){
                        camp.setNMG(false);
                        camp.setMG(true);
                        camp.setCantMG(camp.getCantMG()+1);
                        camp.setCantNMG(camp.getCantNMG()-1);
                        txt_mg.setText(String.valueOf((Integer.parseInt(txt_mg.getText().toString())+1)));
                        txt_nmg.setText(String.valueOf((Integer.parseInt(txt_nmg.getText().toString())-1)));
                        mg.setBackground(getResources().getDrawable(R.drawable.social_mg_hvr));
                        nmg.setBackground(getResources().getDrawable(R.drawable.no_mg));
                        valoracionCamp(String.valueOf(true));
                    }else{
                        camp.setMG(true);
                        camp.setCantMG(camp.getCantMG()+1);
                        txt_mg.setText(String.valueOf((Integer.parseInt(txt_mg.getText().toString())+1)));
                        valoracionCamp(String.valueOf(true));
                        mg.setBackground(getResources().getDrawable(R.drawable.social_mg_hvr));
                    }
                }
            }
        });
        nmg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smg.start();
                if (camp.getNMG()){
                    camp.setNMG(false);
                    camp.setCantNMG(camp.getCantNMG()-1);
                    txt_nmg.setText(String.valueOf((Integer.parseInt(txt_nmg.getText().toString())-1)));
                    valoracionCamp("null");
                    nmg.setBackground(getResources().getDrawable(R.drawable.no_mg));
                }else {
                    if(camp.getMG()){
                        camp.setMG(false);
                        camp.setNMG(true);
                        camp.setCantNMG(camp.getCantNMG()+1);
                        camp.setCantMG(camp.getCantMG()-1);
                        txt_nmg.setText(String.valueOf((Integer.parseInt(txt_nmg.getText().toString())+1)));
                        txt_mg.setText(String.valueOf((Integer.parseInt(txt_mg.getText().toString())-1)));
                        mg.setBackground(getResources().getDrawable(R.drawable.mg));
                        nmg.setBackground(getResources().getDrawable(R.drawable.social_nmg_hvr));
                        valoracionCamp(String.valueOf(false));
                    }else{
                        camp.setNMG(true);
                        camp.setCantNMG(camp.getCantNMG()+1);
                        txt_nmg.setText(String.valueOf((Integer.parseInt(txt_nmg.getText().toString())+1)));
                        valoracionCamp(String.valueOf(false));
                        nmg.setBackground(getResources().getDrawable(R.drawable.social_nmg_hvr));
                    }
                }
            }
        });
        comp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap;
                WebView itemView = (WebView) findViewById(R.id.webView);
                itemView.setDrawingCacheEnabled(true);
                bitmap = Bitmap.createBitmap(itemView.getDrawingCache());
                itemView.setDrawingCacheEnabled(false);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "archivoTemporalGet4u.jpg");
                //File f = new File(Environment.getExternalStorageDirectory() + File.separator + "archivoTemporalHubin.jpg");
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/archivoTemporalGet4u.jpg"));
                //intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(camp.getRuta_imagen()));
                intent.putExtra(Intent.EXTRA_TEXT, camp.getNombre_camp()+"\n"+camp.getDesc_campania()+"\nPara mayor informacion visita Hubin.\n" +
                        "http://www.hubin.cl/");
                startActivity(Intent.createChooser(intent, "Compartir..."));
            }
        });
    }

    public void favCamp(String accion)
    {
        ArrayList<Pair<String, String>> headers = new ArrayList<Pair<String, String>>();
        headers.add(new Pair<String, String>("X-ZUMO-AUTH", token));
        ArrayList<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
        params.add(new Pair<String, String>("idUser",String.valueOf(idUser)));
        params.add(new Pair<String, String>("idCamp",idCamp));
        params.add(new Pair<String, String>("isFav",accion));
        byte[] aux =new byte[]{};
        try
        {
            MobileServiceClient mClient = new MobileServiceClient(getString(R.string.URL_Mobile_Service), this);

            try {
                ListenableFuture<ServiceFilterResponse> result =  mClient.invokeApi("Camps/FavCampByUser",aux, "POST",headers,params);
                Futures.addCallback(result, new FutureCallback<ServiceFilterResponse>() {
                            @Override
                            public void onSuccess(ServiceFilterResponse result) {
                                Log.d("fav",result.getContent());
                                if(fragment!=null) {
                                    fragment.setFav(camp.getFav(), position);
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Log.d("fav",t.getMessage());
                            }
                        }
                );
            }catch (Exception e){;
            }
        }
        catch(MalformedURLException e) {
        }
    }

    public void valoracionCamp(String accion)
    {
        ArrayList<Pair<String, String>> headers = new ArrayList<Pair<String, String>>();
        headers.add(new Pair<String, String>("X-ZUMO-AUTH", token));
        ArrayList<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
        params.add(new Pair<String, String>("idUser",String.valueOf(idUser)));
        params.add(new Pair<String, String>("idCamp",idCamp));
        params.add(new Pair<String, String>("meGusta",accion));
        byte[] aux =new byte[]{};
        try
        {
            MobileServiceClient mClient = new MobileServiceClient(getString(R.string.URL_Mobile_Service), this);

            try {
                ListenableFuture<ServiceFilterResponse> result =  mClient.invokeApi("Camps/MeGustaCampByUser",aux, "POST",headers,params);
                Futures.addCallback(result, new FutureCallback<ServiceFilterResponse>() {
                            @Override
                            public void onSuccess(ServiceFilterResponse result) {
                                try {
                                    JSONObject jsonObject = new JSONObject(result.getContent());
                                    int megusta = jsonObject.getInt("totalMeGusta");
                                    int nomegusta = jsonObject.getInt("totalNoMeGusta");
                                    txt_mg.setText(String.valueOf(megusta));
                                    txt_nmg.setText(String.valueOf(nomegusta));
                                    if(fragment!=null) {
                                        fragment.setMG(megusta, nomegusta, position, camp.getMG(), camp.getNMG());
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                            }
                        }
                );
            }catch (Exception e){;
            }
        }
        catch(MalformedURLException e) {
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
            json.put("valoracion",detalle.valoracionUser);
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                if(flag){
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                finish();
                actualizarPromedio();
                System.runFinalization();
                Runtime.getRuntime().gc();
                System.gc();
                return false;
        }
        return super.onKeyDown(keyCode, event);
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
