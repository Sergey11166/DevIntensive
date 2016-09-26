package com.softdesign.devintensive.ui.view.watchers;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;

import com.softdesign.devintensive.R;

/**
 * @author Sergey Vorobyev.
 */
public class EmailTextWatcher extends AbstractProfileTextWatcher {

    public EmailTextWatcher(TextInputLayout textInputLayout, EditText editText) {
        super(textInputLayout, editText);
    }

    @Override
    public void afterTextChanged(Editable s) {
        String email = s.toString().trim();
        if (!isValidEmail(email)) {
            setError(mResources.getString(R.string.error_edit_text_email_wrong_format));
        } else {
            removeError();
        }
    }

    /**
     * @param email Email
     * @return True if it matches format ***@**.**
     */
    private boolean isValidEmail(String email) {
        String pattern = mResources.getString(R.string.regex_pattern_email);
        return !TextUtils.isEmpty(email) && email.matches(pattern);
    }
}
