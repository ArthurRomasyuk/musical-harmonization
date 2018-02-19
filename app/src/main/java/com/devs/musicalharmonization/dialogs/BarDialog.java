package com.devs.musicalharmonization.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.devs.musicalharmonization.R;
import com.devs.musicalharmonization.activity.CompositionView;
import com.devs.musicalharmonization.singletons.Bar;

import static com.devs.musicalharmonization.singletons.Bar.bottomNumber;

/**
 * @author Artur Romasiuk
 */

public class BarDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.dialog_bar, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText top_number = (EditText) view.findViewById(R.id.top_number);
                        EditText bottom_number = (EditText) view.findViewById(R.id.bottom_number);
                        Bar.topNumber= Integer.parseInt(top_number.getText().toString());
                        bottomNumber= Integer.parseInt(bottom_number.getText().toString());
                        getActivity().findViewById(R.id.composition_canvas).invalidate();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        BarDialog.this.getDialog().cancel();
                    }
                })
        ;
        return builder.create();
    }
}
