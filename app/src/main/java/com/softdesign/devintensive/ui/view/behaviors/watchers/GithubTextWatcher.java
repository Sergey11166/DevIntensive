package com.softdesign.devintensive.ui.view.behaviors.watchers;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;

import com.softdesign.devintensive.R;

/**
 * @author Sergey Vorobyev.
 */

public class GithubTextWatcher extends AbstractProfileTextWatcher {

    public GithubTextWatcher(TextInputLayout textInputLayout, EditText editText) {
        super(textInputLayout, editText);
    }

    @Override
    public void afterTextChanged(Editable s) {
        String gh = s.toString().trim().toLowerCase();
        boolean isValid = false;
        int a = gh.indexOf("github.com");
        if (a != -1) {
            if (a != 0) {
                String newString = gh.substring(a);
                mEditText.removeTextChangedListener(this);
                mEditText.setText(newString);
                mEditText.addTextChangedListener(this);
                mEditText.setSelection(mEditText.getText().toString().length());
            } else {
                isValid = isValid(gh);
            }
        }
        if (!isValid) {
            setError(mResources.getString(R.string.error_edit_text_github_wrong_format));
        } else {
            removeError();
        }
    }

    /**
     * @param github Github url without 'https://'
     * @return True if it matches format github.com/***
     */
    private boolean isValid(String github) {
        String pattern = mResources.getString(R.string.regex_pattern_link_gitHub);
        return !TextUtils.isEmpty(github) && github.matches(pattern);
    }
}
