package com.yuelin.o2cabin;

/**
 * Created by Administrator on 2018\4\28 0028.
 */

import android.app.*;
import android.content.*;
import android.content.res.*;

public class MyApplication extends Application {
    public static ControlThread controlThread;
    public static MyApplication theOnly;

    static {
        MyApplication.controlThread = null;
        MyApplication.theOnly = null;
    }

    public MyApplication() {
        MyApplication.theOnly = this;
        try {
            MyLog.d("MyApplication", "MyApplication::MyApplication()");
        } catch (Exception ex) {
            MyLog.log(ex);
        }
    }

    public void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    public void onCreate() {
        super.onCreate();
        try {
            MyLog.d("MyApplication", "onCreate");
        } catch (Exception ex) {
            MyLog.log(ex);
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
    }

    public void onTerminate() {
        MyApplication.theOnly = null;
        super.onTerminate();
        try {
            MyLog.d("MyApplication", "onTerminate()");
        } catch (Exception ex) {
            MyLog.log(ex);
        }
    }

    public void onTrimMemory(final int n) {
        super.onTrimMemory(n);
    }
}
