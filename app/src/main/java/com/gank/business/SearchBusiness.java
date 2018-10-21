package com.gank.business;

import com.gank.business.constant.GankResultCode;
import com.gank.business.request.ISearchRequest;
import com.gank.data.AppDataManager;
import com.gank.data.DataManager;
import com.gank.data.network.response.ThemeResponse;
import com.kunminx.architecture.business.BaseBusiness;
import com.kunminx.architecture.business.bus.Result;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author MyateJx
 * @date 2018/10/9
 */
public class SearchBusiness extends BaseBusiness<GankBus> implements ISearchRequest {

    private DataManager mDataManager;

    private CompositeDisposable mCompositeDisposable;

    public DataManager getDataManager() {
        return mDataManager;
    }

    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    public SearchBusiness() {
        this.mDataManager = AppDataManager.getInstance();
        this.mCompositeDisposable = AppDataManager.getInstance().getCompositeDisposable();
    }


    @Override
    public void search(String content, String type, String page) {
        getCompositeDisposable().add(getDataManager()
                .getSearchDataCall(content, type, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ThemeResponse>() {
                    @Override
                    public void accept(ThemeResponse themeResponse) throws Exception {
                        GankBus.response(new Result(GankResultCode.SEARCH_LIST_QUERYED, themeResponse.getResults()));
                    }
                }));
    }

    @Override
    public void insertHistory(String content) {
        getDataManager().addSearchHistory(content);
    }

    @Override
    public void querySearchHistory() {
        getCompositeDisposable().add(getDataManager()
                .querySearchHistory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> searchHistories) throws Exception {
                        GankBus.response(new Result(GankResultCode.HISTORY_LIST_QUERYED, searchHistories));
                    }
                })
        );
    }

    @Override
    public void deleteAll() {
        getDataManager().deleteSearchHistory();
    }

}
