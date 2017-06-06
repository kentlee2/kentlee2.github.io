package com.haoqi.yungou;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.File;

/**
 * Created by Administrator on 2016/9/9.
 */
public class GlobalApplication extends Application {
    private ImageLoaderConfiguration imageLoaderConfiguration;
    private SharedPreferences mPreferences;
    private static GlobalApplication instance;
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        instance = this;
        mContext = getApplicationContext();
        initImageLoader();
    }
    public static Context getContext() {
        return mContext;
    }
    public static GlobalApplication getInstance() {
        return instance;
    }
    private void initImageLoader() {
        File SdCardPath = Environment.getExternalStorageDirectory();
        // 缓存在sd卡中指定目录
        File cacheDir = new File(SdCardPath + "/YunGou/img");
        // 完成ImageLoaderConfiguration的配置
        imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(this)
//                .memoryCacheExtraOptions(200, 200)
                // 设置内存缓存的详细信息
                // max width, max height，即保存的每个缓存文件的最大长宽
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                // .discCacheExtraOptions(48, 48, null, 0, null)//设置sd卡缓存的详细信息
                // 线程池内加载的数量
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .diskCache(new UnlimitedDiskCache(cacheDir))
                // 自定义缓存路径,图片缓存到sd卡
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .memoryCache(new LruMemoryCache(4 * 1024 * 1024))
                .memoryCacheSizePercentage(10)
                // 超时时间 5秒
                .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000))
                .imageDecoder(new BaseImageDecoder(true))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
//                .writeDebugLogs()
                .build();// 开始构建
        ImageLoader.getInstance().init(imageLoaderConfiguration); //初始化
    }

    /**
     * 获取配置项
     * @param key Key
     * @param defaultValue 默认值
     * @return Value
     */
    public  String GetValue(String key, String defaultValue){
        if(!mPreferences.contains(key)){
            saveValue(key, defaultValue);
        }
        return mPreferences.getString(key, defaultValue);
    }
    /**
     * 获取配置项
     * @param key Key
     * @param defaultValue 默认值
     * @return Value
     */
    public  boolean GetValue(String key, Boolean defaultValue){
        if(!mPreferences.contains(key)){
            saveValue(key, defaultValue);
        }
        return mPreferences.getBoolean(key, defaultValue);
    }

    /**
     * 获取配置项
     * @param key Key
     * @param defaultValue 默认值
     * @return Value
     */
    public  int GetValue(String key, Integer defaultValue){
        if(!mPreferences.contains(key)){
            saveValue(key, defaultValue);
        }
        return Integer.parseInt(mPreferences.getString(key, defaultValue.toString()));
    }
    /**
     * 清楚指定key
     * @param key
     */
    public void clear(String key){
        if(mPreferences != null){
            SharedPreferences.Editor mEditor = mPreferences.edit();
            mEditor.remove(key);
            mEditor.commit();
        }
    }
    /**把数据保存到配置项中
     * @param key
     * @param value 值
     */

    @SuppressLint("NewApi")
    public  void saveValue(String key ,Object value){
        if(key == null || key.isEmpty() || value == null){
            return;
        }
        if(mPreferences != null){
            SharedPreferences.Editor mEditor = mPreferences.edit();
            if(value instanceof String){
                mEditor.putString(key, value.toString());
            }else if(value instanceof Boolean){
                mEditor.putBoolean(key, (Boolean)value);
            }else if(value instanceof Integer){
                mEditor.putString(key, value.toString());
            }
            mEditor.commit();
        }
    }
    public boolean isLogined(){
        String username = GetValue(Constant.USERNAME,"");
        String password = GetValue(Constant.USERPWD,"");
        if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)){
            return true;
        }
        return false;
    }
    public void logout(){
        if(mPreferences != null) {
            SharedPreferences.Editor mEditor = mPreferences.edit();
            mEditor.remove(Constant.USERPWD);
            mEditor.remove(Constant.USERID);
            mEditor.commit();
        }
    }
    public void toastShortMsg(int msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
    public void toastShortMsg(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
    public void toastCenterMsg(int msg){
        Toast toast =Toast.makeText(this,msg,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
    public void toastCenterMsg(String msg){
        Toast toast =Toast.makeText(this,msg,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

}
