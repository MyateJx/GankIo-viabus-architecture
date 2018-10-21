package com.gank.ui.index;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gank.Constants;
import com.gank.R;
import com.gank.business.GankBus;
import com.gank.business.constant.GankResultCode;
import com.gank.data.network.response.Result;
import com.gank.databinding.FragmentIndexBinding;
import com.gank.ui.adapter.IndexAdapter;
import com.gank.ui.base.BaseFragment;
import com.gank.ui.detail.DetailActivity;
import com.gank.ui.detail.ImageDetailActivity;
import com.gank.util.LogUtils;
import com.kunminx.architecture.business.bus.IResponse;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout;

import java.util.ArrayList;
import java.util.List;

import static com.gank.util.RecyclerViewUtil.invalidateCacheItem;

/**
 * @author MyateJx
 * @date 2018/10/9
 */
public class IndexFragment extends BaseFragment implements IResponse {

    private IndexAdapter mAdapter = new IndexAdapter();
    private int page = 1;
    private FragmentIndexBinding mBinding;

    public static BaseFragment newInstance() {
        BaseFragment fragment = new IndexFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index, container, false);
        mBinding = FragmentIndexBinding.bind(view);
        GankBus.registerResponseObserver(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter.notifyData(new ArrayList<Result>());
        mBinding.rvIndex.setAdapter(mAdapter);
        mBinding.rvIndex.setItemAnimator(new DefaultItemAnimator());
        //吸顶布局滚动监听
        mBinding.rvIndex.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                View stickyInfoView = recyclerView.findChildViewUnder(mBinding.stickyView.getMeasuredWidth() / 2, 5);

                if (stickyInfoView != null && stickyInfoView.getContentDescription() != null) {
                    mBinding.stickyView.setText(String.valueOf(stickyInfoView.getContentDescription()));
                }

                View transInfoView = recyclerView.findChildViewUnder(
                        mBinding.stickyView.getMeasuredWidth() / 2, mBinding.stickyView.getMeasuredHeight() + 1);

                if (transInfoView != null && transInfoView.getTag() != null) {

                    int transViewStatus = (int) transInfoView.getTag();
                    int dealtY = transInfoView.getTop() - mBinding.stickyView.getMeasuredHeight();

                    if (transViewStatus == IndexAdapter.HAS_STICKY_VIEW) {
                        if (transInfoView.getTop() > 0) {
                            mBinding.stickyView.setTranslationY(dealtY);
                        } else {
                            mBinding.stickyView.setTranslationY(0);
                        }
                    } else if (transViewStatus == IndexAdapter.NONE_STICKY_VIEW) {
                        mBinding.stickyView.setTranslationY(0);
                    }
                }
            }
        });
        mAdapter.setOnItemClickListener(new IndexAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("bean", mAdapter.getList().get(position));
                startActivity(intent);
            }

            @Override
            public void onImageViewClick(View v, int position) {
                Intent intent = new Intent(getActivity(), ImageDetailActivity.class);
                intent.putExtra("imgUrl", mAdapter.getList().get(position).getImages().get(0));
                startActivity(intent);
            }
        });
        ProgressLayout header = new ProgressLayout(getActivity());
        header.setColorSchemeResources(R.color.Blue, R.color.Orange, R.color.Yellow, R.color.Green);
        mBinding.refreshLayout.setHeaderView(header);
        mBinding.refreshLayout.setFloatRefresh(true);
        mBinding.refreshLayout.setOverScrollRefreshShow(false);
        mBinding.refreshLayout.setAutoLoadMore(true);
        mBinding.refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page = 1;
                        mAdapter.getList().clear();
                        GankBus.gank().queryIndexList("all/" + Constants.PAGECOUNT + "/" + page);
                    }
                }, 0);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                LogUtils.v("onLoadmore");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GankBus.gank().queryIndexList("all/" + Constants.PAGECOUNT + "/" + page);
                        LogUtils.v(page);

                    }
                }, 1000);
            }

            @Override
            public void onLoadmoreCanceled() {
                LogUtils.v("onLoadmoreCanceled");
                super.onLoadmoreCanceled();
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
        mBinding.refreshLayout.startRefresh();
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
            case GankResultCode.INDEX_LIST_QUERYED:
                List<Result> list = (List<Result>) testResult.getResultObject();
                if (list != null) {
                    mAdapter.getList().addAll(list);
                    mBinding.refreshLayout.finishRefreshing();
                    mBinding.refreshLayout.finishLoadmore();
                }
                break;
            case GankResultCode.FAILURE:
                LogUtils.v("error");
                Constants.ERROR = true;
                mBinding.refreshLayout.finishRefreshing();
                mBinding.refreshLayout.finishLoadmore();
                break;
            default:
        }
    }

    @Override
    protected void refreshUI() {
        TypedValue bground_itemcolor = new TypedValue();
        TypedValue textcolor = new TypedValue();
        TypedValue toplinecolor = new TypedValue();
        TypedValue bottomlinecolor = new TypedValue();
        TypedValue topviewcolor = new TypedValue();
        TypedValue toptextcolor = new TypedValue();
        Resources.Theme theme = mActivity.getTheme();
        theme.resolveAttribute(R.attr.backgroundcolor_item, bground_itemcolor, true);
        theme.resolveAttribute(R.attr.textcolor, textcolor, true);
        theme.resolveAttribute(R.attr.topline, toplinecolor, true);
        theme.resolveAttribute(R.attr.bottomline, bottomlinecolor, true);
        theme.resolveAttribute(R.attr.topview, topviewcolor, true);
        theme.resolveAttribute(R.attr.toptextcolor, toptextcolor, true);
        Resources resources = getResources();
        mBinding.stickyView.setBackgroundColor(resources.getColor(topviewcolor.resourceId));
        mBinding.stickyView.setTextColor(resources.getColor(toptextcolor.resourceId));
        int childCount = mBinding.rvIndex.getChildCount();
        for (int childIndex = 0; childIndex < childCount; childIndex++) {
            LinearLayout ll = (LinearLayout) mBinding.rvIndex.getChildAt(childIndex).findViewById(R.id.item_container);
            ll.setBackgroundColor(resources.getColor(bground_itemcolor.resourceId));
            TextView title = (TextView) ll.findViewById(R.id.tv_title);
            TextView tv_top = (TextView) ll.findViewById(R.id.tv_top);
            View view1 = ll.findViewById(R.id.topline);
            View view2 = mBinding.rvIndex.getChildAt(childIndex).findViewById(R.id.bottomline);
            title.setTextColor(resources.getColor(textcolor.resourceId));
            tv_top.setTextColor(resources.getColor(toptextcolor.resourceId));
            tv_top.setBackgroundColor(resources.getColor(topviewcolor.resourceId));
            view1.setBackgroundColor(resources.getColor(toplinecolor.resourceId));
            view2.setBackgroundColor(resources.getColor(bottomlinecolor.resourceId));
        }
        invalidateCacheItem(mBinding.rvIndex);
    }

}
