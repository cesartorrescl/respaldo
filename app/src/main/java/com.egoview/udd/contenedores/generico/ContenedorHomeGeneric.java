package com.egoview.udd.contenedores.generico;

/** Created by Serial on 03/08/2015 Modified on 06/12/2015.*/

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.media.Image;
import android.media.ImageWriter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Spannable;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egoview.udd.R;
import com.egoview.udd.adapter.MenuLateralAdapter;
import com.egoview.udd.clases.campanias;
import com.egoview.udd.clases.categorias;
import com.egoview.udd.clases.informacion_menu_lateral;
import com.egoview.udd.procesos.AnalyticsApplication;
import com.egoview.udd.procesos.Parseo_Json;
import com.egoview.udd.procesos.Porcentual;
import com.egoview.udd.procesos.Redireccionar_Datos;
import com.egoview.udd.procesos.Rescate_Datos;
import com.egoview.udd.procesos.footer;
import com.egoview.udd.procesos.providers;
import com.egoview.udd.vistas.genericas.Fragment_activity_categorias_generic;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class ContenedorHomeGeneric extends AppCompatActivity implements Serializable{
    private DrawerLayout drawerLayout;
    private ListView listView;
    public static Activity achg;
    private providers mProviders;
    private AlertDialog.Builder alertDialog;
    public boolean flag_global;
    public String token, userName;
    private long idUser = 0, idCamp = 0;
    private DrawerLayout drawerRight;
    public Fragment newFragment;
    private GridView gridView;
    private Tracker mTracker;
    private AnalyticsApplication application;
    public double lat, lon;
    private campanias camp_user;
    public Activity activity = this;
    private Boolean publicador;
    private PercentRelativeLayout menu_hamb, usuario_registrado;
    public MenuLateralAdapter menuLateralAdapter;
    public Button publicar_eliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        achg=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_copy);
        footer foot = new footer();
        foot.footer(this);

        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        // [END shared_tracker]
        application.sendScreen("Principal");

        try {
            token = getIntent().getExtras().getString("Token");
            idUser=getIntent().getExtras().getLong("idUser");
            idCamp=getIntent().getExtras().getLong("idCamp");
            userName=getIntent().getExtras().getString("userName");
            camp_user = (campanias) getIntent().getExtras().get("campanias");
            publicador = getIntent().getExtras().getBoolean("isPublicador");
        }catch(Exception e){        }
        lat = 0.0;
        lon = 0.0;
        gridView = (GridView) findViewById(R.id.gridview);
        drawerRight = (DrawerLayout) findViewById(R.id.drawer_layout_der);
        Typeface type = Typeface.createFromAsset(getAssets(), "roboto_regular.ttf");
        Typeface bold = Typeface.createFromAsset(getAssets(), "roboto_bold.ttf");
        double size = new Porcentual().getScreenInches(this);
        menu_hamb = (PercentRelativeLayout) findViewById(R.id.zona12);
        usuario_registrado = (PercentRelativeLayout) findViewById(R.id.usuario_registrado);

            menu_hamb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.openDrawer(GravityCompat.START);
                    } else {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }
                }
            });

        alertDialog = new AlertDialog.Builder(this);
        dibujar_recursos();
        mProviders = new providers();
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mProviders.providers(token, userName, this, this, locationManager, locationManager, idUser);
        registerReceiver(mProviders.getBroadcastReceiverNET(alertDialog), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        if(AnalyticsApplication.getCodTipo()!=null){
            redireccionarPush();
        }
    }

    private void dibujar_recursos() {
        dibujar_menu_lateral();
        dibujar_fragment(0,false);
    }

    public void dibujar_fragment(int posicion,boolean flag) {
        ArrayList<categorias> list_categorias01 = Rescate_Datos.rescatar_categorias(getIntent());
        flag_global=flag;
        categorias todascategorias= new categorias();
        todascategorias.setId(99);
        todascategorias.setId_categorias(99);
        todascategorias.setDescripcion_campania("Todas las Categorias");
        todascategorias.setTotal_por_categorias(1000);
        list_categorias01.add(todascategorias);
        categorias categoria01=list_categorias01.get(posicion);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        screenInches = (double) Math.round(screenInches * 10) / 10;
        float textSize = (float) (3.1 * screenInches);

        categorias.setCategorias(categoria01);
        newFragment = new Fragment_activity_categorias_generic();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contenedor_frame_der, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public ArrayList<informacion_menu_lateral> getInformacion_menu_lateral(){
        ArrayList<informacion_menu_lateral> opciones = new ArrayList<informacion_menu_lateral>();
            String[] titulos_opciones = new String[]{
                    getString(R.string.Opcion1),
                    getString(R.string.Opcion2),
                    getString(R.string.Opcion4),
                    getString(R.string.Opcion5),
                    getString(R.string.Opcion10),
                    getString(R.string.Opcion11)};
            for (int i = 0; i < titulos_opciones.length; i++) {
                informacion_menu_lateral info = new informacion_menu_lateral();
                info.setTitle(titulos_opciones[i]);
                opciones.add(info);
            }
        return opciones;
    }

    private void dibujar_menu_lateral() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_home);
        if(idUser!=0) {
            listView = (ListView) findViewById(R.id.menuizq);
            ArrayList<informacion_menu_lateral> menu_lateral;
            menu_lateral = getInformacion_menu_lateral();
            menuLateralAdapter = new MenuLateralAdapter(this, menu_lateral, token, idUser, userName, Rescate_Datos.rescatar_categorias(getIntent()), getApplicationContext());

            final LayoutInflater inf_header = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View header = inf_header.inflate(R.layout.header_menu_lateral, null);

            ImageView img_header = (ImageView) header.findViewById(R.id.img_header);
            TextView txt_usuario = (TextView) header.findViewById(R.id.txt_usuario);
            TextView txt_conf = (TextView) header.findViewById(R.id.txt_conf);
            TextView txt_cmercado = (TextView) header.findViewById(R.id.txt_mercado);
            publicar_eliminar = (Button) header.findViewById(R.id.publicar_eliminar);
            Typeface type = Typeface.createFromAsset(getAssets(), "roboto_regular.ttf");
            Typeface bold = Typeface.createFromAsset(getAssets(), "roboto_bold.ttf");

            double size = new Porcentual().getScreenInches(this);
            double width = new Porcentual().getScreenWidth(this);

            txt_usuario.setTypeface(bold);
            txt_usuario.setText("¡Hola "+userName+"!");
            txt_conf.setTypeface(type);
            txt_conf.setTextSize((float) (size * 3));
            txt_usuario.setTextSize((float) (size * 3.5));
            txt_cmercado.setTextSize((float) (size * 3.5));
            txt_cmercado.setTypeface(bold);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) img_header.getLayoutParams();
            layoutParams.width = (int) (width * 0.5);
            layoutParams.height = (int) (layoutParams.width * 0.4);
            layoutParams.setMargins(0, (int) (layoutParams.height * 0.2), 0, (int) (layoutParams.height * 0.2));
            img_header.setLayoutParams(layoutParams);

            LinearLayout.LayoutParams layoutParamsButton = (LinearLayout.LayoutParams) publicar_eliminar.getLayoutParams();
            layoutParamsButton.setMargins(0, (int) (layoutParams.height * 0.2), 0,0);
            layoutParamsButton.width = (int) (width * 0.5);
            layoutParamsButton.height = (int) (layoutParams.width * 0.2);
            publicar_eliminar.setLayoutParams(layoutParamsButton);

            listView.addHeaderView(header);
            listView.setAdapter(menuLateralAdapter);

            if(idCamp==0 || publicador){
                publicar_eliminar.setText("Publica aquí");
            }else{
                publicar_eliminar.setText("Finalizar publicación");
            }

            publicar_eliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(idCamp==0 || publicador) {
                        Intent intent = new Intent("android.intent.action.publicar"+getString(R.string.ambiente_ego));
                        intent.putExtra("Token", token);
                        intent.putExtra("idUser", idUser);
                        intent.putExtra("userName", userName);
                        intent.putExtra("list_categorias", Rescate_Datos.rescatar_categorias(getIntent()));
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }else{
                        Intent intent = new Intent("android.intent.action.eliminar_camp"+getString(R.string.ambiente_ego));
                        intent.putExtra("idCamp", idCamp);
                        intent.putExtra("Token", token);
                        intent.putExtra("campanias", camp_user);
                        intent.putExtra("idUser", idUser);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
            });
        }else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    private void redireccionarPush(){
        String nhCodTipo = AnalyticsApplication.getCodTipo();
        String nhIdTipo = AnalyticsApplication.getIdTipo();
        Intent intent;
        double latitud, longitud;
        if(AnalyticsApplication.getLatFused() != 0){
            latitud = AnalyticsApplication.getLatFused();
            longitud = AnalyticsApplication.getLonFused();
        }else{
            latitud = AnalyticsApplication.getLat();
            longitud = AnalyticsApplication.getLon();
        }
        switch (nhCodTipo){
            case "0":
                intent = new Intent("android.intent.action.ContenedorCampaniadesa"+activity.getString(R.string.ambiente_ego));
                intent.putExtra("idTipo", nhIdTipo);
                Apigetsucursales(latitud, longitud, intent, nhIdTipo);
                break;
        }
    }

    public void Apigetsucursales(final double lat, final double lon, final Intent intent, final String nhIdTipo) {
        List<Pair<String, String>> queryParams;
        MobileServiceClient mClient;
        ListenableFuture<JsonElement> result;
        queryParams = new ArrayList<Pair<String, String>>();
        queryParams.add(new Pair<String, String>("idCamp", nhIdTipo));
        queryParams.add(new Pair<String, String>("latitud", String.valueOf(-33.440550)));
        queryParams.add(new Pair<String, String>("longitud", String.valueOf(-70.650723)));
        try {
            mClient = new MobileServiceClient(getString(R.string.URL_Mobile_Service), this);
            try {
                result = mClient.invokeApi("Camps/DetalleCampGenericaByidCamp", "POST", queryParams);
                Futures.addCallback
                        (result, new FutureCallback<JsonElement>() {
                                    @Override
                                    public void onSuccess(JsonElement result) {
                                        campanias camp = new campanias();
                                        final campanias campania02 = Parseo_Json.parseoJsonCampainFinal(result.toString(), camp);
                                        intent.putExtra("latitud", lat);
                                        intent.putExtra("longitud", lon);
                                        intent.putExtra("flag", false);
                                        intent.putExtra("token", AnalyticsApplication.getToken());
                                        intent.putExtra("idUser", AnalyticsApplication.getIdUser());
                                        intent.putExtra("position", 0);
                                        final String[] mensaje = {""};
                                        if(!campania02.getVisto()){
                                            new AsyncTask<Object, Object, Object>() {

                                                @Override
                                                protected Object doInBackground(Object... params) {
                                                    mensaje[0] = ApiVisto(token, idUser, nhIdTipo, activity);

                                                    return "Execute";
                                                }

                                                @Override
                                                protected void onPostExecute(Object result) {

                                                }
                                            }.execute();
                                        }
                                        while (mensaje[0] == "" && !campania02.getVisto()) {
                                        }
                                        if (mensaje[0].equals("200")) {
                                            campania02.setVisto(true);
                                        }
                                        campanias.setCampanias(campania02);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                    }
                                }
                        );
            } catch (Exception e) {
            }
        } catch (MalformedURLException e) {
        }
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

    public void setPublicacion(Boolean state, long idCamp_new, campanias camp){
        if(publicador){
            idCamp = 0;
            camp_user = camp;
            publicar_eliminar.setText("Publica aquí");
            publicar_eliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent("android.intent.action.publicar"+getString(R.string.ambiente_ego));
                    intent.putExtra("Token", token);
                    intent.putExtra("idUser", idUser);
                    intent.putExtra("userName", userName);
                    intent.putExtra("list_categorias", Rescate_Datos.rescatar_categorias(getIntent()));
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
        }else {
            if (state) {
                idCamp = idCamp_new;
                camp_user = camp;
                publicar_eliminar.setText("Finalizar publicación");
                publicar_eliminar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("android.intent.action.eliminar_camp"+getString(R.string.ambiente_ego));
                        intent.putExtra("idCamp", idCamp);
                        intent.putExtra("Token", token);
                        intent.putExtra("campanias", camp_user);
                        intent.putExtra("idUser", idUser);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });
            } else {
                idCamp = 0;
                camp_user = camp;
                publicar_eliminar.setText("Publica aquí");
                publicar_eliminar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("android.intent.action.publicar"+getString(R.string.ambiente_ego));
                        intent.putExtra("Token", token);
                        intent.putExtra("idUser", idUser);
                        intent.putExtra("userName", userName);
                        intent.putExtra("list_categorias", Rescate_Datos.rescatar_categorias(getIntent()));
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });
            }
        }
        ((Fragment_activity_categorias_generic) newFragment).llamar_refresh();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if(drawerLayout.isDrawerOpen(GravityCompat.START)==true){
                    drawerLayout.closeDrawer(GravityCompat.START);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.contenedor_frame_der, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }else if (drawerRight.isDrawerOpen(GravityCompat.END) == true) {
                            gridView.setVisibility(View.VISIBLE);
                            drawerRight.closeDrawer(GravityCompat.END);

                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.contenedor_frame_der, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(getApplicationContext().getString(R.string.cerrar_sesion));
                        builder.setTitle("Importante");
                        builder.setCancelable(false);
                        final AlertDialog.Builder builder1 = builder.setPositiveButton(getApplicationContext().getString(R.string.respuesta_si),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        clearApplicationData(ContenedorHomeGeneric.this);
                                        android.os.Process.killProcess(android.os.Process.myPid());
                                    }
                                });
                        builder.setNegativeButton(getApplicationContext().getString(R.string.respuesta_no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }


        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0) {
            startActivity(getIntent());
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }

    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mProviders.Pause();
        AnalyticsApplication.activityPaused();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mProviders.Resume();
        AnalyticsApplication.activityResumed(this);
    }

    private void clearApplicationData(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        }
        else if(dir!= null && dir.isFile())
            return dir.delete();
        else {
            return false;
        }
    }

}