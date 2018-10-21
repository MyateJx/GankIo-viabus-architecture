package com.gank.data.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Administrator on 2017/4/17 0017.
 */
@Entity
public class Image implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String imageUrl;
    private String imageId;

    public Image(Long id, String imageUrl, String imageId) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.imageId = imageId;
    }

    @Ignore
    public Image(String imageId) {
        this.imageId = imageId;
    }
    @Ignore
    public Image() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageId() {
        return this.imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.imageUrl);
        dest.writeString(this.imageId);
    }
    @Ignore
    private Image(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.imageUrl = in.readString();
        this.imageId = in.readString();
    }

    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
