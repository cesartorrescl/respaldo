package com.egoview.udd.procesos;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.percent.PercentRelativeLayout;
import android.view.View;
import android.widget.TextView;

import com.egoview.udd.R;

/**
 * Created by serial_dev on 04/04/2017.
 */

public class footer {

    public void footer(final Activity activity){
        PercentRelativeLayout footer = (PercentRelativeLayout) activity.findViewById(R.id.footer);
        footer.bringToFront();

        Typeface type = Typeface.createFromAsset(activity.getAssets(),"roboto_regular.ttf");
        Typeface bold= Typeface.createFromAsset(activity.getAssets(),"roboto_regular.ttf");
        Porcentual porcentual = new Porcentual();

        TextView serial = (TextView) activity.findViewById(R.id.txt_serial);
        TextView powered = (TextView) activity.findViewById(R.id.txt_powered);

        serial.setTypeface(bold);
        powered.setTypeface(type);

        serial.setTextSize((float) (porcentual.getScreenInches(activity)*2.5));
        powered.setTextSize((float) (porcentual.getScreenInches(activity)*2.2));

        serial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.serial.cl");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                activity.startActivity(intent);
            }
        });

        powered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.egoview.cl");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                activity.startActivity(intent);
            }
        });
    }
}
