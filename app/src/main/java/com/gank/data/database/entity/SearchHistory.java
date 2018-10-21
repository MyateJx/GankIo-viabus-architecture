package com.gank.data.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;


/**
 * Created by Administrator on 2017/4/28 0028.
 */
@Entity
public class SearchHistory {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String searchContent;

    public SearchHistory(Long id, String searchContent) {
        this.id = id;
        this.searchContent = searchContent;
    }
    @Ignore
    public SearchHistory() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSearchContent() {
        return this.searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

}
