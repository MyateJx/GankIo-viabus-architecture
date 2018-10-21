package com.gank;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.gank.business.GankBus;
import com.gank.business.GankBusiness;
import com.gank.business.MeiziBusiness;
import com.gank.business.SearchBusiness;
import com.gank.business.SettingBusiness;
import com.gank.data.AppDataManager;
import com.gank.util.LogUtils;
import com.squareup.leakcanary.LeakCanary;


/**
 * Created by Administrator on 2017/3/27 0027.
 */

public class GankApp extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        AppDataManager.getInstance().init(getApplicationContext());
        GankBus.registerRequestHandler(new GankBusiness());
        GankBus.registerRequestHandler(new MeiziBusiness());
        GankBus.registerRequestHandler(new SearchBusiness());
        GankBus.registerRequestHandler(new SettingBusiness());

        new LogUtils.Builder()
                .setLogSwitch(BuildConfig.DEBUG)// 设置log总开关，默认开
                .setGlobalTag("CMJ")// 设置log全局标签，默认为空
                // 当全局标签不为空时，我们输出的log全部为该tag，
                // 为空时，如果传入的tag为空那就显示类名，否则显示tag
                .setLog2FileSwitch(false)// 打印log时是否存到文件的开关，默认关
                .setBorderSwitch(true)// 输出日志是否带边框开关，默认开
                .setLogFilter(LogUtils.V);// log过滤器，和logcat过滤器同理，默认Verbose

        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(context))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(context))
                        .build());

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        //   LeakCanary.install(this);
    }

    public static Context getAppContext() {
        return context;
    }

}
