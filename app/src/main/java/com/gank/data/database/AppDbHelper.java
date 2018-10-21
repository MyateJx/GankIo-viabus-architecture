package com.gank.data.database;

import android.content.Context;

import com.gank.data.database.entity.Image;
import com.gank.data.database.entity.SearchHistory;
import com.gank.data.network.response.Result;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;

import static com.gank.Constants.PAGECOUNT;

/**
 * @author MyateJx
 * @date 2018/10/9
 */
public class AppDbHelper implements DbHelper {

    public AppDbHelper(Context context, String dbName) {
        AppDatabase.init(context, dbName);
    }

    @Override
    public Boolean getIsCollnection(String id) {
        return AppDatabase.getInstance().resultDao().getBean(id) != null;
    }

    @Override
    public Observable<List<Result>> queryForList(final int offset) {
        return Observable.fromCallable(new Callable<List<Result>>() {
            @Override
            public List<Result> call() throws Exception {
                return AppDatabase.getInstance().resultDao().getBeans(PAGECOUNT, (offset - 1) * PAGECOUNT);
            }
        });
    }

    @Override
    public void addSearchHistory(String content) {
        AppDatabase.getInstance().historyDao().insert(new SearchHistory(null, content));
    }

    @Override
    public Observable<List<String>> querySearchHistory() {
        return Observable.fromCallable(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                return AppDatabase.getInstance().historyDao().getContents();
            }
        });

    }

    @Override
    public void deleteSearchHistory() {
        AppDatabase.getInstance().historyDao().deleteAll();
    }

    @Override
    public void addConnection(Result result) {
        AppDatabase.getInstance().resultDao().insert(result);
    }

    @Override
    public void addImage(Image img) {
        AppDatabase.getInstance().imageDao().insert(img);
    }

    @Override
    public void cancelCollection(String id) {
        AppDatabase.getInstance().resultDao().delete(id);
        AppDatabase.getInstance().imageDao().delete(id);
    }


}
