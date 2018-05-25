package com.yuelin.o2cabin;


import java.text.*;
import java.util.*;
import android.content.*;
import android.util.*;
import java.io.*;

public class MyLog
{
    private static String LOGFILENAME;
    private static String LOG_PATH_SDCARD_DIR;
    private static Boolean LOG_SWITCH;
    private static char LOG_TYPE;
    private static SimpleDateFormat LogSdf;
    private static int SDCARD_LOG_FILE_SAVE_DAYS;
    private static SimpleDateFormat logfile;

    static {
        MyLog.LOG_TYPE = 'v';
        MyLog.LOG_SWITCH = true;
        MyLog.SDCARD_LOG_FILE_SAVE_DAYS = 0;
        MyLog.LOGFILENAME = "MyLog";
        MyLog.LOG_PATH_SDCARD_DIR = "/sdcard/";
        MyLog.LogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        MyLog.logfile = new SimpleDateFormat("yyyy-MM-dd");
    }

    private static Boolean LOG_WRITE_TO_FILE() {
        return true;
    }

    public static void d(final String s, final String s2) {
        log(s, s2, 'd');
    }

    public static void delFile() {
        final File file = new File(MyLog.LOG_PATH_SDCARD_DIR, MyLog.logfile.format(getDateBefore()) + MyLog.LOGFILENAME);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void e(final String s, final String s2) {
        log(s, s2, 'e');
    }

    private static Date getDateBefore() {
        final Date time = new Date();
        final Calendar instance = Calendar.getInstance();
        instance.setTime(time);
        instance.set(5, instance.get(5) - MyLog.SDCARD_LOG_FILE_SAVE_DAYS);
        return instance.getTime();
    }

    public static void i(final String s, final String s2) {
        log(s, s2, 'i');
    }

    public static void init(final Context context) {
    }

    public static void log(final Exception ex) {
        try {
            final StackTraceElement[] stackTrace = ex.getStackTrace();
            for (int length = stackTrace.length, i = 0; i < length; ++i) {
                log(null, stackTrace[i].toString(), 'e');
            }
            log(null, ex.getMessage(), 'e');
        }
        catch (Exception ex2) {}
    }

    private static void log(final String s, final String s2, final char c) {
        if (MyLog.LOG_SWITCH) {
            if ('i' == c) {
                Log.e(s, s2);
            }
            else if ('e' == c) {
                Log.i(s, s2);
            }
            else if ('w' == c) {
                Log.w(s, s2);
            }
            else if ('d' == c) {
                Log.d(s, s2);
            }
            else {
                Log.v(s, s2);
            }
            if (LOG_WRITE_TO_FILE()) {
                writeLogtoFile(String.valueOf(c), s, s2);
            }
        }
    }

    public static void v(final String s, final String s2) {
        log(s, s2, 'v');
    }

    public static void w(final String s, final String s2) {
        log(s, s2, 'w');
    }

    private static void writeLogtoFile(final String s, final String s2, final String s3) {
        try {
            final Date date = new Date();
            final String format = MyLog.logfile.format(date);
            final String string = MyLog.LogSdf.format(date) + " " + s + " " + s2 + " " + s3;
            final FileWriter fileWriter = new FileWriter(new File(MyLog.LOG_PATH_SDCARD_DIR, MyLog.LOGFILENAME + format + ".txt"), true);
            final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(string);
            bufferedWriter.newLine();
            bufferedWriter.close();
            fileWriter.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
        }
    }
}

