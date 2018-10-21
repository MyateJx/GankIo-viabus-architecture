package com.gank.business.request;

import com.gank.data.network.response.Result;
import com.gank.ui.order.Order;
import com.kunminx.architecture.business.bus.IRequest;

import java.util.List;

/**
 * @author MyateJx
 * @date 2018/10/9
 */
public interface IGankRequest extends IRequest {

    void queryIndexList(String path);

    void queryOrderList();

    void queryOrderString();

    void setOrderString(List<Order> orderList);

    void queryCollectionData(int page);

    void queryIsLike(String id);

    void addCollection(Result result);

    void cancelLike(String id);

    void queryCommonList(String path);

}
