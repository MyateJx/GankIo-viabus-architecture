package com.gank.business;

import com.gank.business.request.IGankRequest;
import com.gank.business.request.IMeiziRequest;
import com.gank.business.request.ISearchRequest;
import com.gank.business.request.ISettingRequest;
import com.kunminx.architecture.business.bus.BaseBus;

/**
 * @author MyateJx
 * @date 2018/10/9
 */
public class GankBus extends BaseBus {

    public static IGankRequest gank() {
        return (IGankRequest) getRequest(IGankRequest.class);
    }

    public static IMeiziRequest meizi() {
        return (IMeiziRequest) getRequest(IMeiziRequest.class);
    }

    public static ISearchRequest search() {
        return (ISearchRequest) getRequest(ISearchRequest.class);
    }

    public static ISettingRequest setting() {
        return (ISettingRequest) getRequest(ISettingRequest.class);
    }
}
