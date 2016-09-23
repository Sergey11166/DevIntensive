package com.softdesign.devintensive.ui.view.watchers;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.widget.EditText;

import com.softdesign.devintensive.R;

/**
 * @author Sergey Vorobyev
 */

public class PhoneTextWatcher extends AbstractProfileTextWatcher {

    private static final int MAX_DIGITS_COUNT = 11;
    private static final int MAX_SYMBOLS_COUNT = 16;
    private static final String RUSSIAN_PHONE_CODE_7 = "7";
    private static final String RUSSIAN_PHONE_CODE_8 = "8";

    public PhoneTextWatcher(TextInputLayout textInputLayout, EditText editText) {
        super(textInputLayout, editText);
    }

    @Override
    public void afterTextChanged(Editable s) {
        String phone = s.toString().trim();
        boolean isCursorPosToEnd = false;
        String formattedPhone = "";
        int cursorPosition = mEditText.getSelectionStart();
        String errorText;

        if (cursorPosition == phone.length()) isCursorPosToEnd = true;

        errorText = isValidPhone(phone);

        int a = 0;
        if (!phone.startsWith("+")) a = 1;
        //remove the excess of the number of characters
        if (phone.length() > MAX_SYMBOLS_COUNT - a) {
            if (isCursorPosToEnd ||
                    cursorPosition == 0 ||
                    (phone.startsWith(RUSSIAN_PHONE_CODE_7)
                            || phone.startsWith(RUSSIAN_PHONE_CODE_8)) && cursorPosition == 1 ||
                    (phone.startsWith("(" + RUSSIAN_PHONE_CODE_7)
                            || phone.startsWith("(" + RUSSIAN_PHONE_CODE_8)) && cursorPosition == 2) {
                cursorPosition = cursorPosition + 1;
                //remove symbols from the end of number
                phone = phone.substring(0, MAX_SYMBOLS_COUNT - a);
            } else {
                //remove last typed symbol
                phone = phone.substring(0, cursorPosition - 1) + phone.substring(cursorPosition);
                cursorPosition -= 1;
            }
        }

        //reformatting number into +7(ххх)ххх-хх-хх
        phone = phone.replaceAll("\\D", "");
        if (phone.length() > 0) formattedPhone = getFormattedPhone(phone);

        //setup number into EditText and setup cursor position
        mEditText.removeTextChangedListener(this);
        mEditText.setText(formattedPhone);
        if (isCursorPosToEnd || cursorPosition > mEditText.getText().toString().length()) {
            mEditText.setSelection(mEditText.getText().toString().length());
        } else {
            mEditText.setSelection(cursorPosition);
        }
        mEditText.addTextChangedListener(this);

        if(errorText != null) {
            setError(errorText);
        } else {
            removeError();
        }
    }

    /**
     * Validate phone number
     * @param phone input phone number
     * @return True if valid, false if not valid
     */
    private String isValidPhone(String phone) {
        String checkPhone = phone.replaceAll("[\\(\\)\\-\\+]", "");
        if (checkPhone.matches("(\\d*\\D\\d*)*")) {
            return mResources.getString(R.string.error_edit_text_wrong_symbols);
        } else if (checkPhone.length() < MAX_DIGITS_COUNT) {
            return mResources.getString(R.string.error_edit_text_phone_wrong_length);
        } else if (!checkPhone.startsWith(RUSSIAN_PHONE_CODE_7) && !checkPhone.startsWith(RUSSIAN_PHONE_CODE_8)) {
            return mResources.getString(R.string.error_edit_text_phone_wrong_code);
        }
        return null;
    }

    /**
     * Reformat phone number into +7(***)***-**-**
     *
     * @param phone Phone number
     * @return String of reformatted phone
     */
    private String getFormattedPhone(String phone) {
        String countryCode = "";
        String mobileOperatorCode = "";
        String firstNumberPart = "";
        String secondNumberPart = "";
        String thirdNumberPart = "";

        int index = 0;
        if (phone.startsWith(RUSSIAN_PHONE_CODE_7) || phone.startsWith(RUSSIAN_PHONE_CODE_8)) {
            countryCode = "+" + RUSSIAN_PHONE_CODE_7;
            index = 1;
        }

        if (index < phone.length()) {
            phone = phone.substring(index);
            if (phone.length() <= 3) {
                mobileOperatorCode = phone.substring(0, phone.length());
            } else {
                mobileOperatorCode = phone.substring(0, 3);
                if (phone.length() <= 6) {
                    firstNumberPart = phone.substring(3, phone.length());
                } else {
                    firstNumberPart = phone.substring(3, 6);
                    if (phone.length() <= 8) {
                        secondNumberPart = phone.substring(6, phone.length());
                    } else {
                        secondNumberPart = phone.substring(6, 8);
                        thirdNumberPart = phone.substring(8, phone.length());
                    }
                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        if (countryCode.length() > 0) {
            stringBuilder.append(countryCode);
        }
        if (mobileOperatorCode.length() > 0) {
            stringBuilder.append("(");
            stringBuilder.append(mobileOperatorCode);
        }
        if (firstNumberPart.length() > 0) {
            stringBuilder.append(")");
            stringBuilder.append(firstNumberPart);
        }
        if (secondNumberPart.length() > 0) {
            stringBuilder.append("-");
            stringBuilder.append(secondNumberPart);
        }
        if (thirdNumberPart.length() > 0) {
            stringBuilder.append("-");
            stringBuilder.append(thirdNumberPart);
        }
        return stringBuilder.toString();
    }
}
