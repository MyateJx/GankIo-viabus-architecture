package com.gank.business.request;

import com.kunminx.architecture.business.bus.IRequest;

/**
 * @author MyateJx
 * @date 2018/10/9
 */
public interface IMeiziRequest extends IRequest {

    void queryMeiziList(String path);
}
