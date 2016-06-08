package com.teddoll.fitness.intervaltrainer.edit;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.teddoll.fitness.intervaltrainer.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by teddydoll on 6/3/16.
 */
public class SetIntervalCopyDialogFragment extends DialogFragment {


    private String[] mItems;
    private List<Integer> mSelectedItems;

    public static SetIntervalCopyDialogFragment newInstance(ArrayList<Integer> items) {
        SetIntervalCopyDialogFragment fragment = new SetIntervalCopyDialogFragment();
        Bundle args = new Bundle();
        args.putIntegerArrayList("items", items);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        List<Integer> list = getArguments().getIntegerArrayList("items");
        if (list != null) {
            mItems = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                mItems[i] = context.getString(R.string.item_time_format, list.get(i));
            }
        } else {
            mItems = new String[0];
        }
        mSelectedItems = new ArrayList<>();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.copy_dialog_title))
                .setMultiChoiceItems(mItems, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            mSelectedItems.add(which);
                        } else if (mSelectedItems.contains(which)) {
                            // Else, if the item is already in the array, remove it
                            mSelectedItems.remove(Integer.valueOf(which));
                        }
                    }
                })

                .setPositiveButton(getString(R.string.copy_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Collections.sort(mSelectedItems);
                        ((EditActivity) getActivity()).onCopy(mSelectedItems);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);

        return builder.create();

    }
}
