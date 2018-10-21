package com.gank.business;

import com.gank.business.constant.GankResultCode;
import com.gank.business.request.IMeiziRequest;
import com.gank.data.AppDataManager;
import com.gank.data.DataManager;
import com.gank.data.network.response.ThemeResponse;
import com.kunminx.architecture.business.BaseBusiness;
import com.kunminx.architecture.business.bus.Result;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author MyateJx
 * @date 2018/10/9
 */
public class MeiziBusiness extends BaseBusiness<GankBus> implements IMeiziRequest {

    private DataManager mDataManager;

    private CompositeDisposable mCompositeDisposable;

    public DataManager getDataManager() {
        return mDataManager;
    }

    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    public MeiziBusiness() {
        this.mDataManager = AppDataManager.getInstance();
        this.mCompositeDisposable = AppDataManager.getInstance().getCompositeDisposable();
    }

    @Override
    public void queryMeiziList(String path) {
        getCompositeDisposable().add(
                getDataManager().getThemeDataCall(path)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ThemeResponse>() {
                            @Override
                            public void accept(ThemeResponse themeResponse) throws Exception {
                                GankBus.response(new Result(GankResultCode.MEIZI_LIST_QUERYED, themeResponse.getResults()));
                            }
                        }));
    }
}
