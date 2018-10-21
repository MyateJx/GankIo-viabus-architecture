package com.gank.data.network.response;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/3/29 0029.
 */

public class ThemeResponse extends Response{


    @SerializedName("results")
    private List<Result> mResults;

    public static ThemeResponse objectFromData(String str) {

        return new Gson().fromJson(str, ThemeResponse.class);
    }

    public List<Result> getResults() {
        return mResults;
    }

    public void setResults(List<Result> results) {
        this.mResults = results;
    }

}
