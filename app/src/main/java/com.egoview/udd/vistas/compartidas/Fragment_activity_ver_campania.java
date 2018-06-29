package com.egoview.udd.vistas.compartidas;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egoview.udd.R;
import com.egoview.udd.clases.campanias;
import com.egoview.udd.contenedores.compartido.ContenedorCampania;
import com.egoview.udd.procesos.Porcentual;
import com.egoview.udd.procesos.RoundedTransformation;
import com.squareup.picasso.Picasso;

/**
 * Created by Serial on 28/08/2015 modified on 15/10/2015 .
 */
public class Fragment_activity_ver_campania extends Fragment {
    View rootView;
    private ImageView img_camp, val1, val2, val3, val4, val5;
    private Uri uri;
    private campanias camp;
    private TextView txt_empresa, txt_titulo, txt_condiciones_titulo, txt_condiciones, txt_descripcion, txt_valoracion;
    private Button valoracion;
    private LinearLayout area_nombre, area_datos, area_valoracion;
    private long idUser;
    public int valoracionUserInicial, valoracionUser;

    public Fragment_activity_ver_campania() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(rootView==null){
            rootView = inflater.inflate(R.layout.activity_ver_campania, container, false);
        }
        ((ContenedorCampania) getActivity()).setFragment(this);
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
        Porcentual porcentual = new Porcentual();
        double screenInches=  porcentual.getScreenInches(getActivity());
        int width=  porcentual.getScreenWidth(getActivity());
        int height=  porcentual.getScreenHeight(getActivity());
        camp = campanias.getCampanias();
        idUser = getActivity().getIntent().getExtras().getLong("idUser");
        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "roboto_regular.ttf");
        Typeface bold= Typeface.createFromAsset(getActivity().getAssets(), "roboto_regular.ttf");

        img_camp = (ImageView) rootView.findViewById(R.id.img_oferta);
        RelativeLayout.LayoutParams img_params = (RelativeLayout.LayoutParams) img_camp.getLayoutParams();
        img_params.width = (int) (width*0.2);
        img_params.height = img_params.width;
        img_params.setMargins((int) (width * 0.05), (int) (height * 0.02), 0, 0);
        img_camp.setLayoutParams(img_params);
        img_camp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.zoom"+getString(R.string.ambiente_ego));
                intent.putExtra("rutaImagen", camp.getRuta_imagen());
                getActivity().startActivity(intent);
            }
        });

        area_valoracion = (LinearLayout) rootView.findViewById(R.id.area_valoracion);
        area_valoracion.setVisibility(View.GONE);

        txt_descripcion = (TextView) rootView.findViewById(R.id.txt_descripcion);
        txt_descripcion.setTextSize((float) (screenInches * 2.5));
        txt_descripcion.setTypeface(type);

        txt_valoracion = (TextView) rootView.findViewById(R.id.txt_valoracion);
        txt_valoracion.setVisibility(View.GONE);
        txt_valoracion.setTextSize((float) (screenInches * 2.5));
        txt_valoracion.setTypeface(type);

        val1 = (ImageView) rootView.findViewById(R.id.valoracion_1);
        val2 = (ImageView) rootView.findViewById(R.id.valoracion_2);
        val3 = (ImageView) rootView.findViewById(R.id.valoracion_3);
        val4 = (ImageView) rootView.findViewById(R.id.valoracion_4);
        val5 = (ImageView) rootView.findViewById(R.id.valoracion_5);
        LinearLayout.LayoutParams paramsVal = (LinearLayout.LayoutParams) val1.getLayoutParams();
        paramsVal.width = (int) (width*0.05);
        paramsVal.height = paramsVal.width;
        val1.setLayoutParams(paramsVal);
        paramsVal.leftMargin = (int) (width*0.01);
        val2.setLayoutParams(paramsVal);
        val3.setLayoutParams(paramsVal);
        val4.setLayoutParams(paramsVal);
        val5.setLayoutParams(paramsVal);

        val1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valoracionUserInt(1);
            }
        });
        val2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valoracionUserInt(2);
            }
        });
        val3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valoracionUserInt(3);
            }
        });
        val4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valoracionUserInt(4);
            }
        });
        val5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valoracionUserInt(5);
            }
        });

        valoracionUserInicial = camp.getValoracionUser();
        valoracionUser = camp.getValoracionUser();
        valoracionUserInt(valoracionUser);

        valoracion = (Button) rootView.findViewById(R.id.valoracion_empresa);
        valoracion.setTextSize((float) (screenInches*1.5));
        valoracion.setTypeface(type);
        LinearLayout.LayoutParams paramsValoracion = (LinearLayout.LayoutParams) valoracion.getLayoutParams();
        paramsValoracion.width = (int) (width * 0.05);
        paramsValoracion.height = paramsValoracion.width;
        valoracion.setLayoutParams(paramsValoracion);
        valoracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mensaje = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                        .setTitle("VALORACIÓN DE LA EMPRESA")
                        .setMessage(camp.getValoracion()+" "+camp.getNombre_empresa());
                AlertDialog mensajeReady = mensaje.show();
                TextView titleView = (TextView)mensajeReady.findViewById(getActivity().getResources().getIdentifier("alertTitle", "id", "android"));
                if (titleView != null) {
                    titleView.setGravity(Gravity.CENTER_HORIZONTAL);
                    titleView.setTextColor(getResources().getColor(R.color.gris));
                }
                TextView message = (TextView) mensajeReady.findViewById(android.R.id.message);
                message.setGravity(Gravity.CENTER_HORIZONTAL);
                message.setTextColor(getResources().getColor(R.color.gris));
                mensajeReady.show();
            }
        });

        area_nombre = (LinearLayout) rootView.findViewById(R.id.area_nombre);
        area_datos  = (LinearLayout) rootView.findViewById(R.id.area_datos);
        RelativeLayout.LayoutParams paramsMargin = (RelativeLayout.LayoutParams) area_nombre.getLayoutParams();
        paramsMargin.setMargins((int) (width * 0.05), (int) (height * 0.01), (int) (width * 0.05), 0);
        paramsMargin.height = (int) (paramsValoracion.height*1.2);
        area_nombre.setLayoutParams(paramsMargin);
        paramsMargin = (RelativeLayout.LayoutParams) area_datos.getLayoutParams();
        paramsMargin.setMargins((int) (width * 0.05), 0, (int) (width * 0.05), 0);
        area_datos.setLayoutParams(paramsMargin);

        txt_condiciones_titulo = (TextView) rootView.findViewById(R.id.txt_condiciones_titulo);
        txt_condiciones_titulo.setTextSize((float) (screenInches * 3.5));
        txt_condiciones_titulo.setTypeface(type);
        RelativeLayout.LayoutParams tituloC_params = (RelativeLayout.LayoutParams) txt_condiciones_titulo.getLayoutParams();
        tituloC_params.setMargins((int) (width * 0.05), (int) (height * 0.01), (int) (width * 0.05), 0);
        txt_condiciones_titulo.setLayoutParams(tituloC_params);

        txt_condiciones = (TextView) rootView.findViewById(R.id.txt_condiciones);
        txt_condiciones.setText(camp.getCondiciones_uso());
        txt_condiciones.setTextSize((float) (screenInches * 3));
        txt_condiciones.setTypeface(type);
        RelativeLayout.LayoutParams condiciones_params = (RelativeLayout.LayoutParams) txt_condiciones.getLayoutParams();
        condiciones_params.setMargins((int) (width * 0.05), (int) (height * 0.01), (int) (width * 0.05), (int) (height * 0.025));
        txt_condiciones.setLayoutParams(condiciones_params);

        txt_titulo = (TextView) rootView.findViewById(R.id.txt_titulo);
        txt_titulo.setTextSize((float) (3 * screenInches));
        txt_titulo.setTypeface(type);

        txt_empresa = (TextView) rootView.findViewById(R.id.txt_empresa);
        txt_empresa.setTextSize((float) (2.5 * screenInches));
        txt_empresa.setTypeface(type);

        mostrarInformacion();

        String html = camp.getHtmlCampArrayList().get(0).getCod_html().replace("${mts}",String.valueOf((int) camp.getMdistancia()));
        try {
            uri = Uri.parse(camp.getRuta_imagen());
            Picasso.with(getContext())
                    .load(uri)
                    .placeholder(R.drawable.ajax_loader)
                    .transform(new RoundedTransformation(25,4))
                    .fit()
                    .error(R.drawable.sinfoto)
                    .into(img_camp);
        } catch (Exception e) {
            Picasso.with(getContext())
                    .load(R.drawable.sinfoto)
                    .placeholder(R.drawable.ajax_loader)
                    .transform(new RoundedTransformation(25,4))
                    .fit()
                    .into(img_camp);
        }
        if(html!=null){
            WebView mywebview = (WebView) rootView.findViewById(R.id.webView);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mywebview.getLayoutParams();
            params.setMargins((int) (width*0.1),(int) (width*0.025),(int) (width*0.1),0);
            params.height=params.width;
            mywebview.setLayoutParams(params);
            mywebview.getSettings().setUseWideViewPort(true);
            mywebview.getSettings().setLoadWithOverviewMode(true);
            mywebview.setInitialScale(1);
            mywebview.getSettings().setJavaScriptEnabled(true);
            mywebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            mywebview.loadData(html, "text/html; charset=utf-8", "utf-8");
        }
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
        return rootView;
    }

    private void mostrarInformacion(){
        if(idUser==0){
            valoracion.setVisibility(View.GONE);
            area_valoracion.setVisibility(View.GONE);
            txt_valoracion.setVisibility(View.GONE);
            if (camp.getIsPagada()){
                txt_empresa.setText(camp.getNombre_empresa());
                txt_titulo.setText(camp.getNombre_camp());
                txt_descripcion.setText(camp.getDesc_campania());
            }else {
                txt_empresa.setText("PUBLICADO POR");
                txt_titulo.setText(camp.getNombre_empresa());
                txt_titulo.setTextColor(getResources().getColor(R.color.gris));
                txt_descripcion.setVisibility(View.GONE);
            }
        }else{
            if (camp.getIsPagada()){
                txt_empresa.setText(camp.getNombre_empresa());
                txt_titulo.setText(camp.getNombre_camp());
                valoracion.setText(String.valueOf(camp.getValoracion()));
                txt_descripcion.setText(camp.getDesc_campania());
            }else{
                if (camp.getIsEmpresa()) {
                    txt_empresa.setText(camp.getNombre_empresa());
                    txt_titulo.setVisibility(View.GONE);
                    valoracion.setText(String.valueOf(camp.getValoracion()));
                    txt_descripcion.setVisibility(View.GONE);
                }else {
                    valoracion.setVisibility(View.GONE);
                    txt_valoracion.setVisibility(View.GONE);
                    area_valoracion.setVisibility(View.GONE);
                    txt_empresa.setText("PUBLICADO POR");
                    txt_titulo.setTextColor(getResources().getColor(R.color.gris));
                    txt_titulo.setText(camp.getNombre_empresa());
                    txt_descripcion.setVisibility(View.GONE);
                }
            }
        }
    }

    private void valoracionUserInt(int valor){
            switch (valor) {
                case 0:
                    val1.setBackground(getResources().getDrawable(R.drawable.valorno));
                    val2.setBackground(getResources().getDrawable(R.drawable.valorno));
                    val3.setBackground(getResources().getDrawable(R.drawable.valorno));
                    val4.setBackground(getResources().getDrawable(R.drawable.valorno));
                    val5.setBackground(getResources().getDrawable(R.drawable.valorno));
                    break;
//                case 1:
//                    val1.setBackground(getResources().getDrawable(R.drawable.valor5));
//                    val2.setBackground(getResources().getDrawable(R.drawable.valorno));
//                    val3.setBackground(getResources().getDrawable(R.drawable.valorno));
//                    val4.setBackground(getResources().getDrawable(R.drawable.valorno));
//                    val5.setBackground(getResources().getDrawable(R.drawable.valorno));
//                    break;
//                case 2:
//                    val1.setBackground(getResources().getDrawable(R.drawable.valor5));
//                    val2.setBackground(getResources().getDrawable(R.drawable.valor5));
//                    val3.setBackground(getResources().getDrawable(R.drawable.valorno));
//                    val4.setBackground(getResources().getDrawable(R.drawable.valorno));
//                    val5.setBackground(getResources().getDrawable(R.drawable.valorno));
//                    break;
//                case 3:
//                    val1.setBackground(getResources().getDrawable(R.drawable.valor5));
//                    val2.setBackground(getResources().getDrawable(R.drawable.valor5));
//                    val3.setBackground(getResources().getDrawable(R.drawable.valor5));
//                    val4.setBackground(getResources().getDrawable(R.drawable.valorno));
//                    val5.setBackground(getResources().getDrawable(R.drawable.valorno));
//                    break;
//                case 4:
//                    val1.setBackground(getResources().getDrawable(R.drawable.valor5));
//                    val2.setBackground(getResources().getDrawable(R.drawable.valor5));
//                    val3.setBackground(getResources().getDrawable(R.drawable.valor5));
//                    val4.setBackground(getResources().getDrawable(R.drawable.valor5));
//                    val5.setBackground(getResources().getDrawable(R.drawable.valorno));
//                    break;
//                case 5:
//                    val1.setBackground(getResources().getDrawable(R.drawable.valor5));
//                    val2.setBackground(getResources().getDrawable(R.drawable.valor5));
//                    val3.setBackground(getResources().getDrawable(R.drawable.valor5));
//                    val4.setBackground(getResources().getDrawable(R.drawable.valor5));
//                    val5.setBackground(getResources().getDrawable(R.drawable.valor5));
//                    break;
            }
            valoracionUser = valor;
    }
}