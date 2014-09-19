package com.canonale.timerseries;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.canonale.timerseries.dialogs.ListSeriesDialog;
import com.canonale.timerseries.helper.Cronometro;
import com.canonale.timerseries.helper.DBHelper;
import com.canonale.timerseries.helper.SoundManager;
import com.canonale.timerseries.util.Constantes;
import com.canonale.timerseries.util.CountDownTimerWithPause;

import java.util.Locale;

public class MainActivity extends SherlockActivity {

    TextView txtSeriesName;
    TextView txtSeriesTime;
    TextView txtSeriesMil;
    ProgressBar progressBarPrincipal;
    ProgressBar progressBarDescanso;
    ProgressBar progressBarSeries;

    TextView txtDescanso;
    TextView txtDescansoTime;
    TextView txtDescansoMilesimas;
    TextView txtSerie;
    TextView txtSerieNumero;
    ToggleButton btnPrincipal;
    private long TIEMPO;// = 10000;
    private long DESCANSO;// = 5000;
    private int PERIODO;//  = 7;
    private int series; // = PERIODO;
    private boolean SEGUIDO;  // = true;
    private boolean vibrate_flag;

    private long[] pattern_descanso = {0,Constantes.DOT, Constantes.SHORT_GAP,
            Constantes.DOT, Constantes.SHORT_GAP, Constantes.DOT };
    private long[] pattern_serie = {0, Constantes.DASH, Constantes.SHORT_GAP,
            Constantes.MEDIUM_GAP, Constantes.SHORT_GAP};

    public CountDownTimerWithPause cd;
    public TextView tmpDescanso;
    public Handler handler = new Handler();
    private SoundManager soundManager;
    private int descanso_es, fin_es, serie_es;
    private String type_sound;

    private SharedPreferences prefs;
    private boolean vibracion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFixtures();
        pullSistema();
    }

    private void initFixtures(){
        final DBHelper db = new DBHelper(getApplicationContext());
        String locale = Locale.getDefault().getLanguage();
        final String[] nombres = new String[3];
        final Cronometro[] c = {null};
        if (db.length() == 0){

            if (locale == "en" ) {
                nombres[0] = "Abdominals";
                nombres[1] = "Aerobic Training";
                nombres[2] = "Series 4x100";

            } else{
                nombres[0] = "Abdominales";
                nombres[1] = "Entreno Aerobico";
                nombres[2] = "Series 4x100";
            }
            final int[] series = {5, 3, 4};
            final long[] tiempo = {20000, 1200000,300000};
            final long[] descanso= {5000, 300000,300000};
            final boolean[] auto = {false, true,true};
            final boolean[] seleccion={true,false,false};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.loadfixturesTitle);
            builder.setMessage(R.string.loadfixtures);
            builder.setNegativeButton(R.string.text_cancelar, null);
            builder.setPositiveButton(R.string.text_aceptar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        for (i = 0; i < nombres.length; i++){
                            c[0] = new Cronometro();
                            c[0].setNombre(nombres[i]);
                            c[0].setSeries(series[i]);
                            c[0].setTiempo(tiempo[i]);
                            c[0].setDescanso(descanso[i]);
                            c[0].setAuto(auto[i]);
                            c[0].setSeleccion(seleccion[i]);
                            db.insert(c[0]);

                            c[0] = null;
                        }
                    } catch (Exception e){
                        Log.d(Constantes.ERROR_CARGA_POR_DEFECTO, "Excepcion: " + e);
                    } finally {
                        Toast.makeText(getApplicationContext(), R.string.carga_datos_completada, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            builder.create();
            builder.show();

        }
    }

    private void pullSistema(){
        setupActionBar();
        setupFieldsAparience();
        usarSerie();
        actulizacionSeries(0);
        actualizacionDescanso(DESCANSO);
        countDownSerie();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_action_logo);
    }

    private void setupFieldsAparience(){
        txtSeriesName = (TextView) findViewById(R.id.txtSeriesName);
        txtSeriesTime = (TextView) findViewById(R.id.txtSerieTime);
        txtSeriesMil = (TextView) findViewById(R.id.txtSeriesMilesima);
        txtDescanso = (TextView) findViewById(R.id.txtDescanso);
        txtDescansoTime = (TextView) findViewById(R.id.txtDescansoTime);
        txtDescansoMilesimas = (TextView) findViewById(R.id.txtDescansoMilesimas);
        txtSerie = (TextView) findViewById(R.id.txtSerie);
        txtSerieNumero = (TextView) findViewById(R.id.txtSerieNumero);
        btnPrincipal = (ToggleButton) findViewById(R.id.btnPrincipal);
        progressBarSeries = (ProgressBar) findViewById(R.id.progressBar2);
        Typeface tfThin = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
        txtSeriesName.setTypeface(tfThin);
        txtSeriesTime.setTypeface(tfThin);
        txtSeriesMil.setTypeface(tfThin);
        txtDescanso.setTypeface(tfThin);
        txtDescansoTime.setTypeface(tfThin);
        txtDescansoMilesimas.setTypeface(tfThin);
        txtSerie.setTypeface(tfThin);
        txtSerieNumero.setTypeface(tfThin);
        progressBarPrincipal = (ProgressBar) findViewById(R.id.progressBarPrincipal);
        progressBarDescanso = (ProgressBar) findViewById(R.id.progressBar);
        soundManager = new SoundManager(getApplicationContext());
        prefs = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.this);

        type_sound = prefs.getString("sonido", "digital");
        vibracion = prefs.getBoolean("vibracion", true);

        Log.e("PREF", "vibracion: " + vibracion);
        int ejercicio = getResources().getIdentifier(type_sound + "_ejercicio", "raw", getPackageName());
        int finals = getResources().getIdentifier(type_sound + "_final", "raw", getPackageName());
        int serie = getResources().getIdentifier(type_sound + "_serie", "raw", getPackageName());

        descanso_es = soundManager.load(ejercicio);
        fin_es = soundManager.load(finals);
        serie_es = soundManager.load(serie);



        txtSeriesName.setOnClickListener(new ClickOnName());
    }
    private void usarSerie(){
        DBHelper db = new DBHelper(this);
        Cronometro cronometro = null;
        cronometro = db.getSeleccion();
        if (cronometro != null){
            TIEMPO = cronometro.getTiempo();
            DESCANSO = cronometro.getDescanso();
            PERIODO = cronometro.getSeries();
            SEGUIDO = cronometro.getAuto();
            series = PERIODO;
            txtSeriesName.setText(cronometro.getNombre());
        } else{
            TIEMPO = DESCANSO = PERIODO = series = PERIODO = 0;
        }
        progressBarPrincipal.setMax((int) TIEMPO);
        progressBarDescanso.setMax((int) DESCANSO);
        progressBarSeries.setMax(series);
    }
    private void resetBoton(){
        btnPrincipal.setOnClickListener(null);
        btnPrincipal.setOnLongClickListener(null);
    }
    private void countDownSerie(){
        actualizacion(TIEMPO);
        updateSeries();
        resetBoton();
        actualizacionDescanso(DESCANSO);
        cd = null;
        cd = new CountDownTimerWithPause(TIEMPO, 1, true) {
            public void onTick(long millis) { actualizacion(millis); }
            public void onFinish() { resetContador(); if(DESCANSO > 0) countDownDescanso();}
        };

        if(((PERIODO - series) > 1) && (SEGUIDO) && (cd.isRunning()))
        {
            cd.create();
            btnPrincipal.setChecked(true);
        }



        btnPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cd.timeLeft() == 0){
                    cd.create();
                }
                else {
                    if (cd.isRunning())cd.pause();
                    else cd.resume();
                }
            }
        });

        btnPrincipal.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                cd.cancel();
                resetAll();
                Toast.makeText(getApplicationContext(), R.string.reiniciado_tiempo, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
    private void countDownDescanso(){
        btnPrincipal = (ToggleButton) findViewById(R.id.btnPrincipal);

        long tiempo = DESCANSO;

        resetBoton();

        final CountDownTimerWithPause cdx = new CountDownTimerWithPause(tiempo, 1, true) {
            public void onTick(long millis) { actualizacionDescanso(millis); }
            public void onFinish() { resetDescanso();if(series > 0) countDownSerie(); else updateSeries(); }

        };

        cdx.create();

        btnPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cdx.isRunning())cdx.pause();
                else cdx.resume();
            }
        });

        btnPrincipal.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                cdx.cancel();
                //countDownSerie();
                resetAll();
                Toast.makeText(getApplicationContext(), R.string.reiniciado_tiempo, Toast.LENGTH_SHORT).show();

                return true;
            }
        });




    }
    public void actualizacion(long millis){
        long border = 0;
        int mil  = (int) millis % 1000;
        int sec  = (int)(millis/ 1000) % 60 ;
        int min  = (int)((millis/ (1000*60)) % 60);
        int hr   = (int)((millis/ (1000*60*60)) % 24);
        int progresso = (int) (TIEMPO - millis);
        txtSeriesTime.setText(String.format("%02d:%02d:%02d", hr, min, sec));
        txtSeriesMil.setText(String.format(".%03d", mil));
        progressBarPrincipal.setProgress(progresso);
        for (int x = 0; x < pattern_serie.length;x++){border += pattern_serie[x];}
        if ((TIEMPO > border) && (millis < border) && (!vibrate_flag) && (vibracion)){
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(pattern_serie, -1);
            vibrate_flag = true;
        }
    }
    public void actualizacionDescanso(long millis) {
        long border = 0;
        int mil  = (int) millis % 1000;
        int sec  = (int)(millis/ 1000) % 60 ;
        int min  = (int)((millis/ (1000*60)) % 60);
        int hr   = (int)((millis/ (1000*60*60)) % 24);
        int progresso = (int) (DESCANSO - millis);
        txtDescansoTime.setText(String.format("%02d:%02d:%02d", hr, min, sec));
        txtDescansoMilesimas.setText(String.format("%03d",mil));
        progressBarDescanso.setProgress(progresso);
        for (int x = 0; x < pattern_descanso.length;x++){border += pattern_descanso[x];}
        if ((DESCANSO > border) && (millis < border) && (!vibrate_flag) && (vibracion)){
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(pattern_descanso, -1);
            vibrate_flag = true;
        }
    }
    public void actulizacionSeries(final int serie){
        //progressBarSeries.setProgress(serie);
        final int total = PERIODO - series;
        txtSerieNumero.setText(String.valueOf(serie) + "/" + String.valueOf(PERIODO));
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    handler.sendMessage(handler.obtainMessage());
                }catch (Throwable t) {}
                handler.post(new Runnable() {
                    public void run() {
                        progressBarSeries.setProgress(total);
                    }
                });
            }
        });
        thread.start();
    }
    public void resetContador(){
        txtSeriesTime.setText("00:00:00");
        txtSeriesMil.setText(".000");
        soundManager.play(descanso_es);
        if (DESCANSO <= 0) updateSeries();
        vibrate_flag = false;

    }
    public void resetDescanso(){
        txtDescansoTime.setText("00:00:00");
        txtDescansoMilesimas.setText("000");
        if (series == 0) soundManager.play(fin_es);
        else soundManager.play(serie_es);
        vibrate_flag = false;

}
    public void updateSeries(){
        final int total = PERIODO - series;
        series--;

        if(series < 0){
            Toast.makeText(getApplicationContext(), R.string.text_find_ejercicio, Toast.LENGTH_LONG).show();
        }
        txtSerieNumero.setText(String.valueOf(total) + "/" + PERIODO);
        progressBarSeries.setProgress(total);
    }
    public void resetAll(){
        pullSistema();
    }

    private void dialogoSeries(){
        final ListSeriesDialog myDialogDescanso = new ListSeriesDialog(MainActivity.this,
                "TANKE.es",
                new OnReadyListener());
        myDialogDescanso.show();
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent2 = null;
        switch(item.getItemId()) {
            case R.id.menu_settings:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivityForResult(intent,Constantes.PREFERENCES_ACTIVITY_OK);
            break;
            case R.id.submenu_editar:
                intent2 = new Intent(getApplicationContext(), SeriesActivity.class);
                startActivityForResult(intent2, Constantes.SERIES_ACTIVITY_OK);
                break;
            case R.id.submenu_nuevo:
                intent2 = new Intent(getApplicationContext(), EditarSerieActivity.class);
                intent2.putExtra("from", "main");

                startActivityForResult(intent2, Constantes.EDITAR_ACTIVITY_OK);
                break;
            case R.id.menu_save:
                dialogoSeries();
                break;
            default:
                //Toast.makeText(getApplicationContext(), "X DEFECTO", Toast.LENGTH_SHORT).show();
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constantes.EDITAR_ACTIVITY_OK){

            switch (resultCode) {
                case RESULT_OK:
                    Toast.makeText(getApplicationContext(),R.string.text_serie_guardada, Toast.LENGTH_SHORT).show();
                    pullSistema();
                    break;

                /*case RESULT_CANCELED:
                    // Cancelación o cualquier situación de error
                    break;*/
            }
        }
        if(requestCode == Constantes.PREFERENCES_ACTIVITY_OK){
            Toast.makeText(this, R.string.text_preferences_save, Toast.LENGTH_SHORT).show();
            setupFieldsAparience();
        }
        if ((requestCode == Constantes.SERIES_ACTIVITY_OK) && (resultCode == RESULT_OK)){
            pullSistema();
        }
    }

    private class OnReadyListener implements ListSeriesDialog.ReadyListener {
        @Override
        public void ready(String name) {
            Toast.makeText(MainActivity.this, getString(R.string.text_serie_seleccionado) + " " + name, Toast.LENGTH_SHORT).show();
            pullSistema();
        }
    }

    public class ClickOnName implements View.OnClickListener{

        public ClickOnName(){}

        @Override
        public void onClick(View view) {
            DBHelper db = new DBHelper(getApplicationContext());
            Cronometro cronometro = null;
            cronometro = db.getSeleccion();
            int elemento = (int) cronometro.getId();
            Intent intent = new Intent(getApplicationContext(),EditarSerieActivity.class);
            intent.putExtra("id", elemento);
            intent.putExtra("from", "series");
            startActivityForResult(intent, Constantes.EDITAR_ACTIVITY_OK);
        }
    }
}
