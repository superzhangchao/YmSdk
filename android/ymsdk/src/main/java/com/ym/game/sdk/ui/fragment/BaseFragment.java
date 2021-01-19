package com.ym.game.sdk.ui.fragment;


import android.content.Context;


import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.ui.activity.BaseActivity;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class BaseFragment extends Fragment implements FragmentBackHandler {
    protected BaseActivity baseActivity;

    public static <T> T getFragmentByName(BaseActivity context, Class<T> clazz){
        FragmentFactory fragmentFactory = context.getSupportFragmentManager().getFragmentFactory();
        return (T) fragmentFactory.instantiate(context.getClassLoader(),clazz.getName());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        baseActivity = (BaseActivity) context;
    }

    /**
     * 跳转到其它Fragment
     * @param fragment
     */
    protected void redirectFragment(Fragment fragment){
        FragmentManager fragmentManager = baseActivity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(!fragment.isAdded()){
            transaction.hide(this);
            transaction.add(ResourseIdUtils.getId("content"),fragment);
            transaction.addToBackStack(null);
        }else{
            transaction.hide(this);
            transaction.show(fragment);
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 返回
     */
    protected void back(){
        baseActivity.onBackPressed();
    }

    protected void finishActivity(){
        baseActivity.finish();
    }

    @Override
    public boolean onBackPressed() {
        return BackHandlerHelper.handleBackPress(this);
    }

}
