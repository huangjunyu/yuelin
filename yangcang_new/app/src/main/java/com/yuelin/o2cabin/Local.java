package com.yuelin.o2cabin;

import android.content.*;

public class Local
{
    public static SharedPreferences settings;

    static {
        Local.settings = null;
    }

    public static boolean GetBooleanValue(final String s) {
        return getSettings().getBoolean(s, true);
    }

    public static byte[] GetBytesValue(final String s) {
        return Base64.decode(GetStringValue(s));
    }

    public static float GetFloatValue(final String s) {
        return getSettings().getFloat(s, 0.0f);
    }

    public static float GetFloatValue(final String s, final float n) {
        return getSettings().getFloat(s, n);
    }

    public static int GetIntgerValue(final String s) {
        return getSettings().getInt(s, 0);
    }

    public static int GetIntgerValue(final String s, final int n) {
        return getSettings().getInt(s, n);
    }

    public static long GetLongValue(final String s) {
        return getSettings().getLong(s, 0L);
    }

    public static String GetStringValue(final String s) {
        return getSettings().getString(s, "");
    }

    public static String GetStringValue(final String s, final String s2) {
        return getSettings().getString(s, s2);
    }

    public static void SetBooleanValue(final String s, final boolean b) {
        final SharedPreferences.Editor edit = getSettings().edit();
        edit.putBoolean(s, b);
        edit.commit();
    }

    public static void SetBytesValue(final String s, final byte[] array) {
        final SharedPreferences.Editor edit = getSettings().edit();
        edit.putString(s, Base64.encode(array));
        edit.commit();
    }

    public static void SetFloatValue(final String s, final float n) {
        final SharedPreferences.Editor edit = getSettings().edit();
        edit.putFloat(s, n);
        edit.commit();
    }

    public static void SetIntgerValue(final String s, final int n) {
        final SharedPreferences.Editor edit = getSettings().edit();
        edit.putInt(s, n);
        edit.commit();
    }

    public static void SetLongValue(final String s, final long n) {
        final SharedPreferences.Editor edit = getSettings().edit();
        edit.putLong(s, n);
        edit.commit();
    }

    public static void SetStringValue(final String s, final String s2) {
        final SharedPreferences.Editor edit = getSettings().edit();
        edit.putString(s, s2);
        edit.commit();
    }

    public static SharedPreferences getSettings() {
        if (Local.settings == null) {
            return null;
        }
        return Local.settings;
    }
}