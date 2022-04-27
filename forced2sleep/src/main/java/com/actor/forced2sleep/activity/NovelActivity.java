package com.actor.forced2sleep.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.actor.forced2sleep.R;
import com.actor.forced2sleep.adapter.NovelListAdapter;
import com.actor.forced2sleep.bean.NovelBean;
import com.actor.forced2sleep.databinding.ActivityNovelBinding;
import com.actor.forced2sleep.novel.NovelUtils;
import com.actor.myandroidframework.utils.database.GreenDaoUtils;
import com.greendao.gen.NovelBeanDao;

import java.util.List;

/**
 * description: 小说
 * company    :
 * @author    : ldf
 * date       : 2022/4/27 on 14:41
 */
public class NovelActivity extends BaseActivity<ActivityNovelBinding> {

    private final NovelBeanDao DAO = GreenDaoUtils.getDaoSession().getNovelBeanDao();
    private RecyclerView recyclerView;
    private NovelListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = viewBinding.recyclerView;
        setTitle("小说");

        List<NovelBean> novelBeans = GreenDaoUtils.queryAll(DAO);
        recyclerView.setAdapter(mAdapter = new NovelListAdapter(novelBeans, DAO));
    }

    @Override
    public void onViewClicked(View view) {
        super.onViewClicked(view);
        switch (view.getId()) {
            case R.id.btn_novel_spider:
                //小说网站抓取
                startActivity(new Intent(this, NovelSpiderActivity.class));
                break;
            case R.id.btn_reset:
                //恢复默认设置
                setIgnore();
                List<NovelBean> novelBeans = GreenDaoUtils.queryAll(DAO);
                mAdapter.setList(novelBeans);
                break;
            default:
                break;
        }
    }


    //设置忽略网站, 共 22 个
    private void setIgnore() {
        //7个
        NovelUtils.setNovelEnable(DAO, "www.baidu.com", 0);//忽略 百度搜索(bug引起的...)
        NovelUtils.setNovelEnable(DAO, "baijiahao.baidu.com", 0);//忽略 百家号
        NovelUtils.setNovelEnable(DAO, "baike.baidu.com", 0);//忽略 百度百科
        NovelUtils.setNovelEnable(DAO, "jump2.bdimg.com", 0);//忽略 百度贴吧
        NovelUtils.setNovelEnable(DAO, "tieba.baidu.com", 0);//忽略 百度贴吧
        NovelUtils.setNovelEnable(DAO, "zhidao.baidu.com", 0);//百度知道
        NovelUtils.setNovelEnable(DAO, "mbd.baidu.com", 0);//百度验证??

        NovelUtils.setNovelEnable(DAO, "new.qq.com", 0);//腾讯新闻

        NovelUtils.setNovelEnable(DAO, "www.zhihu.com", 0);//知乎
        NovelUtils.setNovelEnable(DAO, "zhuanlan.zhihu.com", 0);//之乎专栏

        //6个
        NovelUtils.setNovelEnable(DAO, "www.bilibili.com", 0);//哔哩哔哩
        NovelUtils.setNovelEnable(DAO, "www.sohu.com", 0);//搜狐
        NovelUtils.setNovelEnable(DAO, "book.douban.com", 0);//豆瓣读书
        NovelUtils.setNovelEnable(DAO, "news.tom.com", 0);//TOM资讯
        NovelUtils.setNovelEnable(DAO, "tech.china.com", 0);//科技频道-中华网
        NovelUtils.setNovelEnable(DAO, "www.thehour.cn", 0);//小时新闻-有用有趣有温度

        //6个
        NovelUtils.setNovelEnable(DAO, "www.12349.net", 0);//魔盟网(游戏下载网站)
        NovelUtils.setNovelEnable(DAO, "www.9k9k.com", 0);//9K9K手游网
        NovelUtils.setNovelEnable(DAO, "m.yxwoo.com", 0);//游戏窝
        NovelUtils.setNovelEnable(DAO, "52pk.com", 0);//52pk游戏网_中文游戏门户站
        NovelUtils.setNovelEnable(DAO, "ngabbs.com", 0);//NGA玩家社区
        NovelUtils.setNovelEnable(DAO, "www.18183.com", 0);//18183手游网
    }
}