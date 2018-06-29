package com.egoview.udd.procesos;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Serial on 09/09/2015.
 */
public class DialogEgoView {

    public static ProgressDialog crear_progres_dialog_campanias(Context context) {
        ProgressDialog progressDialog= new ProgressDialog(context);
        progressDialog.setMessage("Cargando...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

    public static void terminar_progrest(ProgressDialog progressDialog){
        progressDialog.dismiss();
        return;
    }
}
