package com.ym.game.plugin.google.dao;

import android.content.Context;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class DaoUtils {
    private static volatile DaoUtils instance;

    private static final String TAG = DaoUtils.class.getSimpleName();
    private static final boolean DUBUG = true;
    private DaoManager sManager;
    private DaoSession sDaoSession;
    private LocalPurchaseBeanDao localPurchaseBeanDao;

    public DaoUtils(Context context) {
        sManager = DaoManager.getInstance();
        sManager.init(context);
        sDaoSession = sManager.getDaoSession();
        sManager.setDebug(DUBUG);
    }

    public boolean insertPurchase(LocalPurchaseBean localPurchaseBean){
        boolean flag = false;
        flag = sManager.getDaoSession().insert(localPurchaseBean) != -1;
        return flag;
    }

    public boolean insertMultPurchase(List<LocalPurchaseBean> beanList){
        boolean flag = false;
        try {
            sManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (LocalPurchaseBean localPurchaseBean : beanList) {
                        sManager.getDaoSession().insertOrReplace(localPurchaseBean);
                    }
                }
            });
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    public boolean updatePurchase(LocalPurchaseBean localPurchaseBean){
        boolean flag = false;
        try{
            sManager.getDaoSession().update(localPurchaseBean);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;

    }

    public boolean deletePurchase(LocalPurchaseBean localPurchaseBean){
        boolean flag = false;
        try{
            //按照指定的id进行删除 delete from student where _id = ?
            sManager.getDaoSession().delete(localPurchaseBean);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    public void deleteAllPurchase(Class  clz){
        sManager.getDaoSession().deleteAll(clz);
    }

    public List<LocalPurchaseBean> queryAll(){
        return sManager.getDaoSession().loadAll(LocalPurchaseBean.class);

    }

    public LocalPurchaseBean queryOneById(long key){
        return sManager.getDaoSession().load(LocalPurchaseBean.class,key);
    }

    public void queryByNative(String where,String[] selectionArgs){
        List<LocalPurchaseBean> list = sManager.getDaoSession().queryRaw(LocalPurchaseBean.class, where, selectionArgs);

    }
    public void queryBuilder() {
        //查询构建器
//        QueryBuilder<LocalPurchaseBean> builder = sManager.getDaoSession().queryBuilder(LocalPurchaseBean.class);

    }

}
