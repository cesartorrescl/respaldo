package com.egoview.udd.Push;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;

import com.egoview.udd.R;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by serial_dev on 29/12/2016.
 */

public class CreateUpdateService {
    private static final String TAG = "RegIntentService";
    private MobileServiceClient mClient;
    private boolean isInvitado;
    private long idUser;
    private Context context;
    SharedPreferences sharedPreferences;

    public void CreateUpdateService(long id, Context mContext){
        idUser = id;
        context = mContext;
        if (idUser == 0){
            isInvitado = true;
        }else{
            isInvitado = false;
        }
        String resultString = null;
        String regID = null;
        String storedToken = null;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            String FCM_token = FirebaseInstanceId.getInstance().getToken();
            String idID = FirebaseInstanceId.getInstance().getId();
            Log.d(TAG, "FCM Registration Token: " + FCM_token);

            // Storing the registration id that indicates whether the generated token has been
            // sent to your server. If it is not stored, send the token to your server,
            // otherwise your server should have already received the token.
            if (((regID=sharedPreferences.getString("installationId", null)) == null)){
                ServicePush(null,FCM_token);
            }

            // Check if the token may have been compromised and needs refreshing.
            else if ((storedToken=sharedPreferences.getString("FCMtoken", "")) != FCM_token) {
                ServicePush(regID,FCM_token);
            }else{
                ServicePush(regID,storedToken);
            }
        } catch (Exception e) {
            Log.e(TAG, resultString="Failed to complete registration", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
        }
    }

    private void ServicePush(final String InstallationID, final String token) {
        try {
            try {
                mClient = new MobileServiceClient(context.getString(R.string.URL_Mobile_Service), context);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            ArrayList<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
            JSONObject json = new JSONObject();
            try {
                json.put("platform","gcm");
                json.put("handle",token);
                json.put("isInvitado",isInvitado);
                if(idUser!=0) {
                    json.put("idUsuario", idUser);
                }
                json.put("installationId",InstallationID);
            } catch (JSONException e) {
            }
            JsonElement Json = new JsonParser().parse(json.toString());
            ListenableFuture<JsonElement> result = mClient.invokeApi("Push/RegisterDevice", Json, "POST", params);
            Futures.addCallback(result, new FutureCallback<JsonElement>() {
                @Override
                public void onSuccess(JsonElement result) {
                    Log.d("Exito", result.toString());
                    sharedPreferences.edit().putString("FCMtoken", token ).commit();
                    try {
                        JSONObject json = new JSONObject(result.toString());
                        String newInstallationID = json.getString("installationId");
                        sharedPreferences.edit().putString("installationId", newInstallationID ).commit();
                    } catch (JSONException e) {
                        sharedPreferences.edit().putString("installationId", InstallationID ).commit();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("Error", t.getMessage());
                }
            });
        } catch (Exception e) {
        }
    }
}
