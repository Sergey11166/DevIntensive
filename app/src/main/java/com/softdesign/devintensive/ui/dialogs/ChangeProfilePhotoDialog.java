package com.softdesign.devintensive.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.softdesign.devintensive.R;

/**
 * @author Sergey Vorobyev.
 */

public class ChangeProfilePhotoDialog extends DialogFragment {

    private DialogInterface.OnClickListener mOnClickListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.profile_placeholder_load_photo_dialog_title);
        String[] items = getResources().getStringArray(R.array.load_photo_dialog);
        builder.setItems(items, mOnClickListener);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public void setOnClickListener(DialogInterface.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }
}
