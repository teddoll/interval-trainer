package com.teddoll.fitness.intervaltrainer.edit;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.NumberPicker;

import com.teddoll.fitness.intervaltrainer.R;

/**
 * Dialog Fragment for picking interval time.
 * Created by teddydoll on 6/3/16.
 */
public class SetIntervalPickerFragment extends DialogFragment {

    private int mPosition;

    public static SetIntervalPickerFragment newInstance(int position) {
        SetIntervalPickerFragment fragment = new SetIntervalPickerFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getArguments().getInt("position");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
        final NumberPicker picker = new NumberPicker(getContext());
        picker.setMinValue(1);
        picker.setMaxValue(60);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.set_interval_title))
                .setMessage(getString(R.string.set_interval_message)).
                setView(picker)
                .setPositiveButton(getString(R.string.set_interval_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((EditActivity) getActivity()).onAdd(mPosition, picker.getValue());
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);

        return builder.create();

    }
}
