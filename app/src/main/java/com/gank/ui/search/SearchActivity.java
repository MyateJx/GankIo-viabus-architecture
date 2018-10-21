package com.gank.ui.search;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;

import com.gank.R;
import com.gank.business.GankBus;
import com.gank.business.constant.GankResultCode;
import com.gank.databinding.ActivitySearchBinding;
import com.gank.ui.base.BaseActivity;
import com.gank.ui.base.BaseEnum;
import com.gank.util.ReavalUtils;
import com.kunminx.architecture.business.bus.IResponse;
import com.kunminx.architecture.business.bus.Result;


/**
 * Created by Administrator on 2017/4/26 0026.
 */

public class SearchActivity extends BaseActivity implements OnSelectListener, IResponse {

    private SearchResultFragment resultfragment;
    private SearchEmptyFragment emptyFragment;
    private String selectedType;
    private boolean mHasTheme = false;

    //覆盖父类方法 不初始化主题 使用透明主题
    @Override
    protected void initTheme() {

    }

    //覆盖父类方法 手动设置statusbar
    @Override
    public void setColorStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mHasTheme) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.color222222));
            } else {
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        }
        setFloatingActionButtonBgColor();
    }

    private void setFloatingActionButtonBgColor() {
        if (mHasTheme) {
            mBinding.floatButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.night_background)));
        } else {
            mBinding.floatButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
        }
    }

    private ActivitySearchBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GankBus.registerResponseObserver(this);
        GankBus.setting().queryTheme();
        super.onCreate(savedInstanceState);


        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupEnterAnimation();
            setupExitAnimation();
        } else {
            SearchActivity.super.initTheme();
            initViews();
            initContent();
        }
    }

    @Override
    protected void refreshUI() {

    }

    private void initContent() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        emptyFragment = SearchEmptyFragment.newInstance();
        fragmentTransaction.replace(R.id.fragmentContainer, emptyFragment);
        fragmentTransaction.commit();
    }

    private void initViews() {
        mBinding.toolbar.setVisibility(View.VISIBLE);
        initToolbar(mBinding.toolbar);
        //如果是夜间模式
        if (mHasTheme) {
            mBinding.toolbar.setBackgroundColor(getResources().getColor(R.color.color222222));
        } else {
            mBinding.toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        mBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();

            }
        });
        mBinding.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    switchFragment(fragmentTransaction, emptyFragment);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBinding.tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mBinding.editSearch.getText().toString())) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putString("content", mBinding.editSearch.getText().toString());
                    bundle.putString("type", selectedType == null ? BaseEnum.all.getValue() : selectedType);
                    if (resultfragment == null) {
                        resultfragment = SearchResultFragment.newInstance(bundle);
                    } else {
                        if (!resultfragment.isAdded()) {
                            resultfragment.setArguments(bundle);
                        } else {
                            resultfragment.loadData(mBinding.editSearch.getText().toString(), selectedType == null ? BaseEnum.all.getValue() : selectedType);
                            return;
                        }
                    }
                    switchFragment(fragmentTransaction, resultfragment);
                    //添加查询内容到数据库搜索历史记录
                    GankBus.search().insertHistory(mBinding.editSearch.getText().toString());
                } else {
                    Snackbar.make(mBinding.editSearch, "请输入搜索内容", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void switchFragment(FragmentTransaction ft, Fragment fragment) {
        ft.replace(R.id.fragmentContainer, fragment);
        ft.commit();
    }

    @Override
    public void onSelected(String type) {
        selectedType = type;
    }

    @Override
    public void onSearch(String searchContent) {
        mBinding.editSearch.setText(searchContent);
        mBinding.tvSearch.performClick();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.arc_motion);
        getWindow().setSharedElementEnterTransition(transition);
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                mBinding.floatButton.post(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.floatButton.setVisibility(View.INVISIBLE);
                    }
                });

                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    }

    // 动画展示
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void animateRevealShow() {
        ReavalUtils.animateRevealShow(
                this, mBinding.container,
                mBinding.floatButton.getWidth() / 2, R.color.white,
                new ReavalUtils.OnRevealAnimationListener() {
                    @Override
                    public void onRevealHide() {
                    }

                    @Override
                    public void onRevealShow() {
                    }

                    @Override
                    public void onRevealStart() {
                        SearchActivity.super.initTheme();
                        initViews();
                        initContent();
                    }
                }
        );
    }

    // 退出动画
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupExitAnimation() {
        Fade fadeTranslation = new Fade();
        getWindow().setReturnTransition(fadeTranslation);
        fadeTranslation.setDuration(500);
    }

    private void goBack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //效果不理想
            // animateRevealHide();
            mBinding.floatButton.setVisibility(View.VISIBLE);
            defaultBackPressed();

        } else {
            defaultBackPressed();
        }
    }

    private void animateRevealHide() {
        // 退出事件
        ReavalUtils.animateRevealHide(
                this, mBinding.container,
                mBinding.floatButton.getWidth() / 2, R.color.white,
                new ReavalUtils.OnRevealAnimationListener() {
                    @Override
                    public void onRevealHide() {
                        mBinding.floatButton.setVisibility(View.VISIBLE);
                        defaultBackPressed();
                    }

                    @Override
                    public void onRevealShow() {

                    }

                    @Override
                    public void onRevealStart() {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    // 默认回退
    private void defaultBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        GankBus.unregisterResponseObserver(this);
    }

    @Override
    public void onResult(Result testResult) {
        String resultCode = (String) testResult.getResultCode();
        switch (resultCode) {
            case GankResultCode.CHANGE_THEME:
                mHasTheme = (Boolean) testResult.getResultObject();
                break;
            default:
        }
    }
}

