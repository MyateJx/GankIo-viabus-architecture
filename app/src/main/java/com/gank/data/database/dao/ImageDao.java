package com.gank.data.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.gank.data.database.entity.Image;

import java.util.List;

/**
 * @author MyateJx
 * @date 2018/10/9
 */
@Dao
public interface ImageDao {

    @Query("SELECT * FROM Image")
    List<Image> getBeans();

    @Query("SELECT * FROM Image WHERE id=:id")
    List<Image> getBeans(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Image bean);

    @Update
    int update(Image bean);

    @Delete
    int delete(Image bean);

    @Query("Delete FROM Image where id=:id")
    int delete(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserts(List<Image> bean);

    @Update
    void updates(List<Image> bean);

    @Delete
    void deletes(List<Image> bean);
}
