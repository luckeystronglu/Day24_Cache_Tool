package com.qf.util;

import android.graphics.Bitmap;
import android.util.LruCache;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ken on 2016/9/19.9:45
 *
 * 首先去lrucache寻找数据，如果没有则去软引用中寻找，如果再没有再到硬盘中寻找，最后再到网络中下载
 *
 * 图片下载完成：
 * 添加进lruCache和硬盘缓存中
 * 当lruCache中的空间不够时，移除的数据，再添加进软引用缓存中
 *
 * 缓存的查找：
 * 如果在lruCache中找到缓存的数据，则直接显示
 * 如果在软引用中找到缓存的数据，则重新添加进lruCache，并且从软引用中移除
 * 如果在硬盘上找到缓存的数据，则重新添加进lruCache
 */
public class LruCacheUtil {

    /**
     * 内存缓存的对象
     * 参数：内存缓存最大值 -- 通常设置为可使用的内存的1/8
     *
     * lrucache如果缓存的大小已经达到了最大值，采用的移除策略：最近时间最少使用的缓存数据被移除
     *
     * bitmap.getByteCount()
     * bitmap.getRowBytes() * bitmap.getHeight()
     * : 一个bitmap所占的内存大小（字节）
     */
    private static LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() / 8)){

        /**
         * 当一个缓存数据被移除时回调该方法
         * @param evicted
         * @param key
         * @param oldValue
         * @param newValue
         */
        @Override
        protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
            if(evicted){
                //把移除的数据放入软引用
                softCache.put(key, new SoftReference<Bitmap>(oldValue));
            }
        }

        /**
         * 该方法用于计算缓存数据的大小
         * @param key
         * @return
         */
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    };

    /**
     * 软引用
     */
    private static Map<String, SoftReference<Bitmap>> softCache = new HashMap<>();

    /**
     * 添加缓存
     */
    public static void putCache(String url, Bitmap bitmap){
        if(getCache(url) == null){
            String key = MD5Util.MD5(url);
            lruCache.put(key, bitmap);
        }
    }

    /**
     * 获得缓存
     * @param url
     * @return
     */
    public static Bitmap getCache(String url){
        String key = MD5Util.MD5(url);
        Bitmap bitmap = lruCache.get(key);
        if(bitmap == null){
            //再到软引用中寻找
            SoftReference<Bitmap> softBitmap = softCache.get(key);
            if(softBitmap != null){
                bitmap = softBitmap.get();
                if(bitmap != null){
                    //重新添加进LruCache中
                    putCache(key, bitmap);
                    //移除本身
                    softCache.remove(key);
                }
            }
        }
        return bitmap;
    }


    /**
     * 移除缓存
     * @param url
     */
    public static void removeCache(String url){
        if(getCache(url) != null) {
            String key = MD5Util.MD5(url);
            lruCache.remove(key);
            softCache.remove(key);
        }
    }

    /**
     * 清除缓存
     */
    public static void clearCache(){
        if(lruCache.size() > 0){
            //清除缓存
            lruCache.evictAll();
            softCache.clear();
        }
    }
}
