package com.gank.ui.main;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gank.R;
import com.gank.business.GankBus;
import com.gank.business.constant.GankResultCode;
import com.gank.databinding.FragmentMainBinding;
import com.gank.ui.base.LazyFragment;
import com.gank.ui.index.IndexFragment;
import com.gank.ui.order.Order;
import com.gank.ui.order.OrderActivity;
import com.gank.ui.search.SearchActivity;
import com.gank.ui.theme.CommonFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kunminx.architecture.business.bus.IResponse;
import com.kunminx.architecture.business.bus.Result;

import java.util.ArrayList;
import java.util.List;

import static com.gank.Constants.OPENSTATUS;
import static com.gank.ui.order.OrderActivity.ORDERCHANGE;

/**
 * Created by Administrator on 2017/3/29 0029.
 */

public class MainFragment extends LazyFragment implements IResponse {
    public static final String TAG = MainFragment.class.getSimpleName();

    private List<String> tabNames;
    private List<Fragment> fragmentList;

    public static Fragment newInstance() {
        return new MainFragment();
    }

    private FragmentStatePagerAdapter pagerAdapter;
    private boolean mHasTheme = false;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GankBus.setting().queryTheme();
        initfab();
        tabNames = new ArrayList<>();
        fragmentList = new ArrayList<>();
        //固定栏目
        fragmentList.add(IndexFragment.newInstance());
        tabNames.add(getResources().getString(R.string.title1));

        mBinding.tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        //初始化栏目数据
        GankBus.gank().queryOrderString();
    }

    public void onInitLoad(String orderString) {
        if ("".equals(orderString)) {
            mBinding.viewpager.setOffscreenPageLimit(4);
            tabNames.add(getResources().getString(R.string.title2));
            tabNames.add(getResources().getString(R.string.title3));
            tabNames.add(getResources().getString(R.string.title4));
            fragmentList.add(CommonFragment.newInstance("Android"));
            fragmentList.add(CommonFragment.newInstance("iOS"));
            fragmentList.add(CommonFragment.newInstance("前端"));
        } else {
            Gson gson = new Gson();
            List<Order> orders = gson.fromJson(orderString,
                    new TypeToken<List<Order>>() {
                    }.getType());
            for (Order order : orders) {
                if (order.getStatus() == OPENSTATUS) {
                    tabNames.add(order.getTheme());
                    fragmentList.add(CommonFragment.newInstance(order.getTheme()));
                }
            }
            mBinding.viewpager.setOffscreenPageLimit(fragmentList.size());
        }
        pagerAdapter = new ViewpagerAdapter(getChildFragmentManager());
        mBinding.viewpager.setAdapter(pagerAdapter);
        mBinding.tablayout.setupWithViewPager(mBinding.viewpager);
    }

    private void initfab() {
        mBinding.floatButton.setImageResource(mHasTheme ? R.drawable.ic_search_brone_24dp : R.drawable.ic_search_white_24dp);
    }

    @Override
    public void loadData() {

    }

    private FragmentMainBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GankBus.registerResponseObserver(this);
        mBinding = FragmentMainBinding.bind(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.iconAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), OrderActivity.class), 0);
            }
        });
        mBinding.floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                            getActivity(), mBinding.floatButton, mBinding.floatButton.getTransitionName());
                    startActivity(new Intent(getActivity(), SearchActivity.class), options.toBundle());
                } else {
                    startActivity(new Intent(getActivity(), SearchActivity.class));
                }
            }
        });
    }

    @Override
    protected void refreshUI() {
        TypedValue tablayoutcolor = new TypedValue();
        TypedValue addlayoutcolor = new TypedValue();
        TypedValue fbcolor = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.tablayoutbgcolor, tablayoutcolor, true);
        getActivity().getTheme().resolveAttribute(R.attr.addlayout, addlayoutcolor, true);
        getActivity().getTheme().resolveAttribute(R.attr.fbcolor, fbcolor, true);
        mBinding.tablayout.setBackgroundColor(getResources().getColor(tablayoutcolor.resourceId));
        mBinding.addlayout.setBackgroundColor(getResources().getColor(addlayoutcolor.resourceId));
        mBinding.floatButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(fbcolor.resourceId)));
        initfab();
    }

    @Override
    public void onResult(Result testResult) {
        String resultCode = (String) testResult.getResultCode();
        switch (resultCode) {
            case GankResultCode.ORDER_STRING_QUERYED:
                String orderList = (String) testResult.getResultObject();
                onInitLoad(orderList);
                break;
            case GankResultCode.CHANGE_THEME:
                mHasTheme = (Boolean) testResult.getResultObject();
                break;
            default:
        }
    }


    private class ViewpagerAdapter extends FragmentStatePagerAdapter {

        public ViewpagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabNames.get(position);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GankBus.unregisterResponseObserver(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ORDERCHANGE) {
            mBinding.viewpager.setCurrentItem(0);//放在后面的话会先刷新返回时的一页
            List<Order> orders = (List<Order>) data.getSerializableExtra("orderlist");
            tabNames.clear();
            fragmentList.clear();
            fragmentList.add(IndexFragment.newInstance());
            tabNames.add(getResources().getString(R.string.title1));
            for (Order order : orders) {
                if (order.getStatus() == OPENSTATUS) {
                    tabNames.add(order.getTheme());
                    fragmentList.add(CommonFragment.newInstance(order.getTheme()));
                }
            }

            mBinding.tablayout.setupWithViewPager(mBinding.viewpager);
            pagerAdapter.notifyDataSetChanged();
            mBinding.viewpager.setOffscreenPageLimit(fragmentList.size());
        }
    }
}
