package com.yuelin.o2cabin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import com.baidu.location.BDLocation;
import com.zxing.encoding.EncodingHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

public class ControlThread extends Thread {
    public static ControlThread theOnly;
    public int[] O2 = new int[20];
    public int O2Count = 0;
    public short O2Value = 0;
    public int addedMinutes;
    public int age;
    public int cureID;
    public String deviceid = "";
    public int diseaseID;
    public String diseaseName;
    public int height;
    public String httpSession;
    public long lastCureHeartTime = 0L;
    public DataIn lastDataIn = null;
    public DataIn lastDataIn2 = null;
    public long lastDataInTime = 0L;
    public long lastHeartTime = 0L;
    public long lastPayQueryTime = 0L;
    public long lastSeconds;//运行状态下的上一次循环时间
    //    public BDLocation location;
    public boolean loginOK = false;
    public DataIn maxDataIn = new DataIn();
    public int needRunMinutes;
    public String openid = "";
    public String pathBase = "http://111.230.165.137:8080/Service.aspx";
    public String payType = "code";
    public String payUrl1;
    public String payUrl2;
    public boolean paying = false;//false表示已付款
    public boolean returnToMainActivity = false;
    private boolean running = true;
    SerialController serialController = null;
    public Date serverTime;
    public int sex = 1;
    public long startTick;
    public long startTime;//当进行扫码支付的时候,获得当前时间,当usb断开的时候startTime清零.
    boolean stopedByUser = false;
    private MyApplication theApp = null;
    Disease theDisease;
    public int weight;
    public String wxPayUrl = null;
    public final static Long thoushandMilliSecond = 1000L;

    private ControlThread(MyApplication paramMyApplication, SerialController paramSerialController) {
        theOnly = this;
        this.theApp = paramMyApplication;
    }

    public static ControlThread GetTheOnly(MyApplication paramMyApplication, SerialController paramSerialController) {
        if (theOnly == null)
            new ControlThread(paramMyApplication, paramSerialController);
        return theOnly;
    }

    public static void OnTimer(long currentTimeMillis) {
        final int dueDate;//到期日期
        int cureFlag = 1;//治疗标识（是否在治疗状态）
        final StatusActivity localStatusActivity;
        while (true) {
            MyLog.d("ControlThread", "Register_Cure:" + DataOut.Register_Cure);
            try {
                MainActivity.SetTimeString(Tools.GetCurrentTimeString());

                try {
                    if (DataOut.Register_Cure == 0) {
                        //noPayTime与上次轮询未付款间隔时间
                        long lastTimeNopayduring = (currentTimeMillis - theOnly.lastPayQueryTime) / thoushandMilliSecond;
                        long heartLastTimeDuring = (currentTimeMillis - theOnly.lastHeartTime) / thoushandMilliSecond;
                        if ((theOnly.paying) && (lastTimeNopayduring >= 10L)) {//如果月租到期未付费，每隔10s查询一遍月租期限，向服务器请求
                            MyLog.d("ControlThread", "call OnTimer() payquery");
                            theOnly.lastPayQueryTime = currentTimeMillis;
                            JSONObject localJSONObject3 = new JSONObject();
                            localJSONObject3.put("session", theOnly.httpSession);
                            localJSONObject3.put("action", "payquery");
                            localJSONObject3.put("time", currentTimeMillis);
                            String str2 = Tools.HttpPost(theOnly.pathBase, localJSONObject3.toString());
                            if ((str2 == null) || (str2.length() <= 0))
                                break;
                            JSONObject localJSONObject4 = new JSONObject(str2);
                            if ((localJSONObject4 == null) || (!localJSONObject4.has("code")) || (localJSONObject4.getInt("code") != 0) || (!localJSONObject4.has("payresult")))
                                break;
                            if (localJSONObject4.getInt("payresult") != 0)
                                break;
                            dueDate = localJSONObject4.getInt("duedate");
                            MainActivity.theOnly.runOnUiThread(new Runnable() {
                                public void run() {
                                    MainActivity.theOnly.qrcode.setVisibility(View.GONE);
                                    MainActivity localMainActivity = MainActivity.theOnly;
                                    StringBuilder localStringBuilder = new StringBuilder();
                                    localStringBuilder.append("支付成功! 服务器已经续费至 ");
                                    localStringBuilder.append(dueDate);
                                    Toast.makeText(localMainActivity, localStringBuilder.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            DataSaved.SetServerDueDate(dueDate);
                            theOnly.paying = false;
                            break;
                        }

                        //扫码登录，这个考虑点击刷新后，轮询一分钟，降低服务器的压力
                        if ((!theOnly.paying) && (heartLastTimeDuring >= 10L)) {//无需付款，且离上次心跳大于10秒
                            StringBuilder localStringBuilder1 = new StringBuilder();
                            localStringBuilder1.append("call OnTimer() heart session = ");
                            localStringBuilder1.append(theOnly.httpSession);
                            MyLog.d("ControlThread", localStringBuilder1.toString());
                            theOnly.lastHeartTime = currentTimeMillis;
                            JSONObject localJSONObject5 = new JSONObject();
                            localJSONObject5.put("session", theOnly.httpSession);
                            localJSONObject5.put("action", "heart");
                            localJSONObject5.put("time", currentTimeMillis);
                            String str3 = Tools.HttpPost(theOnly.pathBase, localJSONObject5.toString());
                            if ((str3 == null) || (str3.length() <= 0))
                                break;
                            JSONObject localJSONObject6 = new JSONObject(str3);
                            if ((localJSONObject6 == null) || (!localJSONObject6.has("code")) || (localJSONObject6.getInt("code") != 0))
                                break;
                            if (!localJSONObject6.has("openid"))
                                break;
                            theOnly.openid = localJSONObject6.getString("openid");
                            StringBuilder localStringBuilder2 = new StringBuilder();
                            localStringBuilder2.append("openid = ");
                            localStringBuilder2.append(theOnly.openid);
                            MyLog.d("ControlThread", localStringBuilder2.toString());
                            if ((!localJSONObject6.has("age")) || (!localJSONObject6.has("sex")) || (!localJSONObject6.has("height")) || (!localJSONObject6.has("weight")))
                                break;
                            theOnly.age = localJSONObject6.getInt("age");
                            theOnly.sex = localJSONObject6.getInt("sex");
                            theOnly.height = localJSONObject6.getInt("height");
                            theOnly.weight = localJSONObject6.getInt("weight");
                            MainActivity.theOnly.runOnUiThread(new Runnable() {
                                public void run() {
                                    EditText localEditText1 = MainActivity.theOnly.age;
                                    StringBuilder localStringBuilder1 = new StringBuilder();
                                    localStringBuilder1.append("");
                                    localStringBuilder1.append(ControlThread.theOnly.age);
                                    localEditText1.setText(localStringBuilder1.toString());
                                    if (ControlThread.theOnly.sex == 1) {
                                        MainActivity.theOnly.man0.setBackgroundResource(R.mipmap.select1);
                                        MainActivity.theOnly.woman0.setBackgroundResource(R.mipmap.select0);
                                    } else {
                                        MainActivity.theOnly.man0.setBackgroundResource(R.mipmap.select0);
                                        MainActivity.theOnly.woman0.setBackgroundResource(R.mipmap.select1);
                                    }
                                    EditText localEditText2 = MainActivity.theOnly.height;
                                    StringBuilder localStringBuilder2 = new StringBuilder();
                                    localStringBuilder2.append("");
                                    localStringBuilder2.append(ControlThread.theOnly.height);
                                    localEditText2.setText(localStringBuilder2.toString());
                                    EditText localEditText3 = MainActivity.theOnly.weight;
                                    StringBuilder localStringBuilder3 = new StringBuilder();
                                    localStringBuilder3.append("");
                                    localStringBuilder3.append(ControlThread.theOnly.weight);
                                    localEditText3.setText(localStringBuilder3.toString());
                                    MainActivity.theOnly.enter();
                                }
                            });
                            break;
                        }
                    }
                } catch (Exception localException2) {
                    MyLog.log(localException2);
                }

                //处理ItemActivity，查询是否付款
                if ((ItemActivity.theOnly != null) && (StatusActivity.theOnly == null))
                    ItemActivity.theOnly.OnTimer_PayQuery(currentTimeMillis);
                if (DataOut.Register_Cure == cureFlag) {//当DataOut.Register_Cure等于1的时候,说明正在治疗
                    theOnly.CheckData();//将运行的实时数据上传至服务器
                    MyLog.d("paramLong", "parmlong:" + currentTimeMillis + ":" + theOnly.startTime + ":" + theOnly.lastSeconds);

                    long runTimes = (currentTimeMillis - theOnly.startTime) / thoushandMilliSecond;//已运行时长 ？
                    if (runTimes > 60 * 60 * 24 * 12 * 5L) {//如果运行时长已超过了5年
                        currentTimeMillis = System.currentTimeMillis();
                        theOnly.startTime = (System.currentTimeMillis() - thoushandMilliSecond * theOnly.lastSeconds - thoushandMilliSecond);
                        runTimes = (currentTimeMillis - theOnly.startTime) / thoushandMilliSecond;
                    } else {
                        theOnly.lastSeconds = runTimes;
                    }
                    if (runTimes / 60L > theOnly.addedMinutes) {//如果已运行时长已超过记录的分钟数的一分钟，则加一分钟，保存到存储中
                        theOnly.AddTotalMinutes();
                        ControlThread localControlThread = theOnly;
                        localControlThread.addedMinutes = (cureFlag + localControlThread.addedMinutes);
                        if (StatusActivity.theOnly != null)
                            StatusActivity.theOnly.OnTimer(currentTimeMillis);//更新运行状态界面的时间
                        if (theOnly.addedMinutes >= theOnly.needRunMinutes) {
                            if (StatusActivity.theOnly != null) {
                                localStatusActivity = StatusActivity.theOnly;
                                StatusActivity.theOnly = null;
                                localStatusActivity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            Intent localIntent = new Intent(localStatusActivity, MainActivity.class);
                                            localStatusActivity.startActivity(localIntent);
                                            return;
                                        } catch (Exception localException) {
                                            MyLog.log(localException);
                                        }
                                    }
                                });
                            }
                            DataOut.Register_Cure = 0;
                            try {
                                JSONObject localJSONObject1 = new JSONObject();
                                localJSONObject1.put("session", theOnly.httpSession);
                                localJSONObject1.put("action", "cureend");
                                localJSONObject1.put("cureid", theOnly.cureID);
                                localJSONObject1.put("time", currentTimeMillis);
                                String str1 = Tools.HttpPost(theOnly.pathBase, localJSONObject1.toString());
                                if ((str1 == null) || (str1.length() <= 0))
                                    break;
                                JSONObject localJSONObject2 = new JSONObject(str1);
                                if ((localJSONObject2 == null) || (!localJSONObject2.has("code")) || (localJSONObject2.getInt("code") != 0))
                                    break;
                            } catch (Exception localException3) {
                                MyLog.log(localException3);
                            }
                        }
                    }
                    if (StatusActivity.theOnly != null) {
                        StatusActivity.theOnly.OnTimer(currentTimeMillis);
                        return;
                    }
                }
            } catch (Exception localException1) {
                MyLog.log(localException1);
            }
            return;
        }
    }

    public int AddTotalMinutes() {
        int i = 1 + DataSaved.GetTotalRuntimeMinutes();
        DataSaved.SetTotalRuntimeMinutes(i);
        return i;
    }

    void CheckData() {
        boolean bool;
        while (true) {
            try {
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - this.lastCureHeartTime > 10000L)
                    if (this.lastDataIn != null) {
                        if (this.maxDataIn == null)
                            return;
                        this.lastCureHeartTime = currentTimeMillis;
                        JSONObject localJSONObject1 = new JSONObject();
                        localJSONObject1.put("session", this.httpSession);
                        localJSONObject1.put("action", "cureheart");
                        localJSONObject1.put("cureid", this.cureID);
                        localJSONObject1.put("kpa", this.lastDataIn.Register_Kpa);
                        localJSONObject1.put("o2", theOnly.O2Value);
                        localJSONObject1.put("cabino2", this.lastDataIn.Register_Cabin_O2_Concentration);
                        localJSONObject1.put("pm25", this.lastDataIn.Register_PM25);
                        localJSONObject1.put("maxkpa", this.maxDataIn.Register_Kpa);
                        localJSONObject1.put("maxo2", this.maxDataIn.Register_O2_Concentration);
                        localJSONObject1.put("maxcabino2", this.maxDataIn.Register_Cabin_O2_Concentration);
                        localJSONObject1.put("maxpm25", this.maxDataIn.Register_PM25);
                        localJSONObject1.put("cureminutes", theOnly.needRunMinutes);
                        localJSONObject1.put("totalminutes", DataSaved.GetTotalRuntimeMinutes());
                        localJSONObject1.put("time", currentTimeMillis);
                        String str = Tools.HttpPost(theOnly.pathBase, localJSONObject1.toString());
                        bool = true;
                        if ((str == null) || (str.length() <= 0))
                            break;
                        JSONObject localJSONObject2 = new JSONObject(str);
                        if ((localJSONObject2 == null) || (!localJSONObject2.has("code")) || (localJSONObject2.getInt("code") != 0))
                            break;
                        if (localJSONObject2.has("stoped")) {
                            MyLog.d("ControlThread", "Stoped by User");
                            DataOut.Register_Cure = 0;
                            theOnly.stopedByUser = bool;
                            theOnly.startTime = 0L;
                            theOnly.addedMinutes = 0;
                            theOnly.lastSeconds = 0L;
                            theOnly.lastCureHeartTime = 0L;
                            theOnly.needRunMinutes = 0;
                            theOnly.cureID = -1;
                        }
                    } else {
                        return;
                    }
            } catch (Exception localException) {
                MyLog.log(localException);
            }
            return;
        }
    }

    public void Init(long paramLong) {
        try {
            MyLog.d("ControlThread::Init()", "001");
            InitSerial();
            MyLog.d("ControlThread::Init()", "002");
            DeviceInfo localDeviceInfo = new DeviceInfo(this.theApp);
            localDeviceInfo.FactoryID1 = DataSaved.GetFactoryID1();
            localDeviceInfo.FactoryID2 = DataSaved.GetFactoryID2();
            localDeviceInfo.FactoryID3 = DataSaved.GetFactoryID3();
            localDeviceInfo.FactoryID4 = DataSaved.GetFactoryID4();
            StringBuilder localStringBuilder1 = new StringBuilder();
            localStringBuilder1.append("");
            localStringBuilder1.append(DataSaved.GetDiseaseUpdateDate());
            localDeviceInfo.DiseaseUpdateDate = localStringBuilder1.toString();
            StringBuilder localStringBuilder2 = new StringBuilder();
            localStringBuilder2.append("");
            localStringBuilder2.append(DataSaved.GetServersListUpdateDate());
            localDeviceInfo.ServersListUpdateDate = localStringBuilder2.toString();
            int i = 0;
            MyLog.d("ControlThread::Init()", "003");
            while (true) {//循环10次
                int j = i + 1;
                if ((i >= 10) || (this.loginOK))
                    break;
                MyLog.d("ControlThread::Init()", "004");
                JSONObject localJSONObject1 = new JSONObject();
                StringBuilder localStringBuilder3 = new StringBuilder();
                localStringBuilder3.append(localDeviceInfo.DeviceID);
                localStringBuilder3.append(localDeviceInfo.IMEI);
                localStringBuilder3.append(localDeviceInfo.WifiMac);
                localStringBuilder3.append(localDeviceInfo.SIMID);
                localStringBuilder3.append(localDeviceInfo.IMSI);
                localStringBuilder3.append(localDeviceInfo.DiseaseUpdateDate);
                localStringBuilder3.append(paramLong);
                localStringBuilder3.append("#@ylyc20ix888ns");
                String str1 = Tools.MD5(localStringBuilder3.toString());
                this.deviceid = localDeviceInfo.DeviceID;
                this.httpSession = str1;
                localJSONObject1.put("session", str1);
                localJSONObject1.put("action", "login");
                localJSONObject1.put("time", paramLong);
                localJSONObject1.put("version", MainActivity.theOnly.getVersionCode());
                localJSONObject1.put("baseinfo", localDeviceInfo.toString());
                MyLog.d("JSON", localJSONObject1.toString());
                try {
                    boolean bool1 = DataSaved.InitDiseases();
                    StringBuilder localStringBuilder4 = new StringBuilder();
                    localStringBuilder4.append(" InitDiseases result = ");
                    localStringBuilder4.append(bool1);
                    MyLog.d("ControlThread", localStringBuilder4.toString());
                    MyLog.d("ControlThread::Init()", "005");
                    String str2 = Tools.HttpPost(this.pathBase, localJSONObject1.toString());
                    MyLog.d("ControlThread::Init()", "006");
                    StringBuilder localStringBuilder5 = new StringBuilder();
                    localStringBuilder5.append("http ret:");
                    localStringBuilder5.append(str2);
                    MyLog.d("ControlThread", localStringBuilder5.toString());
                    if ((str2 != null) && (str2.length() > 0)) {
                        final JSONObject localJSONObject2 = new JSONObject(str2);
                        if (localJSONObject2 != null) {
                            this.loginOK = true;
                            if ((localJSONObject2.has("id3")) && (localJSONObject2.has("id4"))) {
                                DataSaved.SetFactoryID3(localJSONObject2.getString("id3"));
                                DataSaved.SetFactoryID4(localJSONObject2.getString("id4"));
                                if (MainActivity.theOnly != null)
                                    MainActivity.theOnly.runOnUiThread(new Runnable() {
                                        public void run() {
                                            try {
                                                MainActivity.theOnly.machineid.setText(DataSaved.GetFactoryID());//FactoryID由id1\2\3\4组成
                                                return;
                                            } catch (Exception localException) {
                                                MyLog.log(localException);
                                            }
                                        }
                                    });
                            }
                            boolean bool2 = localJSONObject2.has("currenttime");//获取服务器时间
                            if (bool2)
                                try {
                                    this.serverTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(localJSONObject2.getString("currenttime"));
                                    this.startTick = System.currentTimeMillis();
                                } catch (Exception localException4) {
                                    MyLog.log(localException4);
                                }
                            try {
                                MyLog.d("enter", "=====");
                                if ((localJSONObject2.has("updateurl")) && (localJSONObject2.has("updateinfo")) && (localJSONObject2.has("updateversion"))) {
                                    MyLog.d("updateurl", localJSONObject2.get("updateurl").toString());
                                    MyLog.d("updateinfo", localJSONObject2.get("updateinfo").toString());
                                    MyLog.d("updateversion", localJSONObject2.getString("updateversion").toString());
                                    MainActivity.theOnly.runOnUiThread(new Runnable() {
                                        public void run() {
                                            try {
                                                new Update(MainActivity.theOnly).update(localJSONObject2.getString("updateurl"), localJSONObject2.getString("updateinfo"), localJSONObject2.getInt("updateversion"));
                                                return;
                                            } catch (Exception localException) {
                                                MyLog.log(localException);
                                            }
                                        }
                                    });
                                }
                            } catch (Exception localException3) {
                                MyLog.log(localException3);
                            }
                            if (localJSONObject2.has("duedate")) {
                                DataSaved.SetServerDueDate(localJSONObject2.getInt("duedate"));
                                StringBuilder localStringBuilder8 = new StringBuilder();
                                localStringBuilder8.append("jsonRet has duedate : ");
                                localStringBuilder8.append(localJSONObject2.getInt("duedate"));
                                MyLog.d("ControlThread", localStringBuilder8.toString());
                            } else {
                                MyLog.d("ControlThread", "jsonRet no duedate");
                            }
                            if (localJSONObject2.has("locked")) {
                                DataSaved.SetServerLock(localJSONObject2.getInt("locked"));
                                StringBuilder localStringBuilder7 = new StringBuilder();
                                localStringBuilder7.append("jsonRet has lock data : ");
                                localStringBuilder7.append(localJSONObject2.getInt("locked"));
                                MyLog.d("ControlThread", localStringBuilder7.toString());
                            } else {
                                MyLog.d("ControlThread", "jsonRet no lock data");
                            }
                            if (localJSONObject2.has("loginqrcode")) {
                                final String str4 = localJSONObject2.getString("loginqrcode");
                                MainActivity.theOnly.runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            Bitmap localBitmap = EncodingHandler.createQRCode(str4, 400);
                                            if (localBitmap != null) {
                                                MainActivity.theOnly.qrcode.setImageBitmap(localBitmap);
                                                return;
                                            }
                                        } catch (Exception localException) {
                                            MyLog.log(localException);
                                        }
                                    }
                                });
                            }
                            String str3 = localJSONObject2.getString("payurl");
                            if (str3 != null)
                                theOnly.wxPayUrl = str3;
                            JSONArray localJSONArray = localJSONObject2.getJSONArray("diseases");
                            if (localJSONArray != null) {
                                StringBuilder localStringBuilder6 = new StringBuilder();
                                localStringBuilder6.append(" result = ");
                                localStringBuilder6.append(bool1);
                                MyLog.d("ControlThread", localStringBuilder6.toString());
                                DataSaved.AddOrReplaceDiseases(localJSONArray);
                            }
                        }
                    }
                    Thread.sleep(2000L);
                } catch (Exception localException2) {
                    MyLog.log(localException2);
                }
                i = j;
            }
        } catch (Exception localException1) {
            MyLog.log(localException1);
        }
    }

    public void InitSerial() {
        SerialController localSerialController = new SerialController();
        boolean bool = localSerialController.Init();//检测是否支持usb接口
        int i = 0;
        if (bool)
            while ((i < 2) && (!localSerialController.Open(9600))) {
                i++;
                try {
                    Thread.sleep(thoushandMilliSecond);
                } catch (Exception localException) {
                    MyLog.log(localException);
                }
            }
        if (i >= 30) {
            MyLog.d("ControlThread::InitSerial()", "Error");
            this.serialController = null;
            return;
        }
        MyLog.d("ControlThread::InitSerial()", "OK");
        this.serialController = localSerialController;
    }

    public void OnDataArrived(byte[] paramArrayOfByte) {
        MyLog.d("ControlThread", "Enter OnDataArrived()");
    }

    public void StartCure(DiseaseParam paramDiseaseParam) {
    }

    public void StartDisinfect() {
        DataOut.Register_Disinfect = 1;
    }

    public void StartPay(short paramShort) {
        DataOut.Register_Card_Pay = 1;
        DataOut.Register_Unit_Price = paramShort;
    }

    public void Stop() {
        this.running = false;
        try {
            Thread.sleep(50L);
        } catch (Exception localException) {
        }
    }

    public void StopCure(String paramString) {
    }

    public void StopDisinfect() {
        DataOut.Register_Disinfect = 0;
    }

    public void StopPay() {
        DataOut.Register_Card_Pay = 0;
    }

    public void run() {
        try {
            MyLog.d("ControlThread", "Started!!!");
            long currentTimeMillis = System.currentTimeMillis();
            Init(currentTimeMillis);
            DataSaved.LoadOfflineData();
            while (this.running) {
                OnTimer(currentTimeMillis);
                DisinfectActivity.OnTimer_Static(currentTimeMillis);
                try {
                    Thread.sleep(100L);
                } catch (Exception localException2) {
                }
                currentTimeMillis = System.currentTimeMillis();
            }
        } catch (Exception localException1) {
            MyLog.log(localException1);
            Logger.getLogger(MyApplication.class.getName()).log(Level.SEVERE, null, localException1);
        }
    }
}