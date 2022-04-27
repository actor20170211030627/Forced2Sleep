package com.actor.forced2sleep.novel;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * description: 查找 Elements 监听
 * company    :
 *
 * @author : ldf
 * date       : 2022/4/26 on 18
 * @version 1.0
 */
public interface OnFindElementsListener {

    /**
     * 查找成功
     * @param elements
     */
    public void onSuccess(Elements elements);

    /**
     * 请求可能出错了
     * @param document 请求解析的结果
     */
    public void maybeError(Document document);

    /**
     * 查找失败
     * @param t
     */
    default public void onFail(Throwable t) {
    }
}
