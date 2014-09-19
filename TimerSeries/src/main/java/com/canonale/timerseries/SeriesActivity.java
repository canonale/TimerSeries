package com.canonale.timerseries;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.canonale.timerseries.helper.Cronometro;
import com.canonale.timerseries.helper.DBHelper;
import com.canonale.timerseries.listview.SeriesAdapter;
import com.canonale.timerseries.util.Constantes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class SeriesActivity extends SherlockActivity {

    private ListView listView;
    private SeriesAdapter seriesAdapter;
    public String seleccionado = null;
    public int elemento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);
        // Show the Up button in the action bar.
        setupActionBar();
        actionsActivity();
    }
    private void actionsActivity(){
        DBHelper dbHelper = new DBHelper(getApplicationContext());

        ArrayList<Cronometro> series;
        series = dbHelper.getAll();



        listView = (ListView) findViewById(R.id.listadoSeries);
        seriesAdapter = new SeriesAdapter(series);

        listView.setAdapter(seriesAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String seleccionado = ((Cronometro)adapterView.getAdapter().getItem(i)).getNombre();
                elemento = (int) ((Cronometro)adapterView.getAdapter().getItem(i)).getId();
                //usarSerie(elemento);
                editSerie(elemento);

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                seleccionado = ((Cronometro)adapterView.getAdapter().getItem(i)).getNombre();
                elemento = (int) ((Cronometro)adapterView.getAdapter().getItem(i)).getId();
                AlertDialog.Builder builder = new AlertDialog.Builder(SeriesActivity.this);
                builder.setTitle(R.string.dialogo_acciones_series)
                        .setItems(R.array.opciones_series, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        usarSerie(elemento);
                                        break;
                                    case 1:
                                        editSerie(elemento);
                                        break;
                                    case 2:
                                        //accion ="Eliminar serie " + seleccionado;
                                        deleteSerie(elemento);
                                        break;
                                }
                            }
                        });
                builder.create();
                builder.show();
                return false;
            }
        });

    }


    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setLogo(R.drawable.ic_action_logo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.series, menu);
        return super.onCreateOptionsMenu(menu);
    }
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                fixtures();
                break;
            case R.id.menu_nuevo:
                nuevaSerie();
                break;
            case android.R.id.home:
                onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constantes.EDITAR_ACTIVITY_OK){

            switch (resultCode) {
                case RESULT_OK:
                    Toast.makeText(getApplicationContext(),R.string.text_serie_guardada, Toast.LENGTH_SHORT).show();
                    actionsActivity();
                    break;

                /*case RESULT_CANCELED:
                    // Cancelación o cualquier situación de error
                    break;*/
            }
        }
    }

    private void fixtures(){
        final DBHelper dbHelper = new DBHelper(getApplicationContext());
        Cronometro cronometro = null;
        dbHelper.clear();
        String[] nombre = new String[]{"Abdominales", "Pesas", "5x100", "Anaerobico", "Aerobico", "Abdominales", "Pesas", "5x100", "Anaerobico", "Aerobico"};

        for(int x = 1; x < 15;x++){
            Random r = new Random();
            int rnombre = r.nextInt(11 - 1) + 1;
            long rtiempo = r.nextInt(50000 - 2000) + 2000;
            cronometro = new Cronometro(x,nombre[rnombre], rnombre,rtiempo,rtiempo, true, true);
            dbHelper.insert(cronometro);
        }



    }
    private void deleteSerie(final int id){
        final DBHelper dbHelper = new DBHelper(getApplicationContext());



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialogo_series_error_002);
        builder.setMessage(R.string.text_series_error_002);
        builder.setPositiveButton(R.string.text_aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbHelper.delete(id);
                Toast.makeText(getApplicationContext(), R.string.text_borrado_serie, Toast.LENGTH_SHORT).show();
                actionsActivity();
            }
        });
        builder.setNegativeButton(R.string.text_cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              // finish();
            }
        });
        builder.create();
        builder.show();
    }
    private void editSerie(int id){
        Intent intent = new Intent(getApplicationContext(),EditarSerieActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("from", "series");
        startActivityForResult(intent, Constantes.EDITAR_ACTIVITY_OK);
    }
    private void usarSerie(int id){
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        dbHelper.usarSerie(id);
        setResult(RESULT_OK);
        finish();
    }

    private void nuevaSerie(){
        Intent intent = new Intent(getApplicationContext(),EditarSerieActivity.class);
        intent.putExtra("from", "series");
        startActivityForResult(intent, Constantes.EDITAR_ACTIVITY_OK);
    }
}


