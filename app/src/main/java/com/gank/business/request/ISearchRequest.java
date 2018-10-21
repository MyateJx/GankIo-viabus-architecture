package com.gank.business.request;

import com.kunminx.architecture.business.bus.IRequest;

/**
 * @author MyateJx
 * @date 2018/10/9
 */
public interface ISearchRequest extends IRequest {

    void search(String content, String type, String page);

    void insertHistory(String content);

    void querySearchHistory();

    void deleteAll();
}
