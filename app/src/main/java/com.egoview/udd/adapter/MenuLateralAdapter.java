package com.egoview.udd.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egoview.udd.R;
import com.egoview.udd.clases.categorias;
import com.egoview.udd.clases.informacion_menu_lateral;
import com.egoview.udd.clases.subcategorias;
import com.egoview.udd.contenedores.generico.ContenedorHomeGeneric;
import com.egoview.udd.procesos.AnalyticsApplication;
import com.egoview.udd.procesos.Porcentual;
import com.google.android.gms.analytics.Tracker;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by Serial on 06/11/2015 */

public class MenuLateralAdapter extends BaseAdapter
{
    protected Activity activity;
    protected ArrayList<informacion_menu_lateral> lista;
    private ArrayList<categorias> cat;
    private ArrayList<subcategorias> subcat;
    protected String Token, userName;
    protected long iduser;
    private LayoutInflater layoutInflater;
    Tracker mTracker;
    AnalyticsApplication application;

    public MenuLateralAdapter(Activity activity, ArrayList<informacion_menu_lateral> lista, String token, long user, String userName, ArrayList<categorias> cat, Context context) {
        this.activity = activity;
        this.lista = lista;
        this.Token = token;
        this.iduser = user;
        this.userName = userName;
        layoutInflater = LayoutInflater.from(context);
        this.cat = cat;
        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        application = (AnalyticsApplication) activity.getApplication();
        mTracker = application.getDefaultTracker();
        // [END shared_tracker]
    }
    @Override
    public int getCount() {
        return lista.size();
    }

    public void setCategorias(ArrayList<categorias> nuevas_cat){ this.cat=nuevas_cat; }

    public void setSubCategorias(ArrayList<subcategorias> nuevas_subcat){ this.subcat=nuevas_subcat; }

    @Override
    public Object getItem(int position){
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return lista.get(position).getId();
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent)
    {
        View vi = contentView;
        if (contentView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.custom_row_copy, null);
        }

        ImageView imagen = (ImageView) vi.findViewById(R.id.img_conf);
        TextView texto = (TextView) vi.findViewById(R.id.txt_conf);
        int width = new Porcentual().getScreenWidth(activity);
        int height= new Porcentual().getScreenHeight(activity);
        double sizeL = new Porcentual().getScreenInches(activity);
        Typeface type = Typeface.createFromAsset(activity.getAssets(), "roboto_regular.ttf");

        vi.setMinimumHeight((int) (height*0.075));

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imagen.getLayoutParams();
        layoutParams.width = (int) (width*0.05);
        layoutParams.height = (int) (layoutParams.width*1.2);
        layoutParams.setMarginEnd((int) (width * 0.05));
        imagen.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams txtParams = (RelativeLayout.LayoutParams) texto.getLayoutParams();
        txtParams.setMarginStart((int) (width*0.1));
        texto.setLayoutParams(txtParams);

        texto.setTextSize((float) (sizeL * 3.5));
        texto.setTypeface(type);

        texto.setText(lista.get(position).getTitle());

        switch (position){
            case 0:
                vi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("android.intent.action.Datos_personales"+activity.getString(R.string.ambiente_ego));
                        intent.putExtra("Token", Token);
                        intent.putExtra("idUser", iduser);
                        intent.putExtra("userName", userName);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });
                break;
            case 1:
                vi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("android.intent.action.Cambiar_Contrasena"+activity.getString(R.string.ambiente_ego));
                        intent.putExtra("Token", Token);
                        intent.putExtra("idUser", iduser);
                        intent.putExtra("userName", userName);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });
                break;
            case 2:
                vi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("android.intent.action.contacto"+activity.getString(R.string.ambiente_ego));
                        intent.putExtra("Token", Token);
                        intent.putExtra("idUser", iduser);
                        intent.putExtra("userName", userName);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });
                break;
            case 3:
                vi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("android.intent.action.Cambiar_Preferencias");
                        intent.putExtra("Token", Token);
                        intent.putExtra("idUser", iduser);
                        intent.putExtra("userName", userName);
                        intent.putExtra("categorias", cat);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });

                break;
            case 4:
                vi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ApiLogOffUsuario();
                        SharedPreferences prefPush = PreferenceManager.getDefaultSharedPreferences(activity);
                        String regID = prefPush.getString("installationId", null);
                        SharedPreferences sharedPreferences = activity.getSharedPreferences("info", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();
                        application.sendEvent("main", "logoff", "user");
                        Intent intent = new Intent("android.intent.action.Inicio"+activity.getString(R.string.ambiente_ego));
                        intent.putExtra("early", true);
                        ContenedorHomeGeneric chg = new ContenedorHomeGeneric();
                        if (chg.achg != null) {
                            chg.achg.finish();
                        }
                        CookieSyncManager.createInstance(activity);
                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.removeAllCookie();
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        activity.finish();
                        prefPush.edit().putString("installationId", regID ).commit();
                    }
                });
                break;
            case 5:
                vi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                                .setMessage(activity.getApplicationContext().getString(R.string.cerrar_sesion))
                                .setTitle("Importante")
                                .setCancelable(false)
                                .setPositiveButton(activity.getApplicationContext().getString(R.string.respuesta_si),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                ContenedorHomeGeneric chg = new ContenedorHomeGeneric();
                                                if (chg.achg != null) {
                                                    chg.achg.finish();
                                                }
                                                application.sendEvent("main", "exit", "exit");
                                                android.os.Process.killProcess(android.os.Process.myPid());
                                            }
                                        })
                                .setNegativeButton(activity.getApplicationContext().getString(R.string.respuesta_no), null);
                        builder.create().show();

                        try {
                            File dir = activity.getApplicationContext().getCacheDir();
                            deleteDir(dir);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
        }

        return vi;
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

    public void ApiLogOffUsuario()
    {
        final ProgressDialog dialog = ProgressDialog.show(activity,"","Cerrando Sesión. Por favor espere...", true);
        ArrayList<Pair<String, String>> headers = new ArrayList<Pair<String, String>>();
        headers.add(new Pair<String, String>("X-ZUMO-AUTH", Token));
        ArrayList<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
        params.add(new Pair<String, String>("idUser", String.valueOf(iduser)));

        byte[] aux =new byte[]{};
        try
        {
            MobileServiceClient mClient = new MobileServiceClient(activity.getString(R.string.URL_Mobile_Service), activity);

            try {
                ListenableFuture<ServiceFilterResponse> result =  mClient.invokeApi("Account/LogOff",aux, "GET",headers,params);
                Futures.addCallback(result, new FutureCallback<ServiceFilterResponse>() {
                            @Override
                            public void onSuccess(ServiceFilterResponse result) {
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Log.d("Prueba", t.toString());
                                dialog.dismiss();
                            }
                        }
                );
            }catch (Exception e){dialog.dismiss();
            }
        }
        catch(MalformedURLException e)
        {dialog.dismiss();
        }
    }
}