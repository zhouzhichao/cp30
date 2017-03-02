package com.crestv.cp30.adapter;

import android.content.Context;

import com.crestv.cp30.Enity.PrizeInformationEnity;
import com.crestv.cp30.R;
import com.crestv.cp30.base.CommonAdapter;
import com.crestv.cp30.base.ViewHolder;
import com.crestv.cp30.util.L;

import java.util.List;

/**
 * Created by Administrator on 2017/3/1 0001.
 */

public class PrizeInformationAdapter extends CommonAdapter<PrizeInformationEnity.DataBean.ListHeroBean> {

    public PrizeInformationAdapter(Context context, List<PrizeInformationEnity.DataBean.ListHeroBean> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void convert(ViewHolder helper, PrizeInformationEnity.DataBean.ListHeroBean item) {
        L.e("==name","=="+item.getNickName());
        helper.setText(R.id.tvName,item.getNickName());
        helper.setText(R.id.tvMoney,String.valueOf(item.getIncome()));
    }
}
