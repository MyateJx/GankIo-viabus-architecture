package com.gank.business;

import com.gank.business.constant.GankResultCode;
import com.gank.business.request.ISettingRequest;
import com.gank.data.AppDataManager;
import com.gank.data.DataManager;
import com.gank.util.LogUtils;
import com.kunminx.architecture.business.BaseBusiness;
import com.kunminx.architecture.business.bus.Result;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

/**
 * @author MyateJx
 * @date 2018/10/9
 */
public class SettingBusiness extends BaseBusiness<GankBus> implements ISettingRequest {

    private DataManager mDataManager;

    private CompositeDisposable mCompositeDisposable;

    public DataManager getDataManager() {
        return mDataManager;
    }

    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    public SettingBusiness() {
        this.mDataManager = AppDataManager.getInstance();
        this.mCompositeDisposable = AppDataManager.getInstance().getCompositeDisposable();
    }

    @Override
    public void queryTheme() {
        boolean hasTheme = getDataManager().getTheme();
        GankBus.response(new Result(GankResultCode.CHANGE_THEME, hasTheme));
    }

    @Override
    public void setTheme(boolean isNight) {
        mDataManager.setTheme(isNight);
    }
}
