package com.qf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qf.day24_cache.R;
import com.qf.entity.TeaEntity;
import com.qf.util.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ken on 2016/9/19.9:54
 */
public class TeaAdapter extends BaseAdapter {

    private Context context;
    private List<TeaEntity> datas;

    public TeaAdapter( Context context){
        this.context = context;
        this.datas = new ArrayList<>();
    }

    public void setDatas(List<TeaEntity> datas){
        this.datas = datas;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView != null){
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_layout, null);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tv_other = (TextView) convertView.findViewById(R.id.tv_other);
            viewHolder.iv = (ImageView) convertView.findViewById(R.id.iv_pic);
            convertView.setTag(viewHolder);
        }

        viewHolder.tv_title.setText(datas.get(position).getTitle());
        viewHolder.tv_other.setText(datas.get(position).getSource());
        new ImageLoader(context).load(datas.get(position).getWap_thumb())
                .into(viewHolder.iv)
                .setMrImage(R.mipmap.ic_launcher)
                .down();

        return convertView;
    }

    class ViewHolder{
        TextView tv_title, tv_other;
        ImageView iv;
    }
}
