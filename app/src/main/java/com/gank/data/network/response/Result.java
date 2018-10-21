package com.gank.data.network.response;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.gank.data.database.AppDatabase;
import com.gank.data.database.entity.Image;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/21 0021.
 */
@Entity
public class Result implements Parcelable {
    /**
     * _id : 58d4e454421aa93abd1fd15a
     * createdAt : 2017-03-24T17:18:12.745Z
     * desc : RecyclerView侧滑菜单
     * images : ["http://img.gank.io/99a9d510-195d-4d50-a310-13b098c0c776"]
     * publishedAt : 2017-03-29T11:48:49.343Z
     * source : web
     * type : Android
     * url : http://www.jianshu.com/p/af9f940d8d1c
     * used : true
     * who : pss
     */
    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    private String id;
    @SerializedName("ganhuo_id")
    private String ganhuo_id;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("desc")
    private String desc;
    @SerializedName("publishedAt")
    private String publishedAt;
    @SerializedName("source")
    private String source;
    @SerializedName("type")
    private String type;
    @SerializedName("url")
    private String url;
    @SerializedName("used")
    private boolean used;
    @SerializedName("who")
    private String who;
    @SerializedName("images")
    @Ignore
    private List<String> images;

    //    @ToMany(referencedJoinProperty = "imageId")
    @Ignore
    private List<Image> img;


    public static Result objectFromData(String str) {

        return new Gson().fromJson(str, Result.class);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }


    @Ignore
    public List<Image> getImg() {
        if (img == null) {
            List<Image> imgNew = AppDatabase.getInstance().imageDao().getBeans(id);
            synchronized (this) {
                if (img == null) {
                    img = imgNew;
                }
            }
        }
        return img;
    }

    @Ignore
    public synchronized void resetImg() {
        img = null;
    }

    public void delete() {
        AppDatabase.getInstance().resultDao().delete(id);
    }

    public void update() {
        AppDatabase.getInstance().resultDao().delete(id);
    }


    public String getGanhuo_id() {
        return this.ganhuo_id;
    }

    public void setGanhuo_id(String ganhuo_id) {
        this.ganhuo_id = ganhuo_id;
    }

    @Ignore
    public Result() {
    }


    public Result(String id, String ganhuo_id, String createdAt, String desc, String publishedAt,
                  String source, String type, String url, boolean used, String who) {
        this.id = id;
        this.ganhuo_id = ganhuo_id;
        this.createdAt = createdAt;
        this.desc = desc;
        this.publishedAt = publishedAt;
        this.source = source;
        this.type = type;
        this.url = url;
        this.used = used;
        this.who = who;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.ganhuo_id);
        dest.writeString(this.createdAt);
        dest.writeString(this.desc);
        dest.writeString(this.publishedAt);
        dest.writeString(this.source);
        dest.writeString(this.type);
        dest.writeString(this.url);
        dest.writeByte(this.used ? (byte) 1 : (byte) 0);
        dest.writeString(this.who);
        dest.writeStringList(this.images);
        dest.writeList(this.img);
    }

    @Ignore
    protected Result(Parcel in) {
        this.id = in.readString();
        this.ganhuo_id = in.readString();
        this.createdAt = in.readString();
        this.desc = in.readString();
        this.publishedAt = in.readString();
        this.source = in.readString();
        this.type = in.readString();
        this.url = in.readString();
        this.used = in.readByte() != 0;
        this.who = in.readString();
        this.images = in.createStringArrayList();
        this.img = new ArrayList<Image>();
        in.readList(this.img, android.media.Image.class.getClassLoader());
    }

    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel source) {
            return new Result(source);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };
}
