package com.egoview.udd.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Intent;


import com.egoview.udd.R;
import com.egoview.udd.clases.categorias;
import com.egoview.udd.clases.subcategorias;
import com.egoview.udd.contenedores.generico.ContenedorHomeGeneric;
import com.egoview.udd.procesos.ObjectSerializer;
import com.egoview.udd.procesos.Parseo_Json;
import com.egoview.udd.procesos.Porcentual;
import com.egoview.udd.vistas.genericas.Fragment_activity_categorias_generic;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Serial Desarrollo on 06-01-2016.
 */
public class MenuDerechoAdapter extends BaseAdapter
{
    protected Activity activity;
    protected ArrayList<categorias> lista;
    protected ArrayList<categorias> lista2;
    private String listaAux;
    protected GridView gridView;
    protected Context context;
    protected Fragment_activity_categorias_generic fragment;
    protected int index_cat=0;
    protected String token;
    protected long idUser;
    public MenuDerechoAdapter(Activity activity, ArrayList<categorias> lista, GridView gridLayout, Context context, Fragment_activity_categorias_generic fragment, String token, long idUser) {
        this.activity = activity;
        this.lista = getLista(lista);
        this.gridView = gridLayout;
        this.context = context;
        this.fragment = fragment;
        this.token = token;
        this.idUser = idUser;
    }
    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    public void setLista(ArrayList<categorias> lista){
        this.lista = getLista(lista);
    }

    public void setLista2(ArrayList<subcategorias> lista2){
    }

    @Override
    public Object getItem(int position){
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(final int position, View contentView, ViewGroup parent)
    {
        View vi = contentView;
        if (contentView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.custom_categoria_derecha, null);
        }
        Porcentual porcentual = new Porcentual();
        int width = porcentual.getScreenWidth(activity);
        int height = porcentual.getScreenHeight(activity);
        double screenInches=  new Porcentual().getScreenInches(activity);

        vi.setMinimumWidth((int) (width*0.25));
        vi.setMinimumHeight((int) (height * 0.1));

        TextView nombre_categoria = (TextView) vi.findViewById(R.id.txt_categoria);
        nombre_categoria.setTextSize((float) (screenInches * 3.0));
        nombre_categoria.setText(lista.get(position).getDescripcion_campania());

        ImageView ir = (ImageView) vi.findViewById(R.id.ir);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ir.getLayoutParams();
        layoutParams.width = (int) (width*0.05);
        layoutParams.height = (int) (layoutParams.width*1.2);
        layoutParams.setMarginEnd((int) (width * 0.05));
        ir.setLayoutParams(layoutParams);

        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position != 0) {
                    invokeApiSubcategoriasUser(position);
                }else{
                    ((Fragment_activity_categorias_generic) fragment).MethodAdapter(position);
                }

            }
        });
        return vi;
    }

    public ArrayList<categorias> getLista(ArrayList<categorias> categoriases){
        ArrayList<categorias> cat_return = new ArrayList<>();
        for(int i=0;i<categoriases.size();i++){
            if(categoriases.get(i).getId_categorias()<11 || categoriases.get(i).getId()==999 || categoriases.get(i).getId()==998) {
                cat_return.add(categoriases.get(i));
            }
        }
        return cat_return;
    }


    private void invokeApiSubcategoriasUser(final int p) {

        ListenableFuture<ServiceFilterResponse> result;

        try {
            ArrayList<Pair<String, String>> headers = new ArrayList<Pair<String, String>>();
            headers.add(new Pair<String, String>("X-ZUMO-AUTH", token));
            ArrayList<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
            params.add(new Pair<String, String>("idUsuario",String.valueOf(idUser)));
            params.add(new Pair<String, String>("idCategoria",String.valueOf(p)));
            byte[] aux =new byte[]{};
            URL url= new URL("https://uddappservice.azurewebsites.net/");
            MobileServiceClient mClient= new MobileServiceClient(url,context);//metodo de conexion a azure (this = contexto)
            try {
                result = mClient.invokeApi("Camps/SubCategoriasByUser",aux, "POST", headers,params);
                Futures.addCallback(result, new FutureCallback<ServiceFilterResponse>() {
                    @Override
                    public void onSuccess(ServiceFilterResponse result) {
                        listaAux = result.getContent().toString();
                        JSONArray jsonArray = new JSONArray();
                        ArrayList<categorias> cat_return = new ArrayList<>();
                        ArrayList<categorias> cat_returnaux = new ArrayList<>();

                        if(index_cat==0){
                            lista2 = lista;
                            try {
                                jsonArray = new JSONArray(listaAux);
                                for (int i=0; i<jsonArray.length();i++){
                                    categorias cat = new categorias();
                                    cat.setId(jsonArray.getJSONObject(i).getLong("id_subcategoria"));
                                    cat.setDescripcion_campania(jsonArray.getJSONObject(i).getString("desc_subcategoria"));
                                    cat_return.add(i, cat);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            fragment.setCategorias(cat_return,1);
                            index_cat=1;
                            //((Fragment_activity_categorias_generic) fragment).MethodAdapter(p);
                        }
                        else{
                            index_cat=0;
                            lista = lista2;
                            lista.remove(0);
                            fragment.setCategorias(lista,0);
                            ((Fragment_activity_categorias_generic) fragment).MethodAdapter(p);
                        }
                    }
                    @Override
                    public void onFailure(Throwable t){
                        Log.d("resultado222",t.toString());
                    }
                });
            }catch (Exception e){
                Log.d("error",e.getMessage());
            }
        }catch(MalformedURLException e){
            Log.d("error",e.getMessage());
        }
    }


}