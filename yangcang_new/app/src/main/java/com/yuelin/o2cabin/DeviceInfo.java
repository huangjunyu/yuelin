package com.yuelin.o2cabin;

import android.app.*;
import android.os.*;
import android.provider.*;
import android.telephony.*;
import android.net.wifi.*;

public class DeviceInfo
{
    public String ApiLevel;
    public String DeviceID;
    public String DiseaseUpdateDate;
    public String FactoryID1;
    public String FactoryID2;
    public String FactoryID3;
    public String FactoryID4;
    public String IMEI;
    public String IMSI;
    public String Model;
    public final String OS;
    public String OSVersion;
    public String Operator;
    public String SIMID;
    public String ServersListUpdateDate;
    public String SoftBuild;
    public final String VersionCode;
    public final String VersionName;
    public String WifiIP;
    public String WifiMac;
    
    public DeviceInfo(final Application application) {
        this.OS = "android";
        this.VersionCode = "1";
        this.VersionName = "1.0.0";
        this.SoftBuild = "201709261400";
        this.DiseaseUpdateDate = "20170926";
        this.ServersListUpdateDate = "20170926";
        this.Model = Build.MODEL;
        this.OSVersion = Build.VERSION.RELEASE;
        this.ApiLevel = Build.VERSION.SDK;
        this.DeviceID = Settings.System.getString(application.getContentResolver(), "android_id");
        final TelephonyManager telephonyManager = (TelephonyManager)application.getSystemService("phone");
        this.IMEI = telephonyManager.getDeviceId();
        this.SIMID = telephonyManager.getSimSerialNumber();
        this.IMSI = telephonyManager.getSubscriberId();
        this.Operator = telephonyManager.getNetworkOperatorName();
        final WifiManager wifiManager = (WifiManager)application.getSystemService("wifi");
        WifiInfo connectionInfo;
        if (wifiManager == null) {
            connectionInfo = null;
        }
        else {
            connectionInfo = wifiManager.getConnectionInfo();
        }
        if (connectionInfo != null) {
            this.WifiMac = connectionInfo.getMacAddress();
            this.WifiIP = Integer.toString(connectionInfo.getIpAddress());
        }
    }
    
    public boolean ImsiIsExist() {
        return this.IMSI != null && this.IMSI.length() != 0;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Model=");
        sb.append(this.Model);
        sb.append(",OS=");
        sb.append("android");
        sb.append(",OSVersion=");
        sb.append(this.OSVersion);
        sb.append(",ApiLevel=");
        sb.append(this.ApiLevel);
        sb.append(",FactoryID1=");
        sb.append(this.FactoryID1);
        sb.append(",FactoryID2=");
        sb.append(this.FactoryID2);
        sb.append(",FactoryID3=");
        sb.append(this.FactoryID3);
        sb.append(",FactoryID4=");
        sb.append(this.FactoryID4);
        sb.append(",DeviceID=");
        sb.append(this.DeviceID);
        sb.append(",IMEI=");
        sb.append(this.IMEI);
        sb.append(",SIMID=");
        sb.append(this.SIMID);
        sb.append(",IMSI=");
        sb.append(this.IMSI);
        sb.append(",Operator=");
        sb.append(this.Operator);
        sb.append(",WifiMac=");
        sb.append(this.WifiMac);
        sb.append(",WifiIP=");
        sb.append(this.WifiIP);
        sb.append(",VersionCode=");
        sb.append("1");
        sb.append(",VersionName=");
        sb.append("1.0.0");
        sb.append(",SoftBuild=");
        sb.append(this.SoftBuild);
        sb.append(",DiseaseUpdateDate=");
        sb.append(this.DiseaseUpdateDate);
        sb.append(",ServersListUpdateDate=");
        sb.append(this.ServersListUpdateDate);
        return sb.toString();
    }
}
