package com.example.shareameal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import com.example.shareameal.R;

import org.jetbrains.annotations.NotNull;

public class RejectReportDialogFragment extends AppCompatDialogFragment {
    private AlertDialog.Builder builder;
    private EditText edtReasons;
    private DialogListener listener;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_reject_report, null);
        edtReasons = view.findViewById(R.id.edtReasons);

        builder.setView(view)
                .setTitle("Reason(s) for rejection")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(TextUtils.isEmpty(edtReasons.getText().toString().trim())) {
                            Toast.makeText(getContext(), "Please key in your reason(s).", Toast.LENGTH_SHORT).show();
                        } else {
                            listener.setReasons(edtReasons.getText().toString().trim());
                        }
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        try {
            listener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DialogListener");
        }
    }

    public interface DialogListener {
        void setReasons(String reasons);
    }
}
