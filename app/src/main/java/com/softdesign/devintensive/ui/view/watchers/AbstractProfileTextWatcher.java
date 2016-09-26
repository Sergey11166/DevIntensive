package com.softdesign.devintensive.ui.view.watchers;

import android.content.res.Resources;
import android.support.design.widget.TextInputLayout;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * @author Sergey Vorobyev.
 */
abstract class AbstractProfileTextWatcher implements TextWatcher {

    private TextInputLayout mTextInputLayout;
    EditText mEditText;
    Resources mResources;

    AbstractProfileTextWatcher(TextInputLayout textInputLayout, EditText editText) {
        mResources = editText.getContext().getResources();
        mTextInputLayout = textInputLayout;
        mEditText = editText;
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

    /**
     * Setup error on TextInputLayout.
     *
     * @param message Error message
     */
    void setError(String message) {
        mTextInputLayout.setErrorEnabled(true);
        mTextInputLayout.setError(message);
    }

    /**
     * Remove error from TextInputLayout.     *
     */
    void removeError() {
        mTextInputLayout.setError(null);
        mTextInputLayout.setErrorEnabled(false);
    }
}
