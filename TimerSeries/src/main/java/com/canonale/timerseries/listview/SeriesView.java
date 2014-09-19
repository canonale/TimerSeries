package com.canonale.timerseries.listview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canonale.timerseries.R;
import com.canonale.timerseries.helper.Cronometro;

/**
 * Created by adrian on 4/08/13.
 */
public class SeriesView extends LinearLayout {

    private TextView nombre_serie;

    public SeriesView(Context context) {
        super(context);
        inflate(context,R.layout.listado_series, this);
        Typeface tfCond = Typeface.createFromAsset(context.getAssets(), "Roboto-BoldCondensed.ttf");

        nombre_serie = (TextView) findViewById(R.id.nombre_serie);
        nombre_serie.setTypeface(tfCond);

    }
    public void setNombreSerie(Cronometro cronometro){
        nombre_serie.setText(cronometro.getNombre());
        if(cronometro.getSeleccion()){
            nombre_serie.setTextColor(getResources().getColorStateList(R.color.rojo_claro));
        }else {
            nombre_serie.setTextColor(getResources().getColorStateList(R.color.gris_25));
        }
    }
}
