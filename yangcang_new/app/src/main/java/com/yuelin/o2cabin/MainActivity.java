package com.yuelin.o2cabin;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.*;
import android.content.*;
import org.json.*;
import android.app.*;
import android.graphics.*;
import android.os.*;
import android.support.v4.content.*;
import android.support.v4.app.*;
import android.view.*;
import android.location.*;
//import com.baidu.location.*;
import java.io.IOException;
import java.util.*;

import com.zxing.encoding.EncodingHandler;

public class MainActivity extends Activity implements LocationListener
{
    private static String[] PERMISSIONS_STORAGE;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String TAG = "MainActivity";
    static MainActivity theOnly;
    EditText age;
    ImageButton disinfect;
    ImageButton enter;
    EditText height;
    long lastLogoClickTime;
//    public LocationClient mLocationClient;
    TextView machineid;
    ImageButton man0;
    Button man1;
    ImageButton pay;
    ImageView qrcode;
    TextView time;
    ImageView userinfo;
    EditText weight;
    ImageButton woman0;
    Button woman1;

    static {
        MainActivity.PERMISSIONS_STORAGE = new String[] { "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.READ_PHONE_STATE", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE" };
    }

    public MainActivity() {
        this.lastLogoClickTime = 0L;
    }

    private boolean OutOfDate() {
        final Date serverTime = ControlThread.theOnly.serverTime;
        if (serverTime == null) {
            return true;
        }
        final int n = 10000 * (1900 + serverTime.getYear()) + 100 * (1 + serverTime.getMonth()) + serverTime.getDate();
        final StringBuilder sb = new StringBuilder();
        sb.append("server date=");
        sb.append(n);
        sb.append(",duedate=");
        sb.append(DataSaved.GetServerDueDate());
        MyLog.d("MainActivity", sb.toString());
        return n >= DataSaved.GetServerDueDate();
    }

    public static boolean SetTimeString(final String s) {
        if (MainActivity.theOnly != null) {
            MainActivity.theOnly.runOnUiThread((Runnable)new Runnable() {
                @Override
                public void run() {
                    MainActivity.theOnly.time.setText((CharSequence)Tools.GetDisplayString(DataSaved.GetTotalRuntimeMinutes() / 6));
                }
            });
            return true;
        }
        return false;
    }

    private void findAndInitViews() {
        try {
            this.userinfo = (ImageView)this.findViewById(R.id.userinfo);
            this.age = (EditText)this.findViewById(R.id.age);
            this.man0 = (ImageButton)this.findViewById(R.id.man0);
            this.man1 = (Button)this.findViewById(R.id.man1);
            this.woman0 = (ImageButton)this.findViewById(R.id.woman0);
            this.woman1 = (Button)this.findViewById(R.id.woman1);
            this.height = (EditText)this.findViewById(R.id.height);
            this.weight = (EditText)this.findViewById(R.id.weight);
            this.qrcode = (ImageView)this.findViewById(R.id.qrcode);
            this.disinfect = (ImageButton)this.findViewById(R.id.disinfect);
            this.enter = (ImageButton)this.findViewById(R.id.enter);
            this.pay = (ImageButton)this.findViewById(R.id.pay);
            this.time = (TextView)this.findViewById(R.id.time);
            this.machineid = (TextView)this.findViewById(R.id.machineid);
            if (this.machineid != null) {
                this.machineid.setText((CharSequence)DataSaved.GetFactoryID());
            }
            else {
                MyLog.d("MainActivity", "machineid is null");
            }
            this.pay.setOnClickListener((View.OnClickListener)new View.OnClickListener() {
                public void onClick(final View view) {
                    MyLog.d("MainActivity", "Pay Clicked!");
                    if (ControlThread.theOnly.wxPayUrl != null && ControlThread.theOnly.wxPayUrl != "") {
                        MainActivity.this.showPayDialog();
                        return;
                    }
                    Toast.makeText((Context)MainActivity.this, (CharSequence)"请等待网络连接成功后再操作", 1).show();
                }
            });
            this.userinfo.setOnClickListener((View.OnClickListener)new View.OnClickListener() {
                public void onClick(final View view) {
                    final long currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis - MainActivity.this.lastLogoClickTime > 600L) {
                        MainActivity.this.lastLogoClickTime = currentTimeMillis;
                        return;
                    }
                    MainActivity.this.lastLogoClickTime = 0L;
                    MainActivity.this.startActivity(new Intent((Context)MainActivity.this, (Class)PasswordActivity.class));
                }
            });
            this.disinfect.setOnClickListener((View.OnClickListener)new View.OnClickListener() {
                public void onClick(final View view) {
                    if (ControlThread.theOnly == null || ControlThread.theOnly.lastDataIn2 == null) {
                        Toast.makeText((Context)MainActivity.this, (CharSequence)"舱内压力过大,暂时不能消毒", 1).show();
                        return;
                    }
                    if (ControlThread.theOnly.lastDataIn2.Register_Kpa > 50) {
                        Toast.makeText((Context)MainActivity.this, (CharSequence)"无法检测舱内压力,暂时不能消毒", 1).show();
                        return;
                    }
                    MainActivity.this.startActivity(new Intent((Context)MainActivity.this, (Class)DisinfectActivity.class));
                }
            });
            this.man0.setOnClickListener((View.OnClickListener)new View.OnClickListener() {
                public void onClick(final View view) {
                    MainActivity.this.man0.setBackgroundResource(R.mipmap.select1);
                    MainActivity.this.woman0.setBackgroundResource(R.mipmap.select0);
                    ControlThread.theOnly.sex = 1;
                }
            });
            this.man1.setOnClickListener((View.OnClickListener)new View.OnClickListener() {
                public void onClick(final View view) {
                    MainActivity.this.man0.setBackgroundResource(R.mipmap.select1);
                    MainActivity.this.woman0.setBackgroundResource(R.mipmap.select0);
                    ControlThread.theOnly.sex = 1;
                }
            });
            this.woman0.setOnClickListener((View.OnClickListener)new View.OnClickListener() {
                public void onClick(final View view) {
                    MainActivity.this.woman0.setBackgroundResource(R.mipmap.select1);
                    MainActivity.this.man0.setBackgroundResource(R.mipmap.select0);
                    ControlThread.theOnly.sex = 0;
                }
            });
            this.woman1.setOnClickListener((View.OnClickListener)new View.OnClickListener() {
                public void onClick(final View view) {
                    MainActivity.this.woman0.setBackgroundResource(R.mipmap.select1);
                    MainActivity.this.man0.setBackgroundResource(R.mipmap.select0);
                    ControlThread.theOnly.sex = 0;
                }
            });
            this.enter.setOnClickListener((View.OnClickListener)new View.OnClickListener() {
                public void onClick(final View view) {
                    MainActivity.this.enter();
                    try {
                        if (!"".equals(ControlThread.theOnly.openid)) {
                            final JSONObject jsonObject = new JSONObject();
                            jsonObject.put("session", (Object)ControlThread.theOnly.httpSession);
                            jsonObject.put("action", (Object)"param");
                            jsonObject.put("time", System.currentTimeMillis());
                            jsonObject.put("openid", (Object)ControlThread.theOnly.openid);
                            jsonObject.put("age", ControlThread.theOnly.age);
                            jsonObject.put("sex", ControlThread.theOnly.sex);
                            jsonObject.put("height", ControlThread.theOnly.height);
                            jsonObject.put("weight", ControlThread.theOnly.weight);
                            new HttpTask(MainActivity.this).execute((String[]) new String[] { jsonObject.toString() });
                            MyLog.d("MainActivity", "Enter.setOnClickListener");
                        }
                    }
                    catch (Exception ex) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Exception Message:");
                        sb.append(ex.getMessage());
                        MyLog.d("MainActivity", sb.toString());
                        MyLog.log(ex);
                    }
                }
            });
        }
        catch (Exception ex) {
            MyLog.log(ex);
        }
    }

    private void setFullScreen() {
        this.findViewById(R.id.fullscreen).setSystemUiVisibility(4871);
    }

    private void showPayDialog() {
        final Dialog dialog = new Dialog((Context)this);
        final View inflate = LayoutInflater.from((Context)this).inflate(R.layout.dialog_pay, (ViewGroup)null);
        dialog.setContentView(inflate);
        final ImageView imageView = (ImageView)inflate.findViewById(R.id.payqrcode);
        final TextView textView = (TextView)inflate.findViewById(R.id.duedate);
        final ImageButton imageButton = (ImageButton)inflate.findViewById(R.id.ok);
        try {
            final int getServerDueDate = DataSaved.GetServerDueDate();
            String string;
            if (getServerDueDate >= 20891231) {
                string = "买断方式，请勿支付";
            }
            else {
                final StringBuilder sb = new StringBuilder();
                sb.append("到期日期:");
                sb.append(getServerDueDate / 10000);
                sb.append("(\"年");
                sb.append(getServerDueDate / 100 % 100);
                sb.append("月");
                sb.append(getServerDueDate % 100);
                sb.append("日");
                string = sb.toString();
            }
            textView.setText((CharSequence)string);
            imageButton.setOnClickListener((View.OnClickListener)new View.OnClickListener() {
                public void onClick(final View view) {
                    dialog.dismiss();
                    ControlThread.theOnly.paying = false;
                }
            });
            final Bitmap qrCode = EncodingHandler.createQRCode(ControlThread.theOnly.wxPayUrl, 400);
            Log.e("uuuus",ControlThread.theOnly.wxPayUrl + ".");
            if (qrCode != null) {
                imageView.setImageBitmap(qrCode);
            }
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("PayUrl = ");
            sb2.append(ControlThread.theOnly.wxPayUrl);
            MyLog.d("MainActivity", sb2.toString());
            ControlThread.theOnly.paying = true;
        }
        catch (Exception ex) {
            MyLog.log(ex);
        }
        dialog.show();
    }

    public void OnHttpReturn(final String s) {
    }

    public void enter() {
        MyLog.d("Enter::onClick()", "001");
        final StringBuilder sb = new StringBuilder();
        sb.append("sex = ");
        sb.append(ControlThread.theOnly.sex);
        MyLog.d("MainActivity", sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("age = ");
        sb2.append(ControlThread.theOnly.age);
        MyLog.d("MainActivity", sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("weight = ");
        sb3.append(ControlThread.theOnly.weight);
        MyLog.d("MainActivity", sb3.toString());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("height = ");
        sb4.append(ControlThread.theOnly.height);
        MyLog.d("MainActivity", sb4.toString());
        MyLog.d("Enter::onClick()", "006");
        if (ControlThread.theOnly.serialController != null && ControlThread.theOnly.serialController.IsConnected()) {
            if (System.currentTimeMillis() - ControlThread.theOnly.lastDataInTime < 2000L) {
                MyLog.d("Enter::onClick()", "002");
                if (!ControlThread.theOnly.loginOK && DataSaved.GetServerDueDate() < 20891231) {
                    Toast.makeText((Context)this, (CharSequence)"网络连接失败，请重新开机再次尝试！", 1).show();
                    return;
                }
                MyLog.d("Enter::onClick()", "003");
                if (DataSaved.GetServerLock() != 0) {
                    Toast.makeText((Context)this, (CharSequence)"设备已经被锁定，请联系服务热线进行咨询！", 1).show();
                    return;
                }
                MyLog.d("Enter::onClick()", "004");
                try {
                    ControlThread.theOnly.age = Integer.parseInt(this.age.getText().toString());
                    ControlThread.theOnly.weight = Integer.parseInt(this.weight.getText().toString());
                    ControlThread.theOnly.height = Integer.parseInt(this.height.getText().toString());
                    Log.e("Enter::onClick()", ControlThread.theOnly + "." );

                    if (ControlThread.theOnly.age < 0 || ControlThread.theOnly.age > 150) {
                        Toast.makeText((Context)this, (CharSequence)"请输入正确的年龄", 0).show();
                        return;
                    }
                    MyLog.d("Enter::onClick()", "007");
                    if (ControlThread.theOnly.weight < 0 || ControlThread.theOnly.weight > 499) {
                        Toast.makeText((Context)this, (CharSequence)"请输入正确的体重", 0).show();
                        return;
                    }
                    MyLog.d("Enter::onClick()", "008");
                    if (ControlThread.theOnly.height >= 30 && ControlThread.theOnly.height <= 300) {
                        MyLog.d("Enter::onClick()", "009");
                        final Date date = new Date();
                        final int n = 10000 * (1900 + date.getYear()) + 100 * (1 + date.getMonth()) + date.getDate();
                        final StringBuilder sb5 = new StringBuilder();
                        sb5.append("locadate=");
                        sb5.append(n);
                        sb5.append(",duedate=");
                        sb5.append(DataSaved.GetServerDueDate());
                        MyLog.d("MainActivity", sb5.toString());
                        MyLog.d("Enter::onClick()", "010");
                        if (DataSaved.GetServerDueDate() < 20891231) {
                            if (ControlThread.theOnly.serverTime == null || DataSaved.GetServerDueDate() == 20160428) {
                                Toast.makeText((Context)this, (CharSequence)"目前无法确定服务是否到期，请稍候再试！", 1).show();
                                return;
                            }
                            if (n >= DataSaved.GetServerDueDate() || this.OutOfDate()) {
                                Toast.makeText((Context)this, (CharSequence)"服务已经到期，请扫码支付续费后进行操作！", 1).show();
                                return;
                            }
                        }
                        this.startActivity(new Intent(this, ListActivity_new.class));
                        MyLog.d("Enter::onClick()", "012");
                        return;
                    }
                    Toast.makeText((Context)this, (CharSequence)"请输入正确的身高", 0).show();
                    return;
                }
                catch (Exception ex) {
                    Toast.makeText((Context)this, "请输入正确的参数", 1).show();
//                    Log.e("input_params" , ex.toString() + ".");
                    MyLog.d("Enter::onClick()", ex.toString() + ".");
                    return;
                }
            }
        }
        this.startActivity(new Intent(this, ListActivity_new.class));
        Toast.makeText((Context)this, (CharSequence)"机台控制连接失败，请重新开机再次尝试！", 1).show();
    }

    public int getVersionCode() throws Exception {
        return this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
    }

    @SuppressLint("ResourceType")
    public void onCreate(final Bundle bundle) {
        MyLog.d("MainActivity", "MainActivity::onCreate()");
        super.onCreate(bundle);
        CrashHandler.getInstance().init((Context)this);
        MainActivity.theOnly = this;

        try {
            final Window window = this.getWindow();
            final WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.systemUiVisibility = 1;
            attributes.systemUiVisibility |= 0x0;
            window.setFlags(1024, 1024);
            window.setFlags(128, 128);
            window.setAttributes(attributes);
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
            final int checkSelfPermission = ContextCompat.checkSelfPermission((Context)this, "android.permission.WRITE_EXTERNAL_STORAGE");
            final int checkSelfPermission2 = ContextCompat.checkSelfPermission((Context)this, "android.permission.ACCESS_FINE_LOCATION");
            final int checkSelfPermission3 = ContextCompat.checkSelfPermission((Context)this, "android.permission.READ_PHONE_STATE");
            if (checkSelfPermission != 0 || checkSelfPermission2 != 0 || checkSelfPermission3 != 0) {
                ActivityCompat.requestPermissions(this, MainActivity.PERMISSIONS_STORAGE, 1);
            }
            MyLog.init((Context)this);
            MyLog.d("Label_0955_Outer", "Enter onCreate");
            Local.settings = this.getSharedPreferences("cn.yuelin.o2cabin", 0);
            this.setContentView(R.layout.activity_main);
            this.findAndInitViews();


            MyApplication.controlThread = ControlThread.GetTheOnly(MyApplication.theOnly, null);
            if (!MyApplication.controlThread.isAlive()) {
                MyApplication.controlThread.start();
            }


        }
        catch (Exception ex) {
            MyLog.log(ex);
        }
    }

    public void onDestroy() {
        MainActivity.theOnly = null;
        super.onDestroy();
    }

    public void onLocationChanged(final Location location) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Latitude:");
        sb.append(location.getLatitude());
        sb.append(", Longitude:");
        sb.append(location.getLongitude());
        MyLog.d("Location", sb.toString());
        try {
            final long currentTimeMillis = System.currentTimeMillis();
            final int getDistance = Tools.GetDistance(location.getLatitude(), location.getLongitude(), DataSaved.GetPositionY(), DataSaved.GetPositionX());
            if (currentTimeMillis - DataSaved.GetPositionUploadTime() > 7200000L || getDistance > 10000) {
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("session", (Object)ControlThread.theOnly.httpSession);
                jsonObject.put("action", (Object)"position");
                jsonObject.put("time", currentTimeMillis);
                jsonObject.put("x", location.getLongitude());
                jsonObject.put("y", location.getLatitude());
                new HttpTask(this).execute((String[]) new String[] { jsonObject.toString() });
                DataSaved.SetPositionUploadTime(currentTimeMillis);
                DataSaved.SetPositionX((float)location.getLongitude());
                DataSaved.SetPositionY((float)location.getLatitude());
            }
        }
        catch (Exception ex) {
            MyLog.log(ex);
        }
    }

    public void onPause() {
        super.onPause();
        if (ControlThread.theOnly != null) {
            ControlThread.theOnly.paying = false;
        }
    }

    public void onProviderDisabled(final String s) {
        final StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append(" disabled");
        MyLog.d("Location", sb.toString());
    }

    public void onProviderEnabled(final String s) {
        final StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append(" enabled");
        MyLog.d("Location", sb.toString());
    }


    public void onResume() {
        if (ControlThread.theOnly.returnToMainActivity) {
            ControlThread.theOnly.returnToMainActivity = false;
            this.height.setText((CharSequence)"");
            this.weight.setText((CharSequence)"");
            this.age.setText((CharSequence)"");
        }
        this.time.setText((CharSequence)Tools.GetDisplayString(DataSaved.GetTotalRuntimeMinutes() / 6));
        super.onResume();

    }

    public void onStatusChanged(final String s, final int n, final Bundle bundle) {
        final StringBuilder sb = new StringBuilder();
        sb.append(s);
        sb.append(" status changed.");
        MyLog.d("Location", sb.toString());
    }
}
