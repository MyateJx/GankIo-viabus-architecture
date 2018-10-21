package com.gank.data.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.gank.data.network.response.Result;

import java.util.List;

/**
 * @author MyateJx
 * @date 2018/10/9
 */
@Dao
public interface ResultDao {

    @Query("SELECT * FROM Result")
    List<Result> getBeans();

    @Query("SELECT * FROM Result WHERE id=:id")
    Result getBean(String id);

    @Query("SELECT * FROM Result limit :limit offset :offset")
    List<Result> getBeans(int limit, int offset);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Result bean);

    @Update
    int update(Result bean);

    @Delete
    int delete(Result bean);

    @Query("Delete FROM Result where id=:id")
    int delete(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserts(List<Result> bean);

    @Update
    void updates(List<Result> bean);

    @Delete
    void deletes(List<Result> bean);

    @Query("Delete FROM Result")
    void deleteAll();

}
