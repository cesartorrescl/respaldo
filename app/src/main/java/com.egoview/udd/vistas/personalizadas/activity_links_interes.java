package com.egoview.udd.vistas.personalizadas;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.egoview.udd.procesos.footer;

import com.egoview.udd.R;
import com.egoview.udd.procesos.AnalyticsApplication;
import com.egoview.udd.procesos.Porcentual;


/**
 * Created by Ernesto on 29/04/2016.
 */
public class activity_links_interes extends Activity {


    private TextView txt_titulo, txt_interes;
    private ImageView web, news, intranet;
    private PercentRelativeLayout volver;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_links_interes);
        footer foot = new footer();
        foot.footer(this);
        //Inicializar Variables
        txt_titulo = (TextView) findViewById(R.id.txt_titulo);
        txt_interes = (TextView) findViewById(R.id.txt_interes);
        web = (ImageView) findViewById(R.id.img_web);
        news = (ImageView) findViewById(R.id.img_news);
        //intranet = (ImageView) findViewById(R.id.img_intranet);

        Typeface type = Typeface.createFromAsset(getAssets(),"roboto_regular.ttf");
        Typeface bold= Typeface.createFromAsset(getAssets(),"roboto_regular.ttf");
        //Fin
        //TextSize Ajust
        Porcentual porcentual = new Porcentual();
        txt_titulo.setTextSize((float) (porcentual.getScreenInches(this)*3.5));
        txt_interes.setTextSize((float) (porcentual.getScreenInches(this)*3.0));
        //Fin

        //TypeFace
        txt_titulo.setTypeface(bold);
        txt_interes.setTypeface(type);
        //Fin

        //Net Listener
        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.egoview.cl/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.serial.cl/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
//        intranet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Uri uri = Uri.parse("https://connectischile.sharepoint.com/SitePages/Inicio.aspx");
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);
//            }
//        });
        volver = (PercentRelativeLayout) findViewById(R.id.volver);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        //Fin
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
