package com.canonale.timerseries.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.canonale.timerseries.R;
import com.canonale.timerseries.helper.Cronometro;
import com.canonale.timerseries.helper.DBHelper;
import com.canonale.timerseries.listview.SeriesAdapter;

import java.util.ArrayList;

/**
 * Created by adrian on 19/08/13.
 */
public class ListSeriesDialog extends Dialog {
    public interface ReadyListener {
        public void ready(String name);
    }
    private ListView listView;
    private SeriesAdapter seriesAdapter;
    public String seleccionado = null;
    public int elemento;
    private String name;
    private ReadyListener readyListener;
    private Context context;

    public ListSeriesDialog(Context context, String name,
                            ReadyListener readyListener) {
        super(context, R.style.cust_dialog);
        this.name = name;
        this.readyListener = readyListener;
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listseries_dialog);
        setTitle(R.string.dialogo_select_serie);

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);

        Log.v("DENSIDAD", "Densidad" + metrics.densityDpi);
        switch(metrics.densityDpi){
            case DisplayMetrics.DENSITY_LOW:
                this.getWindow().setLayout(160, 220);
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                this.getWindow().setLayout(220, 300);
                break;
            case DisplayMetrics.DENSITY_HIGH:
                this.getWindow().setLayout(350, 550);
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                this.getWindow().setLayout(450, 550);
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                this.getWindow().setLayout(650,1050);
            default:
                this.getWindow().setLayout(350, 550);
                break;
        }

        actionsActivity();
    }
    private void actionsActivity(){
        final DBHelper dbHelper = new DBHelper(getContext());

        ArrayList<Cronometro> series;
        series = dbHelper.getAll();


        ListView listView = (ListView) findViewById(R.id.listadoSeries);
        seriesAdapter = new SeriesAdapter(series);

        listView.setAdapter(seriesAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String seleccionado = ((Cronometro)adapterView.getAdapter().getItem(i)).getNombre();
                int id = (int) ((Cronometro)adapterView.getAdapter().getItem(i)).getId();
                elemento = (int) ((Cronometro)adapterView.getAdapter().getItem(i)).getId();
                dbHelper.usarSerie(id);
                readyListener.ready(seleccionado);
                ListSeriesDialog.this.dismiss();

            }
        });

    }
}
