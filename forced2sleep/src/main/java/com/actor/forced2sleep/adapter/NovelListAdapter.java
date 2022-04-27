package com.actor.forced2sleep.adapter;

import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import com.actor.forced2sleep.R;
import com.actor.forced2sleep.bean.NovelBean;
import com.actor.myandroidframework.utils.database.GreenDaoUtils;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.greendao.gen.NovelBeanDao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * description: 描述
 * company    :
 *
 * @author : ldf
 * date       : 2022/4/27 on 15
 * @version 1.0
 */
public class NovelListAdapter extends BaseQuickAdapter<NovelBean, BaseViewHolder> {

    private       NovelBeanDao                           DAO;
    private final CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int pos = (int) buttonView.getTag(R.id.switch_compat);
            NovelBean item = getItem(pos);
            item.flag = 1 - item.flag;
            GreenDaoUtils.update(DAO, item);
            boolean enable = item.flag == 1;
            if (enable != isChecked) {
                buttonView.setChecked(enable);
            }
            ToastUtils.showShort(enable ? "可用" : "这网站被忽略");
        }
    };

    public NovelListAdapter(@Nullable List<NovelBean> data, NovelBeanDao DAO) {
        super(R.layout.item_novel_list, data);
        this.DAO = DAO;
        addChildClickViewIds(R.id.btn_copy);
        setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                //复制
                NovelBean item = getItem(position);
                ClipboardUtils.copyText(item.url);
                ToastUtils.showShort("复制成功!");
            }
        });
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, NovelBean item) {
        int position = helper.getAdapterPosition();
        SwitchCompat switchCompat = helper.setText(R.id.tv_pos, String.valueOf(position + 1))
                .setText(R.id.tv_url, item.url)
                .setText(R.id.tv_web_name, item.webName)
                .getView(R.id.switch_compat);
        //  赋值之前取消CheckBox监听
        switchCompat.setOnCheckedChangeListener(null);
        switchCompat.setChecked(item.flag == 1);
        switchCompat.setTag(R.id.switch_compat, position);
        switchCompat.setOnCheckedChangeListener(listener);
    }
}
