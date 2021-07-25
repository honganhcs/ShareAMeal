package com.example.shareameal;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import org.jetbrains.annotations.NotNull;

public class CustomTimePickerFragment extends AppCompatDialogFragment {
    private AlertDialog.Builder builder;
    private NumberPicker hourPicker, minutePicker;
    private TextView txtAM;
    private int selectedHour, selectedMinute;
    private ExampleDialogListener listener;
    private int type;

    public CustomTimePickerFragment(int type) {
        this.type = type;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_time_picker, null);
        hourPicker = view.findViewById(R.id.hourPicker);
        minutePicker = view.findViewById(R.id.minutePicker);
        txtAM = view.findViewById(R.id.txtAM);

        hourPicker.setWrapSelectorWheel(false);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        hourPicker.setValue(0);
        hourPicker.setDisplayedValues(new String[]{"12", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
                                                    "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"});
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(1);
        minutePicker.setDisplayedValues(new String[]{"00", "30"});
        minutePicker.setValue(0);

        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                selectedHour = newVal;

                if(newVal >= 12) {
                    txtAM.setText("PM");
                    txtAM.setTextColor(Color.parseColor("#FF3700B3"));
                } else {
                    txtAM.setText("AM");
                    txtAM.setTextColor(Color.rgb(196, 85, 8));
                }
            }
        });

        minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                selectedMinute = newVal * 30;
            }
        });

        builder.setView(view)
                .setTitle("Select time:")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.setTime(selectedHour, selectedMinute, type);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        try {
            listener = (ExampleDialogListener) context;
        } catch (ClassCastException e) {
           throw new ClassCastException(context.toString() + " must implement ExampleDialogListener");
        }
    }

    public interface ExampleDialogListener {
        void setTime(int hour, int minute, int type);
    }
}
