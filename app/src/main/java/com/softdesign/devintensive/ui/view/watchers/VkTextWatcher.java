package com.softdesign.devintensive.ui.view.watchers;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;

import com.softdesign.devintensive.R;

/**
 * @author Sergey Vorobyev.
 */

public class VkTextWatcher extends AbstractProfileTextWatcher {

    public VkTextWatcher(TextInputLayout textInputLayout, EditText editText) {
        super(textInputLayout, editText);
    }

    @Override
    public void afterTextChanged(Editable s) {
        String vk = s.toString().trim().toLowerCase();
        boolean isValid = false;
        int a = vk.indexOf("vk.com");
        if (a != -1) {
            if (a != 0) {
                String newString = vk.substring(a);
                mEditText.removeTextChangedListener(this);
                mEditText.setText(newString);
                mEditText.addTextChangedListener(this);
                mEditText.setSelection(mEditText.getText().toString().length());
            } else {
                isValid = isValidVK(vk);
            }
        }
        if (!isValid) {
            setError(mResources.getString(R.string.error_edit_text_vk_wrong_format));
        } else {
            removeError();
        }
    }

    /**
     * @param vk Vk url without 'https://'
     * @return True if it matches format vk.com/***
     */
    private boolean isValidVK(String vk) {
        String pattern = mResources.getString(R.string.regex_pattern_link_vk);
        return !TextUtils.isEmpty(vk) && vk.matches(pattern);
    }
}
