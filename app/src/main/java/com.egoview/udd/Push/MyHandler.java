package com.egoview.udd.Push;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Pair;

import com.egoview.udd.R;
import com.egoview.udd.clases.campanias;
import com.egoview.udd.procesos.AnalyticsApplication;
import com.egoview.udd.procesos.Parseo_Json;
import com.egoview.udd.vistas.compartidas.Splash;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.notifications.NotificationsHandler;

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

import me.leolin.shortcutbadger.ShortcutBadger;

public class MyHandler extends NotificationsHandler {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context ctx;
    private String nhMessage, nhTitle, nhCodTipo, nhIdTipo;
    private Intent intent;

    @Override
    public void onReceive(Context context, Bundle bundle) {
        ctx = context.getApplicationContext();
        nhCodTipo = bundle.getString("codtipo");
        nhMessage = bundle.getString("message");
        nhTitle = bundle.getString("title");
        if (nhCodTipo != null && !nhCodTipo.equals("")) {
            switch (nhCodTipo) {
                case "0":
                    intent = new Intent("android.intent.action.ContenedorCampaniadesa" + AnalyticsApplication.getViewVisible().getString(R.string.ambiente_ego));
                    break;
                case "3":
                    if (!AnalyticsApplication.isActivityVisible()) {
                        countNumber(context);
                    }
                    break;
            }
            nhIdTipo = bundle.getString("idtipo");
        }
        if(!nhCodTipo.equals("3")) {
            if (!AnalyticsApplication.isActivityVisible()) {
                sendNotification();
            } else {
                showNotification();
            }
        }
    }

    private void sendNotification() {

        Intent intent = new Intent(ctx, Splash.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(nhCodTipo!=null && !nhCodTipo.equals("")) {
            intent.putExtra("codtipo", nhCodTipo);
            intent.putExtra("idtipo", nhIdTipo);
        }

        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setContentTitle(nhTitle)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(nhMessage))
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setSound(defaultSoundUri)
                        .setContentText(nhMessage);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void showNotification(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AnalyticsApplication.getViewVisible())
                .setTitle(nhTitle)
                .setMessage(nhMessage)
                .setCancelable(true);
        if(nhCodTipo!=null && !nhCodTipo.equals("")){
            builder.setPositiveButton("Ir", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    putDataIntent();
                    dialog.dismiss();
                }
            });
        }
        builder.create().show();
    }

    private void putDataIntent(){
        intent.putExtra("idTipo", nhIdTipo);
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
                Apigetsucursales(latitud, longitud);
                break;

        }
    }

    public void Apigetsucursales(final double lat, final double lon) {
        List<Pair<String, String>> queryParams;
        MobileServiceClient mClient;
        ListenableFuture<JsonElement> result;
        queryParams = new ArrayList<Pair<String, String>>();
        queryParams.add(new Pair<String, String>("idCamp", nhIdTipo));
        queryParams.add(new Pair<String, String>("latitud", String.valueOf(-33.440550)));
        queryParams.add(new Pair<String, String>("longitud", String.valueOf(-70.650723)));
        try {
            mClient = new MobileServiceClient(AnalyticsApplication.getViewVisible().getString(R.string.URL_Mobile_Service), AnalyticsApplication.getViewVisible());
            try {
                result = mClient.invokeApi("Camps/DetalleCampGenericaByidCamp", "POST", queryParams);
                Futures.addCallback
                        (result, new FutureCallback<JsonElement>() {
                                    @Override
                                    public void onSuccess(JsonElement result) {
                                        campanias camp = new campanias();
                                        campanias campania02 = Parseo_Json.parseoJsonCampainFinal(result.toString(), camp);
                                        intent.putExtra("latitud",lat);
                                        intent.putExtra("longitud",lon);
                                        intent.putExtra("flag", false);
                                        intent.putExtra("token", AnalyticsApplication.getToken());
                                        intent.putExtra("idUser", AnalyticsApplication.getIdUser());
                                        intent.putExtra("position", 0);
                                        final String[] mensaje = {""};
                                        if(!campania02.getVisto()){
                                            new AsyncTask<Object, Object, Object>() {

                                                @Override
                                                protected Object doInBackground(Object... params) {
                                                    mensaje[0] = ApiVisto(AnalyticsApplication.getToken(), AnalyticsApplication.getIdUser(), nhIdTipo, AnalyticsApplication.getViewVisible());

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
                                        AnalyticsApplication.getViewVisible().startActivity(intent);
                                        AnalyticsApplication.getViewVisible().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

    private void countNumber(Context context){
        int contador;
        SharedPreferences shared_preferences = context.getApplicationContext().getSharedPreferences("numberIcon", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared_preferences.edit();
        if (shared_preferences.getString("numberIcon", null) != null) {
            try {
                contador = Integer.parseInt(shared_preferences.getString("numberIcon", null)) + 1;
            }catch (Exception e) {
                contador = 1;
            }
            editor.putString("numberIcon", String.valueOf(contador));
        }else{
            contador = 1;
            editor.putString("numberIcon", String.valueOf(contador));
        }
        editor.commit();
        ShortcutBadger.applyCount(context.getApplicationContext(), contador);
    }
}