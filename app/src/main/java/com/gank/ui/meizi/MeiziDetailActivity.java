package com.gank.ui.meizi;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gank.R;
import com.gank.databinding.ActivcityMzDetailBinding;
import com.gank.ui.base.BaseActivity;

/**
 * Created by Administrator on 2017/4/30 0030.
 */

public class MeiziDetailActivity extends BaseActivity {

    private ActivcityMzDetailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activcity_mz_detail);
        initToolbar(mBinding.toolbar);
        mBinding.toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        mBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeiziDetailActivity.super.onBackPressed();
            }
        });
        Glide.with(this).load(getIntent().getStringExtra("img_url"))
                .diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().into(mBinding.photoView);
    }

    @Override
    protected void refreshUI() {

    }
}
