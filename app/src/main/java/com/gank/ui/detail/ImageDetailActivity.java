package com.gank.ui.detail;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.gank.R;
import com.gank.databinding.ActicityImageDetailBinding;
import com.gank.ui.base.BaseActivity;

/**
 * Created by Administrator on 2017/4/17 0017.
 */

public class ImageDetailActivity extends BaseActivity {

    private ActicityImageDetailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.acticity_image_detail);

        String url = getIntent().getStringExtra("imgUrl");
        Glide.with(this).load(url).crossFade().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new GlideDrawableImageViewTarget(mBinding.photoView) {
                    @Override
                    public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                        super.onResourceReady(drawable, anim);
                        mBinding.pb.setVisibility(View.GONE);
                    }
                });
        initToolbar(mBinding.toolbar);
        mBinding.toolbar.setBackgroundColor(getResources().getColor(R.color.black));

    }

    @Override
    protected void refreshUI() {

    }
}
