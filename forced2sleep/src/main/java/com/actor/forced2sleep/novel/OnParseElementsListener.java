package com.actor.forced2sleep.novel;

import java.util.Map;

/**
 * description: 解析Elements
 * company    :
 *
 * @author : ldf
 * date       : 2022/4/26 on 18
 * @version 1.0
 */
public interface OnParseElementsListener {

    /**
     * 解析成功
     * @param URLS
     */
    void onSuccess(Map<String, String> URLS);

    /**
     * 解析失败
     * @param t
     */
    void onFail(Throwable t);
}
