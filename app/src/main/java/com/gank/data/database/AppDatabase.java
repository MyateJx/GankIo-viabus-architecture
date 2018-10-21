package com.gank.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.gank.data.database.dao.HistoryDao;
import com.gank.data.database.dao.ImageDao;
import com.gank.data.database.dao.ResultDao;
import com.gank.data.database.entity.Image;
import com.gank.data.database.entity.SearchHistory;
import com.gank.data.network.response.Result;

/**
 * @author MyateJx
 * @date 2018/6/30
 */
@Database(entities = {Result.class, Image.class, SearchHistory.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static String sDbPath = null;

    private static AppDatabase INSTANCE = null;

    private static Context mContext;


    public static void init(Context context, String dbPath) {
        mContext = context;
        sDbPath = dbPath;
        if (INSTANCE != null) {
            INSTANCE.close();
            INSTANCE = null;
        }
    }

    public synchronized static AppDatabase getInstance() {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(mContext.getApplicationContext(), AppDatabase.class, sDbPath)
                            .allowMainThreadQueries()
                            .build();
                }

            }
        }
        return INSTANCE;
    }

    public static RoomDatabase getDatabase(@NonNull Context context, String dbPath) {
        return Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, dbPath)
                .allowMainThreadQueries()
                .build();
    }

    public abstract HistoryDao historyDao();

    public abstract ImageDao imageDao();

    public abstract ResultDao resultDao();
}
