package com.egoview.udd.vistas.compartidas;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Pair;
import android.view.Window;
import android.widget.Toast;

import com.egoview.udd.Push.CreateUpdateService;
import com.egoview.udd.Push.MyHandler;
import com.egoview.udd.Push.NotificationSettings;
import com.egoview.udd.Push.RegistrationIntentService;
import com.egoview.udd.R;
import com.egoview.udd.clases.campanias;
import com.egoview.udd.contenedores.generico.ContenedorHomeGeneric;
import com.egoview.udd.procesos.AnalyticsApplication;
import com.egoview.udd.procesos.Conexion;
import com.egoview.udd.procesos.footer;
import com.egoview.udd.procesos.providers;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by Serial on 22/08/2015 modified on 04/01/2016.
 */
public class Splash extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private static final String TAG = Splash.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private static int UPDATE_INTERVAL = 10000;
    private static int FATEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 10;
    public long idUser = 0;
    public static Splash splash;
    private LocationRequest mLocationRequest;

    private boolean flag;
    private SharedPreferences shared_preferences;
    private SharedPreferences.Editor editor;
    private Conexion conexion;
    private boolean validar_wifi;
    private boolean validar_datos;
    private boolean validar_gps;
    private LocationManager locationManagerGPS;
    private LocationManager locationManagerNET;
    private AlertDialog.Builder alertDialog;
    private AlertDialog.Builder builderGPS;
    private providers mProviders;
    private campanias camp;
    private Activity activity = this;

    private MobileServiceClient mClient;
    private ListenableFuture<JsonElement> result;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        footer foot = new footer();
        foot.footer(this);

        ShortcutBadger.removeCount(getApplicationContext());
        SharedPreferences shared_preferences_number = getApplicationContext().getSharedPreferences("numberIcon", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor_number = shared_preferences_number.edit();
        editor_number.putString("numberIcon","0");
        editor_number.commit();

        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();

            createLocationRequest();

            service_push();
            try {
                if (getIntent().getExtras() != null) {
                    AnalyticsApplication.setCodTipo(getIntent().getExtras().getString("codTipo"));
                    AnalyticsApplication.setIdTipo(getIntent().getExtras().getString("idTipo"));
                }
            }catch (Exception e){}
            camp = new campanias();
            locationManagerGPS = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManagerNET = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            alertDialog = new AlertDialog.Builder(this);
            mProviders = new providers();
            mProviders.providers(null, null, this, this, locationManagerGPS, locationManagerNET, 0);
            validar_wifi = Conexion.Conexion_Wifi(this);
            validar_datos = Conexion.Conexion_Datos(this);
            iniciar_app(0);
        }else{
            Toast.makeText(activity, "Actualice Google Play Service para continuar.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            iniciar_app(0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent=new Intent();
                    intent.setClass(this, this.getClass());
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                } else {
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void iniciar_app(final int requestCode){
        //if existe registro  pasa a buscar_categorias
        // Start the next activity
        shared_preferences = getSharedPreferences("info", Context.MODE_PRIVATE);
        editor = shared_preferences.edit();
            if (shared_preferences.getString("correo", null) != null && shared_preferences.getString("info", null) != null) {
                //Intent intent = new Intent("android.intent.action.MainActivity" + getString(R.string.ambiente_ego));
                ApiGetUsuario(shared_preferences.getString("correo", null), shared_preferences.getString("info", null));
                // Close the activity so the user won't able to go back this
                // activity pressing Back button
            } else {
                //Intent intent = new Intent("android.intent.action.BuscarCategoriasGeneric" + getString(R.string.ambiente_ego));
                Intent intent = new Intent("android.intent.action.Inicio"+getString(R.string.ambiente_ego));
                intent.putExtra("early", true);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                // Close the activity so the user won't able to go back this
                // activity pressing Back button
                finish();
            }
    }

    public void ApiGetUsuario(final String correo, final String pass)
    {
        JsonObject json = new JsonObject();
        try {
            json.addProperty("Email", correo);
            json.addProperty("Password", pass);
        } catch (JsonIOException e) {
        }
        ArrayList<Pair<String, String>> headers = new ArrayList<Pair<String, String>>();
        headers.add(new Pair<String, String>("Content-Type", "application/json"));
        try
        {
            mClient = new MobileServiceClient(getString(R.string.URL_Mobile_Service), this);

            try {
                result =  mClient.invokeApi("Auth/Login",json, "POST", headers);
                Futures.addCallback(result, new FutureCallback<JsonElement>() {
                    @Override
                    public void onSuccess(JsonElement result) {
                        Log.d("JsonElement", result.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());
                            String userName = jsonObject.getJSONObject("user").getString("name");
                            ContenedorHomeGeneric chg = new ContenedorHomeGeneric();
                            if (chg.achg != null) {
                                chg.achg.finish();
                            }
                            Intent intent = new Intent("android.intent.action.BuscarCategoriasGenericdesa" + getString(R.string.ambiente_ego));
                            intent.putExtra("Token",jsonObject.getString("authenticationToken"));
                            idUser = Long.parseLong(jsonObject.getJSONObject("user").getString("userId"));
                            iniciar_push();
                            AnalyticsApplication.setIdUser(idUser);
                            AnalyticsApplication.setToken(jsonObject.getString("authenticationToken"));
                            AnalyticsApplication.setUserName(userName);
                            intent.putExtra("idUser", idUser);
                            intent.putExtra("userName",userName);
                            intent.putExtra("radioGPS", jsonObject.getJSONObject("user").getInt("rango"));
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        } catch (JSONException e) {

                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(activity,"Hubo un problema al iniciar sesion, sera redirigido para que ingrese sus datos nuevamente.",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent("android.intent.action.Inicio"+getString(R.string.ambiente_ego));
                        intent.putExtra("early",true);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        editor.clear();
                        editor.commit();
                        finish();
                    }
                });
            }
            catch (Exception e){
            }
        }
        catch(MalformedURLException e)
        {
        }
    }

    private void iniciar_push(){
        CreateUpdateService service = new CreateUpdateService();
        service.CreateUpdateService(idUser, this);
    }

    private void service_push(){
        NotificationsManager.handleNotifications(this, NotificationSettings.SenderId, MyHandler.class);
        registerWithNotificationHubs();
    }
    public void registerWithNotificationHubs()
    {
        if (checkPlayServices()) {
            // Start IntentService to register this application with FCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AnalyticsApplication.activityResumed(this);
        checkPlayServices();

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AnalyticsApplication.activityPaused();
        //stopLocationUpdates();
    }

    /**
     * Method to display the location on UI
     * */
    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            Log.i("LOG Resultado: ", "lat/lon vale: " + latitude + ", " + longitude);

        } else {
        }
    }



    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
