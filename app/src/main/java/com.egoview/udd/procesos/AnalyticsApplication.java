package com.egoview.udd.procesos;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;

import com.egoview.udd.clases.categorias;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * This is a subclass of {@link Application} used to provide shared objects for this app, such as
 * the {@link Tracker}.
 */
public class AnalyticsApplication extends Application {
    private Tracker mTracker;
    private static boolean activityVisible;
    private static Activity viewVisible;
    private static String token, userName;
    private static long idUser;
    private static double lat, lon, latFused=0, lonFused;
    private static ArrayList<categorias> cat;

    private static String codTipo, idTipo;


    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker("UA-76601072-1");
        }
        return mTracker;
    }

    public void sendScreen(String idScreen ){
        mTracker.setScreenName(idScreen);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void sendEvent(String category, String action, String label){
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Activity getViewVisible(){
        return viewVisible;
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed(Activity view) {
        viewVisible = view;
        activityVisible = true;
        ShortcutBadger.removeCount(viewVisible.getApplicationContext());
        SharedPreferences shared_preferences_number = viewVisible.getApplicationContext().getSharedPreferences("numberIcon", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor_number = shared_preferences_number.edit();
        editor_number.putString("numberIcon","0");
        editor_number.commit();
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    public static void setToken(String nToken){ token = nToken; }
    public static void setUserName(String nUser){ userName = nUser; }
    public static void setIdUser(long nId){ idUser = nId; }
    public static void setLat(double nLat){ lat= nLat; }
    public static void setLon(double nLon){ lon = nLon; }
    public static void setLatFused(double nLat){ latFused= nLat; }
    public static void setLonFused(double nLon){ lonFused = nLon; }
    public static void setCat(ArrayList<categorias> nCat){ cat = nCat; }

    public static String getToken(){ return token;}
    public static String getUserName(){ return userName;}
    public static long getIdUser(){ return idUser;}
    public static double getLat(){ return lat;}
    public static double getLon(){ return lon;}
    public static double getLatFused(){ return latFused;}
    public static double getLonFused(){ return lonFused;}
    public static ArrayList<categorias> getCat(){ return cat;}
    public static String getCodTipo() {
        return codTipo;
    }

    public static void setCodTipo(String _codTipo) {
        codTipo = _codTipo;
    }

    public static String getIdTipo() {
        return idTipo;
    }

    public static void setIdTipo(String _idTipo) {
        idTipo = _idTipo;
    }

}