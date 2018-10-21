package com.gank.data;

import android.content.Context;

import com.gank.Constants;
import com.gank.data.database.AppDbHelper;
import com.gank.data.database.DbHelper;
import com.gank.data.database.entity.Image;
import com.gank.data.network.ApiHelper;
import com.gank.data.network.AppApiHelper;
import com.gank.data.network.response.Result;
import com.gank.data.network.response.ThemeResponse;
import com.gank.data.preference.AppSharePreferences;
import com.gank.data.preference.SharePreferenecesHelper;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Administrator on 2017/3/29 0029.
 */
public class AppDataManager implements DataManager {

    private ApiHelper apiHelper;
    private DbHelper dbHelper;
    private SharePreferenecesHelper sharePreferenecesHelper;
    private CompositeDisposable mCompositeDisposable;

    private static AppDataManager sAppDataManager;

    public static AppDataManager getInstance() {
        if (sAppDataManager == null) {
            sAppDataManager = new AppDataManager();
        }
        return sAppDataManager;
    }

    private AppDataManager() {

    }

    public void init(Context context) {
        dbHelper = new AppDbHelper(context, Constants.DB_NAME);
        apiHelper = new AppApiHelper(context);
        sharePreferenecesHelper = new AppSharePreferences(context, Constants.PREFERENCE_NAME);
    }

    @Override
    public Observable<ThemeResponse> getThemeDataCall(String path) {
        return apiHelper.getThemeDataCall(path);
    }

    @Override
    public Observable<ThemeResponse> getSearchDataCall(String content, String type, String page) {
        return apiHelper.getSearchDataCall(content, type, page);
    }


    @Override
    public Boolean getIsCollnection(String id) {
        return dbHelper.getIsCollnection(id);
    }

    @Override
    public void addConnection(Result result) {
        dbHelper.addConnection(result);
    }

    @Override
    public void addImage(Image img) {
        dbHelper.addImage(img);
    }

    @Override
    public void cancelCollection(String id) {
        dbHelper.cancelCollection(id);
    }


    @Override
    public Observable<List<Result>> queryForList(int offset) {
        return dbHelper.queryForList(offset);
    }

    @Override
    public void addSearchHistory(String content) {
        dbHelper.addSearchHistory(content);
    }

    @Override
    public Observable<List<String>> querySearchHistory() {
        return dbHelper.querySearchHistory();
    }

    @Override
    public void deleteSearchHistory() {
        dbHelper.deleteSearchHistory();
    }

    @Override
    public void setOrder(String orderJsonStirng) {
        sharePreferenecesHelper.setOrder(orderJsonStirng);
    }

    @Override
    public String getOrderString() {
        return sharePreferenecesHelper.getOrderString();
    }

    @Override
    public void setTheme(boolean isNight) {
        sharePreferenecesHelper.setTheme(isNight);
    }

    @Override
    public boolean getTheme() {
        return sharePreferenecesHelper.getTheme();
    }

    public CompositeDisposable getCompositeDisposable() {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        return mCompositeDisposable;
    }

}
