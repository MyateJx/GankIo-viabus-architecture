package com.gank.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gank.R;
import com.gank.business.GankBus;
import com.gank.business.constant.GankResultCode;
import com.gank.databinding.FragmentSearchFirstBinding;
import com.gank.ui.base.BaseEnum;
import com.gank.ui.base.BaseFragment;
import com.gank.util.DensityUtil;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.kunminx.architecture.business.bus.IResponse;
import com.kunminx.architecture.business.bus.Result;

import java.util.List;

/**
 * Created by Administrator on 2017/4/27 0027.
 */

public class SearchEmptyFragment extends BaseFragment implements IResponse {


    OnSelectListener listener;

    public static SearchEmptyFragment newInstance() {
        Bundle args = new Bundle();
        SearchEmptyFragment fragment = new SearchEmptyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void refreshUI() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchActivity) {
            listener = (OnSelectListener) context;
        }
    }

    private FragmentSearchFirstBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_first, container, false);
        mBinding = FragmentSearchFirstBinding.bind(view);
        GankBus.registerResponseObserver(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFlexbox();
    }

    private void initFlexbox() {
        mBinding.fblType.setFlexWrap(FlexWrap.WRAP);
        for (BaseEnum nameEnum : BaseEnum.values()) {
            final TextView tv_bq = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.tv_bq, mBinding.fblType, false);
            mBinding.fblType.addView(tv_bq);
            FlexboxLayout.LayoutParams layoutParams = (FlexboxLayout.LayoutParams) tv_bq.getLayoutParams();
            layoutParams.setMargins(DensityUtil.dip2px(getActivity(), 10),
                    DensityUtil.dip2px(getActivity(), 5), 0, DensityUtil.dip2px(getActivity(), 5));
            tv_bq.setText(nameEnum.getValue());
            mBinding.fblType.getChildAt(0).setSelected(true);
            tv_bq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mBinding.fblType.getChildCount(); i++) {
                        mBinding.fblType.getChildAt(i).setSelected(false);
                    }
                    tv_bq.setSelected(true);
                    listener.onSelected(tv_bq.getText().toString());
                }
            });
        }
        mBinding.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GankBus.search().deleteAll();
                GankBus.search().querySearchHistory();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GankBus.search().querySearchHistory();
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
            case GankResultCode.HISTORY_LIST_QUERYED:
                List<String> results = (List<String>) testResult.getResultObject();
                mBinding.fblHistory.setFlexWrap(FlexWrap.WRAP);
                mBinding.fblHistory.removeAllViews();
                for (String content : results) {
                    final TextView tv_his = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.tv_bq, mBinding.fblHistory, false);
                    mBinding.fblHistory.addView(tv_his);
                    tv_his.setText(content);
                    FlexboxLayout.LayoutParams layoutParams = (FlexboxLayout.LayoutParams) tv_his.getLayoutParams();
                    layoutParams.setMargins(DensityUtil.dip2px(getActivity(), 10),
                            DensityUtil.dip2px(getActivity(), 5), 0, DensityUtil.dip2px(getActivity(), 5));
                    tv_his.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onSearch(tv_his.getText().toString());
                        }
                    });
                }
                break;
            case GankResultCode.FAILURE:

                break;
            default:
        }
    }
}
