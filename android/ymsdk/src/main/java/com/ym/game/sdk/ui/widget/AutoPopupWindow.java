package com.ym.game.sdk.ui.widget;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;



import com.ym.game.sdk.bean.HistoryAccountBean;
import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.ui.Adapter.HistoryPopAdapter;

import java.util.List;


public class AutoPopupWindow extends BasePopupWindow {

    private ListView mListView;
    private HistoryPopAdapter mAdapter;
    private IAutoPopSelectedListener mListener;
    
    public DisplayMetrics getDisplayMetrics() {
        return mOutMetrics;
    }

    public AutoPopupWindow(Activity context, IAutoPopSelectedListener listener) {
        super(context, null);
        mListener = listener;
    }

    public void setBaseAdapter(HistoryPopAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    public void initData() {
    	
        if (mAdapter == null){
        	
        	return;
        }
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    mAdapter.getItem(position).setCheck(true);

                mListener.onAutoPopSelected(mAdapter.getItem(position),position);
			}
		});
    }

    @Override
    public View initContentView(Bundle bundle) {
        View view = LayoutInflater.from(mContext).inflate(ResourseIdUtils.getLayoutId("ym_popup_history"), null);
        mListView = (ListView) view.findViewById(ResourseIdUtils.getId("ym_popuplist"));
        return view;
    }

    @Override
    public void resetTopArrowLocation(int offsetX) {
    }

    public interface IAutoPopSelectedListener {
        void onAutoPopSelected(HistoryAccountBean historyAccountBean, int position);
        void onItemDelet(int position, List<HistoryAccountBean> list);
    }
}
