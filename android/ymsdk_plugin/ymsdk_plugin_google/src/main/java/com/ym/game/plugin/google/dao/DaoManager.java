package com.ym.game.plugin.google.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.query.QueryBuilder;

public class DaoManager {
    private static  final String   TAG = DaoManager.class.getSimpleName();
    private static  final String  DB_NAME="localPurchase.db";//数据库名称
    private volatile  static DaoManager mDaoManager;//多线程访问
    private  static DaoMaster.DevOpenHelper mHelper;
    private static  DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;
    private static SQLiteDatabase db;
    private Context context;

    public static DaoManager getInstance(){
        if (mDaoManager==null){
            synchronized (DaoManager.class){
                if (mDaoManager==null){
                    mDaoManager = new DaoManager();
                }
            }
        }
        return mDaoManager;
    }

    public void init(Context context){
        this.context = context;
    }

    public DaoMaster getDaoMaster(){
        if (mDaoMaster==null){
            mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
            mDaoMaster = new DaoMaster(mHelper.getWritableDatabase());
        }
        return mDaoMaster;
    }

    public DaoSession getDaoSession(){
        if (mDaoSession==null){
            if (mDaoMaster==null){
                mDaoMaster = getDaoMaster();
            }
            mDaoSession= mDaoMaster.newSession();
        }
        return mDaoSession;
    }

    public void setDebug(boolean flag){
        QueryBuilder.LOG_SQL = flag;
        QueryBuilder.LOG_VALUES = flag;
    }

    public void closeDatabase(){
        closeHelper();
        closeSession();
    }

    private void closeHelper() {
        if (mHelper!=null){
            mHelper.close();
            mHelper = null;
        }
    }

    private void closeSession() {
        if (mDaoSession != null){
            mDaoSession.clear();
            mDaoSession = null;
        }
    }
}
