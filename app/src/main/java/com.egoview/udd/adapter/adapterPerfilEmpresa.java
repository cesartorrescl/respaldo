package com.egoview.udd.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.egoview.udd.R;
import com.egoview.udd.clases.campanias;
import com.egoview.udd.procesos.OnSwipeTouchListener;
import com.egoview.udd.procesos.Porcentual;
import com.egoview.udd.procesos.Redireccionar_Datos;

import java.util.ArrayList;

/**
 * Created by Serial Desarrollo on 11-01-2016.
 */
public class adapterPerfilEmpresa extends RecyclerView.Adapter<adapterPerfilEmpresa.ViewHolderRecyclerView>{

    private LayoutInflater layoutInflater;
    private static ArrayList<campanias> campania_lista;
    private static boolean isFav;
    private Context context;
    private double latitud;
    private double longitud;
    private Activity activity;
    public static String token;
    public static long idUser;


    public adapterPerfilEmpresa(boolean isFav, String token, long idUser, Context context, double latitud, double longitud, Activity activity, ArrayList<campanias> campania_lista){
        layoutInflater = LayoutInflater.from(context);
        this.campania_lista = campania_lista;
        this.context = context;
        this.latitud = latitud;
        this.longitud = longitud;
        this.activity = activity;
        this.idUser = idUser;
        this.token = token;
        this.isFav = isFav;
    }

    @Override
    public ViewHolderRecyclerView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.content_campsactivas, parent, false);
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
        String html= "<html lang=\"en\">\n" +
                "<head>\n" +
                "                <meta charset=\"UTF-8\">\n" +
                "                <meta name=\"viewport\" content=\"width=390, initial-scale=1\">\n" +
                "</head>\n" +
                "<body>"+camp.getHtmlCampArrayList().get(0).getCod_html().replace("${mts}",String.valueOf((int) camp.getMdistancia()))+"</body>\n" +
                "</html>";
        holder.webView.loadData(html, "text/html; charset=utf-8", "utf-8");
    }

    @Override
    public int getItemCount() {
        return campania_lista.size();
    }

    class ViewHolderRecyclerView extends RecyclerView.ViewHolder{

        private WebView webView;
        private RelativeLayout contenedor;

        public ViewHolderRecyclerView(final View itemView, final Context context, final double latitud, final double longitud, final Activity activity){
            super(itemView);

            Porcentual porcentual = new Porcentual();
            final int width = porcentual.getScreenWidth(activity);
            final int height = porcentual.getScreenHeight(activity);
            itemView.setMinimumWidth(width);

            contenedor = (RelativeLayout) itemView.findViewById(R.id.contenedorCampEmpresa);
            RecyclerView.LayoutParams paramsContenedor = (RecyclerView.LayoutParams) contenedor.getLayoutParams();
            paramsContenedor.width = width;
            contenedor.setLayoutParams(paramsContenedor);

            webView = (WebView) itemView.findViewById(R.id.webview_oferta);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) webView.getLayoutParams();
            params.setMargins((int) (width*0.2),(int) (width*0.1),0,0);
            params.width = (int) (width*0.7);
            params.height = (int) (height * 0.4);
            webView.setLayoutParams(params);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.setInitialScale(1);
            webView.setOnTouchListener(new OnSwipeTouchListener(activity){
                public void onSimpleClick(float x, float y) {
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
        }
    }

}
