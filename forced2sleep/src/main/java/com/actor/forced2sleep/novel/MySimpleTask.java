package com.actor.forced2sleep.novel;

import com.blankj.utilcode.util.ThreadUtils;

/**
 * description: 任务
 * company    :
 *
 * @author : ldf
 * date       : 2022/4/26 on 18
 * @version 1.0
 */
public abstract class MySimpleTask <Document> extends ThreadUtils.SimpleTask<Document> {

    public String                 webName;
    public OnFindElementsListener findElementsListener;
    public OnParseElementsListener parseElementsListener;

    private MySimpleTask() {
    }

    public MySimpleTask(String webName, OnParseElementsListener parseElementsListener) {
        this.webName = webName;
        this.parseElementsListener = parseElementsListener;
    }

    public MySimpleTask(OnFindElementsListener findElementsListener) {
        this.findElementsListener = findElementsListener;
    }
}
