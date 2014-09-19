package com.canonale.timerseries;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuInflater;
import com.canonale.timerseries.dialogs.DataPickerDialog;
import com.canonale.timerseries.dialogs.EditSeriesDialogFragment;
import com.canonale.timerseries.helper.Cronometro;
import com.canonale.timerseries.helper.DBHelper;
import com.canonale.timerseries.util.Validate;

public class EditarSerieActivity extends SherlockFragmentActivity {

    public CheckBox cbActivado;
    public TextView tv_text_continuo;
    public EditText txtTiempoDescanso;
    public TextView tv_text_descanso;
    public EditText txtRepeticiones;
    public TextView tv_text_series;
    public EditText txtTiempoSerie;
    public TextView tv_text_duracion;
    public EditText txtNombreSerie;
    public TextView tv_actividad_editar;
    public int id;
    public String activity;
    public Cronometro serie;
    public final int NUEVA_SERIE = -1;
    public boolean def;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);
        // Show the Up button in the action bar.
        setupActionBar();
        setupFieldsAparience();

        id = getIntent().getIntExtra("id", NUEVA_SERIE);
        activity = getIntent().getStringExtra("from");

        cbActivado = (CheckBox) findViewById(R.id.cbActivado);
        txtTiempoDescanso = (EditText) findViewById(R.id.txtTiempoDescanso);
        txtRepeticiones = (EditText) findViewById(R.id.txtRepeticiones);
        txtTiempoSerie = (EditText) findViewById(R.id.txtTiempoSerie);
        txtNombreSerie = (EditText) findViewById(R.id.txtNombreSerie);
        if (id != NUEVA_SERIE){
            DBHelper dbHelper  = new DBHelper(this);
            serie = dbHelper.get(id);
            def = serie.getSeleccion();
            txtNombreSerie.setText(String.valueOf(serie.getNombre()));
            txtTiempoSerie.setText(serie.getHoras(serie.getTiempo()));
            txtRepeticiones.setText(String.valueOf(serie.getSeries()));
            txtTiempoDescanso.setText(serie.getHoras(serie.getDescanso()));
            cbActivado.setChecked(serie.getAuto());
        }


        txtTiempoSerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_actividad_editar.requestFocus();
                final DataPickerDialog myDialog = new DataPickerDialog(EditarSerieActivity.this,
                        txtTiempoSerie.getText().toString(),
                        new OnReadyListener());
                myDialog.show();
            }
        });
        txtTiempoDescanso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_actividad_editar.requestFocus();
                final DataPickerDialog myDialogDescanso = new DataPickerDialog(EditarSerieActivity.this,
                        txtTiempoDescanso.getText().toString(),
                        new OnReadyListenerDescanso());
                myDialogDescanso.show();
            }
        });

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setLogo(R.drawable.ic_action_logo);
    }
    private void setupFieldsAparience(){
        cbActivado = (CheckBox) findViewById(R.id.cbActivado);
        tv_text_continuo = (TextView) findViewById(R.id.tv_text_continuo);
        txtTiempoDescanso = (EditText) findViewById(R.id.txtTiempoDescanso);
        tv_text_descanso = (TextView) findViewById(R.id.tv_text_descanso);
        txtRepeticiones = (EditText) findViewById(R.id.txtRepeticiones);
        tv_text_series = (TextView) findViewById(R.id.tv_text_series);
        txtTiempoSerie = (EditText) findViewById(R.id.txtTiempoSerie);
        tv_text_duracion = (TextView) findViewById(R.id.tv_text_duracion);
        txtNombreSerie = (EditText) findViewById(R.id.txtNombreSerie);
        tv_actividad_editar = (TextView) findViewById(R.id.tv_actividad_editar);
        Typeface tfMed = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        Typeface tfThin = Typeface.createFromAsset(getAssets(), "Roboto-Condensed.ttf");
        cbActivado.setTypeface(tfMed);
        tv_text_continuo.setTypeface(tfThin);
        txtTiempoDescanso.setTypeface(tfMed);
        tv_text_descanso.setTypeface(tfThin);
        txtRepeticiones.setTypeface(tfMed);
        tv_text_series.setTypeface(tfThin);
        txtTiempoSerie.setTypeface(tfMed);
        tv_text_duracion.setTypeface(tfThin);
        txtNombreSerie.setTypeface(tfMed);
        tv_actividad_editar.setTypeface(tfThin);
        tv_actividad_editar.setFocusableInTouchMode(true);
        tv_actividad_editar.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.edicion_serie, menu);
        return super.onCreateOptionsMenu(menu);
    }
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                if (validated()) guardarSerie();
                else showAlert();
                break;
            case android.R.id.home:
                onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditarSerieActivity.this);
        builder.setTitle(R.string.dialogo_series_error);
        builder.setMessage(R.string.text_series_error);
        builder.setNeutralButton(R.string.text_aceptar, null);
        builder.create();
        builder.show();
    }
    private void guardarSerie(){
        Cronometro c = new Cronometro();
        long last_id;
        DBHelper db = new DBHelper(getApplicationContext());
        c.setNombre(txtNombreSerie.getText().toString());
        c.setTiempo(c.getMilesimas(txtTiempoSerie.getText().toString()));
        c.setDescanso(c.getMilesimas(txtTiempoDescanso.getText().toString()));
        c.setSeries(Integer.parseInt(txtRepeticiones.getText().toString()));
        c.setAuto(cbActivado.isChecked());
        if (id == NUEVA_SERIE){
            db.insert(c);
            if (activity.equals("main")){
                last_id = db.getLastId();
                db.usarSerie((int) last_id);
            }
        }else{
            c.setSeleccion(def);
            c.setId(id);
            db.update(c);
        }
        setResult(RESULT_OK);
        finish();
    }

    protected boolean validated() {
        boolean validated = true;
        if (!Validate.hasText(txtNombreSerie)) validated = false;
        if (!Validate.hasText(txtTiempoSerie)) validated = false;
        if (!Validate.hasText(txtTiempoDescanso)) validated = false;
        if (!Validate.hasNaturalNumber(txtRepeticiones)) validated = false;
        return validated;
    }

    private class OnReadyListener implements DataPickerDialog.ReadyListener {
        @Override
        public void ready(String name) {
            //Toast.makeText(EditarSerieActivity.this, name, Toast.LENGTH_LONG).show();
            if (!name.equals("")){
                txtTiempoSerie.setText(name);
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(EditarSerieActivity.this);
                builder.setTitle(R.string.dialogo_series_error_001);
                builder.setMessage(R.string.text_series_error_001);
                builder.setNeutralButton(R.string.text_aceptar, null);
                builder.create();
                builder.show();
            }
        }
    }
    private class OnReadyListenerDescanso implements DataPickerDialog.ReadyListener {
        @Override
        public void ready(String name) {
            //Toast.makeText(EditarSerieActivity.this, name, Toast.LENGTH_LONG).show();
            if (!name.equals("")){
                txtTiempoDescanso.setText(name);
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(EditarSerieActivity.this);
                builder.setTitle(R.string.dialogo_series_error_001);
                builder.setMessage(R.string.text_series_error_001);
                builder.setNeutralButton(R.string.text_aceptar, null);
                builder.create();
                builder.show();
            }
        }
    }
}
