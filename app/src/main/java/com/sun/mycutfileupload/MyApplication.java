package com.sun.mycutfileupload;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;

import com.readystatesoftware.chuck.ChuckInterceptor;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.cache.converter.SerializableDiskConverter;
import com.zhouyou.http.model.HttpHeaders;
import com.zhouyou.http.model.HttpParams;

import java.util.UUID;

/**
 * @ProjectName: MyCutFileUpload
 * @Package: com.sun.mycutfileupload
 * @ClassName: MyApplication
 * @Author: littletree
 * @CreateDate: 2020/9/16/016 14:55
 */
public class MyApplication extends Application {
    private static MyApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        initeasyhttp();    //easyhttp初始化
    }

    public void initeasyhttp() {
        EasyHttp.init(this);
        //获取本地user_agent;
        //设置请求头
        HttpHeaders headers = new HttpHeaders();
        //  添加公共的头
        headers.put("token", "需要的token");
        //设置请求参数
        HttpParams params = new HttpParams();
        EasyHttp.getInstance()
                .debug("Ulimt", true)
                .setReadTimeOut(60 * 1000)
                .setWriteTimeOut(60 * 1000)
                .setConnectTimeout(60 * 1000)
                .setRetryCount(1)                   //默认网络不好自动重试3次
                .setRetryDelay(1000)                 //每次延时500ms重试
                .setRetryIncreaseDelay(500)         //每次延时叠加500ms
                .setBaseUrl("域名")
                .setCacheDiskConverter(new SerializableDiskConverter())//默认缓存使用序列化转化
                .setCacheMaxSize(50 * 1024 * 1024)  //设置缓存大小为50M
                .setCacheVersion(1)                 //缓存版本为1
//                .setHostnameVerifier(new UnSafeHostnameVerifier(AppConstant.BASE_URL))//全局访问规则
                .setCertificates()                  //信任所有证书
                //.addConverterFactory(GsonConverterFactory.create(gson))//本框架没有采用Retrofit的Gson转化，所以不用配置
                .addCommonHeaders(headers)          //设置全局公共头
                .addCommonParams(params)       //设置全局公共参数
                .addInterceptor(new ChuckInterceptor(getInstance()));
        //  .addInterceptor(new CustomSignInterceptor());//添加参数签名拦截器
        //.addInterceptor(new HeTInterceptor());//处理自己业务的拦截器
    }

    /**
     * 获取User-Agent
     *
     * @param context
     * @return
     */
    public String getUserAgent(Context context) {
        String userAgent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(context);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        sb.append(" Android ZDB version=" + getVersionName(context));

        return sb.toString();
    }

    /**
     * @return 当前程序的版本名称
     */
    public String getVersionName(Context context) {
        String version;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            version = "";
        }
        return version;
    }

    public static MyApplication getInstance(){
        return application;
    }
}

