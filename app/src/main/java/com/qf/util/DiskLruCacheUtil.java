package com.qf.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Ken on 2016/9/19.11:16
 *
 * 硬盘缓存
 */
public class DiskLruCacheUtil {


    public static DiskLruCache diskLruCache;
    private static int maxSize = 1024 * 1024 * 10;

    public static void init(Context context){
        if(diskLruCache == null){
            try {
                diskLruCache = DiskLruCache.open(
                        getCacheFile(context, "bitmap"),
                        getAppVersion(context),
                        1,
                        maxSize);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写入缓存
     * @param url
     * @param bitmap
     */
    public static boolean setCache(String url, Bitmap bitmap){
        if(url != null && bitmap != null && getCache(url) == null){
            String key = MD5Util.MD5(url);
            DiskLruCache.Editor edit = null;
            try {
                edit = diskLruCache.edit(key);
                OutputStream out = edit.newOutputStream(0);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                edit.commit();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * 读取缓存
     * @param url
     * @return
     */
    public static Bitmap getCache(String url){
        if(url != null){
            String key = MD5Util.MD5(url);
            DiskLruCache.Snapshot snapshot = null;
            try {
                snapshot = diskLruCache.get(key);
                if(snapshot != null) {
                    InputStream in = snapshot.getInputStream(0);
                    return BitmapFactory.decodeStream(in);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 清空缓存
     */
    public static void clearCache(){
        try {
            diskLruCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步日志文件
     */
    public static void flushLog(){
        try {
            diskLruCache.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭缓存
     */
    public static void close(){
        try {
            diskLruCache.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得磁盘缓存的路径
     * @return
     */
    private static File getCacheFile(Context context, String endPath){
        String cachepath;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            cachepath = context.getExternalCacheDir().getAbsolutePath();
        } else {
            cachepath = context.getCacheDir().getAbsolutePath();
        }

        File cacheFile = new File(cachepath, endPath);
        if(!cacheFile.exists()){
            cacheFile.mkdirs();
        }
        Log.d("print", "----》缓存的磁盘的路径：" + cacheFile.getAbsolutePath());
        return cacheFile;
    }

    /**
     * 获得App的版本号
     * @param context
     * @return
     */
    private static int getAppVersion(Context context){
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
