package com.ym.game.sdk.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.ym.game.sdk.bean.PurchaseBean;
import com.ym.game.sdk.presenter.PurchasePresenter;
import com.ym.game.sdk.ui.fragment.PurchaseFragment;

import androidx.annotation.Nullable;

import static com.ym.game.sdk.common.base.config.TypeConfig.LOGIN;
import static com.ym.game.sdk.common.base.config.TypeConfig.PAY;

public class YmPurchaseActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        PurchaseBean purchaseBean = (PurchaseBean) getIntent().getSerializableExtra("purchaseBean");
        PurchaseFragment purchaseFragment = PurchaseFragment.getFragmentByName(this, PurchaseFragment.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("purchaseBean", purchaseBean);
        purchaseFragment.setArguments(bundle);
        initFragment(purchaseFragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onResume() {
        super.onResume();
        PurchasePresenter.onResume(this);
    }
}


