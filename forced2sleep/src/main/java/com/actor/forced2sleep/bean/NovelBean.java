package com.actor.forced2sleep.bean;

import androidx.annotation.Keep;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * description: 小说实体
 * company    :
 *
 * @author : ldf
 * date       : 2022/4/26 on 16
 * @version 1.0
 * <p>
 * TODO: 2022/4/26 生成的Dao会使用get/set方法, 如果generateGettersSetters = false, dao会报错...
 */
@Keep
@Entity(nameInDb = "novel", createInDb = true, generateGettersSetters = true,
        generateConstructors = false)
public class NovelBean {

    @Id
    @Property(nameInDb = "id")
    public Long id;

    //网站名称
    @Property(nameInDb = "web_name")
    public String webName;

    @Property(nameInDb = "url")
    public String url;

    //创建时间
    @Property(nameInDb = "createTime")
    public Date createTime;

    //flag=1能用(默认), flag=0不能用
    @Property(nameInDb = "flag")
    public int flag = 1;


    public NovelBean() {
    }
    public NovelBean(String webName, String url) {
        this.webName = webName;
        this.url = url;
        createTime = new Date();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWebName() {
        return this.webName;
    }

    public void setWebName(String webName) {
        this.webName = webName;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getFlag() {
        return this.flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

}
