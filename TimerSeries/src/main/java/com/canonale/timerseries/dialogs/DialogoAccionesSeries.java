package com.canonale.timerseries.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.app.Activity;

import com.actionbarsherlock.R;


/**
 * Created by adrian on 5/08/13.
 */
public class DialogoAccionesSeries extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Titulo");
        /*builder.setItems(R.array.opciones_series, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // The 'which' argument contains the index position
                // of the selected item
            }
        });*/
        return builder.create();
    }

}
