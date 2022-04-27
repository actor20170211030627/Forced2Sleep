package com.actor.forced2sleep.novel;

import android.text.TextUtils;

import androidx.annotation.IntRange;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.TextUtils2;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ThreadUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * description: 百度查找小说
 *
 * 搜索"长夜余火"示例, 参数说明(https://blog.csdn.net/weixin_35938424/article/details/117524181):
 * 第1页:
 * https://www.baidu.com/s?
 *      wd=%E9%95%BF%E5%A4%9C%E4%BD%99%E7%81%AB
 *      &pn=0
 *      &oq=%E9%95%BF%E5%A4%9C%E4%BD%99%E7%81%AB
 *      &ie=utf-8
 *      &usm=1
 *      &rsv_pq=961783cc00118a6c
 *      &rsv_t=a517tCVJ%2BKOq39Ijb%2BxmMksJxjP7IkcfNl87ubhwhelW68qGLXoUqPVXHfs
 *
 * 第2页:
 * https://www.baidu.com/s?
 *      wd=%E9%95%BF%E5%A4%9C%E4%BD%99%E7%81%AB
 *      &pn=10
 *      &oq=%E9%95%BF%E5%A4%9C%E4%BD%99%E7%81%AB
 *      &ie=utf-8
 *      &usm=1
 *      &rsv_pq=c65cbe070017401b
 *      &rsv_t=fd5eqYpB4aFt18wiwrTXRMsLq0UInlFCJYGnimTGWViYTdBfP8USom0%2FBd4
 *
 * 第3页:
 * https://www.baidu.com/s?
 *      wd=%E9%95%BF%E5%A4%9C%E4%BD%99%E7%81%AB
 *      &pn=20
 *      &oq=%E9%95%BF%E5%A4%9C%E4%BD%99%E7%81%AB
 *      &ie=utf-8
 *      &usm=1
 *      &rsv_pq=9189e25b000c0fd9
 *      &rsv_t=e042GcwGqE0nAa19rwnTUWJDLfOHxHckzpcdn2ZSryVINVpDaS2B61HJsog
 *
 * url搜索参数说明:
 * wd: 长夜余火: 查询的关键词
 * pn: (Page Number)：显示结果的页数, pn = (page - 1) * 10
 * oq: 长夜余火: 你在输入搜索词时，输入一半，选择了下拉框之前输入的词；oq= 指的是搜索词，只有当url地址出现rsp而非rsv_bp时，oq才指的是从搜索词跳转到相关搜索
 * ie: (Input Encoding)：查询关键词的编码，缺省设置为简体中文，即ie=gb2312
 * usm: 在百度搜索任何词从任何一页点到第三页今后的，城市随机呈现usm的参数。当usm=0的时候是正常排名。当usm=1的时候所有排名后移一位，当usm=2的时候排名后移两位，以此类推。而词参数影响排名的结果只对第三页以及第三页今后的有效，也就是说前20名的排名不受词因素影响。此参数详细浸染未知；
 * rsv_pq: 搜索验证, 记录关键词和上一次搜素的关键词（相关关键词）的，需要解码；?
 * rsv_t: 搜索验证
 *
 *
 * 百度快照链接:
 * url                                                     <div class=                              <a class=(靠谱)      <span class=
 * 起点中文网                       c-row source_1Vdff OP_LOG_LINK c-gap-top-xsmall                  siteLink_9TPP3      c-color-gray
 * tieba.baidu.com/                c-row source_1Vdff  第2个div: site_3BHdI                                              c-color-gray
 * 百度百科                         c-row source_1Vdff source_1rJIg                                  siteLink_9TPP3      c-color-gray
 * www.zmccx.com/49_49001/         c-row source_1Vdff se-source_3S8D3                               siteLink_9TPP3      c-color-gray
 * www.xbiquge.la/15/42            c-row source_1Vdff se-source_3S8D3                               siteLink_9TPP3      c-color-gray
 * ww.zhuishuku.com/book/20895     c-row source_1Vdff se-source_3S8D3                               siteLink_9TPP3      c-color-gray
 * www.bswtan.com/52/52663         c-row source_1Vdff se-source_3S8D3                               siteLink_9TPP3      c-color-gray
 * 笔趣阁                           c-row source_1Vdff OP_LOG_LINK c-gap-top-xsmall source_s_3aixw   siteLink_9TPP3      c-color-gray
 * www.biqugesk.org/biquge/71454/  c-row source_1Vdff OP_LOG_LINK c-gap-top-xsmall source_s_3aixw   siteLink_9TPP3      c-color-gray
 * www.maxreader.net/read/hongmen  c-row source_1Vdff se-source_3S8D3                               siteLink_9TPP3      c-color-gray
 *
 *
 * company    :
 *
 * @author : ldf
 * date       : 2022/4/26 on 17
 * @version 1.0
 */
public class BaiDuNovelFinder implements BaseNovelFinder {

    private static final String BAIDU_SEARCH_URL = "https://www.baidu.com/s?" +
            "wd=%s" +
            "&pn=%d" +
            "&oq=%s" +
            "&ie=utf-8" +
            "&usm=1"// +
//            "&rsv_pq=9189e25b000c0fd9" +
//            "&rsv_t=e042GcwGqE0nAa19rwnTUWJDLfOHxHckzpcdn2ZSryVINVpDaS2B61HJsog"
            ;
    /**
     * 用来保存查找出来的url
     */
    private final Map<String, String> URLS = new LinkedHashMap<>();

    private long parseTime, parseBaiDuUrlCount;

    /**
     * 在百度/bing 查找小说网站: "百度快照"左侧的url
     * @param novelName 小说名称
     * @see org.jsoup.select.Selector
     */
    @Override
    public void findNovelWeb(String novelName, @IntRange(from = 1, to = 99) int page, OnFindElementsListener listener) {
        long start = System.currentTimeMillis();
        LogUtils.formatError("开始请求, start=%d", start);
        int pn = (page - 1) * 10;
        String searchUrl = TextUtils2.getStringFormat(BAIDU_SEARCH_URL, novelName, pn, novelName);
        ThreadUtils.executeByIo(new MySimpleTask<Elements>(listener) {
            @Override
            public Elements doInBackground() throws Throwable {
                /**
                 * <title>百度安全验证</title>
                 * https://blog.csdn.net/cute_boy_/article/details/106125023
                 */
                Document document = Jsoup.connect(searchUrl)
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.150 Safari/537.36")
                        .get();
//                Document document = Jsoup.parse(htmlStr);

                long end = System.currentTimeMillis();
                LogUtils.formatError("请求&解析Document对象完成, end=%d, 耗时=%d", end, end - start);

                //<a标签, 且class="siteLink_9TPP3"
                Elements select = document.select("a[class=siteLink_9TPP3]");

                parseTime = System.currentTimeMillis();
                LogUtils.formatError("查找<a标签完成, end=%d, 耗时=%d", parseTime, parseTime - end);

                if (parseTime - end <= 10) {
                    LogUtils.formatError("可能出错了, 请求内容: %s", document.toString());
                    if (findElementsListener != null) {
                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findElementsListener.maybeError(document);
                            }
                        });
                    }
                }
                return select;
            }

            @Override
            public void onSuccess(Elements result) {
                if (findElementsListener != null) {
                    findElementsListener.onSuccess(result);
                }
            }

            @Override
            public void onFail(Throwable t) {
                super.onFail(t);
                if (findElementsListener != null) {
                    findElementsListener.onFail(t);
                }
            }
        });
    }

    /**
     * 查找并解析成 Elements 成功
     * @param result
     * @param listener 解析监听
     * @return
     */
    @Override
    public void parseElements(Elements result, OnParseElementsListener listener) {
        parseBaiDuUrlCount = 0;
        for (Element element : result) {
            String string = element.toString();
            LogUtils.error(string);

//            String val = element.val();
//            String data = element.data();
//            /**
//             * <span class="c-color-gray" aria-hidden="true">笔趣阁</span>
//             * <span class="c-color-gray" aria-hidden="true">www.biquge.sh/book/290...</span>
//             */
//            String html = element.html();
//            String ownText = element.ownText();
//            /**
//             * whole /həʊl/ 全部的，整个的；完整的
//             * 笔趣阁, www.biquge.sh/book/290..., 起点中文网
//             */
//            String wholeText = element.wholeText();
//            //笔趣阁, www.biquge.sh/book/290..., 起点中文网
            String text = element.text();
//            LogUtils.formatError("val=%s, data=%s, html=%s, ownText=%s, wholeText=%s, text=%s", val, data, html, ownText, wholeText, text);

            boolean empty = TextUtils.isEmpty(text);

//            if (RegexUtils.isURL(text)) {
            if (empty || RegexUtils.isZh(text)) {
                String url = element.attr("href");
//                urls.put(url, false);
                getRealUrl(text, url, listener);
            } else {
                URLS.put(text, null);
            }
        }
        long forTime = System.currentTimeMillis();
        LogUtils.formatError("遍历完成<a标签完成, end=%d, 耗时=%d", forTime, forTime - parseTime);

        //解析完成, 回调
        if (parseBaiDuUrlCount == 0) {
            if (listener != null) {
                listener.onSuccess(URLS);
            }
        }
    }

    /**
     * 获取真实地址, 主要针对百度
     * @param webName 网站名称
     * @param encodeedUrl 加密的地址
     */
    @Override
    public void getRealUrl(String webName, String encodeedUrl, OnParseElementsListener listener) {
        parseBaiDuUrlCount ++;
        ThreadUtils.executeByIo(new MySimpleTask<Document>(webName, listener) {


            @Override
            public Document doInBackground() throws Throwable {
                Document document = Jsoup.connect(encodeedUrl).get();
                return document;
            }

            @Override
            public void onSuccess(Document result) {
                parseBaiDuUrlCount --;
                String baseUri = result.baseUri();
                //如果url为空
                if (TextUtils.isEmpty(baseUri)) {
                    LogUtils.formatError("解析的baseUri = null!!!, result = %s", result.toString());
                } else {
                    /**
                     * https://www.9game.cn/zyyh/gonglue-28-1/
                     * https://www.xbiquwx.la/57_57764/
                     * https://book.qidian.com/info/1023422452/
                     */
//                LogUtils.formatError("baseUri = %s", baseUri);
                    URLS.put(baseUri, webName);
                }

                //全部解析完成
                if (parseBaiDuUrlCount <= 0) {
                    if (parseElementsListener != null) {
                        parseElementsListener.onSuccess(URLS);
                    }
                }
            }

            //有些网站会400/404, 会进入这里
            @Override
            public void onFail(Throwable t) {
                super.onFail(t);
                parseBaiDuUrlCount --;
                if (parseBaiDuUrlCount <= 0) {
                    if (parseElementsListener != null) {
                        parseElementsListener.onFail(t);
                    }
                }
            }
        });
    }
}
