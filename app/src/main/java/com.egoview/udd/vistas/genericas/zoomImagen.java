package com.egoview.udd.vistas.genericas;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;

import com.egoview.udd.R;
import com.egoview.udd.procesos.AnalyticsApplication;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Ernesto on 21/09/2016.
 */
public class zoomImagen extends Activity {

    private String ruta;
    private uk.co.senab.photoview.PhotoView imagen;
    private PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.zoomimagen);

        imagen = (uk.co.senab.photoview.PhotoView) findViewById(R.id.imagen_zoom);

        ruta = getIntent().getExtras().getString("rutaImagen");

        try {
            Picasso.with(this)
                    .load(Uri.parse(ruta))
                    .placeholder(R.drawable.ajax_loader)
                    .error(R.drawable.sinfoto)
                    .into(imagen);
        } catch (Exception e) {
            Picasso.with(this)
                    .load(R.drawable.sinfoto)
                    .placeholder(R.drawable.ajax_loader)
                    .into(imagen);
        }

        mAttacher = new PhotoViewAttacher(imagen);
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
