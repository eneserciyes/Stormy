package com.example.enes.stormy.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import com.example.enes.stormy.R;

/**
 * Created by Enes on 6/18/2016.
 */
public class AlertDialogFragment extends DialogFragment{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.error_title)
                .setMessage(R.string.erroe_message)
                .setPositiveButton(R.string.error_OK,null);

        AlertDialog dialog = builder.create();
        return dialog;
    }
}
