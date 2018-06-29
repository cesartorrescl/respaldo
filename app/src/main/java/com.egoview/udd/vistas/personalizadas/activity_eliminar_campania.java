package com.egoview.udd.vistas.personalizadas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.egoview.udd.R;
import com.egoview.udd.clases.campanias;
import com.egoview.udd.contenedores.generico.ContenedorHomeGeneric;
import com.egoview.udd.procesos.DialogEgoView;
import com.egoview.udd.procesos.Porcentual;
import com.egoview.udd.procesos.footer;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Serial on 28/08/2015 modified on 15/10/2015 .
 */
public class activity_eliminar_campania extends Activity {

    private ImageView img_camp, boton_si, boton_no;
    private PercentRelativeLayout volver;
    private Activity activity = this;
    private String token;
    private long idUser;
    private TextView txt_empresa, txt_titulo, txt_condiciones_titulo, txt_condiciones, txt_eliminar, txt_oferta;
    private ContenedorHomeGeneric homeGeneric;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_eliminar_camp);
        footer foot = new footer();
        foot.footer(this);

        token = getIntent().getExtras().getString("Token");
        idUser = getIntent().getExtras().getLong("idUser");
        homeGeneric = new ContenedorHomeGeneric();

        Porcentual porcentual = new Porcentual();
        double screenInches=  porcentual.getScreenInches(this);
        int width=  porcentual.getScreenWidth(this);
        int height=  porcentual.getScreenHeight(this);
        Typeface type = Typeface.createFromAsset(this.getAssets(), "roboto_regular.ttf");
        Typeface bold= Typeface.createFromAsset(this.getAssets(), "roboto_regular.ttf");

        txt_oferta = (TextView) findViewById(R.id.txt_oferta);

        txt_oferta.setTypeface(bold);

        txt_oferta.setTextSize((float) (screenInches * 3.5));

        img_camp = (ImageView) findViewById(R.id.img_oferta_detalle);
        RelativeLayout.LayoutParams img_params = (RelativeLayout.LayoutParams) img_camp.getLayoutParams();
        img_params.width = (int) (width*0.2);
        img_params.height = img_params.width;
        img_params.setMargins((int) (width * 0.05), (int) (height * 0.02), 0, 0);
        img_camp.setLayoutParams(img_params);

        txt_eliminar = (TextView) findViewById(R.id.txt_eliminar);
        txt_eliminar.setTextSize((float) (screenInches * 2.7));
        txt_eliminar.setTypeface(bold);

        txt_condiciones_titulo = (TextView) findViewById(R.id.txt_condiciones_titulo);
        txt_condiciones_titulo.setTextSize((float) (screenInches * 3.5));
        txt_condiciones_titulo.setTypeface(type);
        RelativeLayout.LayoutParams tituloC_params = (RelativeLayout.LayoutParams) txt_condiciones_titulo.getLayoutParams();
        tituloC_params.setMargins((int) (width * 0.05), (int) (height * 0.01), (int) (width * 0.05), 0);
        txt_condiciones_titulo.setLayoutParams(tituloC_params);

        txt_condiciones = (TextView) findViewById(R.id.txt_condiciones);
        txt_condiciones.setTextSize((float) (screenInches * 3));
        txt_condiciones.setTypeface(type);
        RelativeLayout.LayoutParams condiciones_params = (RelativeLayout.LayoutParams) txt_condiciones.getLayoutParams();
        condiciones_params.setMargins((int) (width * 0.05), (int) (height * 0.01), (int) (width * 0.05), (int) (height * 0.025));
        txt_condiciones.setLayoutParams(condiciones_params);

        txt_titulo = (TextView) findViewById(R.id.txt_titulo);
        txt_titulo.setTextSize((float) (3 * screenInches));
        txt_titulo.setTypeface(type);
        RelativeLayout.LayoutParams titulo_params = (RelativeLayout.LayoutParams) txt_titulo.getLayoutParams();
        titulo_params.setMargins((int) (width * 0.05), (int) (height * 0.01), (int) (width * 0.05), 0);
        txt_titulo.setLayoutParams(titulo_params);

        txt_empresa = (TextView) findViewById(R.id.txt_empresa);
        txt_empresa.setTextSize((float) (2.5 * screenInches));
        txt_empresa.setTypeface(type);
        RelativeLayout.LayoutParams empresa_params = (RelativeLayout.LayoutParams) txt_empresa.getLayoutParams();
        empresa_params.setMargins((int) (width * 0.05), (int) (height * 0.02), (int) (width * 0.05), 0);
        txt_empresa.setLayoutParams(empresa_params);
        final long idCamp = getIntent().getExtras().getLong("idCamp");

        ScrollView scroll = (ScrollView) findViewById(R.id.scrollEliminar);
        LinearLayout.LayoutParams sParams = (LinearLayout.LayoutParams) scroll.getLayoutParams();
        sParams.height = (int)(height*0.586);
        scroll.setLayoutParams(sParams);

        Apigetsucursales();

        boton_no = (ImageView) findViewById(R.id.boton_no);
        boton_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        boton_si = (ImageView) findViewById(R.id.boton_si);
        boton_si.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiDelete(idCamp);
            }
        });

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

        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    private class OpenUrl extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }

    private void invokeApiGetCampUser() {
        ListenableFuture<ServiceFilterResponse> result;
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Cargando publicación, espere un momento...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            ArrayList<Pair<String, String>> headers = new ArrayList<Pair<String, String>>();
            headers.add(new Pair<String, String>("X-ZUMO-AUTH", token));
            ArrayList<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
            params.add(new Pair<String, String>("idUser",String.valueOf(idUser)));
            byte[] aux =new byte[]{};
            MobileServiceClient mClient= new MobileServiceClient(getString(R.string.URL_Mobile_Service), this);
            try {
                result = mClient.invokeApi("Usuarios/HasCampUser",aux, "POST",headers, params);
                Futures.addCallback(result, new FutureCallback<ServiceFilterResponse>() {
                    @Override
                    public void onSuccess(ServiceFilterResponse result) {

                        try {
                            JSONObject json = new JSONObject(result.getContent());
                            txt_condiciones.setText(json.getString("condiciones_uso"));
                            txt_titulo.setText(json.getString("nombre_camp"));
                            txt_empresa.setText(json.getString("nombreUser"));
                            String html = "<html lang=\"en\">\n" +
                                    "<head>\n" +
                                    "                <meta charset=\"UTF-8\">\n" +
                                    "                <meta name=\"viewport\" content=\"width=390, initial-scale=1\">\n" +
                                    "</head>\n" +
                                    "<body>" + json.getString("cod_html").replace("${mts}", "") + "</body>\n" +
                                    "</html>";
                            try {
                                Uri uri = Uri.parse(json.getString("ruta_img"));
                                Picasso.with(activity)
                                        .load(uri)
                                        .placeholder(R.drawable.ajax_loader)
                                        .fit()
                                        .error(R.drawable.sinfoto)
                                        .into(img_camp);
                            } catch (Exception e) {
                                Picasso.with(activity)
                                        .load(R.drawable.sinfoto)
                                        .placeholder(R.drawable.ajax_loader)
                                        .fit()
                                        .into(img_camp);
                            }
                            if (html != null) {
                                WebView mywebview = (WebView) findViewById(R.id.webView);
                                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mywebview.getLayoutParams();
                                params.height = params.width;
                                Porcentual porcentual = new Porcentual();
                                int width = porcentual.getScreenWidth(activity);
                                params.setMargins((int) (width * 0.05), (int) (width * 0.025), (int) (width * 0.05), 0);
                                mywebview.setWebViewClient(new OpenUrl());
                                mywebview.setLayoutParams(params);
                                mywebview.getSettings().setUseWideViewPort(true);
                                mywebview.getSettings().setLoadWithOverviewMode(true);
                                mywebview.setInitialScale(1);
                                mywebview.loadData(html, "text/html; charset=utf-8", "utf-8");
                            }
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onFailure(Throwable t){
                        progressDialog.dismiss();
                        Toast.makeText(activity, "Ha ocurrido un error inesperado, intentelo nuevamente.",Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }catch (Exception e){
                progressDialog.dismiss();
                Log.d("error",e.getMessage());
            }
        }catch(MalformedURLException e){
            progressDialog.dismiss();
            Log.d("error",e.getMessage());
        }
    }

    public void Apigetsucursales() {
        campanias campania01 = (campanias) getIntent().getExtras().get("campanias");
        if(campania01!=null) {
            txt_condiciones.setText(campania01.getCondiciones_uso());
            txt_titulo.setText(campania01.getNombre_camp());
            txt_empresa.setText(campania01.getNombre_empresa());
            String html = "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "                <meta charset=\"UTF-8\">\n" +
                    "                <meta name=\"viewport\" content=\"width=390, initial-scale=1\">\n" +
                    "</head>\n" +
                    "<body>" + campania01.getHtmlCampArrayList().get(0).getCod_html().replace("${mts}", "") + "</body>\n" +
                    "</html>";
            try {
                Uri uri = Uri.parse(campania01.getRuta_imagen());
                Picasso.with(activity)
                        .load(uri)
                        .placeholder(R.drawable.ajax_loader)
                        .fit()
                        .error(R.drawable.sinfoto)
                        .into(img_camp);
            } catch (Exception e) {
                Picasso.with(activity)
                        .load(R.drawable.sinfoto)
                        .placeholder(R.drawable.ajax_loader)
                        .fit()
                        .into(img_camp);
            }
            if (html != null) {
                WebView mywebview = (WebView) findViewById(R.id.webView);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mywebview.getLayoutParams();
                params.height = params.width;
                Porcentual porcentual = new Porcentual();
                int width = porcentual.getScreenWidth(activity);
                params.setMargins((int) (width * 0.05), (int) (width * 0.025), (int) (width * 0.05), 0);
                mywebview.setWebViewClient(new OpenUrl());
                mywebview.setLayoutParams(params);
                mywebview.getSettings().setUseWideViewPort(true);
                mywebview.getSettings().setLoadWithOverviewMode(true);
                mywebview.setInitialScale(1);
                mywebview.loadData(html, "text/html; charset=utf-8", "utf-8");
            }
        }else{
            invokeApiGetCampUser();
        }
    }

    public void ApiDelete(long idCamp) {
        final ProgressDialog progressDialog;
        progressDialog = DialogEgoView.crear_progres_dialog_campanias(this);
        List<Pair<String, String>> queryParams;
        MobileServiceClient mClient;
        ListenableFuture<JsonElement> result;
        queryParams = new ArrayList<Pair<String, String>>();
        queryParams.add(new Pair<String, String>("idCamp", String.valueOf(idCamp)));

        ArrayList<Pair<String, String>> header = new ArrayList<Pair<String, String>>();
        header.add(new Pair<String, String>("X-ZUMO-AUTH", token));
        try {
            mClient = new MobileServiceClient(getString(R.string.URL_Mobile_Service), this);
            try {
                ListenableFuture<ServiceFilterResponse> result2 = mClient.invokeApi("Camps/DeleteCampByUser", null, "POST", header, queryParams);
                Futures.addCallback
                        (result2, new FutureCallback<ServiceFilterResponse>() {
                                    @Override
                                    public void onSuccess(ServiceFilterResponse result) {
                                        DialogEgoView.terminar_progrest(progressDialog);
                                        DialogEgoView.terminar_progrest(progressDialog);
                                        AlertDialog.Builder exito = new AlertDialog.Builder(activity)
                                                .setMessage(" Tu publicación se ha eliminado.")
                                                .setTitle("EgoviewDemo")
                                                .setCancelable(false)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        ((ContenedorHomeGeneric) homeGeneric.achg).setPublicacion(false,0, null);
                                                        activity.finish();
                                                    }
                                                });
                                        exito.create().show();
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        DialogEgoView.terminar_progrest(progressDialog);
                                        DialogEgoView.terminar_progrest(progressDialog);
                                        AlertDialog.Builder error = new AlertDialog.Builder(activity)
                                                .setMessage("Ha ocurrido un error inesperado, intentelo nuevamente.")
                                                .setTitle("Error")
                                                .setCancelable(false)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        error.create().show();
                                    }
                                }
                        );
            } catch (Exception e) {
                DialogEgoView.terminar_progrest(progressDialog);
            }
        } catch (MalformedURLException e) {
            DialogEgoView.terminar_progrest(progressDialog);
        }
    }
}