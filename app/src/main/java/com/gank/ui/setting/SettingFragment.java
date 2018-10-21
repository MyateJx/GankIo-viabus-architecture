package com.gank.ui.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.bumptech.glide.Glide;
import com.gank.R;
import com.gank.business.GankBus;
import com.gank.databinding.FragmentSettingBinding;
import com.gank.ui.base.LazyFragment;
import com.gank.ui.base.RxBus;
import com.gank.ui.collection.CollectionActivity;
import com.gank.util.DataCleanManager;

import static com.gank.Constants.isNight;

/**
 * Created by Administrator on 2017/5/12 0012.
 */

public class SettingFragment extends LazyFragment {

    private FragmentSettingBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        mBinding = FragmentSettingBinding.bind(view);
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.toolbar.setTitle("我的");
        mBinding.llCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CollectionActivity.class));
            }
        });
        //清除缓存
        mBinding.llClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataCleanManager.cleanApplicationData(getActivity(), new String[0]);
                Snackbar.make(mBinding.llClean, "清除缓存成功", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void loadData() {

        if (isNight) {
            Glide.with(getActivity()).load(R.drawable.night1).crossFade().into(mBinding.ivBg);
            mBinding.switchCompat.setChecked(true);
        } else {
            Glide.with(getActivity()).load(R.drawable.day1).crossFade().into(mBinding.ivBg);
            mBinding.switchCompat.setChecked(false);
        }
        mBinding.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isNight = true;
                    getActivity().setTheme(R.style.NightTheme);
                    Glide.with(getActivity()).load(R.drawable.night1).crossFade().into(mBinding.ivBg);

                } else {
                    isNight = false;
                    getActivity().setTheme(R.style.DayTheme);
                    Glide.with(getActivity()).load(R.drawable.day1).crossFade().into(mBinding.ivBg);
                }
                GankBus.setting().setTheme(isNight);
                RxBus.getInstance().post(isNight);

            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected void refreshUI() {
        refreshToolbar(mBinding.toolbar);
        TypedValue bottomline = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.bottomline, bottomline, true);
        mBinding.line1.setBackgroundResource(bottomline.resourceId);
        mBinding.line2.setBackgroundResource(bottomline.resourceId);
        mBinding.line3.setBackgroundResource(bottomline.resourceId);
    }
}
