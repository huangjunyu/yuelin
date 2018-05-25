package com.yuelin.o2cabin;

import android.annotation.SuppressLint;
import android.content.*;
import java.text.*;
import java.io.*;
import java.util.*;
import android.content.pm.*;
import java.lang.reflect.*;
import android.os.*;
import android.os.Process;

public class CrashHandler implements Thread.UncaughtExceptionHandler
{
    private static CrashHandler INSTANCE;
    public static final String TAG = "CrashHandler";
    private DateFormat formatter;
    private Map<String, String> infos;
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    
    static {
        CrashHandler.INSTANCE = new CrashHandler();
    }
    
    private CrashHandler() {
        this.infos = new HashMap<String, String>();
        this.formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    }
    
    public static CrashHandler getInstance() {
        return CrashHandler.INSTANCE;
    }
    
    private boolean handleException(final Throwable t) {
        if (t == null) {
            return false;
        }
        this.collectDeviceInfo(this.mContext);
        this.saveCrashInfo2File(t);
        return true;
    }
    
    private String saveCrashInfo2File(final Throwable t) {
        final StringBuffer sb = new StringBuffer();
        for (final Map.Entry<String, String> entry : this.infos.entrySet()) {
            final String s = entry.getKey();
            final String s2 = entry.getValue();
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(s);
            sb2.append("=");
            sb2.append(s2);
            sb2.append("\n");
            sb.append(sb2.toString());
        }
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);
        t.printStackTrace(printWriter);
        for (Throwable t2 = t.getCause(); t2 != null; t2 = t2.getCause()) {
            t2.printStackTrace(printWriter);
        }
        printWriter.close();
        sb.append(stringWriter.toString());
        try {
            MyLog.d("================", sb.toString());
            final long currentTimeMillis = System.currentTimeMillis();
            final String format = this.formatter.format(new Date());
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("crash-");
            sb3.append(format);
            sb3.append("-");
            sb3.append(currentTimeMillis);
            sb3.append(".log");
            final String string = sb3.toString();
            if (Environment.getExternalStorageState().equals("mounted")) {
                final File file = new File("/sdcard/crash/");
                if (!file.exists()) {
                    file.mkdirs();
                }
                final StringBuilder sb4 = new StringBuilder();
                sb4.append("/sdcard/crash/");
                sb4.append(string);
                final FileOutputStream fileOutputStream = new FileOutputStream(sb4.toString());
                fileOutputStream.write(sb.toString().getBytes());
                fileOutputStream.close();
            }
            return string;
        }
        catch (Exception ex) {
            MyLog.log(ex);
            return null;
        }
    }
    
    public void collectDeviceInfo(final Context context) {
        try {
            @SuppressLint("WrongConstant") final PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 1);
            if (packageInfo != null) {
                String versionName;
                if (packageInfo.versionName == null) {
                    versionName = "null";
                }
                else {
                    versionName = packageInfo.versionName;
                }
                final StringBuilder sb = new StringBuilder();
                sb.append(packageInfo.versionCode);
                sb.append("");
                final String string = sb.toString();
                this.infos.put("versionName", versionName);
                this.infos.put("versionCode", string);
            }
        }
        catch (PackageManager.NameNotFoundException ex) {
            MyLog.log((Exception)ex);
        }
        for (final Field field : Build.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                this.infos.put(field.getName(), field.get(null).toString());
                final StringBuilder sb2 = new StringBuilder();
                sb2.append(field.getName());
                sb2.append(" : ");
                sb2.append(field.get(null));
                MyLog.d("CrashHandle", sb2.toString());
            }
            catch (Exception ex2) {
                MyLog.log(ex2);
            }
        }
    }
    
    public void init(final Context mContext) {
        this.mContext = mContext;
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)this);
    }
    
    @Override
    public void uncaughtException(final Thread thread, final Throwable t) {
        if (!this.handleException(t) && this.mDefaultHandler != null) {
            this.mDefaultHandler.uncaughtException(thread, t);
            return;
        }
        try {
            Thread.sleep(3000L);
        }
        catch (InterruptedException ex) {
            MyLog.log(ex);
        }
        Process.killProcess(Process.myPid());
        System.exit(1);
    }
}
