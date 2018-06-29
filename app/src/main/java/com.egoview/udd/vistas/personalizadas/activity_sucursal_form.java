package com.egoview.udd.vistas.personalizadas;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.percent.PercentRelativeLayout;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.egoview.udd.R;
import com.egoview.udd.clases.campanias;
import com.egoview.udd.clases.categorias;
import com.egoview.udd.clases.comunas;
import com.egoview.udd.clases.direccion;
import com.egoview.udd.contenedores.generico.ContenedorHomeGeneric;
import com.egoview.udd.procesos.AnalyticsApplication;
import com.egoview.udd.procesos.Parseo_Json;
import com.egoview.udd.procesos.Porcentual;
import com.egoview.udd.procesos.footer;
import com.google.android.gms.analytics.Tracker;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ernesto on 04/08/2016.
 */
public class activity_sucursal_form extends Activity {

    private EditText direccion_sucursal, numero_sucursal;
    private AutoCompleteTextView comuna_sucursal;
    private EditText titulo_camp, descripcion_camp, condiciones_uso;
    private TextView tu_publicacion, imagen, publicar, titulo_camp_prev, desc_camp_prev, txtSuperior, caduca;
    private Bitmap imagen_camp;
    private MobileServiceClient mClient;
    private ImageView tomar_foto, foto_galeria, imagen_get;
    private PercentRelativeLayout volver;
    private direccion dir_sucursal;
    private Activity activity=this;
    private LinearLayout enviar_camp, contenedor;
    private Uri uri_envio;
    private String url_imagen;
    private URI blobUri;
    private StorageCredentials credencial;
    private ImageView imagenCamp;
    private ArrayList<comunas> lista_comunas;
    private ProgressDialog dialog;
    private MediaPlayer exito;

    private PercentRelativeLayout contenedor_publicacion;

    public static Activity achg;

    private int idComuna;
    private String nombreImagen, rutaImagen;
    private String mensajeVacios="";

    private String token;
    private long idUser;
    private String userName;
    private AnalyticsApplication application;
    private Tracker mTracker;

    private ArrayList<categorias> categoriases;
    private List<String> txt_cat = new ArrayList<String>();

    private ContenedorHomeGeneric homeGeneric;

    protected void onCreate(Bundle savedInstanceState) {
        achg=this;
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sucursal_form);
        footer foot = new footer();
        foot.footer(this);

        exito = MediaPlayer.create(this, R.raw.publicacion);

        application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        application.sendScreen("Publicar");

        try{
            apigetcomunas();
            token = getIntent().getExtras().getString("Token");
            idUser=getIntent().getExtras().getLong("idUser");
            userName = getIntent().getExtras().getString("userName");
            homeGeneric = new ContenedorHomeGeneric();
        }catch (Exception e){
        }
        Typeface type = Typeface.createFromAsset(getAssets(),"roboto_regular.ttf");
        Typeface bold= Typeface.createFromAsset(getAssets(),"roboto_bold.ttf");

        imagen_get = (ImageView) findViewById(R.id.imagen_get);
        contenedor_publicacion = (PercentRelativeLayout) findViewById(R.id.contenedor_publicacion);

        txtSuperior = (TextView) findViewById(R.id.txtSuperior);
        caduca = (TextView) findViewById(R.id.txt_vista_previa);

        titulo_camp = (EditText) findViewById(R.id.titulo);
        descripcion_camp = (EditText) findViewById(R.id.descripcion);
        direccion_sucursal = (EditText) findViewById(R.id.direccion);
        numero_sucursal = (EditText) findViewById(R.id.numero_direccion);
        comuna_sucursal = (AutoCompleteTextView) findViewById(R.id.comuna);
        condiciones_uso = (EditText) findViewById(R.id.condiciones);

        volver = (PercentRelativeLayout) findViewById(R.id.volver);
        contenedor = (LinearLayout) findViewById(R.id.contenedor_form);

        tu_publicacion = (TextView) findViewById(R.id.txt_tu_publicacion);
        imagen = (TextView) findViewById(R.id.agregarImagen);
        publicar = (TextView) findViewById(R.id.txt_publicar);
        enviar_camp = (LinearLayout) findViewById(R.id.enviar_camp);
        tomar_foto = (ImageView) findViewById(R.id.foto);
        foto_galeria = (ImageView) findViewById(R.id.galeria);

        imagenCamp = (ImageView) findViewById(R.id.imageView_campania);
        titulo_camp_prev = (TextView) findViewById(R.id.textView_titulo_campania);
        desc_camp_prev = (TextView) findViewById(R.id.textView_descripcion_campania);

        Porcentual porcentual = new Porcentual();
        double screen = porcentual.getScreenInches(this);

        tu_publicacion.setTypeface(bold);
        imagen.setTypeface(type);
        publicar.setTypeface(bold);
        titulo_camp_prev.setTypeface(type);
        desc_camp_prev.setTypeface(type);
        txtSuperior.setTypeface(type);
        caduca.setTypeface(type);

        tu_publicacion.setTextSize((float) (screen*3));
        imagen.setTextSize((float) (screen*2.7));
        publicar.setTextSize((float) (screen*3.5));
        titulo_camp_prev.setTextSize((float) (screen*3));
        desc_camp_prev.setTextSize((float) (screen*2.5));
        txtSuperior.setTextSize((float) (screen*2.5));
        caduca.setTextSize((float) (screen*2.2));

        LinearLayout.LayoutParams contenedorPLayout = (LinearLayout.LayoutParams) contenedor_publicacion.getLayoutParams();
        contenedorPLayout.width = (int) (porcentual.getScreenWidth(this));
        contenedorPLayout.height = (int) (porcentual.getScreenHeight(this)*0.07);
        contenedor_publicacion.setLayoutParams(contenedorPLayout);

        final LinearLayout.LayoutParams enviarLayout = (LinearLayout.LayoutParams) enviar_camp.getLayoutParams();
        enviarLayout.width = (int) (porcentual.getScreenWidth(this)*0.5);
        enviarLayout.height = (int) (porcentual.getScreenHeight(this)*0.07);
        enviarLayout.setMargins(0, (int) (porcentual.getScreenHeight(this)*0.02),0,(int) (porcentual.getScreenHeight(this)*0.01));
        enviar_camp.setLayoutParams(enviarLayout);

        LinearLayout.LayoutParams galeriaLayout = (LinearLayout.LayoutParams) foto_galeria.getLayoutParams();
        galeriaLayout.width = (int) (porcentual.getScreenWidth(this)*0.05);
        galeriaLayout.height = (int) (galeriaLayout.width*2);
        galeriaLayout.setMargins(0, 0, (int) (porcentual.getScreenWidth(this)*0.04),0);
        foto_galeria.setLayoutParams(galeriaLayout);

        LinearLayout.LayoutParams tomarLayout = (LinearLayout.LayoutParams) tomar_foto.getLayoutParams();
        tomarLayout.width = (int) (porcentual.getScreenWidth(this)*0.05);
        tomarLayout.height = (int) (galeriaLayout.width*2);
        tomarLayout.setMargins(0, 0,(int) (porcentual.getScreenWidth(this)*0.04),0);
        tomar_foto.setLayoutParams(tomarLayout);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imagenCamp.getLayoutParams();
        layoutParams.width = (int) (porcentual.getScreenWidth(this)*0.3);
        layoutParams.height = layoutParams.width;
        imagenCamp.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams layoutImagen = (RelativeLayout.LayoutParams) imagen_get.getLayoutParams();
        layoutImagen.width = (int) (porcentual.getScreenWidth(this)*0.6);
        layoutImagen.height = (int) (porcentual.getScreenHeight(this)*0.15);
        imagen_get.setLayoutParams(layoutImagen);

        ScrollView.LayoutParams contenedorLayout = (ScrollView.LayoutParams) contenedor.getLayoutParams();
        contenedorLayout.setMargins((int) (porcentual.getScreenWidth(this)*0.1),0,(int) (porcentual.getScreenWidth(this)*0.1),0);
        contenedor.setLayoutParams(contenedorLayout);

        ScrollView scroll = (ScrollView) findViewById(R.id.scrollCont);
        LinearLayout.LayoutParams sParams = (LinearLayout.LayoutParams) scroll.getLayoutParams();
        sParams.height = (int)(porcentual.getScreenWidth(this)*1.22);
        scroll.setLayoutParams(sParams);

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

        enviar_camp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarCampoes()) {

                            dialog = new ProgressDialog(activity);
                            dialog.setCancelable(false);
                            dialog.setMessage("Tu publicación se está cargando...");
                            dialog.show();

                            Intent intent = new Intent("android.intent.action.creando_camp"+getString(R.string.ambiente_ego));
                            intent.putExtra("idUser",idUser);
                            intent.putExtra("token",token);
                            intent.putExtra("inicio_dir", direccion_sucursal.getText().toString());
                            intent.putExtra("numero_dir", numero_sucursal.getText().toString());
                            intent.putExtra("comuna_dir", comuna_sucursal.getText().toString());
                            intent.putExtra("idComuna",idComuna);
                            campanias camp = new campanias();
                            camp.setNombre_empresa(userName);
                            camp.setCondiciones_uso(condiciones_uso.getText().toString());
                            camp.setDesc_campania(descripcion_camp.getText().toString());
                            camp.setId_tipoCampania(String.valueOf(0));
                            camp.setNombre_camp(titulo_camp.getText().toString());
                            intent.putExtra("camp",(campanias) camp);
                            intent.putExtra("nombreImagen", nombreImagen);
                            intent.putExtra("uri",(Uri) uri_envio);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                            .setTitle("Por favor, ingrese los siguientes datos:")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    mensajeVacios = "";
                                }
                            })
                            .setMessage(mensajeVacios);
                    builder.create().show();
                }
            }
        });

        comuna_sucursal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lista_comunas==null){
                    AlertDialog.Builder comunas_error = new AlertDialog.Builder(activity)
                            .setTitle("Get4U")
                            .setMessage("Ha ocurrido una problema, espere un momento o vuelva atras para intentar nuevamente. Disculpe las molestias.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    comunas_error.create().show();
                }
            }
        });

        titulo_camp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    titulo_camp_prev.setText(titulo_camp.getText().toString());
                }
            }
        });

        descripcion_camp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    desc_camp_prev.setText(descripcion_camp.getText().toString());
                }
            }
        });
        contenedor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    if(titulo_camp.isFocused()){
                        Rect outRect = new Rect();
                        titulo_camp.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                            titulo_camp.clearFocus();
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                        titulo_camp_prev.setText(titulo_camp.getText().toString());
                    }
                    if(descripcion_camp.isFocused()){
                        Rect outRect = new Rect();
                        descripcion_camp.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                            descripcion_camp.clearFocus();
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                        desc_camp_prev.setText(descripcion_camp.getText().toString());
                    }
                }
                return false;
            }
        });
        foto_galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                99);
                    }else{
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"),1);
                    }
                }else{
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"),1);
                }
            }
        });
        tomar_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {

                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                99);
                    }else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
                        startActivityForResult(intent, 5);
                    }
                }else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
                    startActivityForResult(intent, 5);
                }
            }
        });
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    public boolean validarComuna(){
        boolean state = false;
        if(lista_comunas!=null) {
            for (int i = 0; i < lista_comunas.size(); i++) {
                if (lista_comunas.get(i).getmDescomuna().equals(comuna_sucursal.getText().toString())) {
                    idComuna = lista_comunas.get(i).getmIdcomuna();
                    state = true;
                }
            }
        }
        return  state;
    }

    public boolean validarCampoes(){
        boolean state = false;
        if(!titulo_camp.getText().toString().equals("") && !descripcion_camp.getText().toString().equals("") && !condiciones_uso.getText().toString().equals("")
                && !direccion_sucursal.getText().toString().equals("") && !numero_sucursal.getText().toString().equals("") && !comuna_sucursal.getText().toString().equals("") && validarComuna()){
            state = true;
        }else{
            if(titulo_camp.getText().toString().equals("")){
                mensajeVacios = mensajeVacios + "Titulo\n";
            }
            if(descripcion_camp.getText().toString().equals("")){
                mensajeVacios = mensajeVacios + "Descripción\n";
            }
            if(direccion_sucursal.getText().toString().equals("") || numero_sucursal.getText().toString().equals("")){
                mensajeVacios = mensajeVacios + "Dirección y número\n";
            }
            if(!validarComuna()) {
                mensajeVacios = mensajeVacios + "Comuna\n";
            }
            if(condiciones_uso.getText().toString().equals("")){
                mensajeVacios = mensajeVacios + "Condiciones de uso\n";
            }
        }
        return state;
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
    public void onActivityResult(int reqCode, int resCode, Intent data)
    {
        if(resCode==RESULT_OK)
        {
            if(reqCode==1) {
                Uri uri = data.getData();
                application.sendEvent("Publicar", "addImagenGaleria", "correcto");
                CropImage.activity(uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setFixAspectRatio(true)
                        .setAspectRatio(1,1)
                        .setMinCropResultSize(300,300)
                        .start(this);
            }
            if (reqCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                System.runFinalization();
                Runtime.getRuntime().gc();
                System.gc();
                if (resCode == RESULT_OK) {
                    final Uri resultUri = result.getUri();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inDither = false;
                    options.inPurgeable = true;
                    options.inInputShareable = true;
                    options.inSampleSize = 1;
                    options.inScaled = true;
                    options.inTempStorage = new byte[32*1024];
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap scale = BitmapFactory.decodeFile(resultUri.getPath(), options);
                    Bitmap tres = Bitmap.createScaledBitmap(scale,300,300,true);
                    final Uri tempUri = getImageUri(getApplicationContext(), tres);
                    uri_envio = Uri.parse(getRealPathFromURI(tempUri));
                    imagenCamp.setImageBitmap(tres);
                } else if (reqCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }

            if(reqCode==5){
                Uri uri = data.getData();
                if(uri==null){
                    application.sendEvent("Publicar", "addImagenCam", "correcto");
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"),1);
                }else {
                    application.sendEvent("Publicar", "addImagenCam", "correcto");
                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setFixAspectRatio(true)
                            .setAspectRatio(1, 1)
                            .setMinCropResultSize(300, 300)
                            .start(this);
                }
            }
        }
    }

    private void apigetcomunas(){
        ArrayList<Pair<String,String>> queryParams = new ArrayList<Pair<String, String>>();
        queryParams.add(new Pair<String, String>("idCiudad", ""));
        queryParams.add(new Pair<String, String>("todasComunas", "true"));
        try
        {
            mClient = new MobileServiceClient(getString(R.string.URL_Mobile_Service), this);
            try {
                ListenableFuture<JsonElement> result_comunas = mClient.invokeApi("Manage/ListaComunas", "POST", queryParams);
                Futures.addCallback(result_comunas, new FutureCallback<JsonElement>() {
                    @Override
                    public void onSuccess(JsonElement result) {
                        lista_comunas = Parseo_Json.parseoJsonComunas(result.toString());
                        String[] comunas = new String[lista_comunas.size()];
                        for(int i=0; i<lista_comunas.size(); i++){
                            comunas[i]=lista_comunas.get(i).getmDescomuna();
                        }
                        ArrayAdapter<String> adapterAuto = new ArrayAdapter<String>(activity,R.layout.list_comunas_view,comunas);
                        comuna_sucursal.setAdapter(adapterAuto);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        apigetcomunas();
                    }
                });
            }
            catch (Exception e){
            }
        }
        catch(MalformedURLException e){
        }
    }

    public void exito(final long idCamp){
        dialog.dismiss();
        exito.start();
        application.sendEvent("Publicar", "Publicar", "correcto");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(userName)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(homeGeneric.achg!=null) {
                            ((ContenedorHomeGeneric) homeGeneric.achg).setPublicacion(true, idCamp, null);
                        }
                        finish();
                    }
                })
                .setMessage(" Tu publicación se ha cargado");
        builder.create().show();
    }

    public void errorDireccion(){
        dialog.dismiss();
        application.sendEvent("Publicar", "Publicar", "fallido");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle("Error")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setMessage("No se ha encontrado la dirección.");
        builder.create().show();
    }

    public void errorImagen(){
        dialog.dismiss();
        application.sendEvent("Publicar", "Publicar", "fallido");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle("Error")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setMessage("Se ha producido un error con la imagen. Inténtalo nuevamente.");
        builder.create().show();
    }

    public void errorServicio(){
        dialog.dismiss();
        application.sendEvent("Publicar", "Publicar", "fallido");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle("Error")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setMessage("Sé ha producido un error. Inténtalo nuevamente.");
        builder.create().show();
    }



}
