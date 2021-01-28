package com.ym.game.sdk.ui.widget;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;


public abstract class PasswordTextWatcher implements TextWatcher {
    private static final String LOG_TAG = PasswordTextWatcher.class
            .getSimpleName();
    // password match rule
    private static final String PASSWORD_REGEX = "[A-Z0-9a-z!@#$%^&*.~/\\{\\}|()'\"?><,.`\\+-=_\\[\\]:;]+";

    private boolean mIsMatch;
    private CharSequence mResult;
    private int mSelectionStart;
    private int mSelectionEnd;
    private EditText mPswEditText;

    public PasswordTextWatcher() {};

    public PasswordTextWatcher(EditText editText) {
        mPswEditText = editText;
    };

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
        mSelectionStart = mPswEditText.getSelectionStart();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        CharSequence charSequence = "";
        if ((mSelectionStart + count) <= s.length()) {
            charSequence = s.subSequence(mSelectionStart, mSelectionStart
                    + count);
        }
        mIsMatch = pswFilter(charSequence);
        if (!mIsMatch) {
            String temp = s.toString();
            mResult = temp.replace(charSequence, "");
            mSelectionEnd = start;
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!mIsMatch) {
            mPswEditText.setText(mResult);
            mPswEditText.setSelection(mSelectionEnd);
        }
    }


    private boolean pswFilter(CharSequence s) {
        if (TextUtils.isEmpty(s)) {
            return true;
        }
        Pattern pattern = Pattern.compile(PASSWORD_REGEX);
        Matcher matcher = pattern.matcher(s);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }
}
