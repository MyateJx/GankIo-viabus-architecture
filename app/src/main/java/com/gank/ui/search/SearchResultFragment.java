package com.gank.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gank.Constants;
import com.gank.R;
import com.gank.business.GankBus;
import com.gank.business.constant.GankResultCode;
import com.gank.data.network.response.Result;
import com.gank.databinding.FragmentSearchTwoBinding;
import com.gank.ui.adapter.CommonAdapter;
import com.gank.ui.base.BaseFragment;
import com.gank.ui.detail.DetailActivity;
import com.gank.util.ItemDecoration;
import com.gank.util.RecyclerViewUtil;
import com.gank.util.WrapContentLinearLayoutManager;
import com.kunminx.architecture.business.bus.IResponse;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/27 0027.
 */

public class SearchResultFragment extends BaseFragment implements IResponse {

    private CommonAdapter commonAdapter = new CommonAdapter();
    private int page;
    private boolean isRefresh = true;
    private String content;
    private String type;
    private List<Result> list = new ArrayList<>();

    public static SearchResultFragment newInstance(Bundle bundle) {
        SearchResultFragment fragment = new SearchResultFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private FragmentSearchTwoBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_two, container, false);
        mBinding = FragmentSearchTwoBinding.bind(view);
        GankBus.registerResponseObserver(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerView.addItemDecoration(new ItemDecoration(getActivity(), ItemDecoration.VERTICAL_LIST));
        RecyclerViewUtil.setHeader(getActivity(), mBinding.refreshLayout);
        mBinding.refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page = 1;
                        isRefresh = true;
                        GankBus.search().search(content, type, page + "");
                    }
                }, 0);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GankBus.search().search(content, type, page + "");
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
                super.onFinishLoadMore();
            }

        });

        commonAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                Result resultbean = list.get(position);
                intent.putExtra("bean", resultbean);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBinding.recyclerView.setAdapter(commonAdapter);
        loadData(getArguments().getString("content"), getArguments().getString("type"));
    }

    public void loadData(String content, String type) {
        this.content = content;
        this.type = type;
        mBinding.refreshLayout.startRefresh();
    }

    @Override
    protected void refreshUI() {

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
            case GankResultCode.SEARCH_LIST_QUERYED:
                List<Result> results = (List<Result>) testResult.getResultObject();
                if (isRefresh) {
                    list.clear();
                    isRefresh = false;
                }
                int start = list.size();
                list.addAll(results);
                RecyclerViewUtil.loadMoreSetting(mBinding.refreshLayout, list);
                commonAdapter.setSearchData(list, true);
                commonAdapter.notifyData(start, results.size());
                break;
            case GankResultCode.FAILURE:
                Constants.ERROR = true;
                mBinding.refreshLayout.onFinishLoadMore();
                break;
            default:
        }
    }
}
