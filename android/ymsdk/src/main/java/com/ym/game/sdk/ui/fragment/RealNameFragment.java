package com.ym.game.sdk.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ym.game.sdk.YmConstants;
import com.ym.game.sdk.bean.AccountBean;
import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.common.utils.ToastUtils;
import com.ym.game.sdk.presenter.UserPresenter;
import com.ym.game.utils.IdentityUtils;
import com.ym.game.utils.ImageUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.ym.game.sdk.YmConstants.COMMONLOGINREALNAMETYPE;
import static com.ym.game.sdk.YmConstants.GUESTLOGINREALNAMETYPE;
import static com.ym.game.sdk.YmConstants.LIMITREALNAMETYPE;
import static com.ym.game.sdk.YmConstants.UNLIMITREALNAMETYPE;

public class RealNameFragment extends UserBaseFragment implements View.OnClickListener {

    private ImageView ymImBack;
    private ImageView ymImClose;
    private ImageView fengjiexianRight;
    private EditText ymRealName;
    private EditText ymIdcardCode;
    private Button ymBtRealName;
    private AccountBean mAccountData;
    private LinearLayout ymLlRealName;
    private Button ymBtUnrealname;
    private Button ymBtRealNameShort;
    private TextView ymTvRealname;
    private boolean canReLogin;
    private int realNameType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(ResourseIdUtils.getLayoutId("fragment_realname"), null, true);
        ymImBack = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_back"));
        ymImClose = (ImageView) view.findViewById(ResourseIdUtils.getId("ym_im_close"));
        fengjiexianRight = (ImageView) view.findViewById(ResourseIdUtils.getId("fengjiexian_right"));
        ymRealName = (EditText) view.findViewById(ResourseIdUtils.getId("ym_real_name"));
        ymIdcardCode = (EditText) view.findViewById(ResourseIdUtils.getId("ym_idcard_code"));
        ymBtRealName = (Button) view.findViewById(ResourseIdUtils.getId("ym_bt_realname"));
        ymLlRealName = (LinearLayout) view.findViewById(ResourseIdUtils.getId("ym_ll_realname"));
        ymBtUnrealname = (Button) view.findViewById(ResourseIdUtils.getId("ym_bt_unrealname"));
        ymBtRealNameShort = (Button) view.findViewById(ResourseIdUtils.getId("ym_bt_realname_short"));
        ymTvRealname = (TextView) view.findViewById(ResourseIdUtils.getId("ym_tv_realname"));

        ymImBack.setOnClickListener(this);
        ymBtRealName.setOnClickListener(this);
        ymBtUnrealname.setOnClickListener(this);
        ymBtRealNameShort.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ymImClose.setVisibility(View.INVISIBLE);
        fengjiexianRight.setImageBitmap(ImageUtils.rotateIm(baseActivity, ResourseIdUtils.getMipmapId("ym_fenjiexian")));

        mAccountData = getAccountData();
        realNameType = getArguments().getInt("realNameType");
        ymTvRealname.setTextSize(11);
        switch (realNameType){
            case COMMONLOGINREALNAMETYPE:
                ymImBack.setVisibility(View.VISIBLE);
                ymBtRealName.setVisibility(View.VISIBLE);
                ymLlRealName.setVisibility(View.GONE);
                ymTvRealname.setTextSize(10);
                break;
            case GUESTLOGINREALNAMETYPE:
                ymImBack.setVisibility(View.VISIBLE);
                ymBtRealName.setVisibility(View.GONE);
                ymLlRealName.setVisibility(View.VISIBLE);
                String guestLoginRealNameTip = getString(ResourseIdUtils.getStringId("ym_tv_guestshimingtip1"));
                ymTvRealname.setText(guestLoginRealNameTip);

                break;
            case LIMITREALNAMETYPE:
                ymImBack.setVisibility(View.INVISIBLE);
                ymBtRealName.setVisibility(View.VISIBLE);
                ymLlRealName.setVisibility(View.GONE);
                String limitrealNameTip = getString(ResourseIdUtils.getStringId("ym_tv_guestshimingtip2"));
                ymTvRealname.setText(limitrealNameTip);
                break;
            case UNLIMITREALNAMETYPE:
                ymImBack.setVisibility(View.INVISIBLE);
                ymBtRealName.setVisibility(View.GONE);
                ymLlRealName.setVisibility(View.VISIBLE);
                String unlimitrealNameTip = getString(ResourseIdUtils.getStringId("ym_tv_guestshimingtip1"));
                ymTvRealname.setText(unlimitrealNameTip);
                break;
            default:
                break;
        }


    }

    @Override
    public void onClick(View view) {
        if(view.getId()== ymImBack.getId()){
            //TODO:关闭实名界面
            UserPresenter.cancelRealName(this,YmConstants.REALNAMERELOGINSTATE,null);
        }else if(view.getId() == ymBtRealName.getId()){
            startRealName();
        }else if(view.getId() == ymBtUnrealname.getId()){
            if (realNameType == GUESTLOGINREALNAMETYPE){
                UserPresenter.cancelRealName(this,YmConstants.REALNAMESKIPSTATE,mAccountData);
            }else if (realNameType == UNLIMITREALNAMETYPE){
                UserPresenter.cancelRealName(this,YmConstants.REALNAMECALLBACKSTATE,null);
            }
        }else if (view.getId() == ymBtRealNameShort.getId()){
            startRealName();
        }
    }

    private void startRealName(){
        String name = ymRealName.getText().toString();
        String idCard = ymIdcardCode.getText().toString();

        if(name.isEmpty()&&idCard.isEmpty()){
            ToastUtils.showToast(baseActivity,getString(ResourseIdUtils.getStringId("ym_tip_realname1")));
            ymRealName.requestFocus();
        }else if(name.isEmpty()){
            ToastUtils.showToast(baseActivity,getString(ResourseIdUtils.getStringId("ym_tip_realname2")));
            ymRealName.requestFocus();
        }else if(idCard.isEmpty()){
            ToastUtils.showToast(baseActivity,getString(ResourseIdUtils.getStringId("ym_tip_realname3")));
            ymIdcardCode.requestFocus();
        }else if(!IdentityUtils.checkIDCard(idCard)){
            ToastUtils.showToast(baseActivity,getString(ResourseIdUtils.getStringId("ym_tip_realname5")));
            ymIdcardCode.requestFocus();
        }else{
            AccountBean accountBean = mAccountData;
            accountBean.setName(name);
            accountBean.setIdCard(idCard);
            UserPresenter.startRealName(this,accountBean);
        }
    }

    @Override
    public AccountBean getAccountData() {
        AccountBean accountData = (AccountBean) getArguments().getSerializable("accountBean");
        return accountData;
    }

    @Override
    public boolean onBackPressed() {

        return true;
    }
}
