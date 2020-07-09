package com.ym.game.sdk.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.orhanobut.logger.Logger;
import com.ym.game.utils.ResourseIdUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AccountLoginFragment extends BaseFragment {

    private View content;
    private static final String TAG = "Ymsdk";
    private View ll_content;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(ResourseIdUtils.getLayoutId("fragment_account_login"), null, true);
        ll_content = view.findViewById(ResourseIdUtils.getId("ll_content2"));



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ll_content.post(new Runnable(){

            @Override
            public void run() {
                int height = ll_content.getMeasuredHeight();
                int width = ll_content.getMeasuredWidth();
            }


        });
        int width = ll_content.getMeasuredWidth();
        int height = ll_content.getMeasuredHeight();


    }
}
