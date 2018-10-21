package com.gank.ui.collection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gank.R;
import com.gank.business.GankBus;
import com.gank.business.constant.GankResultCode;
import com.gank.data.database.entity.Image;
import com.gank.data.network.response.Result;
import com.gank.databinding.FragmentCollectionBinding;
import com.gank.ui.adapter.CommonAdapter;
import com.gank.ui.base.BaseFragment;
import com.gank.ui.detail.DetailActivity;
import com.gank.util.ItemDecoration;
import com.gank.util.LogUtils;
import com.gank.util.RecyclerViewUtil;
import com.kunminx.architecture.business.bus.IResponse;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/20 0020.
 */

public class CollectionFragment extends BaseFragment implements IResponse {

    private int page = 1;
    private List<Result> list = new ArrayList<>();
    private CommonAdapter mCommonAdapter = new CommonAdapter();

    private FragmentCollectionBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
        mBinding = FragmentCollectionBinding.bind(view);
        GankBus.registerResponseObserver(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity.initToolbar(mBinding.toolbar);
        mBinding.toolbar.setTitle(R.string.main3);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mBinding.recyclerView.addItemDecoration(new ItemDecoration(getActivity(), ItemDecoration.VERTICAL_LIST));
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.recyclerView.setLayoutManager(linearLayoutManager);
        mBinding.recyclerView.setAdapter(mCommonAdapter);
        RecyclerViewUtil.setHeader(getActivity(), mBinding.refreshLayout);
        mBinding.refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page = 1;
                        list.clear();
                        GankBus.gank().queryCollectionData(page);
                    }
                }, 0);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                LogUtils.v("onLoadmore");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GankBus.gank().queryCollectionData(page);
                    }
                }, 500);
            }

            @Override
            public void onFinishLoadMore() {
                page++;
                LogUtils.v("onFinishLoadMore");
                super.onFinishLoadMore();
            }

        });

        mCommonAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                Result resultbean = list.get(position);
                if (resultbean.getImg() != null && resultbean.getImg().size() > 0) {
                    List<String> imgList = new ArrayList<String>();
                    for (Image image : resultbean.getImg()) {
                        imgList.add(image.getImageUrl());
                        resultbean.setImages(imgList);
                    }
                }
                intent.putExtra("bean", resultbean);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
            case GankResultCode.INDEX_LIST_QUERYED:
                List<Result> collections = (List<Result>) testResult.getResultObject();
                if (collections != null) {
                    list.addAll(collections);
                    RecyclerViewUtil.loadMoreSetting(mBinding.refreshLayout, list);
                    mCommonAdapter.setData(list, true);
                    mCommonAdapter.notifyDataSetChanged();
                }
                break;
            case GankResultCode.FAILURE:

                break;
            default:
        }
    }
}
