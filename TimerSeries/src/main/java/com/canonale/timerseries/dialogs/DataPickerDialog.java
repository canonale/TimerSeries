package com.canonale.timerseries.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.canonale.timerseries.R;
import com.canonale.timerseries.util.Validate;

/**
 * Created by adrian on 11/08/13.
 */
public class DataPickerDialog extends Dialog {
    public interface ReadyListener {
        public void ready(String name);
    }

    private String name;
    private ReadyListener readyListener;
    private EditText txtHoras, txtMinutos, txtSegundos;
    private final static int MAXIMO = 59;
    private final static int MINIMO = 0;
    Button btAcept, btCancel;
    TextView tv_text_minutos2;
    TextView textView;
    TextView tv_text_minutos;

    private Context context;

    public DataPickerDialog(Context context, String name,
                            ReadyListener readyListener) {
        super(context, R.style.cust_dialog);
        this.name = name;
        this.context = context;
        this.readyListener = readyListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timerpicker_dialog);
        setTitle(R.string.text_titulo_timer_dialog);
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        switch(metrics.densityDpi){
            case DisplayMetrics.DENSITY_LOW:
                this.getWindow().setLayout(185, 220);
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                this.getWindow().setLayout(250, 240);
                break;
            case DisplayMetrics.DENSITY_HIGH:
                this.getWindow().setLayout(375, 390);
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                this.getWindow().setLayout(500, 450);
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                this.getWindow().setLayout(650, 640);
                break;
            default:
                this.getWindow().setLayout(375, 350);
                break;
        }

        Log.d("PANTALLA", String.valueOf(metrics.densityDpi));

        btAcept = (Button) findViewById(R.id.btAcept);
        btCancel = (Button) findViewById(R.id.btCancel);
        String[] digitos = name.split(":");

        txtHoras = (EditText) findViewById(R.id.txtHoras);
        txtMinutos = (EditText) findViewById(R.id.txtMinutos);
        txtSegundos = (EditText) findViewById(R.id.txtSegundos);
        txtHoras.requestFocus();
        txtHoras.setSelected(true);
        txtHoras.setSelection(0, txtHoras.getText().length());
        if (digitos.length == 3){
            txtHoras.setText(digitos[0]);
            txtMinutos.setText(digitos[1]);
            txtSegundos.setText(digitos[2]);
        }

        txtHoras.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus){
                if ((hasFocus) && (((EditText)v).getText().length() == 2))
                    ((EditText)v).selectAll();
            }
        });
        txtMinutos.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus){
                if ((hasFocus) && (((EditText)v).getText().length() == 2))
                    ((EditText)v).selectAll();
            }
        });
        txtSegundos.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus){
                if ((hasFocus) && (((EditText)v).getText().length() == 2))
                    ((EditText)v).selectAll();
            }
        });

        txtHoras.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {


            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if(charSequence.length() == 2){
                    txtMinutos.requestFocus();
                    txtMinutos.setSelected(true);
                    txtMinutos.setSelection(0, txtMinutos.getText().length());
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {  }
        });

        txtMinutos.addTextChangedListener(new TextWatcher() {
            private CharSequence prevtext;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                prevtext = txtMinutos.getText().toString();
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if(charSequence.length() == 2){
                    int minutos = Integer.parseInt(String.valueOf(charSequence));
                    if (minutos > 59){ txtMinutos.setText(prevtext); }
                    else {
                        txtSegundos.requestFocus();
                        txtSegundos.setSelected(true);
                        txtSegundos.setSelection(0, txtSegundos.getText().length());
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {  }
        });

        txtSegundos.addTextChangedListener(new TextWatcher() {
            private CharSequence prevtext;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                prevtext = txtSegundos.getText().toString();
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (charSequence.length() != 0){
                    int segundos = Integer.parseInt(String.valueOf(charSequence));
                    if (segundos > 59){
                        txtSegundos.setText(prevtext);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String texto = txtSegundos.getText().toString();
                if(txtSegundos.getText().toString().length() > 2){
                    txtSegundos.requestFocus();
                    txtSegundos.setText(texto.substring(0,2));
                    txtSegundos.setSelected(true);
                    txtSegundos.setSelection(0, txtSegundos.getText().length());
                }
            }
        });

        btAcept.setOnClickListener(new OKListener());

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataPickerDialog.this.dismiss();
            }
        });
        setupFieldsAparience();


    }
    private void setupFieldsAparience(){
        tv_text_minutos = (TextView) findViewById(R.id.tv_text_minutos);
        txtHoras = (EditText) findViewById(R.id.txtHoras);
        textView = (TextView) findViewById(R.id.textView);
        txtSegundos = (EditText) findViewById(R.id.txtSegundos);
        tv_text_minutos = (TextView) findViewById(R.id.tv_text_minutos);
        txtMinutos = (EditText) findViewById(R.id.txtMinutos);
        btAcept = (Button) findViewById(R.id.btAcept);
        btCancel = (Button) findViewById(R.id.btCancel);
        Typeface tfMed = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Medium.ttf");
        Typeface tfThin = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Condensed.ttf");
        txtHoras.setTypeface(tfThin);
        textView.setTypeface(tfThin);
        txtSegundos.setTypeface(tfThin);
        tv_text_minutos.setTypeface(tfThin);
        txtMinutos.setTypeface(tfThin);
        btAcept.setTypeface(tfThin);
        btCancel.setTypeface(tfThin);
    }

    private class OKListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (validated()){
                String tmp = String.valueOf(txtHoras.getText()) + ":" + String.valueOf(txtMinutos.getText())
                        + ":" + String.valueOf(txtSegundos.getText());
                readyListener.ready(String.valueOf(tmp));
                DataPickerDialog.this.dismiss();
            }else{
                Toast.makeText(getContext(),R.string.dialogo_timer_campos_erroneo, Toast.LENGTH_SHORT).show();
            }
        }
    }
    protected boolean validated() {
        boolean validated = true;
        if (!Validate.hasText(txtHoras)) validated = false;
        if (!Validate.hasMinute(txtMinutos)) validated = false;
        if (!Validate.hasMinute(txtSegundos)) validated = false;
        return validated;
    }

}
