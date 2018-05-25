package com.yuelin.o2cabin;

import android.app.*;
import android.util.Log;
import android.widget.*;
//import cn.zxing.encoding.*;
import android.view.*;
import android.content.*;
import android.os.*;

public class StatusActivity extends Activity
{
    static final int AverageCount = 10;
    private static final String TAG = "StatusActivity";
    public static StatusActivity theOnly;
    int[] Kpa;
    int Kpa_average;
    int[] O2Con;
    int O2Con_average;
    int[] PM25;
    int PM25_average;
    ImageButton back;
    int count;
    private long lastUpdateTick;
//    TextView leftTime;
    TextView name;
    ImageView pressure;
    ImageView qrcode;
    ImageView quality;
    ImageView thickness;
    TextView totalTime;
    ImageView saftImg;
    public StatusActivity() {
        this.lastUpdateTick = -1L;
        this.count = 0;
        this.Kpa = new int[10];
        this.PM25 = new int[10];
        this.O2Con = new int[10];
    }
    
    private void findAndInitViews() {
        try {
            this.name = (TextView)this.findViewById(R.id.name);
            if (this.name != null) {
                this.name.setText((CharSequence)ControlThread.theOnly.diseaseName);
            }
            this.totalTime = (TextView)this.findViewById(R.id.totalTime);
//            this.leftTime = (TextView)this.findViewById(R.id.leftTime);
            this.quality = (ImageView)this.findViewById(R.id.quality);
            this.thickness = (ImageView)this.findViewById(R.id.thickness);
            this.pressure = (ImageView)this.findViewById(R.id.pressure);
            this.qrcode = (ImageView)this.findViewById(R.id.qrcode);
            saftImg = (ImageView)this.findViewById(R.id.activity_status_saft_img);
            if (this.qrcode != null) {
                final StringBuilder sb = new StringBuilder();
                sb.append(ControlThread.theOnly.deviceid);
                sb.append(",");
                sb.append(ControlThread.theOnly.httpSession);
                sb.append(",");
                sb.append(ControlThread.theOnly.cureID);
                sb.append(",");
                sb.append(ControlThread.theOnly.pathBase);
//                this.qrcode.setImageBitmap(EncodingHandler.createQRCode(sb.toString(), 400));
            }
            this.back = (ImageButton)this.findViewById(R.id.back);
            if (this.back != null) {
                this.back.setOnClickListener((View.OnClickListener)new View.OnClickListener() {
                    public void onClick(final View view) {
                        if (DataOut.Register_Cure == 0) {
                            ControlThread.theOnly.returnToMainActivity = true;
                        }
                        StatusActivity.this.finish();
                    }
                });
            }
            final TextView totalTime = this.totalTime;
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("");
            sb2.append(ControlThread.theOnly.needRunMinutes);
            totalTime.setText((CharSequence)sb2.toString());
//            final TextView leftTime = this.leftTime;
            final StringBuilder sb3 = new StringBuilder();
            sb3.append("");
            sb3.append(ControlThread.theOnly.needRunMinutes);
//            leftTime.setText((CharSequence)sb3.toString());
            if (ControlThread.theOnly != null && ControlThread.theOnly.lastDataIn != null) {
                this.OnData(ControlThread.theOnly.lastDataIn);
            }
        }
        catch (Exception ex) {
            MyLog.log(ex);
        }
    }
    
    private void setFullScreen() {
        this.findViewById(R.id.fullscreen).setSystemUiVisibility(4871);
    }
    
    public void OnData(final DataIn dataIn) {
        final int[] kpa = this.Kpa;
        final int count = this.count;
        int count2 = 10;
        kpa[count % count2] = dataIn.Register_Kpa;
        this.PM25[this.count % count2] = dataIn.Register_PM25;
        this.O2Con[this.count % count2] = dataIn.Register_O2_Concentration;
        ++this.count;
        if (this.count <= count2) {
            count2 = this.count;
        }
        int i = 0;
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        while (i < count2) {//多次取值，求平均值
            MyLog.d("Kpa[i]i",this.Kpa + ".");
            n += this.Kpa[i];
            n2 += this.PM25[i];
            n3 += this.O2Con[i];
            ++i;
        }
        this.Kpa_average = n / count2;
        this.PM25_average = n2 / count2;
        this.O2Con_average = n3 / count2;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    StatusActivity.this.SetPressure((short)StatusActivity.this.Kpa_average);
                    StatusActivity.this.SetQuality((short)StatusActivity.this.PM25_average);
                    StatusActivity.this.SetThickness((short)StatusActivity.this.O2Con_average);
                }
                catch (Exception ex) {
                    MyLog.log(ex);
                }
            }
        });
    }
    
    public void OnTimer(final long n) {
        try {
            this.runOnUiThread((Runnable)new Runnable() {
                @Override
                public void run() {
                    try {
                        if (n - StatusActivity.this.lastUpdateTick > 1000L) {
                            StatusActivity.this.lastUpdateTick = n;
                            if (DataOut.Register_Cure == 1) {
                                final StringBuilder sb = new StringBuilder();
                                sb.append("OnTimer(): needRunMinutes = ");
                                sb.append(ControlThread.theOnly.needRunMinutes);
                                sb.append(", addedMinutes = ");
                                sb.append(ControlThread.theOnly.addedMinutes);
                                sb.append(" ,");
                                sb.append(ControlThread.theOnly.needRunMinutes);
                                MyLog.d("StatusActivity", sb.toString());
//                                final TextView leftTime = StatusActivity.this.leftTime;
                                final StringBuilder sb2 = new StringBuilder();
                                sb2.append("");
                                sb2.append(ControlThread.theOnly.needRunMinutes - ControlThread.theOnly.addedMinutes);
                                totalTime.setText((CharSequence)sb2.toString());
                                final TextView totalTime = StatusActivity.this.totalTime;
                                final StringBuilder sb3 = new StringBuilder();
                                sb3.append("");
                                sb3.append(ControlThread.theOnly.needRunMinutes);
//                                totalTime.setText((CharSequence)sb3.toString());
                                return;
                            }
                            MyLog.d("StatusActivity", "End Cure。Finish");
                            StatusActivity.this.startActivity(new Intent((Context)StatusActivity.this, (Class)MainActivity.class));
                        }
                    }
                    catch (Exception ex) {
                        MyLog.log(ex);
                    }
                }
            });
        }
        catch (Exception ex) {
            MyLog.log(ex);
        }
    }
    
    public void SetPressure(final short n) {
        final int[] array = { R.mipmap.pressure0, R.mipmap.pressure1, R.mipmap.pressure2, R.mipmap.pressure3, R.mipmap.pressure4, R.mipmap.pressure5, R.mipmap.pressure6, R.mipmap.pressure7, R.mipmap.pressure8, R.mipmap.pressure9, R.mipmap.pressure10 };
        double n2 = 2.0 * (n / 6.9 / 9.0);
        if (n2 < 0.0) {
            n2 = 0.0;
        }
        else if (n2 > 10.0) {
            n2 = 10.0;
        }
        MyLog.d("setpresssu",n2 + ":" + n + ":" + (int)Math.ceil(n2));
        double saftLevel = n2/2;
        if (0 <= saftLevel && saftLevel < 3.8){
            saftImg.setImageResource(R.mipmap.saft_02d);
        }else if(3.8 <= saftLevel && saftLevel < 4.2){
            saftImg.setImageResource(R.mipmap.satf_02c);
        }else if(4.2 <= saftLevel && 4.9 < saftLevel){
            saftImg.setImageResource(R.mipmap.saft_02b);
        }else{
            saftImg.setImageResource(R.mipmap.saft_02a);
        }
        Log.e("m2222",n2 + ".");
        if (n2 >= 6.2 && n2 <= 7.4){
            n2 = 7.0;
        }
        this.pressure.setImageResource(array[(int)Math.ceil(n2)]);
    }
    
    public void SetQuality(final short n) {
        MyLog.d("quality_test",n + ".");
//        Toast.makeText(StatusActivity.this,n + ".",Toast.LENGTH_LONG).show();
        if (n >= 0 && n < 50) {
            this.quality.setImageResource(R.mipmap.quality0);
            return;
        }
        if (n >= 50 && n < 100 ) {
            this.quality.setImageResource(R.mipmap.quality1);
            return;
        }
        if (n >= 100 && n < 150) {
            this.quality.setImageResource(R.mipmap.quality2);
            return;
        }else this.quality.setImageResource(R.mipmap.quality3);
    }
    
    public void SetThickness(final short n) {
        if (n < 100) {
            this.thickness.setImageResource(R.mipmap.thickness0);
            return;
        }
        if (n < 300) {
            this.thickness.setImageResource(R.mipmap.thickness1);
            return;
        }
        if (n < 500) {
            this.thickness.setImageResource(R.mipmap.thickness2);
            return;
        }
        if (n < 700) {
            this.thickness.setImageResource(R.mipmap.thickness3);
            return;
        }
        if (n < 900) {
            this.thickness.setImageResource(R.mipmap.thickness4);
            return;
        }
        this.thickness.setImageResource(R.mipmap.thickness5);
    }
    
    public void onCreate(final Bundle bundle) {
        StatusActivity.theOnly = this;
        this.getWindow().setFlags(1024, 1024);
        this.getWindow().setFlags(128, 128);
        super.onCreate(bundle);
        this.setContentView(R.layout.activity_status);
        this.findAndInitViews();
    }

    public void onDestroy() {
        StatusActivity.theOnly = null;
        super.onDestroy();
    }

    public void onResume() {
        super.onResume();
    }
}
