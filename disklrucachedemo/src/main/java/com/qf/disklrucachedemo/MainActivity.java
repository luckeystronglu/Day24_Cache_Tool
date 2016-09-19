package com.qf.disklrucachedemo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 硬盘缓存
 * DiskLruCache
 *
 * 硬盘缓存的位置
 * 外部存储：getExternalCacheDir() --> SDCard/Android/data/<应用程序包名>/cache
 * 内部存储: getCacheDir() --> data/data/<应用程序包名>/cache
 * 该路径会随着APP的卸载而自动删除掉
 */
public class MainActivity extends AppCompatActivity {

    /*
    磁盘缓存的对象
     */
    private DiskLruCache diskLruCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * 参数1：磁盘缓存的路径
         * 参数2：设置APP的版本号，版本号升级，则磁盘缓存自动清空
         * 参数3：表示一个key值最多可以对应多少个value
         * 参数4：最大可以缓存多少数据
         */
        try {
            diskLruCache = DiskLruCache.open(
                    getCacheFile(this, "bitmap"),
                    getAppVersion(this),
                    1,
                    1024 * 1024 * 10);
        } catch (Exception e) {
            e.printStackTrace();
        }


        /**
         * 写入缓存
         */
        try {
            DiskLruCache.Editor inputCache = diskLruCache.edit("key");
            OutputStream outputStream = inputCache.newOutputStream(0);
            Bitmap bitmap = null;
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            inputCache.commit();//提交缓存
        } catch (IOException e) {
            e.printStackTrace();
        }


        /**
         * 读取缓存
         */
        try {
            DiskLruCache.Snapshot getCache = diskLruCache.get("key");
            InputStream in = getCache.getInputStream(0);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * 移除缓存
         */
        try {
            boolean key = diskLruCache.remove("key");
        } catch (IOException e) {
            e.printStackTrace();
        }


        /**
         * 清空缓存
         */
        try {
            diskLruCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }


        /**
         * 获得当前已经缓存的文件大小
         */
        diskLruCache.size();


    }


    @Override
    protected void onPause() {
        super.onPause();
        /**
         * 同步磁盘缓存日志文件
         */
        try {
            diskLruCache.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 关闭磁盘缓存
         */
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
    public File getCacheFile(Context context, String endPath){
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
        return cacheFile;
    }

    /**
     * 获得App的版本号
     * @param context
     * @return
     */
    public int getAppVersion(Context context){
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

}
