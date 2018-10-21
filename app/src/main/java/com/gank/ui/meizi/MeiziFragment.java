package com.gank.ui.meizi;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gank.Constants;
import com.gank.R;
import com.gank.business.GankBus;
import com.gank.business.constant.GankResultCode;
import com.gank.data.network.response.Result;
import com.gank.databinding.FragmentMzBinding;
import com.gank.ui.adapter.MeiziAdapter;
import com.gank.ui.adapter.base.MultiItemTypeAdapter;
import com.gank.ui.base.BaseEnum;
import com.gank.ui.base.BaseFragment;
import com.gank.util.LogUtils;
import com.gank.util.RecyclerViewUtil;
import com.kunminx.architecture.business.bus.IResponse;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/4/24 0024.
 */

public class MeiziFragment extends BaseFragment implements IResponse {

    private MeiziAdapter mMeiziAdapter;
    private int page;
    private boolean isRefresh;
    private List<Result> list = new ArrayList<>();

    private FragmentMzBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mz, container, false);
        mBinding = FragmentMzBinding.bind(view);
        GankBus.registerResponseObserver(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.toolbar.setTitle(R.string.main2);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mBinding.rvMz.setLayoutManager(staggeredGridLayoutManager);
        mMeiziAdapter = new MeiziAdapter(getActivity(), R.layout.item_mz, list);
        mBinding.rvMz.setAdapter(mMeiziAdapter);

        RecyclerViewUtil.setHeader(getActivity(), mBinding.refreshLayout);
        mBinding.refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page = 1;
                        isRefresh = true;
                        GankBus.meizi().queryMeiziList(BaseEnum.fuli.getValue() + "/" + Constants.PAGECOUNT + "/" + page);
                    }
                }, 0);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                LogUtils.v("onLoadmore");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GankBus.meizi().queryMeiziList(BaseEnum.fuli.getValue() + "/" + Constants.PAGECOUNT + "/" + page);
                    }
                }, 500);
            }

            @Override
            public void onFinishLoadMore() {
                if (Constants.ERROR) {
                    Constants.ERROR = false;
                } else {
                    page++;
                }
                LogUtils.v("onFinishLoadMore");
                super.onFinishLoadMore();
            }

        });

        mMeiziAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(getActivity(), view, getString(R.string.translate_mz));
                    startActivity(new Intent(getActivity(), MeiziDetailActivity.class).putExtra("img_url", list.get(position).getUrl()), options.toBundle());
                } else {
                    startActivity(new Intent(getActivity(), MeiziDetailActivity.class).putExtra("img_url", list.get(position).getUrl()));
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

        mBinding.refreshLayout.startRefresh();
    }

    @Override
    protected void refreshUI() {
        refreshToolbar(mBinding.toolbar);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GankBus.unregisterResponseObserver(this);
    }

    @Override
    public void onResult(com.kunminx.architecture.business.bus.Result testResult) {
        String resultCode = (String) testResult.getResultCode();
        switch (resultCode) {
            case GankResultCode.MEIZI_LIST_QUERYED:
                List<Result> list = (List<Result>) testResult.getResultObject();
                if (isRefresh) {
                    mMeiziAdapter.clearData();
                    isRefresh = false;
                }
                if (list == null) {
                    return;
                }
                RecyclerViewUtil.loadMoreSetting(mBinding.refreshLayout, list);
                mMeiziAdapter.addData(list);
                break;
            case GankResultCode.FAILURE:
                Constants.ERROR = true;
                mBinding.refreshLayout.finishLoadmore();
                break;
            default:
        }
    }
}
