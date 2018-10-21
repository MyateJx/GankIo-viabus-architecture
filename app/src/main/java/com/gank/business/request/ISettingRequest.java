package com.gank.business.request;

import com.kunminx.architecture.business.bus.IRequest;

/**
 * @author MyateJx
 * @date 2018/10/9
 */
public interface ISettingRequest extends IRequest {

    void queryTheme();

    void setTheme(boolean hasTheme);
}
