package com.gank.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gank.R;
import com.gank.business.GankBus;
import com.gank.business.constant.GankResultCode;
import com.gank.data.network.response.Result;
import com.gank.databinding.ActivityDetailBinding;
import com.gank.ui.base.BaseActivity;
import com.gank.util.DensityUtil;
import com.kunminx.architecture.business.bus.IResponse;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2017/4/10 0010.
 */


public class DetailActivity extends BaseActivity implements IResponse {

    private String webUrl;

    private boolean isLike;

    private String detailId;
    private String imgUrl;

    private Result mResult;
    private boolean hasImage;

    private static Field sConfigCallback;

    static {
        try {
            sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback");
            sConfigCallback.setAccessible(true);
        } catch (Exception e) {
            // ignored
        }
    }

    private ActivityDetailBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GankBus.registerResponseObserver(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        mResult = getIntent().getParcelableExtra("bean");
        webUrl = mResult.getUrl();
        detailId = mResult.getGanhuo_id() == null ? mResult.getId() : mResult.getGanhuo_id();
        mResult.setId(detailId);
        initView();
        initWebview();
        initListener();
        GankBus.gank().queryIsLike(detailId);

    }

    @Override
    protected void refreshUI() {

    }


    private void initView() {

        //  initToolbar(toolbar);
        mBinding.toolbar.inflateMenu(R.menu.menu);
        mBinding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mBinding.toolbar.setTitle(mResult.getDesc());
        mBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (mResult.getImages() != null) {
            Glide.with(this)
                    .load(mResult.getImages().get(0) + DensityUtil.sizeOfImageforFullWidth(this, 200))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .crossFade()
                    .centerCrop()
                    .into(mBinding.ivHead);
            hasImage = true;
        } else {
            mBinding.ivHead.setVisibility(View.GONE);
            mBinding.space.setVisibility(View.GONE);
            hasImage = false;
        }
        mBinding.pb.setVisibility(View.VISIBLE);
        mBinding.space.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.ivHead.performClick();
            }
        });
        mBinding.ivHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DetailActivity.this, ImageDetailActivity.class).putExtra("imgUrl", mResult.getImages().get(0)));
            }
        });
    }

    private void initListener() {
        addScrollListener();
        mBinding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_like:
                        if (isLike) {
                            GankBus.gank().cancelLike(detailId);
                            Snackbar.make(mBinding.toolbar, "已取消收藏", Snackbar.LENGTH_SHORT).show();
                        } else {
                            GankBus.gank().addCollection(mResult);
                            Snackbar.make(mBinding.toolbar, "已收藏", Snackbar.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                }
                return false;
            }
        });
    }

    private void initWebview() {
        WebSettings webSettings = mBinding.webview.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBuiltInZoomControls(true);

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(mBinding.webview.getContext().getCacheDir().getAbsolutePath());

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//网络正常时使用默认缓存策略
        } else {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ONLY);//网络不可用时只使用缓存
        }

        mBinding.webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mBinding.pb.setVisibility(View.VISIBLE);
                view.loadUrl(url);
                if (hasImage) {
                    mBinding.scrollView.setScrollY(DensityUtil.dip2px(DetailActivity.this, 200));
                } else {
                    mBinding.scrollView.setScrollY(0);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //使webview切换页面后高度自适应，防止留下大片空白
                view.loadUrl("javascript:App.resize(document.body.getBoundingClientRect().height)");
                mBinding.pb.setVisibility(View.GONE);
            }
        });
        mBinding.webview.addJavascriptInterface(this, "App");
        mBinding.webview.loadUrl(webUrl);

    }


    @JavascriptInterface
    public void resize(final float height) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mBinding.webview.setLayoutParams(new FrameLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, (int) (height * getResources().getDisplayMetrics().density)));
            }
        });

    }

    private void addScrollListener() {
        mBinding.scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (hasImage) {
                    changeHeaderPosition();
                    changeToolbarAlpha();
                }
            }
        });
    }

    //调整toolbar透明度
    private void changeToolbarAlpha() {
        int scrollY = mBinding.scrollView.getScrollY();
        float contentHeight = mBinding.ivHead.getHeight();
        float ratio = Math.max(1 - scrollY / contentHeight, 0);
        if (ratio == 0) {
            mBinding.toolbar.getBackground().mutate().setAlpha(0xFF);

        } else {
            mBinding.toolbar.getBackground().mutate().setAlpha((int) (ratio * 0xFF));
        }

    }

    private void changeHeaderPosition() {
        int scrollY = mBinding.scrollView.getScrollY();
        int headerScrollY = (scrollY > 0) ? (scrollY / 2) : 0;
        mBinding.headlayout.setScrollY(headerScrollY);
        mBinding.headlayout.requestLayout();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mBinding.webview.canGoBack()) {
                mBinding.webview.goBack();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        try {
            if (sConfigCallback != null) {
                sConfigCallback.set(null, null);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        destroy();
        super.onDestroy();
        GankBus.unregisterResponseObserver(this);
    }

    public void destroy() {
        if (mBinding.webview != null) {
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            ViewParent parent = mBinding.webview.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mBinding.webview);
            }

            mBinding.webview.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            mBinding.webview.getSettings().setJavaScriptEnabled(false);
            mBinding.webview.clearHistory();
            mBinding.webview.clearView();
            mBinding.webview.removeAllViews();

            try {
                mBinding.webview.destroy();
            } catch (Throwable ex) {

            }
        }
    }

    @Override
    public void onResult(com.kunminx.architecture.business.bus.Result testResult) {
        String resultCode = (String) testResult.getResultCode();
        switch (resultCode) {
            case GankResultCode.SHOW_LIKE:
                boolean showLike = (Boolean) testResult.getResultObject();
                isLike = showLike;
                mBinding.toolbar.getMenu().getItem(0).setIcon(
                        isLike ? R.drawable.ic_star_black_24dp_red : R.drawable.ic_star_black_24dp);
                break;
            case GankResultCode.FAILURE:

                break;
            default:
        }
    }
}
