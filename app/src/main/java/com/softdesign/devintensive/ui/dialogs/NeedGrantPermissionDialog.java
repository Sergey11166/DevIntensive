package com.softdesign.devintensive.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.softdesign.devintensive.R;

import static com.softdesign.devintensive.utils.Constants.DIALOG_MESSAGE_KEY;

/**
 * @author Sergey Vorobyev
 */

public class NeedGrantPermissionDialog extends DialogFragment {

    private OnClickListener mOnPositiveButtonClickListener;
    private OnClickListener mOnNegativeButtonClickListener;

    public static NeedGrantPermissionDialog newInstance(int message) {
        NeedGrantPermissionDialog dialog = new NeedGrantPermissionDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(DIALOG_MESSAGE_KEY, message);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int message = getArguments().getInt(DIALOG_MESSAGE_KEY);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_title_need_grant_permission)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_positive_button, mOnPositiveButtonClickListener)
                .setNegativeButton(R.string.dialog_negative_button, mOnNegativeButtonClickListener);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public void setOnPositiveButtonClickListener(OnClickListener onPositiveButtonClickListener) {
        mOnPositiveButtonClickListener = onPositiveButtonClickListener;
    }

    public void setOnNegativeButtonClickListener(OnClickListener onNegativeButtonClickListener) {
        mOnNegativeButtonClickListener = onNegativeButtonClickListener;
    }
}
