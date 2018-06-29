package com.egoview.udd.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egoview.udd.R;
import com.egoview.udd.clases.campanias;
import com.egoview.udd.procesos.Porcentual;
import com.egoview.udd.procesos.Redireccionar_Datos;
import com.egoview.udd.vistas.genericas.Fragment_activity_categorias_generic;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by Serial Desarrollo on 11-01-2016.
 */
public class adapterRecyclerView extends RecyclerView.Adapter<adapterRecyclerView.ViewHolderRecyclerView>{

    private LayoutInflater layoutInflater;
    private static ArrayList<campanias> campania_lista;
    private static boolean isFav;
    private Context context;
    private double latitud;
    private double longitud;
    private Activity activity;
    public static String token, userName;
    public static long idUser;
    private MediaPlayer mg;
    private MediaPlayer fav;


    public adapterRecyclerView(boolean isFav, String token, long idUser, Context context, double latitud, double longitud, Activity activity, ArrayList<campanias> campania_lista, String userName){
        layoutInflater = LayoutInflater.from(context);
        this.campania_lista = campania_lista;
        this.context = context;
        this.latitud = latitud;
        this.longitud = longitud;
        this.activity = activity;
        this.idUser = idUser;
        this.token = token;
        this.isFav = isFav;
        this.userName = userName;
        mg = MediaPlayer.create(context, R.raw.mg);
        fav = MediaPlayer.create(context, R.raw.favorito);
    }

    @Override
    public ViewHolderRecyclerView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.activity_listview_content_recycler_view, parent, false);
        ViewHolderRecyclerView viewHolderRecyclerView = new ViewHolderRecyclerView(view, context, latitud, longitud, activity);
        return viewHolderRecyclerView;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(ViewHolderRecyclerView holder, int position) {

        final campanias camp = campania_lista.get(position);


        holder.txt_mg.setText(String.valueOf(camp.getCantMG()));
        holder.txt_nmg.setText(String.valueOf(camp.getCantNMG()));
        holder.valoracion.setText(String.valueOf(camp.getValoracion()));

        if (idUser!=0) {
            holder.titulo_camp.setText(camp.getNombre_categoria());
            holder.descripcion_camp.setText(camp.getNombre_camp());
            holder.txt_empresa_pagada.setText(camp.getDesc_campania());
        }else{
            holder.titulo_camp.setText(camp.getNombre_camp());
            holder.descripcion_camp.setText(camp.getDesc_campania());
        }
        if(camp.getVisto()){
            holder.img_visto.setBackground(activity.getResources().getDrawable(R.drawable.vst_hvr));
        }else{
            holder.img_visto.setBackground(activity.getResources().getDrawable(R.drawable.vst));
        }
        if(camp.getMG()){
            holder.img_mg.setBackground(activity.getResources().getDrawable(R.drawable.mg_hvr));
        }else{
            holder.img_mg.setBackground(activity.getResources().getDrawable(R.drawable.social_mg));
        }
        if(camp.getNMG()){
            holder.img_nmg.setBackground(activity.getResources().getDrawable(R.drawable.nmg_hvr));
        }else{
            holder.img_nmg.setBackground(activity.getResources().getDrawable(R.drawable.social_nmg));
        }
        if(camp.getFav()){
            holder.img_fav.setBackground(activity.getResources().getDrawable(R.drawable.fav_hvr));
        }else{
            holder.img_fav.setBackground(activity.getResources().getDrawable(R.drawable.fav));
        }
        if(!camp.getIsEmpresa()){
            holder.valoracion.setVisibility(View.GONE);
        }
        if (!camp.getIsPagada()){
            holder.txt_empresa_pagada.setVisibility(View.GONE);
            holder.area_ver_todas.setVisibility(View.GONE);
        }
        holder.distancia.setVisibility(View.GONE);
        holder.ic_distancia.setVisibility(View.GONE);
        try {
            Uri uri = Uri.parse(camp.getRuta_imagen());
            Picasso.with(context)
                    .load(uri)
                    .placeholder(R.drawable.ajax_loader)
                    .fit()
                    .error(R.drawable.sinfoto)
                    .into(holder.campania_imagen);
        } catch (Exception e) {
            Picasso.with(context)
                    .load(R.drawable.sinfoto)
                    .placeholder(R.drawable.ajax_loader)
                    .fit()
                    .into(holder.campania_imagen);
        }
    }

    public void setMG(int mg,int nmg, int position, boolean isMG, boolean isNMG){
        campanias camp = campania_lista.get(position);
        camp.setCantMG(mg);
        camp.setCantNMG(nmg);
        camp.setMG(isMG);
        camp.setNMG(isNMG);
        campania_lista.set(position, camp);
        notifyDataSetChanged();
    }

    public void setFav(boolean fav, int position){
        campanias camp = campania_lista.get(position);
        camp.setFav(fav);
        campania_lista.set(position, camp);
        notifyDataSetChanged();
    }

    public void setValoracion(double valoracion,int valoracionUser, int position){
        campanias camp = campania_lista.get(position);
        camp.setValoracionUser(valoracionUser);
        camp.setValoracion(valoracion);
        campania_lista.set(position, camp);
        notifyDataSetChanged();
    }

    public void setVisto(int position){
        campanias camp = campania_lista.get(position);
        camp.setVisto(true);
        campania_lista.set(position, camp);
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        campania_lista.remove(position);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return campania_lista.size();
    }

    class ViewHolderRecyclerView extends RecyclerView.ViewHolder{

        private TextView titulo_camp;
        private TextView descripcion_camp;
        private TextView distancia;
        private ImageView campania_imagen;
        private RelativeLayout area_camp, area_ver_todas, contFav;
        private ImageView ic_distancia, img_mg, img_nmg, img_fav, img_comp, img_den, img_ver_todas, img_visto;
        private LinearLayout area_social;
        private Button valoracion;
        private TextView txt_mg, txt_nmg, txt_ver_todas, txt_empresa_pagada, txt_visto;

        private Animation fadeIn, fadeOut;
        private AnimationSet animation;

        public ViewHolderRecyclerView(final View itemView, final Context context, final double latitud, final double longitud, final Activity activity){
            super(itemView);

            fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(1000);

            fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator());
            fadeOut.setStartOffset(1000);
            fadeOut.setDuration(1000);

            animation = new AnimationSet(false);
            animation.addAnimation(fadeIn);

            Porcentual porcentual = new Porcentual();
            final int width = porcentual.getScreenWidth(activity);
            final int height = porcentual.getScreenHeight(activity);
            double screenInches= new Porcentual().getScreenInches(activity);
            Typeface type = Typeface.createFromAsset(activity.getAssets(), "roboto_regular.ttf");

            valoracion = (Button) itemView.findViewById(R.id.valoracion_empresa);
            valoracion.setTextSize((float) (screenInches*1.5));
            valoracion.setTypeface(type);
            LinearLayout.LayoutParams paramsValoracion = (LinearLayout.LayoutParams) valoracion.getLayoutParams();
            paramsValoracion.width = (int) (width * 0.05);
            paramsValoracion.height = paramsValoracion.width;
            valoracion.setLayoutParams(paramsValoracion);

            txt_empresa_pagada = (TextView) itemView.findViewById(R.id.textView_empresa_pagada);
            txt_empresa_pagada.setTextSize((float) (screenInches*2.5));
            txt_empresa_pagada.setTypeface(type);

            txt_ver_todas = (TextView) itemView.findViewById(R.id.txt_ver_todas);
            txt_ver_todas.setTextSize((float) (screenInches*2.5));
            txt_ver_todas.setTypeface(type);

            img_ver_todas = (ImageView) itemView.findViewById(R.id.img_ver_todas);
            RelativeLayout.LayoutParams paramsVerTodas = (RelativeLayout.LayoutParams) img_ver_todas.getLayoutParams();
            paramsVerTodas.width = (int) (width*0.05);
            paramsVerTodas.height = (int) (paramsVerTodas.width *1.2);
            img_ver_todas.setLayoutParams(paramsVerTodas);

            img_mg = (ImageView) itemView.findViewById(R.id.social_mg);
            img_nmg = (ImageView) itemView.findViewById(R.id.social_nmg);
            img_fav = (ImageView) itemView.findViewById(R.id.social_fav);
            img_visto = (ImageView) itemView.findViewById(R.id.social_visto);
            img_comp = (ImageView) itemView.findViewById(R.id.social_comp);
            //img_den = (ImageView) itemView.findViewById(R.id.social_denunciar);

            LinearLayout.LayoutParams paramsImages = (LinearLayout.LayoutParams) img_mg.getLayoutParams();
            paramsImages.width = (int) (width*0.06);
            paramsImages.topMargin = (int) (height*0.01);
            paramsImages.leftMargin = (int) (width * 0.07);
            paramsImages.bottomMargin = (int) (height*0.01);
            paramsImages.height = paramsImages.width;
            img_mg.setLayoutParams(paramsImages);
            img_nmg.setLayoutParams(paramsImages);
            img_visto.setLayoutParams(paramsImages);
            img_comp.setLayoutParams(paramsImages);
            //img_den.setLayoutParams(paramsImages);
            RelativeLayout.LayoutParams paramsImagesFav = (RelativeLayout.LayoutParams) img_fav.getLayoutParams();
            paramsImagesFav.width = (int) (width*0.06);
            paramsImagesFav.topMargin = (int) (height*0.01);
            paramsImagesFav.height = paramsImages.width;
            img_fav.setLayoutParams(paramsImagesFav);

            contFav = (RelativeLayout) itemView.findViewById(R.id.contFav);
            RelativeLayout.LayoutParams paramsCont = (RelativeLayout.LayoutParams) contFav.getLayoutParams();
            paramsCont.width = (int) (paramsImagesFav.width*1.5);
            paramsCont.height = (int) (paramsImagesFav.height*1.5);
            paramsCont.leftMargin = (int) -(width * 0.1);
            contFav.setLayoutParams(paramsCont);

            area_social = (LinearLayout) itemView.findViewById(R.id.area_social);

            txt_mg = (TextView) itemView.findViewById(R.id.txt_cantidad_mg);
            txt_mg.setTextSize((float) (screenInches*2.2));
            txt_mg.setTypeface(type);
            txt_nmg = (TextView) itemView.findViewById(R.id.txt_cantidad_nmg);
            txt_nmg.setTextSize((float) (screenInches*2.2));
            txt_nmg.setTypeface(type);

            txt_visto = (TextView) itemView.findViewById(R.id.txt_visto);
            txt_visto.setTextSize((float) (screenInches*2.2));
            txt_visto.setTypeface(type);

            area_ver_todas = (RelativeLayout) itemView.findViewById(R.id.area_ver_todas);

            area_ver_todas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent("android.intent.action.ofertas_empresa"+activity.getString(R.string.ambiente_ego));
                    intent.putExtra("userName", userName);
                    intent.putExtra("token", token);
                    intent.putExtra("idUser", idUser);
                    intent.putExtra("idEmpresa", campania_lista.get(getAdapterPosition()).getIdEmpresa());
                    intent.putExtra("position", getAdapterPosition());
                    intent.putExtra("lat", latitud);
                    intent.putExtra("lon", longitud);
                    campanias.setCampanias(campania_lista.get(getAdapterPosition()));
                    activity.startActivity(intent);
                }
            });

            area_camp = (RelativeLayout) itemView.findViewById(R.id.area_camp);

            area_camp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    campanias campania01;
                    campania01 = campania_lista.get(getAdapterPosition());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            System.runFinalization();
                            Runtime.getRuntime().gc();
                            System.gc();
                        }
                    }).start();
                    Redireccionar_Datos.Cargar_Datos_Contenedor_Campania(getAdapterPosition(), token, idUser, campania01, context, context.getString(R.string.ambiente_ego), latitud, longitud, activity, false);
                }
            });

            img_mg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mg.start();
                    img_nmg.clearAnimation();
                    img_mg.startAnimation(animation);
                    campanias camp = campania_lista.get(getAdapterPosition());
                    if (camp.getMG()) {
                        camp.setMG(false);
                        camp.setCantMG(camp.getCantMG()-1);
                        txt_mg.setText(String.valueOf((Integer.parseInt(txt_mg.getText().toString())-1)));
                        valoracionCamp(getAdapterPosition(), "null", activity);
                        img_mg.setBackground(activity.getResources().getDrawable(R.drawable.social_mg));
                    } else {
                        if (camp.getNMG()) {
                            camp.setNMG(false);
                            camp.setMG(true);
                            camp.setCantMG(camp.getCantMG()+1);
                            camp.setCantNMG(camp.getCantNMG()-1);
                            txt_mg.setText(String.valueOf((Integer.parseInt(txt_mg.getText().toString())+1)));
                            txt_nmg.setText(String.valueOf((Integer.parseInt(txt_nmg.getText().toString())-1)));
                            img_mg.setBackground(activity.getResources().getDrawable(R.drawable.mg_hvr));
                            img_nmg.setBackground(activity.getResources().getDrawable(R.drawable.social_nmg));
                            valoracionCamp(getAdapterPosition(), String.valueOf(true), activity);
                        } else {
                            camp.setMG(true);
                            camp.setCantMG(camp.getCantMG()+1);
                            txt_mg.setText(String.valueOf((Integer.parseInt(txt_mg.getText().toString())+1)));
                            valoracionCamp(getAdapterPosition(), String.valueOf(true), activity);
                            img_mg.setBackground(activity.getResources().getDrawable(R.drawable.mg_hvr));
                        }
                    }
                    campania_lista.set(getAdapterPosition(), camp);
                }
            });

            img_nmg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mg.start();
                    img_mg.clearAnimation();
                    img_nmg.startAnimation(animation);
                    campanias camp = campania_lista.get(getAdapterPosition());
                    if (camp.getNMG()) {
                        camp.setNMG(false);
                        camp.setCantNMG(camp.getCantNMG()-1);
                        txt_nmg.setText(String.valueOf((Integer.parseInt(txt_nmg.getText().toString())-1)));
                        valoracionCamp(getAdapterPosition(), "null", activity);
                        img_nmg.setBackground(activity.getResources().getDrawable(R.drawable.social_nmg));
                    } else {
                        if (camp.getMG()) {
                            camp.setMG(false);
                            camp.setNMG(true);
                            camp.setCantNMG(camp.getCantNMG()+1);
                            camp.setCantMG(camp.getCantMG()-1);
                            txt_nmg.setText(String.valueOf((Integer.parseInt(txt_nmg.getText().toString())+1)));
                            txt_mg.setText(String.valueOf((Integer.parseInt(txt_mg.getText().toString())-1)));
                            img_mg.setBackground(activity.getResources().getDrawable(R.drawable.social_mg));
                            img_nmg.setBackground(activity.getResources().getDrawable(R.drawable.nmg_hvr));
                            valoracionCamp(getAdapterPosition(), String.valueOf(false), activity);
                        } else {
                            camp.setNMG(true);
                            camp.setCantNMG(camp.getCantNMG()+1);
                            txt_nmg.setText(String.valueOf((Integer.parseInt(txt_nmg.getText().toString())+1)));
                            valoracionCamp(getAdapterPosition(), String.valueOf(false), activity);
                            img_nmg.setBackground(activity.getResources().getDrawable(R.drawable.nmg_hvr));
                        }
                    }
                    campania_lista.set(getAdapterPosition(), camp);
                }
            });

            contFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fav.start();
                    img_fav.startAnimation(animation);
                    campanias camp = campania_lista.get(getAdapterPosition());
                    camp.setFav(!camp.getFav());
                    favCamp(getAdapterPosition(), String.valueOf(camp.getFav()), activity, width);
                    if (camp.getFav()) {
                        img_fav.setBackground(activity.getResources().getDrawable(R.drawable.fav_hvr));
                    } else {
                        img_fav.setBackground(activity.getResources().getDrawable(R.drawable.fav));
                    }
                    campania_lista.set(getAdapterPosition(), camp);
                }
            });

            img_comp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    campanias camp = campania_lista.get(getAdapterPosition());
                    img_comp.startAnimation(animation);
                    Bitmap bitmap;
                    itemView.setDrawingCacheEnabled(true);
                    bitmap = Bitmap.createBitmap(itemView.getDrawingCache());
                    itemView.setDrawingCacheEnabled(false);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    File f = new File(Environment.getExternalStorageDirectory() + File.separator + "archivoTemporalGet4u.jpg");
                    try {
                        f.createNewFile();
                        FileOutputStream fo = new FileOutputStream(f);
                        fo.write(bytes.toByteArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/archivoTemporalGet4u.jpg"));
                    intent.putExtra(Intent.EXTRA_TEXT, camp.getNombre_camp()+"\n"+camp.getDesc_campania()+"\nPara mayor informacion visita Hubin.\n" +
                            "http://www.hubin.cl/");
                    activity.startActivity(Intent.createChooser(intent, "Compartir..."));
                }
            });

            /*img_den.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    img_den.startAnimation(animation);
                    Intent intent = new Intent("android.intent.action.denunciar");
                    intent.putExtra("token", token);
                    intent.putExtra("idUser", idUser);
                    intent.putExtra("idCamp", campania_lista.get(getAdapterPosition()).getIdCampania());
                    activity.startActivity(intent);
                }
            });*/

            campania_imagen = (ImageView) itemView.findViewById(R.id.imageView_campania);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) campania_imagen.getLayoutParams();
            layoutParams.width = (int) (width*0.3);
            layoutParams.height = layoutParams.width;
            campania_imagen.setLayoutParams(layoutParams);
            campania_imagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent("android.intent.action.zoom"+activity.getString(R.string.ambiente_ego));
                    intent.putExtra("rutaImagen", campania_lista.get(getAdapterPosition()).getRuta_imagen());
                    activity.startActivity(intent);
                }
            });

            itemView.setMinimumHeight(layoutParams.height);

            titulo_camp = (TextView) itemView.findViewById(R.id.textView_titulo_campania);
            titulo_camp.setTypeface(type);
            titulo_camp.setTextSize((float) (screenInches * 3));
            LinearLayout.LayoutParams paramsTitulo = (LinearLayout.LayoutParams) titulo_camp.getLayoutParams();
            paramsTitulo.rightMargin = (int) (width*0.1);
            titulo_camp.setLayoutParams(paramsTitulo);

            descripcion_camp = (TextView) itemView.findViewById(R.id.textView_descripcion_campania);
            descripcion_camp.setTypeface(type);
            descripcion_camp.setTextSize((float) (screenInches * 2.5));
            RelativeLayout.LayoutParams paramsDesc = (RelativeLayout.LayoutParams) descripcion_camp.getLayoutParams();
            paramsDesc.rightMargin = (int) (width*0.1);
            descripcion_camp.setLayoutParams(paramsDesc);

            ic_distancia = (ImageView) itemView.findViewById(R.id.imageView3);
            RelativeLayout.LayoutParams icParams = (RelativeLayout.LayoutParams) ic_distancia.getLayoutParams();
            icParams.width = (int) (width*0.04);
            icParams.height = (int) (icParams.width * 1.5);
            icParams.topMargin = (int) (height * 0.14);
            icParams.leftMargin = (int) -(width*0.05);

            distancia = (TextView) itemView.findViewById(R.id.textView_distancia);
            distancia.setTypeface(type);
            distancia.setTextSize((float) (screenInches*2));
            RelativeLayout.LayoutParams distanciaParams = (RelativeLayout.LayoutParams) distancia.getLayoutParams();
            distanciaParams.topMargin = (int) (height * 0.14);
        }



        public void valoracionCamp(final int position, String accion, Activity activity)
        {
            ArrayList<Pair<String, String>> headers = new ArrayList<Pair<String, String>>();
            headers.add(new Pair<String, String>("X-ZUMO-AUTH", token));
            ArrayList<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
            params.add(new Pair<String, String>("idUser",String.valueOf(idUser)));
            params.add(new Pair<String, String>("idCamp",campania_lista.get(position).getIdCampania()));
            params.add(new Pair<String, String>("meGusta",accion));
            byte[] aux =new byte[]{};
            try
            {
                MobileServiceClient mClient = new MobileServiceClient(activity.getString(R.string.URL_Mobile_Service), activity);

                try {
                    ListenableFuture<ServiceFilterResponse> result =  mClient.invokeApi("Camps/MeGustaCampByUser",aux, "POST",headers,params);
                    Futures.addCallback(result, new FutureCallback<ServiceFilterResponse>() {
                                @Override
                                public void onSuccess(ServiceFilterResponse result) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(result.getContent());
                                        int megusta = Integer.parseInt(jsonObject.getString("totalMeGusta"));
                                        int nomegusta = Integer.parseInt(jsonObject.getString("totalNoMeGusta"));
                                        campanias camp = campania_lista.get(position);
                                        camp.setCantMG(megusta);
                                        camp.setCantNMG(nomegusta);
                                        txt_mg.setText(String.valueOf(megusta));
                                        txt_nmg.setText(String.valueOf(nomegusta));
                                    } catch (JSONException e) {
                                    }
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    Log.d("prueba", t.getMessage());
                                }
                            }
                    );
                }catch (Exception e){;
                }
            }
            catch(MalformedURLException e) {
            }
        }

        public void favCamp(final int position, final String accion, final Activity activity, final int width)
        {
            ArrayList<Pair<String, String>> headers = new ArrayList<Pair<String, String>>();
            headers.add(new Pair<String, String>("X-ZUMO-AUTH", token));
            ArrayList<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
            params.add(new Pair<String, String>("idUser",String.valueOf(idUser)));
            params.add(new Pair<String, String>("idCamp",campania_lista.get(position).getIdCampania()));
            params.add(new Pair<String, String>("isFav",accion));
            byte[] aux =new byte[]{};
            try
            {
                MobileServiceClient mClient = new MobileServiceClient(activity.getString(R.string.URL_Mobile_Service), activity);

                try {
                    ListenableFuture<ServiceFilterResponse> result =  mClient.invokeApi("Camps/FavCampByUser",aux, "POST",headers,params);
                    Futures.addCallback(result, new FutureCallback<ServiceFilterResponse>() {
                                @Override
                                public void onSuccess(ServiceFilterResponse result) {
                                    Log.d("mas", result.getContent());
                                    campanias camp = campania_lista.get(position);
                                    if(accion.equals("true")) {
                                        camp.setFav(true);
                                    }else{
                                        camp.setFav(false);
                                        if (isFav){
                                            try {
                                                Fragment_activity_categorias_generic fm = (Fragment_activity_categorias_generic) Fragment_activity_categorias_generic.fragment;
                                                fm.changeFav(camp.getIdCampania());
                                            }catch (Exception e){}
                                            removeItem(position);
                                        }
                                    }

                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    campanias camp = campania_lista.get(position);
                                    camp.setFav(!camp.getFav());
                                    if(camp.getFav()){
                                        img_fav.setBackground(activity.getResources().getDrawable(R.drawable.favoritos_hvr));
                                    }else{
                                        img_fav.setBackground(activity.getResources().getDrawable(R.drawable.favoritos));
                                    }
                                    Log.d("mas", t.getMessage());
                                }
                            }
                    );
                }catch (Exception e){;
                }
            }
            catch(MalformedURLException e) {
            }
        }
    }

}
