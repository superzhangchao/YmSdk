package com.ym.game.sdk.ui.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ym.game.sdk.bean.HistoryAccountBean;
import com.ym.game.sdk.common.utils.ResourseIdUtils;
import com.ym.game.sdk.ui.widget.AutoPopupWindow.IAutoPopSelectedListener;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * 弹出下拉列表的adapter
 */
public class HistoryPopAdapter extends BaseAdapter {

    private static final String TAG = "ymsdk";
    private Activity mActivity;
    private List<HistoryAccountBean> mDataList;
    private DisplayMetrics displayMetrics;
    private IAutoPopSelectedListener listener;
    private int backid;
    private AlertDialog alertDialog;
    private HashMap<Integer,View> lmap = new HashMap<Integer,View>();

    public HistoryPopAdapter(Activity context) {
        this.mActivity = context;
//        this.displayMetrics = displayMetrics;
    }

    public void notifyDataSetChanged(List<HistoryAccountBean> mdata) {
        this.mDataList = mdata;
        notifyDataSetChanged();

    }

    public void setListener(IAutoPopSelectedListener listener){
        this.listener = listener;
    }

    public List<HistoryAccountBean> getmDataList(){
        return mDataList;
    }

    @Override
    public int getCount() {
        if (mDataList == null)
            return 0;
        return mDataList.size();
    }

    @Override
    public HistoryAccountBean getItem(int position) {
        if (mDataList == null || mDataList.size() == 0)
            return null;
        return mDataList.get(mDataList.size()-position-1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
    public void setBackground(int backid) {
        this.backid = backid;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int currentPosition=position;
        ViewHolder holder ;
        if (lmap.get(position)==null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mActivity).inflate(ResourseIdUtils.getLayoutId("item_history_account"), null);

            holder.llContent = (LinearLayout) convertView.findViewById(ResourseIdUtils.getId("ll_content"));
            holder.tvPhone = (TextView) convertView.findViewById(ResourseIdUtils.getId("ym_tv_phone"));
            holder.tvTime = (TextView) convertView.findViewById(ResourseIdUtils.getId("ym_tv_time"));
            holder.imClose = (ImageView) convertView.findViewById(ResourseIdUtils.getId("ym_im_history_close"));
            int ymHistoryWidth = (int)mActivity.getResources().getDimension(ResourseIdUtils.getDimenId("ym_history_width"));
            int ymHistoryHeight = (int)mActivity.getResources().getDimension(ResourseIdUtils.getDimenId("ym_item_heigth"));
            LayoutParams params = new LayoutParams(ymHistoryWidth, ymHistoryHeight);
            convertView.setLayoutParams(params);
            lmap.put(position,convertView);
            convertView.setTag(holder);
        } else {
            convertView = lmap.get(position);
            holder = (ViewHolder) convertView.getTag();
        }

        final HistoryAccountBean mData = mDataList.get(mDataList.size()-currentPosition-1);
        if (mData.isCheck()){
            holder.llContent.setBackgroundResource(ResourseIdUtils.getMipmapId("ym_historybg_ck"));
        }
        holder.tvPhone.setText(mData.getPhone());
        holder.imClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deletDialog(currentPosition,mDataList);

            }
        });
//        long time = mData.getTime();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(mData.getLastLoginTime());//获取当前时间
        String curtime = formatter.format(curDate);
        holder.tvTime.setText("最后登录："+curtime);

        return convertView;
    }
    private void deletDialog(final int position, final List<HistoryAccountBean> dataList){

        SpannableString message = new SpannableString("您确定删除账号"+dataList.get(mDataList.size()-position-1).getPhone()+"?");
        message.setSpan(new ForegroundColorSpan(Color.RED),7,18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        AlertDialog dialog = new AlertDialog.Builder(mActivity)
                .setTitle("温馨提示")
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton(mActivity.getString(ResourseIdUtils.getStringId("ym_tv_cancel")), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(mActivity.getString(ResourseIdUtils.getStringId("ym_tv_detele")), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onItemDelet(position,dataList);
                        notifyDataSetChanged();
                    }
                })
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
    }



    class ViewHolder {
        LinearLayout llContent;
        TextView tvPhone;
        TextView tvTime;
        ImageView imClose;
    }
}