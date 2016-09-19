package com.qf.day24_cache;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qf.adapter.TeaAdapter;
import com.qf.entity.TeaEntity;
import com.qf.util.Constants;
import com.qf.util.DownUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 图片的缓存
 *
 * 三级缓存：
 * 一级缓存：内存缓存
 * 二级缓存：硬盘缓存
 * 三级缓存：网络缓存
 *
 * Runtime.getRuntime().maxMemory() : 获得系统可以分配给本APP的最大内存
 */
public class MainActivity extends AppCompatActivity implements DownUtil.OnDownListener {

    private ListView lv;
    private TeaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.lv);
        adapter = new TeaAdapter(this);
        lv.setAdapter(adapter);
        loadDatas();
    }

    private void loadDatas() {
        new DownUtil().setOnDownListener(this).downJSON(Constants.URL_NEWS);
    }

    @Override
    public Object paresJson(String json) {
        if(json != null){
            try {
                JSONArray data = new JSONObject(json).getJSONArray("data");

                TypeToken<List<TeaEntity>> tt = new TypeToken<List<TeaEntity>>(){};
                return new Gson().fromJson(data.toString(), tt.getType());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void downSucc(Object object) {
        if(object != null){
            List<TeaEntity> datas = (List<TeaEntity>) object;
            adapter.setDatas(datas);
        }
    }
}
