package com.actor.forced2sleep.novel;

import androidx.annotation.IntRange;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.TextUtils2;
import com.blankj.utilcode.util.ThreadUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * description: bing查找小说
 *
 * 搜索"长夜余火"示例, 参数说明(https://docs.fuyeor.com/answer/6612.html):
 * 第1页:
 * https://cn.bing.com/search?
 *      q=%E9%95%BF%E5%A4%9C%E4%BD%99%E7%81%AB
 *      &form=QBLH
 *      &sp=-1
 *      &pq=%E9%95%BF%E5%A4%9C%E4%BD%99%E7%81%AB
 *      &sc=8-4
 *      &qs=n
 *      &sk=
 *      &cvid=24C94848E4654026B5960EF6CAF11D25
 *
 * 第2页:
 * https://cn.bing.com/search?
 *      q=%e9%95%bf%e5%a4%9c%e4%bd%99%e7%81%ab
 *      &sp=-1
 *      &pq=%e9%95%bf%e5%a4%9c%e4%bd%99%e7%81%ab
 *      &sc=8-4
 *      &qs=n
 *      &sk=
 *      &cvid=24C94848E4654026B5960EF6CAF11D25
 *      &first=10
 *      &FORM=PERE
 *
 * 第3页:
 * https://cn.bing.com/search?
 *      q=%e9%95%bf%e5%a4%9c%e4%bd%99%e7%81%ab
 *      &sp=-1
 *      &pq=%e9%95%bf%e5%a4%9c%e4%bd%99%e7%81%ab
 *      &sc=8-4
 *      &qs=n
 *      &sk=
 *      &cvid=24C94848E4654026B5960EF6CAF11D25
 *      &first=20
 *      &FORM=PERE1
 *
 * 点击搜索按钮:
 * https://cn.bing.com/search?
 *      q=%E9%95%BF%E5%A4%9C%E4%BD%99%E7%81%AB  长夜余火
 *      &go=%E6%90%9C%E7%B4%A2                  搜索, 从哪里发起搜索，例如点击搜索框的搜索图标发起搜索就是 &go=搜索，每个语言版本的 go 值不同
 *      &qs=ds
 *      &form=QBRE
 *
 * url搜索参数说明:
 * q: 查询的关键词
 * sp: SuggestionPosition，意思是你选择了搜索框下拉建议。比如你点击了第四个建议，就是 sp=4。如果没有选择建议，直接回车搜索，就会发起 &sp=-1
 * pq: PartialQuery，意思是你上一个搜索词是什么。这个参数用来关联搜索词和相关搜索，也用来统计用户行为
 * sc: SuggestionCount 。意思是你从第几个下拉框搜索建议中点击的，从 0 开始。比如一共有8个建议，而你点击了第四个建议，那么就会发起：sc=8-3
 * qs: 查询的 SuggestionType（查询类型）例如点击搜索下拉框建议就是 &qs=AS，直接发起搜索就是 &qs=ds，直接发起隐私搜索就是 &qs=n 等等
 * sk: SkipValue ，因为你可能会跳过结果页面
 * cvid: JavaScript 参数 ConversationId 。Bing 使用此键将你的搜索结果集合标识为对你的查询的回复
 * first: 从第几个结果开始显示。例如 first=100，就从第一百条结果（第十一页）开始显示
 * FORM: 搜索从何处发出，例如点击搜索框的搜索图标发起搜索就是 form=QBRE, QBLH?, PERE?, PQRE?, PERE1??
 *
 *
 * bing链接:
 * url                                                             <h2          <a h=
 * //page 3:
 * https://www.xxbiqudu.com/133_133821/178029062.html           ID=SERP,5105.1
 * https://m.xxbiqudu.com/133_133821_44_1                       ID=SERP,5122.1
 * https://weread.qq.com/web/reader/8fe327b071fdfc748fe8fe7     ID=SERP,5141.1
 *
 *
 * company    :
 *
 * @author : ldf
 * date       : 2022/4/26 on 21
 * @version 1.0
 */
public class BingNovelFinder implements BaseNovelFinder {

    private static final String              BING_SEARCH_URL = "https://cn.bing.com/search?" +
            "q=%s" +
            "&go=搜索" +
            "&form=QBRE" +
//            "&sp=-1" +
//            "&pq=%s" +
//            "&sc=8-4" +
            "&qs=ds" +
//            "&sk=" +
//            "&cvid=24C94848E4654026B5960EF6CAF11D25" +
            "&first=%d"
            ;
    /**
     * 用来保存查找出来的url
     */
    private final        Map<String, String> URLS            = new LinkedHashMap<>();

    private long parseTime;


    /**
     * 在百度/bing 查找小说网站: "百度快照"左侧的url
     * @param novelName 小说名称
     * @see org.jsoup.select.Selector
     */
    @Override
    public void findNovelWeb(String novelName, @IntRange(from = 1, to = 99) int page, OnFindElementsListener listener) {
        long start = System.currentTimeMillis();
        LogUtils.formatError("开始请求, start=%d", start);
        int first = page == 1 ? 1 : (page - 1) * 10;
        String searchUrl = TextUtils2.getStringFormat(BING_SEARCH_URL, novelName, first);
        ThreadUtils.executeByIo(new MySimpleTask<Elements>(listener) {
            @Override
            public Elements doInBackground() throws Throwable {
                Document document = Jsoup.connect(searchUrl).get();
//                Document document = Jsoup.parse(htmlStr);

                long end = System.currentTimeMillis();
                LogUtils.formatError("请求&解析Document对象完成, end=%d, 耗时=%d", end, end - start);

                //h2元素里的<a标签, 且<a 有 h= 属性
                Elements select = document.select("h2 a[h]");

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
        for (Element element : result) {
            //<a target="_blank" href="https://baike.baidu.com/item/%E9%95%BF%E5%A4%9C%E4%BD%99%E7%81%AB/54586531" h="ID=SERP,5142.1"><strong>长夜余火</strong>_百度百科</a>
            String string = element.toString();
            LogUtils.error(string);

//            String val = element.val();
//            String data = element.data();
//            /**
//             * <strong>长夜余火</strong>_百度百科
//             */
//            String html = element.html();
            //最新章节列表_笔趣阁
            String ownText = element.ownText();
//            /**
//             * whole /həʊl/ 全部的，整个的；完整的
//             * 长夜余火最新章节列表_笔趣阁
//             */
//            String wholeText = element.wholeText();
//            //长夜余火最新章节列表_笔趣阁
//            String text = element.text();
//            LogUtils.formatError("val=%s, data=%s, html=%s, ownText=%s, wholeText=%s, text=%s", val, data, html, ownText, wholeText, text);

            String url = element.attr("href");
            URLS.put(url, ownText);
        }
        long forTime = System.currentTimeMillis();
        LogUtils.formatError("遍历完成<a标签完成, end=%d, 耗时=%d", forTime, forTime - parseTime);

        //回调
        if (listener != null) {
            listener.onSuccess(URLS);
        }
    }

    /**
     * 获取真实地址, 主要针对百度
     * @param webName 网站名称
     * @param encodeedUrl 加密的地址
     */
    @Override
    public void getRealUrl(String webName, String encodeedUrl, OnParseElementsListener listener) {
        //bing没有百度的骚操作
    }
}
