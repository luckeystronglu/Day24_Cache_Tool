package com.qf.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Ken on 2016/9/19.14:34
 * 图片下载
 *
 * 内存缓存
 * 磁盘缓存
 * 图片错位
 */
public class ImageLoader {
    //创建一个线程池
    private static ExecutorService executorService = Executors.newFixedThreadPool(5);

    private Handler handler = new Handler();

    //下载到的目标位置
    private ImageView iv;
    //下载的url
    private String url;

    public ImageLoader(Context context){
        //磁盘缓存的初始化
        DiskLruCacheUtil.init(context);
    }

    //设置需要下载的URL
    public ImageLoader load(String url){
        this.url = url;
        return this;
    }

    //设置需要下载到的ImageView对象
    public ImageLoader into(ImageView iv){
        this.iv = iv;
        this.iv.setTag(url);
        return this;
    }

    /**
     * 设置默认图片
     * @param resid
     * @return
     */
    public ImageLoader setMrImage(int resid){
        this.iv.setImageResource(resid);
        return this;
    }

    //表示开始下载
    public void down(){
        //从缓存中获得图片
        Bitmap bitmap = LruCacheUtil.getCache(url);
        if(bitmap == null){
            //从磁盘缓存中获得
            bitmap = DiskLruCacheUtil.getCache(url);
            if(bitmap != null){
                //再次放入内存缓存
                LruCacheUtil.putCache(url, bitmap);
            }
        }

        //如果bitmap任然为空，则开始下载
        if(bitmap != null){
            if(iv.getTag().equals(url)){
                iv.setImageBitmap(bitmap);
            }
        } else {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    byte[] bytes = HttpUtil.requestURL(url);
                    if (bytes != null){
                        final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        //显示到ImageView上
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(iv.getTag().equals(url)){
                                    iv.setImageBitmap(bitmap);
                                }
                            }
                        });

                        //写入内存缓存
                        LruCacheUtil.putCache(url, bitmap);
                        //写入磁盘缓存
                        DiskLruCacheUtil.setCache(url, bitmap);
                    }
                }
            });
        }
    }

}
