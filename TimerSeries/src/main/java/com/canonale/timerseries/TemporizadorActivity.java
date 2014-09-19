package com.canonale.timerseries;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.canonale.timerseries.dialogs.DataPickerDialog;
import com.canonale.timerseries.helper.Cronometro;
import com.canonale.timerseries.helper.DBHelper;
import com.canonale.timerseries.helper.SoundManager;
import com.canonale.timerseries.util.Validate;


public class TemporizadorActivity extends Activity {


    public TextView id;
    public TextView nombre;
    public TextView series;
    public TextView descanso;
    public TextView auto;
    public TextView seleccion;
    public Button boton;
    private SoundManager soundManager;
    private EditText hora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temporizador);

        descanso = (TextView) findViewById(R.id.descanso);
        series = (TextView) findViewById(R.id.series);
        boton = (Button) findViewById(R.id.button);
        hora = (EditText) findViewById(R.id.hora);

        DBHelper dbHelper = new DBHelper(getApplicationContext());

        Cronometro cronometro = new Cronometro();
        cronometro.setNombre("Abdominales");
        cronometro.setTiempo(10000);
        cronometro.setDescanso(2000);
        cronometro.setSeries(7);
        cronometro.setAuto(true);
        cronometro.setSeleccion(true);
        //dbHelper.insert(cronometro);


                soundManager = new SoundManager(getApplicationContext());
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        final int alarma = soundManager.load(R.raw.findeserie);

        //final DataPickerDialog myDialog = new DataPickerDialog(this, "",
        //        new OnReadyListener());
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //myDialog.show();
                //soundManager.play(alarma);
                if (validated()){
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(TemporizadorActivity.this);
                    dialogo1.setTitle("Importante");
                    dialogo1.setMessage("¿ Acepta la ejecución de este programa en modo prueba ?");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            finish();
                        }
                    });
                    dialogo1.create();
                    dialogo1.show();
                } else {
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(TemporizadorActivity.this);
                    dialogo1.setTitle("Importante");
                    dialogo1.setMessage("¿ Acepta la ejecución de este programa en modo prueba ?");
                    dialogo1.setCancelable(false);
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            finish();
                        }
                    });
                    dialogo1.create();
                    dialogo1.show();
                }
            }
        });


    }

    protected boolean validated(){
        boolean validated = true;
        if (!Validate.hasMinute(hora)) validated = false;
        return validated;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.temporizador, menu);
        return true;
    }
    private class OnReadyListener implements DataPickerDialog.ReadyListener {
        @Override
        public void ready(String name) {
            Toast.makeText(TemporizadorActivity.this, name, Toast.LENGTH_LONG).show();
        }
    }
    
}
