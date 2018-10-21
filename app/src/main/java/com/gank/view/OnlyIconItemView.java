package com.gank.view;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.gank.R;

import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;

/**
 * Created by Administrator on 2017/4/20 0020.
 */

public class OnlyIconItemView extends BaseTabItem{
    private ImageView mIcon;

    private int mDefaultDrawable;
    private int mCheckedDrawable;

    public OnlyIconItemView(Context context) {
        this(context,null);
    }

    public OnlyIconItemView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public OnlyIconItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.item_only_icon, this, true);

        mIcon = (ImageView) findViewById(R.id.icon);
    }

    public void initialize(@DrawableRes int drawableRes, @DrawableRes  int checkedDrawableRes)
    {
        mDefaultDrawable = drawableRes;
        mCheckedDrawable = checkedDrawableRes;
    }

    @Override
    public void setChecked(boolean checked) {
        mIcon.setImageResource(checked ? mCheckedDrawable : mDefaultDrawable);
    }

    @Override
    public void setMessageNumber(int number) {
        //不需要就不用管
    }

    @Override
    public void setHasMessage(boolean hasMessage) {
        //不需要就不用管
    }

    @Override
    public String getTitle() {
        return "no title";
    }
}
