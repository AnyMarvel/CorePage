package cn.edu.zafu.corepage.sample;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import cn.edu.zafu.corepage.core.CoreConfig;

public class BaseApplication extends Application {
    private String pageJson="[" +
            "  {" +
            "    'name': 'test4'," +
            "    'class': 'cn.edu.zafu.corepage.sample.TestFragment4'," +
            "    'params': ''" +
            "  }]";
    private RefWatcher refWatcher;
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.d( "loadDex", "App attachBaseContext ");
        if (!quickStart()) {
            if (needWait(base)){
                waitForDexopt(base);
            }
            MultiDex.install (this );
        } else {
            return;
        }
    }




    public boolean quickStart() {
        if (StringUtils.contains( getCurProcessName(this), ":mini")) {
            Log.d( "loadDex", ":mini start!");
            return true;
        }
        return false ;
    }
    //neead wait for dexopt ?
    private boolean needWait(Context context) {
        String flag = get2thDexSHA1(context);
        Log.d( "loadDex", "dex2-sha1 "+flag);
        SharedPreferences sp = null;
        try {
            sp = context.getSharedPreferences(
                    context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName, MODE_MULTI_PROCESS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String saveValue = sp.getString("KEY_DEX2_SHA1", "");
        return !StringUtils.equals(flag,saveValue);
    }
    /**
     * Get classes.dex file signature
     * @param
     * @return
     */
    private String get2thDexSHA1(Context context) {
        ApplicationInfo ai = context.getApplicationInfo();
        String source = ai.sourceDir;
        try {
            JarFile jar = new JarFile(source);
            Manifest mf = jar.getManifest();
            Map<String, Attributes> map = mf.getEntries();
            Attributes a = map.get("classes2.dex");
            return a.getValue("SHA1-Digest");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null ;
    }
    // optDex finish
    public void installFinish(Context context) {
        SharedPreferences sp = null;
        try {
            sp = context.getSharedPreferences(
                    context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName, MODE_MULTI_PROCESS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        sp.edit().putString("KEY_DEX2_SHA1",get2thDexSHA1(context)).commit();
    }
    //得到当前进程名
    public static String getCurProcessName(Context context) {
        try {
            int pid = android.os.Process.myPid();
            ActivityManager mActivityManager = (ActivityManager) context
                    .getSystemService(Context. ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                    .getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    return appProcess. processName;
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return null ;
    }
    public void waitForDexopt(Context base) {
        Intent intent = new Intent();
        ComponentName componentName = new
                ComponentName( "cn.edu.zafu.corepage.sample", LoadResActivity.class.getName());
        intent.setComponent(componentName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        base.startActivity(intent);
        long startWait = System.currentTimeMillis ();
        long waitTime = 10 * 1000 ;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1 ) {
            waitTime = 20 * 1000 ;//实测发现某些场景下有些2.3版本有可能10s都不能完成optdex
        }
        while (needWait(base)) {
            try {
                long nowWait = System.currentTimeMillis() - startWait;
                Log.d("loadDex" , "wait ms :" + nowWait);
                if (nowWait >= waitTime) {
                    return;
                }
                Thread.sleep(200 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }








    @Override
    public void onCreate() {
        super.onCreate();
        if (quickStart()) {
            return;
        }
        CoreConfig.init(this, pageJson);
        refWatcher=LeakCanary.install(this);
        // or such as this
        //CoreConfig.init(this);
        //CoreConfig.readConfig(pageJson);
    }

    public static RefWatcher getRefWatcher(Context context) {
        BaseApplication application = (BaseApplication) context.getApplicationContext();
        return application.refWatcher;
    }


}
