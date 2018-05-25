package com.yuelin.o2cabin;

import java.lang.Process;
import java.text.*;

import android.annotation.SuppressLint;
import android.net.http.*;
import org.apache.http.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.util.*;
import org.apache.http.*;
import java.security.*;
import java.io.*;
import java.util.*;
import android.os.*;

public class Tools
{
    private static final int EARTH_RADIUS = 6378137;
    static final String TAG = "Tools";
    static String[] chars;

    static {
        Tools.chars = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
    }

    public static String Bin2String(final byte b) {
        final StringBuilder sb = new StringBuilder();
        sb.append(" 0x");
        sb.append(Tools.chars[0xF & b >> 4]);
        final String string = sb.toString();
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(string);
        sb2.append(Tools.chars[b & 0xF]);
        return sb2.toString();
    }

    public static int CRC16(final byte[] array, final int n, final byte[] array2) {
        if (n == 0) {
            return 0;
        }
        int n2 = 65535;
        int n3;
        for (int i = 0; i < n; ++i, n2 = n3) {
            n3 = (n2 ^ (0xFF & array[i]));
            for (int j = 0; j < 8; ++j) {
                if ((n3 & 0x1) != 0x0) {
                    n3 = (0xA001 ^ n3 >> 1);
                }
                else {
                    n3 >>= 1;
                }
            }
        }
        System.out.println(Integer.toHexString(n2));
        array2[0] = (byte)(n2 & 0xFF);
        array2[1] = (byte)(n2 >> 8);
        return 0;
    }

    public static String GetCurrentTimeString() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public static String GetDisplayString(final int n) {
        final StringBuilder sb = new StringBuilder();
        sb.append(n / 10);
        sb.append(".");
        sb.append(n % 10);
        return sb.toString();
    }

    public static String GetDisplayString(final short n) {
        final StringBuilder sb = new StringBuilder();
        sb.append(n / 10);
        sb.append(".");
        sb.append(n % 10);
        return sb.toString();
    }

    public static int GetDistance(final double n, final double n2, final double n3, final double n4) {
        final double rad = rad(n);
        final double rad2 = rad(n3);
        return (int)(6378137.0 * (2.0 * Math.asin(Math.sqrt(Math.pow(Math.sin((rad - rad2) / 2.0), 2.0) + Math.cos(rad) * Math.cos(rad2) * Math.pow(Math.sin((rad(n2) - rad(n4)) / 2.0), 2.0)))));
    }

    public static String HttpPost(final String s, final String s2) {
        final AndroidHttpClient instance = AndroidHttpClient.newInstance("");
        Object entity;
        try {
            entity = new StringEntity(s2);
        }
        catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            entity = null;
        }
        final HttpPost httpPost = new HttpPost(s);
        httpPost.setEntity((HttpEntity)entity);
        HttpResponse execute;
        try {
            execute = instance.execute((HttpUriRequest)httpPost);
        }
        catch (Exception ex2) {
            MyLog.log(ex2);
            execute = null;
        }
        if (execute != null && execute.getStatusLine() != null && execute.getStatusLine().getStatusCode() == 200) {
            final HttpEntity entity2 = execute.getEntity();
            try {
                return EntityUtils.toString(entity2);
            }
            catch (Exception ex3) {
                MyLog.log(ex3);
            }
        }
        return null;
    }

    public static String MD5(final String s) {
        final String[] array = new String[16];
        int i = 0;
        array[0] = "0";
        array[1] = "1";
        array[2] = "2";
        array[3] = "3";
        array[4] = "4";
        array[5] = "5";
        array[6] = "6";
        array[7] = "7";
        array[8] = "8";
        array[9] = "9";
        array[10] = "A";
        array[11] = "B";
        array[12] = "C";
        array[13] = "D";
        array[14] = "E";
        array[15] = "F";
        try {
            final byte[] bytes = s.getBytes();
            final MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(bytes);
            final byte[] digest = instance.digest();
            final int length = digest.length;
            String string = "";
            while (i < length) {
                final byte b = digest[i];
                final StringBuilder sb = new StringBuilder();
                sb.append(string);
                sb.append(array[0xF & b >>> 4]);
                final String string2 = sb.toString();
                final StringBuilder sb2 = new StringBuilder();
                sb2.append(string2);
                sb2.append(array[b & 0xF]);
                string = sb2.toString();
                ++i;
            }
            return string;
        }
        catch (Exception ex) {
            MyLog.log(ex);
            return null;
        }
    }

    static Process createSuProcess() throws IOException {
        final File file = new File("/system/xbin/ru");
        if (file.exists()) {
            return Runtime.getRuntime().exec(file.getAbsolutePath());
        }
        return Runtime.getRuntime().exec("su");
    }

    static Process createSuProcess(final String s) throws IOException {
        final Process suProcess = createSuProcess();
        FilterOutputStream filterOutputStream = null;
        try {
            final DataOutputStream dataOutputStream = new DataOutputStream(suProcess.getOutputStream());
            try {
                final StringBuilder sb = new StringBuilder();
                sb.append(s);
                sb.append("\n");
                dataOutputStream.writeBytes(sb.toString());
                dataOutputStream.writeBytes("exit $?\n");
                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    }
                    catch (IOException ex) {}
                    return suProcess;
                }
            }
            finally {}
        }
        finally {
            filterOutputStream = null;
        }
        if (filterOutputStream != null) {
            try {
                filterOutputStream.close();
            }
            catch (IOException ex2) {}
        }
        return suProcess;
    }

    private static double rad(final double n) {
        return n * 3.141592653589793 / 180.0;
    }

    static void requestPermission() throws InterruptedException, IOException {
        createSuProcess("chmod 666 /dev/alarm").waitFor();
    }

    @SuppressLint("WrongConstant")
    public static void setDate(final int n, final int n2, final int n3) throws IOException, InterruptedException {
        requestPermission();
        final Calendar instance = Calendar.getInstance();
        instance.set(1, n);
        instance.set(2, n2);
        instance.set(5, n3);
        final long timeInMillis = instance.getTimeInMillis();
        if (timeInMillis / 1000L < 2147483647L) {
            SystemClock.setCurrentTimeMillis(timeInMillis);
        }
        if (Calendar.getInstance().getTimeInMillis() - timeInMillis > 1000L) {
            throw new IOException("failed to set Date.");
        }
    }

    @SuppressLint("WrongConstant")
    public static void setDateTime(final int n, final int n2, final int n3, final int n4, final int n5) throws IOException, InterruptedException {
        requestPermission();
        final Calendar instance = Calendar.getInstance();
        instance.set(1, n);
        instance.set(2, n2 - 1);
        instance.set(5, n3);
        instance.set(11, n4);
        instance.set(12, n5);
        final long timeInMillis = instance.getTimeInMillis();
        if (timeInMillis / 1000L < 2147483647L) {
            SystemClock.setCurrentTimeMillis(timeInMillis);
        }
        if (Calendar.getInstance().getTimeInMillis() - timeInMillis > 1000L) {
            throw new IOException("failed to set Date.");
        }
    }

    public static void setDateTime(final Date date) throws IOException, InterruptedException {
        SystemClock.setCurrentTimeMillis(date.getTime());
    }

    @SuppressLint("WrongConstant")
    public static void setTime(final int n, final int n2) throws IOException, InterruptedException {
        requestPermission();
        final Calendar instance = Calendar.getInstance();
        instance.set(11, n);
        instance.set(12, n2);
        final long timeInMillis = instance.getTimeInMillis();
        if (timeInMillis / 1000L < 2147483647L) {
            SystemClock.setCurrentTimeMillis(timeInMillis);
        }
        if (Calendar.getInstance().getTimeInMillis() - timeInMillis > 1000L) {
            throw new IOException("failed to set Time.");
        }
    }
}