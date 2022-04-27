package com.actor.forced2sleep.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.actor.forced2sleep.R;
import com.actor.forced2sleep.databinding.ActivityNovelSpiderBinding;
import com.actor.forced2sleep.global.Global;
import com.actor.forced2sleep.novel.BaiDuNovelFinder;
import com.actor.forced2sleep.novel.BaseNovelFinder;
import com.actor.forced2sleep.novel.BingNovelFinder;
import com.actor.forced2sleep.novel.NovelUtils;
import com.actor.forced2sleep.novel.OnFindElementsListener;
import com.actor.forced2sleep.novel.OnParseElementsListener;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.SPUtils;
import com.actor.myandroidframework.utils.database.GreenDaoUtils;
import com.actor.myandroidframework.widget.ItemSpinnerLayout;
import com.actor.myandroidframework.widget.ItemTextInputLayout;
import com.blankj.utilcode.util.ToastUtils;
import com.greendao.gen.NovelBeanDao;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Map;

/**
 * description: 小说网站抓取
 * company    :
 * @author    : ldf
 * date       : 2022/4/26 on 9:33
 */
public class NovelSpiderActivity extends BaseActivity<ActivityNovelSpiderBinding> {

    private ItemTextInputLayout itilNovelName, itilPage, itilSearchResult, itilHandInputUrl, itilHandInputWebname;
    private ItemSpinnerLayout<String> islEngineType;

    private final NovelBeanDao    DAO = GreenDaoUtils.getDaoSession().getNovelBeanDao();
    private final StringBuilder   sb  = new StringBuilder();
    //小说搜索器
    private       BaseNovelFinder baiDuNovelFinder, bingNovelFinder, currentFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("小说网站抓取");
        itilNovelName = viewBinding.itilNovelName;
        itilPage = viewBinding.itilPage;
        itilSearchResult = viewBinding.itilSearchResult;
        islEngineType = viewBinding.islEngineType;
        itilHandInputUrl = viewBinding.itilHandInputUrl;
        itilHandInputWebname = viewBinding.itilHandInputWebname;

        itilNovelName.setText(SPUtils.getString(Global.NOVEL_NAME));
    }

    @Override
    public void onViewClicked(View view) {
        super.onViewClicked(view);
        switch (view.getId()) {
            case R.id.btn_search:
                if (isNoEmpty(itilNovelName, itilPage)) {
                    String novelPage = getText(itilPage);
                    int page = Integer.parseInt(novelPage);
                    if (page <= 0) {
                        toast("请输入正确的页数!");
                        return;
                    }
                    //小说名称
                    String novelName = getText(itilNovelName);
                    SPUtils.putString(Global.NOVEL_NAME, novelName);
                    switch (islEngineType.getSelectedItemPosition()) {
                        case 0:
                            //百度
                            itilSearchResult.setText("");
                            baiduFinder(novelName, page);
                            break;
                        case 1:
                            //bing
                            itilSearchResult.setText("");
                            bingFinder(novelName, page);
                            break;
                        default:
                            toast("该引擎暂未实现搜索功能!");
                            break;
                    }
                }
                break;
            case R.id.btn_add2db:
                if (isNoEmpty(itilHandInputWebname, itilHandInputUrl)) {
                    String urlText = getText(itilHandInputUrl);
                    String url = NovelUtils.filterUrl(urlText);
                    if (TextUtils.isEmpty(url)) {
                        toast("请输入正确的网址!");
                    } else {
                        String webName = getText(itilHandInputWebname);
                        NovelUtils.saveData2Db(DAO, url, webName);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 去百度查找小说
     * @param novelName 小说名称
     */
    private void baiduFinder(String novelName, int page) {
        if (baiDuNovelFinder == null) {
            baiDuNovelFinder = new BaiDuNovelFinder();
        }
        currentFinder = baiDuNovelFinder;
        showNetWorkLoadingDialog();
        baiDuNovelFinder.findNovelWeb(novelName, page, new MyOnFindElementsListener());
    }

    /**
     * 去百度查找小说
     * @param novelName 小说名称
     */
    private void bingFinder(String novelName, int page) {
        if (bingNovelFinder == null) {
            bingNovelFinder = new BingNovelFinder();
        }
        currentFinder = bingNovelFinder;
        showNetWorkLoadingDialog();
        bingNovelFinder.findNovelWeb(novelName, page, new MyOnFindElementsListener());
    }

    /**
     * 查找监听, 有默认处理方法
     */
    private class MyOnFindElementsListener implements OnFindElementsListener {
        @Override
        public void onSuccess(Elements elements) {
            currentFinder.parseElements(elements, new OnParseElementsListener() {
                @Override
                public void onSuccess(Map<String, String> URLS) {
                    dismissNetWorkLoadingDialog();
                    if (URLS.isEmpty()) {
                        return;
                    }
                    //过滤url, 返回过滤结果
                    Map<String, String> filterResult = NovelUtils.filterUrls(URLS);
                    //显示搜索结果
                    appendSearchResult(filterResult, true);
                    //保存进数据库, 返回插入成功的数据
                    Map<String, String> saveResult = NovelUtils.saveDatas2Db(DAO, filterResult);
                    //显示保存结果
                    appendSearchResult(saveResult, false);
                    //清空搜索数据
                    URLS.clear();
                    //其实下面2个是同一个引用
//                    filterResult.clear();
                    saveResult.clear();

                    //查找所有数据
//                    NovelUtils.showAllDb(DAO);
                }
                @Override
                public void onFail(Throwable t) {
                    dismissNetWorkLoadingDialog();
                    toastFormat("解析出错: %s", t);
                }
            });
        }
        @Override
        public void maybeError(Document document) {
            ToastUtils.showShort("请求可能出错了, 具体请看下方结果!");
            itilSearchResult.setText(document.toString());
        }

        @Override
        public void onFail(Throwable t) {
            dismissNetWorkLoadingDialog();
            toastFormat("查找失败: %s", t);
        }
    }

    /**
     * 显示搜索结果
     * @param URLS 搜索的结果
     * @param isSearchResultData 是否是搜索结果的数据
     */
    private void appendSearchResult(Map<String, String> URLS, boolean isSearchResultData) {
        //如果搜索结果为空
        if (isSearchResultData) {
//            sb.delete(0, sb.length());//耗时:471
            sb.setLength(0);//耗时:385
//            sb.replace(0, sb.length(), "");//耗时:??

            if (URLS.isEmpty()) {
                return;
            }
        }
        //如果是搜索结果
        if (isSearchResultData) {
            for (String key : URLS.keySet()) {
                sb.append(key);
                sb.append("\n");
            }
        } else {
            sb.append("\n插入到数据库的不重复数据:");
            if (URLS.isEmpty()) {
                sb.append(" 0");
            } else {
                sb.append("\n");
                LogUtils.error("//////////成功插入数据库的数据://////////\n");
                for (Map.Entry<String, String> entry : URLS.entrySet()) {
                    String url = entry.getKey();
                    String webName = entry.getValue();
                    sb.append(url);
                    sb.append("\n");
                    LogUtils.formatError("url = %s, webName = %s", url, webName);
                }
            }
        }
        itilSearchResult.setText(sb);
    }
}