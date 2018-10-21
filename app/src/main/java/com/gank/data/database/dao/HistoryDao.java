package com.gank.data.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.gank.data.database.entity.SearchHistory;

import java.util.List;

/**
 * @author MyateJx
 * @date 2018/10/9
 */
@Dao
public interface HistoryDao {

    @Query("SELECT * FROM SearchHistory")
    List<SearchHistory> getBeans();

    @Query("SELECT distinct searchContent FROM SearchHistory")
    List<String> getContents();

    @Query("SELECT * FROM SearchHistory WHERE id=:id")
    SearchHistory getBean(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(SearchHistory bean);

    @Update
    int update(SearchHistory bean);

    @Delete
    int delete(SearchHistory bean);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserts(List<SearchHistory> bean);

    @Update
    void updates(List<SearchHistory> bean);

    @Delete
    void deletes(List<SearchHistory> bean);

    @Query("Delete FROM SearchHistory")
    void deleteAll();

}
