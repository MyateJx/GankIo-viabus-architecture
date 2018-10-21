package com.gank.business;

import com.gank.business.constant.GankResultCode;
import com.gank.business.request.IGankRequest;
import com.gank.data.AppDataManager;
import com.gank.data.DataManager;
import com.gank.data.database.entity.Image;
import com.gank.data.network.response.Result;
import com.gank.data.network.response.ThemeResponse;
import com.gank.ui.order.Order;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunminx.architecture.business.BaseBusiness;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * @author MyateJx
 * @date 2018/10/9
 */
public class GankBusiness extends BaseBusiness<GankBus> implements IGankRequest {
    private Gson gson = new Gson();

    private DataManager mDataManager;

    private CompositeDisposable mCompositeDisposable;

    public DataManager getDataManager() {
        return mDataManager;
    }

    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    public GankBusiness() {
        this.mDataManager = AppDataManager.getInstance();
        this.mCompositeDisposable = AppDataManager.getInstance().getCompositeDisposable();
    }

    @Override
    public void queryIndexList(String path) {
        final List<Result> results = new ArrayList<>();
        getCompositeDisposable().add(getDataManager()
                .getThemeDataCall(path)
                //将返回值转换为多个Observable<Result>
                .concatMap(new Function<ThemeResponse, ObservableSource<Result>>() {
                    @Override
                    public ObservableSource<Result> apply(ThemeResponse themeResponse) throws Exception {
                        if (!themeResponse.isError()) {
                            return Observable.fromIterable(themeResponse.getResults());
                        }
                        return null;
                    }
                })
                //过滤不需要的项目
                .filter(new Predicate<Result>() {
                    @Override
                    public boolean test(Result result) throws Exception {
                        return filter(result.getType());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Result>() {
                    @Override
                    public void accept(Result result) throws Exception {
                        results.add(result);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        GankBus.response(new com.kunminx.architecture.business.bus.Result(GankResultCode.INDEX_LIST_QUERYED, null));
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        GankBus.response(new com.kunminx.architecture.business.bus.Result(GankResultCode.INDEX_LIST_QUERYED, results));
                    }
                }));
    }

    private boolean filter(String requestType) {
        try {
            String type = URLDecoder.decode(requestType, "utf-8");
            if ("Android".equals(type) || "iOS".equals(type) || "前端".equals(type) || "拓展资源".equals(type)) {
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void queryOrderList() {
        handleRequest(new IAsync() {
            @Override
            public com.kunminx.architecture.business.bus.Result onExecute(ObservableEmitter<com.kunminx.architecture.business.bus.Result> e) throws IOException {
                String orderJsonString = getDataManager().getOrderString();
                List<Order> retList = gson.fromJson(orderJsonString, new TypeToken<List<Order>>() {
                }.getType());
                if (retList != null && retList.size() > 0) {
                    return new com.kunminx.architecture.business.bus.Result(GankResultCode.ORDER_LIST_QUERYED, retList);
                } else {
                    return new com.kunminx.architecture.business.bus.Result(GankResultCode.FAILURE, null);
                }
            }
        });
    }

    @Override
    public void queryOrderString() {
        handleRequest(new IAsync() {
            @Override
            public com.kunminx.architecture.business.bus.Result onExecute(ObservableEmitter<com.kunminx.architecture.business.bus.Result> e) throws IOException {
                String orderList = getDataManager().getOrderString();
                return new com.kunminx.architecture.business.bus.Result(GankResultCode.ORDER_STRING_QUERYED, orderList);
            }
        });

    }

    @Override
    public void setOrderString(final List<Order> orderList) {
        handleRequest(new IAsync() {
            @Override
            public com.kunminx.architecture.business.bus.Result onExecute(ObservableEmitter<com.kunminx.architecture.business.bus.Result> e) throws IOException {
                String jsonString = gson.toJson(orderList);
                getDataManager().setOrder(jsonString);
                return null;
            }
        });
    }

    @Override
    public void queryCollectionData(int page) {
        getCompositeDisposable().add(getDataManager()
                .queryForList(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Result>>() {
                    @Override
                    public void accept(List<Result> collections) throws Exception {
                        GankBus.response(new com.kunminx.architecture.business.bus.Result(GankResultCode.COLLECT_LIST_QUERYED, collections));
                    }
                }));
    }

    @Override
    public void queryIsLike(final String id) {
        handleRequest(new IAsync() {
            @Override
            public com.kunminx.architecture.business.bus.Result onExecute(ObservableEmitter<com.kunminx.architecture.business.bus.Result> e) throws IOException {
                boolean showLike = getDataManager().getIsCollnection(id);
                return new com.kunminx.architecture.business.bus.Result(GankResultCode.SHOW_LIKE, showLike);
            }
        });
    }

    @Override
    public void addCollection(final Result result) {
        handleRequest(new IAsync() {
            @Override
            public com.kunminx.architecture.business.bus.Result onExecute(ObservableEmitter<com.kunminx.architecture.business.bus.Result> e) throws IOException {
                getDataManager().addConnection(result);
                //本地存储图片
                if (result.getImages() != null) {
                    for (String imgUrl : result.getImages()) {
                        getDataManager().addImage(new Image(null, imgUrl, result.getId()));
                    }
                }
                return new com.kunminx.architecture.business.bus.Result(GankResultCode.SHOW_LIKE, true);
            }
        });
    }

    @Override
    public void cancelLike(final String id) {
        handleRequest(new IAsync() {
            @Override
            public com.kunminx.architecture.business.bus.Result onExecute(ObservableEmitter<com.kunminx.architecture.business.bus.Result> e) throws IOException {
                getDataManager().cancelCollection(id);
                return new com.kunminx.architecture.business.bus.Result(GankResultCode.SHOW_LIKE, false);
            }
        });
    }

    @Override
    public void queryCommonList(String path) {
        getCompositeDisposable().add(getDataManager()
                .getThemeDataCall(path)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ThemeResponse>() {
                    @Override
                    public void accept(ThemeResponse themeResponse) throws Exception {
                        GankBus.response(new com.kunminx.architecture.business.bus.Result(GankResultCode.COMMON_LIST_QUERYED, themeResponse.getResults()));
                    }
                }));
    }

}
