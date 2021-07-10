package com.appiedi.jammbell;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.NumberPicker;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class NumberPickerDialog extends DialogFragment
{
    private NumberPicker.OnValueChangeListener valueChangeListener;
    private int valoreMinimo;
    private int valoreMassimo;
    private int valoreDefault;

    public NumberPickerDialog(int minValue, int maxValue, int Value) {
        valoreMinimo = minValue;
        valoreMassimo = maxValue;
        valoreDefault = Value;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        final NumberPicker numberPicker = new NumberPicker(getActivity());
        numberPicker.setMinValue(valoreMinimo);
        numberPicker.setMaxValue(valoreMassimo);
        numberPicker.setValue(valoreDefault);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Seleziona: ");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                valueChangeListener.onValueChange(numberPicker, numberPicker.getValue(), numberPicker.getValue());
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                valueChangeListener.onValueChange(numberPicker,
                        numberPicker.getValue(), numberPicker.getValue());
            }
        });

        builder.setView(numberPicker);
        return builder.create();
    }

    public NumberPicker.OnValueChangeListener getValueChangeListener() {
        return valueChangeListener;
    }

    public void setValueChangeListener(NumberPicker.OnValueChangeListener valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }
}