package com.egoview.udd.vistas.genericas;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.egoview.udd.R;
import com.egoview.udd.adapter.MenuDerechoAdapter;
import com.egoview.udd.adapter.adapterRecyclerView;
import com.egoview.udd.clases.campanias;
import com.egoview.udd.clases.categorias;
import com.egoview.udd.clases.subcategorias;
import com.egoview.udd.contenedores.generico.ContenedorHomeGeneric;
import com.egoview.udd.procesos.AnalyticsApplication;
import com.egoview.udd.procesos.Conexion;
import com.egoview.udd.procesos.Parseo_Json;
import com.egoview.udd.procesos.Porcentual;
import com.egoview.udd.procesos.Rescate_Datos;
import com.egoview.udd.procesos.providers;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import static android.text.TextUtils.isEmpty;

/**
 * Create for Serial on 24/11/2015 modified on 06/12/2015
 */
public class Fragment_activity_categorias_generic extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private static final String TAG = Fragment_activity_categorias_generic.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private GoogleApiClient mGoogleApiClient;
    private static int UPDATE_INTERVAL = 10000;
    private static int FATEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 10;
    private LocationRequest mLocationRequest;
    public static Fragment fragment;

    private DrawerLayout drawerLayout;
    private LinearLayout btn_categorias;
    private GridView gridLayout;
    private ArrayList<categorias> menu_derecho;

    private boolean flag;
    private MobileServiceClient mClient;
    private RecyclerView recyclerView;
    private View rootView;
    private List<Pair<String, String>> queryParams;
    private Bundle b_categoria;
    private String categoria_num;
    private ListenableFuture<JsonElement> result;
    private ProgressBar progressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SwipeRefreshLayout refresh_null;
    private ArrayList<campanias> campaniaList01 = null, campsFav;
    private ArrayList<categorias> categorias_exa;
    private ArrayList<subcategorias> subcategorias_exa;

    public double latitud=-33.440550;
    public double longitud=-70.650723;
    private LocationManager locationManager;
    private providers mProviders;
    boolean flag_button = false;
    private categorias categoria01 = new categorias();
    private adapterRecyclerView adapter;
    private LinearLayout busqueda_off2, menuDer;
    private PercentRelativeLayout busqueda_on;
    private Button buscador2, btn_cat2, press_favoritos, press_ubicacionRegistrado, press_interes;
    private Button limpiar;
    private EditText text_busqueda;
    private FrameLayout frameLayout;
    private Tracker mTracker;
    private AnalyticsApplication application;
    private String token, userName;
    private long idUser = 0;
    private int radioGPS;
    private MenuDerechoAdapter adapter_menuderecho;
    private ImageView floating;
    private TextView txt_sentimos, txt_encontrado, txt_area;
    private Boolean redSocial, isFav=false;
    private PercentRelativeLayout contUbicacion, contFav;
    private boolean viewComunidad;


    public Fragment_activity_categorias_generic() { /*Required empty public constructor*/}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = this;
        viewComunidad = false;
        if (rootView == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.runFinalization();
                    Runtime.getRuntime().gc();
                    System.gc();
                }
            }).start();

            // [START shared_tracker]
            // Obtain the shared Tracker instance.
            application = (AnalyticsApplication) getActivity().getApplication();
            mTracker = application.getDefaultTracker();
            // [END shared_tracker]

            try {
                token = getActivity().getIntent().getExtras().getString("Token");
                idUser=getActivity().getIntent().getExtras().getLong("idUser");
                userName=getActivity().getIntent().getExtras().getString("userName");
                radioGPS=getActivity().getIntent().getExtras().getInt("radioGPS");
                redSocial=getActivity().getIntent().getExtras().getBoolean("redSocial");
            }catch(Exception e){        }

            Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "roboto_regular.ttf");
            Typeface bold = Typeface.createFromAsset(getActivity().getAssets(), "roboto_bold.ttf");
            Porcentual porcentual = new Porcentual();
            int width = porcentual.getScreenWidth(getActivity());
            int height= porcentual.getScreenHeight(getActivity());
            double screenInches = porcentual.getScreenInches(getActivity());

            contUbicacion = (PercentRelativeLayout) getActivity().findViewById(R.id.contUb);
            contFav = (PercentRelativeLayout) getActivity().findViewById(R.id.contFav);

            menuDer = (LinearLayout) getActivity().findViewById(R.id.menuder);
            menuDer.bringToFront();

            mProviders = new providers();
            locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
            mProviders.providers(token, userName, getActivity().getBaseContext(), getActivity(), locationManager, locationManager, idUser);

            rootView = inflater.inflate(R.layout.activity_fragment_categorias_recicler_view, container, false);
            txt_area = (TextView) getActivity().findViewById(R.id.txt_area);
            txt_area.setTextSize((float) (screenInches*3));
            txt_area.setTypeface(type);
            txt_sentimos = (TextView) getActivity().findViewById(R.id.txt_sentimos);
            txt_sentimos.setTextSize((float) (screenInches * 6));
            txt_sentimos.setTypeface(type);
            txt_encontrado = (TextView) getActivity().findViewById(R.id.txt_encontrado);
            txt_encontrado.setTextSize((float) (screenInches * 3));
            txt_encontrado.setTypeface(type);

            progressBar = (ProgressBar) rootView.findViewById(R.id.progess_layout);
            progressBar.setIndeterminate(false);
            floating = (ImageView) rootView.findViewById(R.id.fab);
            floating.setVisibility(View.GONE);
            RelativeLayout.LayoutParams paramsFloating = (RelativeLayout.LayoutParams) floating.getLayoutParams();
            paramsFloating.width = (int) (width * 0.15);
            paramsFloating.height = paramsFloating.width;
            paramsFloating.setMargins(0,0, (int) (width*0.01), (int) (height*0.05));
            floating.setLayoutParams(paramsFloating);
            floating.bringToFront();
            floating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerView.computeVerticalScrollOffset() < 15000) {
                        recyclerView.smoothScrollToPosition(0);
                    } else {
                        recyclerView.scrollToPosition(0);
                    }
                }
            });
            frameLayout = (FrameLayout) getActivity().findViewById(R.id.contenedor_frame_der);

            categoria01 = categorias.getCategorias();

            buscador2 = (Button) getActivity().findViewById(R.id.press_buscador2);

            btn_cat2 = (Button) getActivity().findViewById(R.id.press_categoria2);

            press_ubicacionRegistrado = (Button) getActivity().findViewById(R.id.press_ub);
            press_ubicacionRegistrado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewComunidad = false;
                    refresh_null.setVisibility(View.GONE);
                    frameLayout.setVisibility(View.VISIBLE);
                    isFav = false;
                    categoria_num = Integer.toString(99);
                    progressBar.setVisibility(View.GONE);
                    mostrar_lista();
                    contFav.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    contUbicacion.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                        drawerLayout.closeDrawer(GravityCompat.END);
                    }
                }
            });
            press_favoritos = (Button) getActivity().findViewById(R.id.press_fav);
            press_favoritos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewComunidad = false;
                    refresh_null.setVisibility(View.GONE);
                    frameLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    isFav = true;
                    getFavoritos();
                    contFav.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    contUbicacion.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                        drawerLayout.closeDrawer(GravityCompat.END);
                    }
                }
            });

            busqueda_on = (PercentRelativeLayout) getActivity().findViewById(R.id.open);
            busqueda_off2 = (LinearLayout) getActivity().findViewById(R.id.zona_buscar_on2);
            limpiar = (Button) getActivity().findViewById(R.id.limpiar);
            text_busqueda = (EditText) getActivity().findViewById(R.id.text_busqueda);
            text_busqueda.setTextSize((float) (screenInches*2.5));
            btn_cat2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!drawerLayout.isDrawerOpen(GravityCompat.END)) {
                        drawerLayout.openDrawer(GravityCompat.END);
                    } else {
                        drawerLayout.closeDrawer(GravityCompat.END);
                    }
                }
            });
            buscador2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    application.sendEvent("main", "opensearch", "open");
                    busqueda_off2.setVisibility(View.GONE);
                    busqueda_on.setVisibility(View.VISIBLE);
                    text_busqueda.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                    text_busqueda.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
                    if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                        drawerLayout.closeDrawer(GravityCompat.END);
                    }
                }
            });
            limpiar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isEmpty(text_busqueda.getText())) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                        busqueda_on.setVisibility(View.GONE);
                            busqueda_off2.setVisibility(View.VISIBLE);
                        application.sendEvent("main", "closesearch", "close");
                        estado_anterior();
                    } else {
                        text_busqueda.setText("");
                    }
                }
            });

            text_busqueda.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        application.sendEvent("main", "gosearch", "go");
                        buscar_txt(String.valueOf(text_busqueda.getText()));
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(text_busqueda.getWindowToken(), 0);
                        return true;
                    }
                    return false;
                }
            });

            try {
                menu_derecho = getCategorias();
            } catch (UnsupportedEncodingException e) {
            }
            categorias categorias01 = new categorias();
            categorias_exa = new ArrayList<categorias>();
            for (int i = 0; i < menu_derecho.size(); i++) {
                categorias aux = new categorias();
                aux.setId_categorias(menu_derecho.get(i).getId_categorias());
                aux.setId(menu_derecho.get(i).getId());
                aux.setDescripcion_campania(menu_derecho.get(i).getDescripcion_campania());
                aux.setTotal_por_categorias(menu_derecho.get(i).getTotal_por_categorias());
                categorias_exa.add(aux);
            }

            gridLayout = (GridView) getActivity().findViewById(R.id.gridview);
            categoria_num = Integer.toString(99);
            double lat = Rescate_Datos.rescatar_latitud(getActivity().getIntent());
            double lon = Rescate_Datos.rescatar_longitud(getActivity().getIntent());
            mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.activity_main_swipe_refresh_layout);
            mSwipeRefreshLayout.setDistanceToTriggerSync(200);
            ViewGroup.LayoutParams paramsCenter = mSwipeRefreshLayout.getLayoutParams();
            paramsCenter.height = (int)(height * 0.68);
            mSwipeRefreshLayout.setLayoutParams(paramsCenter);

            refresh_null = (SwipeRefreshLayout) getActivity().findViewById(R.id.refresh_null);
            refresh_null.setDistanceToTriggerSync(200);
            recyclerView = (RecyclerView) rootView.findViewById(R.id.listado_campanias_rv);
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (recyclerView.computeVerticalScrollOffset() > 1000) {
                        floating.setVisibility(View.VISIBLE);
                    } else {
                        floating.setVisibility(View.GONE);
                    }
                }
            });

            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);

            if (checkPlayServices()) {

                buildGoogleApiClient();

                createLocationRequest();
            }

            dibujar_menu_derecho();
                flag = false;
                    Boolean con_wifi = Conexion.Conexion_Wifi(getActivity());
                    Boolean con_datos = Conexion.Conexion_Datos(getActivity());
                    if (con_datos || con_wifi) {
                        b_categoria = getArguments();
                        ApiGetAllCampain();
                    }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.runFinalization();
                    Runtime.getRuntime().gc();
                    System.gc();
                }
            }).start();
            return rootView;
        } else {
            return rootView;
        }
    }

    public void buscar_txt(String query){
        String delimitadores = "[ .,;?!��\'\"\\[\\]]+";
        String[] palabrasSeparadas = new String[0];
        palabrasSeparadas = query.split(delimitadores);
        final ArrayList<campanias> listcampanias = new ArrayList<>();
        boolean validar;
        if(isFav){
            if (campsFav!=null) {
                for (int j = 0; j < campsFav.size(); j++) {
                    validar = false;
                    for (int m = 0; m < palabrasSeparadas.length; m++) {
                        if (!validar && (deAccent(campsFav.get(j).getDesc_campania().toLowerCase()).contains(deAccent(palabrasSeparadas[m].toLowerCase())) || deAccent(campsFav.get(j).getNombre_camp().toLowerCase()).contains(deAccent(palabrasSeparadas[m].toLowerCase()))) || deAccent(campsFav.get(j).getNombre_empresa().toLowerCase()).contains(deAccent(palabrasSeparadas[m].toLowerCase()))) {
                            listcampanias.add(campsFav.get(j));
                            validar = true;
                        }
                    }
                }
                if (listcampanias.size() != 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mostrar_lista(listcampanias);
                        }
                    }).start();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mostrar_lista(null);
                        }
                    }).start();
                }
            }
        }else {
            for (int i = 0; i < categorias_exa.size(); i++) {
                if (categorias_exa.get(i).getId_categorias() == 999 && categorias_exa.get(i).getCamps() != null) {
                    for (int j = 0; j < categorias_exa.get(i).camps.size(); j++) {
                        validar = false;
                        for (int m = 0; m < palabrasSeparadas.length; m++) {
                            if (!validar && (deAccent(categorias_exa.get(i).camps.get(j).getDesc_campania().toLowerCase()).contains(deAccent(palabrasSeparadas[m].toLowerCase())) || deAccent(categorias_exa.get(i).camps.get(j).getNombre_camp().toLowerCase()).contains(deAccent(palabrasSeparadas[m].toLowerCase()))) || deAccent(categorias_exa.get(i).camps.get(j).getNombre_empresa().toLowerCase()).contains(deAccent(palabrasSeparadas[m].toLowerCase()))) {
                                listcampanias.add(categorias_exa.get(i).camps.get(j));
                                validar = true;
                            }
                        }
                    }
                }
            }
            if (listcampanias.size() != 0) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mostrar_lista(listcampanias);
                    }
                }).start();
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mostrar_lista(null);
                    }
                }).start();
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.runFinalization();
                Runtime.getRuntime().gc();
                System.gc();
            }
        }).start();
    }

    private void dibujar_menu_derecho() {
        drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout_der);
        adapter_menuderecho = new MenuDerechoAdapter(getActivity(), menu_derecho, gridLayout, getContext(), this, token, idUser);
        gridLayout.setAdapter(adapter_menuderecho);
    }

    public void MethodAdapter(int position){
        isFav = false;
        contFav.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        contUbicacion.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                drawerLayout.closeDrawer(GravityCompat.END);
            }
        }, 200);
        if (result != null) {
            result.cancel(true);
        }
        recyclerView.clearAnimation();
        categoria01 = menu_derecho.get(position);
        application.sendEvent("main", "categorychange", "C"+categoria01.getId_categorias());
        categoria_num = "" + categoria01.getId_categorias();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mostrar_lista();
            }
        }).start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gridLayout.setVisibility(View.VISIBLE);
            }
        }, 200);
    }


    public void changeFav(String id_campania){
        for (int i = 0 ; i < categorias_exa.size() ; i++){
            for (int j = 0 ; j < categorias_exa.get(i).getCamps().size() ; j++){
                if(categorias_exa.get(i).getCamps().get(j).getIdCampania().equals(id_campania)){
                    categorias_exa.get(i).getCamps().get(j).setFav(false);
                }
            }
        }
    }

    private ArrayList<categorias> getCategorias() throws UnsupportedEncodingException {
        ArrayList<categorias> base=Rescate_Datos.rescatar_categorias(getActivity().getIntent());
        ArrayList<categorias> cat_return = new ArrayList<>();
        boolean masCategorias=true;
        for(int i=0;i<base.size();i++){
            if(i==0){
                categorias categorias01 = new categorias();
                categorias01.setId(999);
                categorias01.setId_categorias(999);
                categorias01.setDescripcion_campania("Todas las Categorias");
                cat_return.add(categorias01);
            }
            cat_return.add(base.get(i));
        }
        return cat_return;
    }

    private ArrayList<subcategorias> getSubCategorias() throws UnsupportedEncodingException {
        ArrayList<subcategorias> base=Rescate_Datos.rescatar_sub_categorias(getActivity().getIntent());
        ArrayList<subcategorias> subcat_return = new ArrayList<>();
        boolean masCategorias=true;
        for(int i=0;i<base.size();i++){
            if(i==0){
                subcategorias subcategorias01 = new subcategorias();
                subcategorias01.setId(999);
                subcategorias01.setDescripcion_campania("Todas las SubCategorias");
                subcat_return.add(subcategorias01);
            }
            subcat_return.add(base.get(i));
        }
        return subcat_return;
    }

    private void estado_anterior() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.runFinalization();
                Runtime.getRuntime().gc();
                System.gc();
            }
        }).start();
        progressBar.setVisibility(View.GONE);
        recyclerView.clearFocus();
        if(isFav){
            mostrar_lista(campsFav);
        }else {
            double lat = Rescate_Datos.rescatar_latitud(getActivity().getIntent());
            double lon = Rescate_Datos.rescatar_longitud(getActivity().getIntent());
            int categoria = categoria01.getId_categorias();

            flag = false;
            mostrar_lista();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.runFinalization();
                Runtime.getRuntime().gc();
                System.gc();
            }
        }).start();
    }
    public void llamar_refresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.runFinalization();
                Runtime.getRuntime().gc();
                System.gc();
            }
        }).start();
        if (isFav){
            progressBar.setVisibility(View.VISIBLE);
            getFavoritos();
        }else {
            application.sendEvent("main", "refresh", "category");
            if (isEmpty(text_busqueda.getText())) {
                progressBar.setVisibility(View.GONE);
                recyclerView.clearFocus();
                categorias categoria01 = new categorias();
                categoria01 = categorias.getCategorias();
                int categoria = categoria01.getId_categorias();
                String direccion = Rescate_Datos.rescatar_direccion(getActivity().getIntent());
                double lat = Rescate_Datos.rescatar_latitud(getActivity().getIntent());
                double lon = Rescate_Datos.rescatar_longitud(getActivity().getIntent());

                ApiGetAllCampain();
            } else {
                buscar_txt(String.valueOf(text_busqueda.getText()));
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.runFinalization();
                Runtime.getRuntime().gc();
                System.gc();
            }
        }).start();
    }

    @SuppressLint("StaticFieldLeak")
    private void ApiGetAllCampain() {
        progressBar.setVisibility(View.VISIBLE);
        if(idUser==0) {
            queryParams = new ArrayList<Pair<String, String>>();
            queryParams.add(new Pair<String, String>("latitud", String.valueOf(latitud)));
            queryParams.add(new Pair<String, String>("longitud", String.valueOf(longitud)));
            try {
                mClient = new MobileServiceClient(getString(R.string.URL_Mobile_Service), getActivity());

                try {
                    result = mClient.invokeApi("Camps/AllCampsGenericas", "POST", queryParams);
                    Futures.addCallback
                            (result, new FutureCallback<JsonElement>() {
                                        @Override
                                        public void onSuccess(JsonElement result) {
                                            campaniaList01 = Parseo_Json.parseoJsonCampain(result.toString());
                                            ArrayList<campanias> todas = new ArrayList<>();
                                            for (int i = 0; i < categorias_exa.size(); i++) {
                                                ArrayList<campanias> aux = new ArrayList<>();
                                                for (int j = 0; j < campaniaList01.size(); j++) {
                                                    if (campaniaList01.get(j).getId_tipoCampania().equals(String.valueOf(categorias_exa.get(i).getId_categorias()))) {
                                                        aux.add(campaniaList01.get(j));
                                                    }
                                                    if (i == 0) {
                                                        todas.add(campaniaList01.get(j));
                                                    }
                                                }
                                                categorias_exa.get(i).setCamps(aux);
                                                categorias_exa.get(i).setTotal_por_categorias(categorias_exa.get(i).getCamps().size());
                                                if (categorias_exa.get(i).getId_categorias() == 999) {
                                                    categorias_exa.get(i).setCamps(todas);
                                                    categorias_exa.get(i).setTotal_por_categorias(todas.size());
                                                }
                                            }
                                            adapter_menuderecho.setLista(categorias_exa);
                                            adapter_menuderecho.notifyDataSetChanged();
                                            mostrar_lista();
                                            progressBar.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onFailure(Throwable t) {
                                            progressBar.setVisibility(View.GONE);
                                            for(int i=0; i<categorias_exa.size();i++){
                                                categorias_exa.get(i).setCamps(null);
                                                categorias_exa.get(i).setTotal_por_categorias(0);
                                            }
                                            mostrar_lista();
                                        }

                                    }
                            );
                } catch (Exception e) {
                    progressBar.setVisibility(View.GONE);
                }
            } catch (MalformedURLException e) {
                progressBar.setVisibility(View.GONE);
            }
        }else{
            final String[] mensaje = {""};
            new AsyncTask<Object, Object, Object>() {

                @Override
                protected Object doInBackground(Object... params) {
                    mensaje[0] = ApiGetuser();
                    while (mensaje[0] == "") {
                    }
                    campaniaList01 = Parseo_Json.parseoJsonCampain(mensaje[0]);
                    ArrayList<campanias> todas = new ArrayList<>();
                    try {
                        for (int i = 0; i < categorias_exa.size(); i++) {
                            ArrayList<campanias> aux = new ArrayList<>();
                            for (int j = 0; j < campaniaList01.size(); j++) {
                                if (campaniaList01.get(j).getId_tipoCampania().equals(String.valueOf(categorias_exa.get(i).getId_categorias()))) {
                                    aux.add(campaniaList01.get(j));
                                }
                                if (i == 0) {
                                    todas.add(campaniaList01.get(j));
                                }
                            }
                            categorias_exa.get(i).setCamps(aux);
                            categorias_exa.get(i).setTotal_por_categorias(aux.size());
                            if (categorias_exa.get(i).getId_categorias() == 999) {
                                categorias_exa.get(i).setCamps(todas);
                                categorias_exa.get(i).setTotal_por_categorias(todas.size());
                            }
                        }
                    }catch(Exception e){}
                    return "Execute";
                }

                @Override
                protected void onPostExecute(Object result) {
                    mostrar_lista();
                    progressBar.setVisibility(View.GONE);
                    adapter_menuderecho.setLista(categorias_exa);
                    adapter_menuderecho.notifyDataSetChanged();
                }
            }.execute();

        }
    }

    private String ApiGetuser(){
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(getString(R.string.URL_Mobile_Service)+"/api/Camps/AllCampsByUser");
        httpPost.addHeader("X-ZUMO-AUTH", token);
        httpPost.addHeader("ZUMO-API-VERSION","2.0.0");
        httpPost.addHeader("Content-type","application/json");
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            json.put("IdUser",idUser);
            json.put("latitud",latitud);
            json.put("longitud",longitud);
            httpPost.setEntity(new ByteArrayEntity(json.toString().getBytes("ISO-8859-1")));
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException e) {
        }
        try {
            response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String b;
            while ((b = reader.readLine()) != null) {
                stringBuilder.append(b+"\n");
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONArray jsonObject = new JSONArray();
        try {
            jsonObject = new JSONArray(stringBuilder.toString());
            return jsonObject.toString();

        } catch (JSONException e) {
            return stringBuilder.toString();
        }
    }

    public void mostrar_lista(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.runFinalization();
                Runtime.getRuntime().gc();
                System.gc();
            }
        }).start();
        int ubicacion=0;
        for(int i=0; i<categorias_exa.size();i++){
            if(categorias_exa.get(i).getId_categorias()==Integer.parseInt(categoria_num)){
                ubicacion = i;
            }
        }
        if (categorias_exa.get(ubicacion).getCamps()!=null && categorias_exa.get(ubicacion).getCamps().size()!=0){
            flag_button=true;
            adapter = new adapterRecyclerView(isFav, token, idUser,getContext(), latitud, longitud, getActivity(), categorias_exa.get(ubicacion).getCamps(), userName);
            recyclerView.setSelected(true);
            if(recyclerView.getAdapter()!= null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //recyclerView.swapAdapter(adapter,true);
                        recyclerView.setAdapter(adapter);
                        recyclerView.getAdapter().notifyDataSetChanged();
                        refresh_null.setVisibility(View.GONE);
                        frameLayout.setVisibility(View.VISIBLE);
                    }
                });
            }else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(adapter);
                        refresh_null.setVisibility(View.GONE);
                        frameLayout.setVisibility(View.VISIBLE);
                    }
                });
            }
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refresh_null.setVisibility(View.VISIBLE);
                    frameLayout.setVisibility(View.GONE);
                    if(isFav){
                        txt_encontrado.setText("Tu lista de favoritos");
                        txt_area.setText("está vacía.");
                    }else {
                        txt_encontrado.setText("No hemos encontrado");
                        txt_area.setText("información para mostrar.");
                    }
                }
            });
        }
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                llamar_refresh();
            }
        });
        refresh_null.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refresh_null.setVisibility(View.GONE);
                        frameLayout.setVisibility(View.VISIBLE);
                        refresh_null.setRefreshing(false);
                    }
                });
                llamar_refresh();
            }
        });
    }

    public void mostrar_lista(ArrayList<campanias> camps){

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.runFinalization();
                Runtime.getRuntime().gc();
                System.gc();
            }
        }).start();
        if (camps!=null && camps.size()!=0){
            flag_button=true;
            adapter = new adapterRecyclerView(isFav, token, idUser,getContext(), latitud, longitud, getActivity(), camps, userName);
            recyclerView.setSelected(true);
            if(recyclerView.getAdapter()!= null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //recyclerView.swapAdapter(adapter,true);
                        recyclerView.setAdapter(adapter);
                        recyclerView.getAdapter().notifyDataSetChanged();
                        refresh_null.setVisibility(View.GONE);
                        frameLayout.setVisibility(View.VISIBLE);
                    }
                });
            }else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(adapter);
                        refresh_null.setVisibility(View.GONE);
                        frameLayout.setVisibility(View.VISIBLE);
                    }
                });
            }
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refresh_null.setVisibility(View.VISIBLE);
                    frameLayout.setVisibility(View.GONE);
                    if(isFav){
                        txt_encontrado.setText("Tu lista de favoritos");
                        txt_area.setText("está vacía.");
                    }else {
                        txt_encontrado.setText("No hemos encontrado");
                        txt_area.setText("promociones cerca de tu área.");
                    }
                }
            });
        }
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                llamar_refresh();
            }
        });
        refresh_null.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refresh_null.setVisibility(View.GONE);
                        frameLayout.setVisibility(View.VISIBLE);
                        refresh_null.setRefreshing(false);
                    }
                });
                llamar_refresh();
            }
        });
    }

    public String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String aux = pattern.matcher(nfdNormalizedString).replaceAll("");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    public void setMG(int mg, int nmg, int position, boolean isMG, boolean isNMG){
        ((adapterRecyclerView)recyclerView.getAdapter()).setMG(mg, nmg, position, isMG, isNMG);
    }

    public void setFav(boolean fav, int position){
        if(!fav && isFav){
            campsFav.remove(position);
            mostrar_lista(campsFav);
        }else{
            ((adapterRecyclerView)recyclerView.getAdapter()).setFav(fav, position);
        }
    }

    public void setValoracion(double valoracion,int valoracionUser, int position){
        ((adapterRecyclerView)recyclerView.getAdapter()).setValoracion(valoracion, valoracionUser, position);
    }

    public void setVisto(int position){
        ((adapterRecyclerView)recyclerView.getAdapter()).setVisto(position);
    }

    public void setCategorias(ArrayList<categorias> categorias_nuevas, int cond){
        categorias_exa = getCategoriasUpdate(categorias_nuevas, cond);
        adapter_menuderecho.setLista(categorias_exa);
        adapter_menuderecho.notifyDataSetChanged();
        ((ContenedorHomeGeneric)getActivity()).menuLateralAdapter.setCategorias(categorias_nuevas);
        ((ContenedorHomeGeneric)getActivity()).menuLateralAdapter.notifyDataSetChanged();
        llamar_refresh();
    }

    public void setSubCategorias(ArrayList<subcategorias> subcategorias_nuevas){
        subcategorias_exa = getSubCategoriasUpdate(subcategorias_nuevas);
        adapter_menuderecho.setLista2(subcategorias_exa);
        adapter_menuderecho.notifyDataSetChanged();
        ((ContenedorHomeGeneric)getActivity()).menuLateralAdapter.setSubCategorias(subcategorias_nuevas);
        ((ContenedorHomeGeneric)getActivity()).menuLateralAdapter.notifyDataSetChanged();
        llamar_refresh();
    }

    private ArrayList<categorias> getCategoriasUpdate(ArrayList<categorias> categorias_nuevas, int cond){
        ArrayList<categorias> base=categorias_nuevas;
        ArrayList<categorias> cat_return = new ArrayList<>();
        for(int i=0;i<base.size();i++){
            if(i==0){
                categorias categorias01 = new categorias();
                if (cond == 1) {
                    categorias01.setId(999);
                    categorias01.setId_categorias(999);
                    categorias01.setDescripcion_campania("Todas las Sub-Categorias");
                }else{
                    categorias01.setId(999);
                    categorias01.setId_categorias(999);
                    categorias01.setDescripcion_campania("Todas las Categorias");

                }
                cat_return.add(categorias01);
            }
            cat_return.add(base.get(i));
        }
        return cat_return;
    }

    private ArrayList<subcategorias> getSubCategoriasUpdate(ArrayList<subcategorias> subcategorias_nuevas){
        ArrayList<subcategorias> base=subcategorias_nuevas;
        ArrayList<subcategorias> subcat_return = new ArrayList<>();
        for(int i=0;i<base.size();i++){
            if(i==0){
                subcategorias subcategorias01 = new subcategorias();
                subcategorias01.setId(999);
                subcategorias01.setDescripcion_campania("Todas las SubCategorias");
                subcat_return.add(subcategorias01);
            }
            subcat_return.add(base.get(i));
        }
        return subcat_return;
    }


    public void getFavoritos(){
        final String[] mensaje = {""};
        new AsyncTask<Object, Object, Object>() {

            @Override
            protected Object doInBackground(Object... params) {
                mensaje[0] = ApiGetFav();
                while (mensaje[0] == "") {
                }
                campsFav = Parseo_Json.parseoJsonCampain(mensaje[0]);
                return "Execute";
            }

            @Override
            protected void onPostExecute(Object result) {
                if(isFav) {
                    mostrar_lista(campsFav);
                    progressBar.setVisibility(View.GONE);
                }
            }
        }.execute();
    }

    private String ApiGetFav(){
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(getString(R.string.URL_Mobile_Service)+"/api/Camps/ListFavUser");
        httpPost.addHeader("X-ZUMO-AUTH", token);
        httpPost.addHeader("ZUMO-API-VERSION","2.0.0");
        httpPost.addHeader("Content-type","application/json");
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            json.put("IdUser",idUser);
            json.put("latitud",latitud);
            json.put("longitud",longitud);
            httpPost.setEntity(new ByteArrayEntity(json.toString().getBytes("ISO-8859-1")));
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException e) {
        }
        try {
            response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String b;
            while ((b = reader.readLine()) != null) {
                stringBuilder.append(b+"\n");
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONArray jsonObject = new JSONArray();
        try {
            jsonObject = new JSONArray(stringBuilder.toString());
            return jsonObject.toString();

        } catch (JSONException e) {
            return stringBuilder.toString();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        checkPlayServices();

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }

    /**
     * Method to display the location on UI
     * */
    private Location displayLocation() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        Location location = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        Log.i("Location Fused: ", String.valueOf(location));
        if(location!=null) {
            AnalyticsApplication.setLatFused(location.getLatitude());
            AnalyticsApplication.setLonFused(location.getLongitude());
        }
        return location;


    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
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
                .isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        //displayLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
        Runtime.getRuntime().gc();
    }

    protected class GeocodeAPI2 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpGet httpGet = new HttpGet("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + params[0]);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            StringBuilder stringBuilder = new StringBuilder();
            String direccion;

            try {
                response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                int b;
                while ((b = stream.read()) != -1) {
                    stringBuilder.append((char) b);
                }
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = new JSONObject(stringBuilder.toString());
                String status = jsonObject.getString("status");
                String location_type = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getString("location_type");
                direccion = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(1).getString("short_name") + " " +
                        jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(0).getString("short_name") + " " +
                        jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(4).getString("short_name");
                if (status.equals("OK") && !location_type.equals("APPROXIMATE") && !location_type.equals("GEOMETRIC_CENTER")) {
                    return new String(direccion.getBytes("ISO-8859-1"), "UTF-8");
                } else return null;

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }

        }
    }



}

