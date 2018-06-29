package com.egoview.udd.procesos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import com.egoview.udd.R;
import com.egoview.udd.contenedores.generico.ContenedorHomeGeneric;

/**
 * Created by Serial Desarrollo on 28-12-2015.
 */
public class providers{

    private Activity activity;
    private Context mContext;
    private boolean pause;
    private AlertDialog alerta;
    private String token, userName;
    private long idUser = 0;
    private Boolean redSocial=false;


    public void providers(String token, String userName, Context mContext, Activity activity, LocationManager locationManagerGPS, LocationManager locationManagerNET, long idUser) {
        this.mContext = mContext;
        this.activity = activity;
        this.idUser=idUser;
        this.pause = false;
        this.token = token;
        this.userName = userName;
    }

    public BroadcastReceiver getBroadcastReceiverNET(final AlertDialog.Builder alertDialog) {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                if(!pause) {
                    if (intent.getExtras() != null) {
                        NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                        if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                            if(alerta!=null) {
                                alerta.dismiss();
                            }
                        } else {
                            if(alerta!=null) {
                                alerta.dismiss();
                            }
                                alertDialog.setTitle("Problemas con Internet");
                                alertDialog.setMessage(" Por favor, verificar su conexión a internet.");
                                alertDialog.setCancelable(false);
                                alertDialog.setNeutralButton("Ajustes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                        activity.startActivityForResult(intent, 1);
                                        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        alerta = alertDialog.create();
                                        alerta.show();
                                    }
                                });
                                alertDialog.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ContenedorHomeGeneric chg = new ContenedorHomeGeneric();
                                        if (chg.achg != null) {
                                            chg.achg.finish();
                                        }
                                        android.os.Process.killProcess(android.os.Process.myPid());
                                        activity.finish();
                                    }
                                });
                                alerta = alertDialog.create();
                                alerta.show();
                        }
                    }
                }
            }
        };
    }

    public void Pause(){this.pause=true;}
    public void Resume(){this.pause=false;}
}

