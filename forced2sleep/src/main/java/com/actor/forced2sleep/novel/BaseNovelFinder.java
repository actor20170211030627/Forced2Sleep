package com.actor.forced2sleep.novel;

import androidx.annotation.IntRange;

import org.jsoup.select.Elements;

/**
 * description: 小说地址查找器基类
 * company    :
 *
 * @author : ldf
 * date       : 2022/4/26 on 17
 * @version 1.0
 */
public interface BaseNovelFinder {

    /**
     * 在百度/bing 查找小说网站
     * @param novelName 小说名称
     * @param page 第几页, 从1开始
     * @see org.jsoup.select.Selector
     */
    void findNovelWeb(String novelName, @IntRange(from = 1, to = 99) int page, OnFindElementsListener listener);

    /**
     * 查找并解析成 Elements 成功
     * @param result
     * @param listener 解析监听
     * @return
     */
    void parseElements(Elements result, OnParseElementsListener listener);

    /**
     * 从加密地址 获取真实地址, 主要针对百度
     * @param webName 网站名称
     * @param encodeedUrl 加密的地址
     */
    void getRealUrl(String webName, String encodeedUrl, OnParseElementsListener listener);
}
