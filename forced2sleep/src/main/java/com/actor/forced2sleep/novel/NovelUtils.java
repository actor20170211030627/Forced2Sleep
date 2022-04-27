package com.actor.forced2sleep.novel;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.actor.forced2sleep.bean.NovelBean;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.database.GreenDaoUtils;
import com.greendao.gen.NovelBeanDao;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * description: 小说工具类
 * company    :
 *
 * @author : ldf
 * date       : 2022/4/26 on 18
 * @version 1.0
 */
public class NovelUtils {

    private static final Map<String, String> FILTER_RESULT = new LinkedHashMap<>();

    /**
     * 过滤url: https://book.qidian.com/info/1023422452/ => book.qidian.com
     * @return 返回过滤后的数据
     */
    public static Map<String, String> filterUrls(Map<String, String> URLS) {
        FILTER_RESULT.clear();
        for (Map.Entry<String, String> entry : URLS.entrySet()) {
            String url = entry.getKey();
            String webName = entry.getValue();
            LogUtils.formatError("url = %s, webName = %s", url, webName);

            url = filterUrl(url);
            if (TextUtils.isEmpty(url)) {
                continue;
            }
            FILTER_RESULT.put(url, webName);
        }
        return FILTER_RESULT;
    }


    /**
     * 过滤url: https://book.qidian.com/info/1023422452/ => book.qidian.com
     * @param url
     * @return
     */
    @Nullable
    public static String filterUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return url;
        } else if (url.toLowerCase().startsWith("http")) {
            //https://book.qidian.com/info/1023422452/ => book.qidian.com/info/1023422452/
            url = url.split("//")[1];
        }
        if (url.contains("/")) {
            //book.qidian.com
            url = url.substring(0, url.indexOf('/'));
        }
        return url;
    }


    /**
     * 保存到数据库
     * @param DAO
     * @param FILTER_RESULT 过滤后的数据
     * @return 返回插入成功的数据
     */
    public static Map<String, String> saveDatas2Db(NovelBeanDao DAO, Map<String, String> FILTER_RESULT) {
        Iterator<Map.Entry<String, String>> iterator = FILTER_RESULT.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String url = entry.getKey();
            String webName = entry.getValue();
            boolean isSave = saveData2Db(DAO, url, webName);
            if (!isSave) {
                //没有插入到数据库, 删除
                iterator.remove();
            }
        }
        LogUtils.formatError("本次共插入数据条数: %d", FILTER_RESULT.size());
        return FILTER_RESULT;
    }


    /**
     * 保存单条数据
     * @param url url
     * @param webName 网址名称
     * @return 是否已经保存
     */
    public static boolean saveData2Db(NovelBeanDao DAO, String url, String webName) {
        //查找
        NovelBean novelBean = GreenDaoUtils.queryUnique(DAO, NovelBeanDao.Properties.Url.eq(url));
        //如果数据库中没有
        if (novelBean == null) {
            GreenDaoUtils.insert(DAO, new NovelBean(webName, url));
            return true;
        }
        return false;
    }


    /**
     * 查找所有数据
     * @param DAO
     */
    public static void showAllDb(NovelBeanDao DAO) {
        LogUtils.error("//////////////////查找数据库/////////////////////////////");
        List<NovelBean> novelBeans = GreenDaoUtils.queryAll(DAO);
        for (NovelBean bean : novelBeans) {
            LogUtils.formatError("id=%d, webName=%s, url=%s, flag=%d", bean.id, bean.webName, bean.url, bean.flag);
        }
    }


    /**
     * 设置是否屏蔽网站
     * @param url 网址url
     * @param flag 1需要屏蔽, 0忽略
     */
    public static void setNovelEnable(NovelBeanDao DAO, String url, int flag) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        NovelBean bean = GreenDaoUtils.queryUnique(DAO, NovelBeanDao.Properties.Url.eq(url));
        if (bean != null && bean.flag != flag) {
            bean.setFlag(flag);
            GreenDaoUtils.update(DAO, bean);
        }
    }
}
