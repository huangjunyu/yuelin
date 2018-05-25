package com.yuelin.o2cabin;

import android.app.*;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.text.*;

import org.json.*;
//import cn.zxing.encoding.*;
import android.content.*;
import android.os.*;

import com.zxing.encoding.EncodingHandler;

public class ItemActivity extends Activity {
    public static ItemActivity theOnly;
    int HttpType;
    long StaticReturnTime;
    ImageButton back;
    EditText code;
    TextView disease;
    TextView duration;
    ImageButton enter;
    TextView fee;
    long lastLogoClickTime;
    long lastPayQueryTime;
    boolean paying;//这是一个标识,用于判断是否已经进行过扫码付款,true表示还未进行过付款
    ImageView qrcode;
    private final static Long tanSecond = 10000L;

    public ItemActivity() {
        this.paying = true;
        this.StaticReturnTime = -1L;
        this.lastLogoClickTime = 0L;
        this.lastPayQueryTime = 0L;
        this.HttpType = 0;
    }

    private void findAndInitViews() {
        try {
            final ImageButton enterImg = (ImageButton) this.findViewById(R.id.enter);
            if (enterImg != null) {
                enterImg.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
                    public void onClick(final View view) {
                        ItemActivity.this.paying = false;
                        MyLog.d("ItemActivity", "Test Mode Enter");
                        ControlThread.theOnly.theDisease = DataSaved.Diseases.get(ControlThread.theOnly.diseaseID);
                        ControlThread.theOnly.startTime = System.currentTimeMillis();
                        ControlThread.theOnly.addedMinutes = 0;
                        ControlThread.theOnly.lastSeconds = 0L;
                        ControlThread.theOnly.lastCureHeartTime = 0L;

                        final short n = DataOut.Register_Total_Time = (short) DataOut.GetCureMinutes(ControlThread.theOnly.sex, ControlThread.theOnly.age, ControlThread.theOnly.weight, ControlThread.theOnly.height, ControlThread.theOnly.theDisease.minutes);

                        ControlThread.theOnly.needRunMinutes = DataOut.Register_Total_Time;
                        final short n2 = (short) (n % 4);
                        final short n3 = DataOut.Register_Stage1_Time = (short) (n / 4);
                        DataOut.Register_Stage1_PSI = (short) DataSaved.Get_Stage1_PSI();
                        DataOut.Register_Stage2_Time = n3;
                        DataOut.Register_Stage2_PSI = (short) DataSaved.Get_Stage2_PSI();
                        DataOut.Register_Stage3_Time = n3;
                        DataOut.Register_Stage3_PSI = (short) DataSaved.Get_Stage3_PSI();
                        DataOut.Register_Stage4_Time = (short) (n2 + n3);
                        DataOut.Register_Stage4_PSI = (short) DataSaved.Get_Stage4_PSI();
                        final Disease theDisease = ControlThread.theOnly.theDisease;
                        DataOut.Register_Cure = 1;
                        DataOut.Register_Card_Pay = 0;
                        final StringBuilder sb = new StringBuilder();
                        sb.append("DataOut.Register_Cure = 1, DataOut.Register_Total_Time = ");
                        sb.append(n3);
                        MyLog.d("ItemActivity", sb.toString());
                        ItemActivity.this.StartCure(null);
                        try {
                            Thread.sleep(100L);
                        } catch (Exception ex) {
                        }
                    }
                });
            }
            ((ImageButton) this.findViewById(R.id.back)).setOnClickListener((View.OnClickListener) new View.OnClickListener() {
                public void onClick(final View view) {
                    if (DataOut.Register_Cure != 1) {
                        ItemActivity.this.finish();
                    }
                }
            });
            DataOut.Register_Card_Pay = 1;
            ControlThread.theOnly.theDisease = DataSaved.Diseases.get(ControlThread.theOnly.diseaseID);
            DataOut.Register_Card_Pay = 1;
            final short n = DataOut.Register_Total_Time = (short) DataOut.GetCureMinutes(ControlThread.theOnly.sex, ControlThread.theOnly.age, ControlThread.theOnly.weight, ControlThread.theOnly.height, ControlThread.theOnly.theDisease.minutes);
            final short n2 = (short) (n % 4);
            final short n3 = DataOut.Register_Stage1_Time = (short) (n / 4);
            DataOut.Register_Stage1_PSI = 10000;
            DataOut.Register_Stage2_Time = n3;
            DataOut.Register_Stage2_PSI = 10000;
            DataOut.Register_Stage3_Time = n3;
            DataOut.Register_Stage3_PSI = 10000;
            DataOut.Register_Stage4_Time = (short) (n3 + n2);
            DataOut.Register_Stage4_PSI = 10000;
            final Disease theDisease = ControlThread.theOnly.theDisease;
            DataOut.Register_Total_Pay = (short) DataSaved.GetDiseasePrice(theDisease.id, theDisease.price);
            final StringBuilder sb = new StringBuilder();
            sb.append("Price=");
            sb.append((short) DataSaved.GetDiseasePrice(theDisease.id, theDisease.price));
            MyLog.d("ItemActivity", sb.toString());
            if (ControlThread.theOnly != null) {
                ControlThread.theOnly.needRunMinutes = DataOut.Register_Total_Time;
            }
            this.disease = (TextView) this.findViewById(R.id.title);
            if (this.disease != null) {
                this.disease.setText((CharSequence) ControlThread.theOnly.diseaseName);
            }
            this.duration = (TextView) this.findViewById(R.id.time);
            if (this.duration != null) {
                final TextView duration = this.duration;
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("");
                sb2.append(DataOut.Register_Total_Time);
                duration.setText((CharSequence) sb2.toString());
            }
            this.duration = (TextView) this.findViewById(R.id.time);
            this.fee = (TextView) this.findViewById(R.id.fee);
            if (this.fee != null) {
                this.fee.setText((CharSequence) Tools.GetDisplayString(DataSaved.GetDiseasePrice(theDisease.id, theDisease.price)));
                this.fee.setOnEditorActionListener((TextView.OnEditorActionListener) new TextView.OnEditorActionListener() {
                    public boolean onEditorAction(final TextView textView, final int n, final KeyEvent keyEvent) {
                        if (n == 6) {
                            try {
                                final double double1 = Double.parseDouble(ItemActivity.this.fee.getText().toString());
                                ItemActivity.this.fee.setEnabled(false);
                                final int id = theDisease.id;
                                final int n2 = (int) (10.0 * double1);
                                DataSaved.SetDiseasePrice(id, n2);
                                DataOut.Register_Total_Pay = (short) DataSaved.GetDiseasePrice(theDisease.id, theDisease.price);
                                final StringBuilder sb = new StringBuilder();
                                sb.append("Price=");
                                sb.append((short) DataSaved.GetDiseasePrice(theDisease.id, theDisease.price));
                                MyLog.d("ItemActivity", sb.toString());
                                ItemActivity.this.order();
                                if ((int) (100.0 * double1) != n2 * 10) {
                                    Toast.makeText((Context) ItemActivity.this, (CharSequence) "价格设置错误,保留1位小数,请重新设置", 1).show();
                                    return true;
                                }
                                if (double1 < 0.1) {
                                    Toast.makeText((Context) ItemActivity.this, (CharSequence) "价格设置错误,不能小于0.1,请重新设置", 1).show();
                                    return true;
                                }
                                if (double1 > 10000.0) {
                                    Toast.makeText((Context) ItemActivity.this, (CharSequence) "价格设置错误,不能超过1万元,请重新设置", 1).show();
                                    return true;
                                }
                            } catch (Exception ex) {
                                MyLog.log(ex);
                            }
                        }
                        return true;
                    }
                });
            }
            this.disease.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
                public void onClick(final View view) {
                    final long currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis - ItemActivity.this.lastLogoClickTime > 600L) {
                        ItemActivity.this.lastLogoClickTime = currentTimeMillis;
                        return;
                    }
                    ItemActivity.this.lastLogoClickTime = 0L;
                    ItemActivity.this.fee.setEnabled(true);
                    final CharSequence text = ItemActivity.this.fee.getText();
                    if (text instanceof Spannable) {
                        Selection.setSelection((Spannable) text, text.length());
                    }
                }
            });
            this.qrcode = (ImageView) this.findViewById(R.id.qrcode);
            this.order();
        } catch (Exception ex) {
            MyLog.log(ex);
        }
    }

    private void order() {
        try {
            Disease theDisease = DataSaved.Diseases.get(ControlThread.theOnly.diseaseID);
            if (theDisease == null) {
                theDisease = ControlThread.theOnly.theDisease;
            }
            ControlThread.theOnly.stopedByUser = false;
            DataOut.Register_Total_Pay = (short) DataSaved.GetDiseasePrice(theDisease.id, theDisease.price);
            final StringBuilder sb = new StringBuilder();
            sb.append("Price=");
            sb.append((short) DataSaved.GetDiseasePrice(theDisease.id, theDisease.price));
            MyLog.d("ItemActivity", sb.toString());
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("session", (Object) ControlThread.theOnly.httpSession);
            jsonObject.put("action", (Object) "order");
            jsonObject.put("time", System.currentTimeMillis());
            jsonObject.put("sex", ControlThread.theOnly.sex);
            jsonObject.put("age", ControlThread.theOnly.age);
            jsonObject.put("weight", ControlThread.theOnly.weight);
            jsonObject.put("height", ControlThread.theOnly.height);
            jsonObject.put("cureminutes", (int) DataOut.Register_Total_Time);
            jsonObject.put("minutes", theDisease.minutes);
            jsonObject.put("paramdate", DataSaved.GetDiseaseUpdateDate());
            jsonObject.put("diseaseid", ControlThread.theOnly.diseaseID);
            jsonObject.put("price", DataSaved.GetDiseasePrice(ControlThread.theOnly.diseaseID, theDisease.price));
            this.HttpType = 0;
            new HttpTask(this).execute((String[]) new String[]{jsonObject.toString()});
        } catch (Exception ex) {
            MyLog.log(ex);
        }
    }

    private void setFullScreen() {
        this.findViewById(R.id.fullscreen).setSystemUiVisibility(4871);
    }

    public void OnHttpReturn(final String s) {
        if (s != null) {
            try {
                if (s.length() > 0) {
                    final JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject != null && jsonObject.has("code") && jsonObject.getInt("code") == 0 && this.HttpType == 0) {
                        ControlThread.theOnly.cureID = jsonObject.getInt("cureid");
                        ControlThread.theOnly.payUrl1 = jsonObject.getString("payurl1");
                        ControlThread.theOnly.payUrl2 = jsonObject.getString("payurl2");
                        if ("".equals(ControlThread.theOnly.payUrl1)) {
                            this.qrcode.setVisibility(8);
                            return;
                        }
                        final Bitmap qrCode = EncodingHandler.createQRCode(ControlThread.theOnly.payUrl1, 400);
                        if (qrCode != null) {
                            this.qrcode.setImageBitmap(qrCode);
                        }
                    }
                }
            } catch (Exception ex) {
                MyLog.log(ex);
            }
        }
    }

    public void OnTimer_PayQuery(final long currentTimeMillis) {
        if (ControlThread.theOnly.serialController != null && !ControlThread.theOnly.serialController.IsConnected()) {
            ControlThread.theOnly.returnToMainActivity = true;
            this.finish();
            return;
        }
        if (currentTimeMillis - this.lastPayQueryTime > tanSecond) {
            this.lastPayQueryTime = currentTimeMillis;
            if (DataOut.Register_Cure == 0 && this.paying) {
                try {
                    final JSONObject jsonObject = new JSONObject();
                    jsonObject.put("session", (Object) ControlThread.theOnly.httpSession);
                    jsonObject.put("action", (Object) "curepayquery");
                    jsonObject.put("cureid", ControlThread.theOnly.cureID);
                    jsonObject.put("time", currentTimeMillis);
                    final String httpPost = Tools.HttpPost(ControlThread.theOnly.pathBase, jsonObject.toString());
                    boolean b = false;
                    if (httpPost != null) {
                        final int length = httpPost.length();
                        b = false;
                        if (length > 0) {
                            final JSONObject jsonObject2 = new JSONObject(httpPost);
                            b = false;
                            if (jsonObject2 != null) {
                                final boolean has = jsonObject2.has("code");
                                b = false;
                                if (has) {
                                    final int int1 = jsonObject2.getInt("code");
                                    b = false;
                                    if (int1 == 0) {
                                        if (jsonObject2.getInt("payresult") == 0) {
                                            final String string = jsonObject2.getString("paytype");
                                            if (string != null && string.length() != 0) {
                                                ControlThread.theOnly.payType = string;
                                                this.paying = false;
                                                MyLog.d("ItemActivity", "Wechat Pay OK");
                                                Log.e("jsono2", jsonObject2.toString() + ".");
                                                ControlThread.theOnly.startTime = System.currentTimeMillis();
                                                ControlThread.theOnly.addedMinutes = 0;
                                                ControlThread.theOnly.lastSeconds = 0L;
                                                ControlThread.theOnly.lastCureHeartTime = 0L;
                                                ControlThread.theOnly.needRunMinutes = DataOut.Register_Total_Time;
                                                DataOut.Register_Cure = 1;
                                                DataOut.Register_Card_Pay = 0;
                                                this.StartCure(null);//如果已经付费，则进入治疗界面
                                            }
                                        }
                                        try {
                                            Thread.sleep(100L);
                                        } catch (Exception ex2) {
                                        }
                                        b = true;
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    MyLog.log(ex);
                }
            }
        }
    }

    public void StartCure(final DataIn dataIn) {
        try {
            ControlThread.theOnly.paying = false;
            if (dataIn == null) {
                ControlThread.theOnly.maxDataIn.Register_Kpa = 0;
                ControlThread.theOnly.maxDataIn.Register_O2_Concentration = 0;
                ControlThread.theOnly.maxDataIn.Register_Cabin_O2_Concentration = 0;
                ControlThread.theOnly.maxDataIn.Register_PM25 = 0;
            } else {
                ControlThread.theOnly.maxDataIn.Register_Kpa = 0;
                ControlThread.theOnly.maxDataIn.Register_O2_Concentration = 0;
                ControlThread.theOnly.maxDataIn.Register_Cabin_O2_Concentration = 0;
                ControlThread.theOnly.maxDataIn.Register_PM25 = 0;
                ControlThread.theOnly.maxDataIn.Register_CardID = dataIn.Register_CardID;
                ControlThread.theOnly.maxDataIn.Register_Card_Balance = dataIn.Register_Card_Balance;
            }
            ControlThread.theOnly.O2Count = 0;
            ControlThread.theOnly.O2Value = 0;
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("session", (Object) ControlThread.theOnly.httpSession);
            jsonObject.put("action", (Object) "curebegin");
            jsonObject.put("cureid", ControlThread.theOnly.cureID);
            jsonObject.put("paytype", (Object) ControlThread.theOnly.payType);
            jsonObject.put("cardid", ControlThread.theOnly.maxDataIn.Register_CardID);
            jsonObject.put("cardbalance", ControlThread.theOnly.maxDataIn.Register_Card_Balance);
            jsonObject.put("time", System.currentTimeMillis());
            this.HttpType = 1;
            new HttpTask(this).execute((String[]) new String[]{jsonObject.toString()});
        } catch (Exception ex) {
            MyLog.log(ex);
        }
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    MyLog.d("进入Status", "ItemActivity StartCure()");
                    ItemActivity.this.startActivity(new Intent(ItemActivity.this, StatusActivity.class));
                } catch (Exception ex) {
                    MyLog.log(ex);
                }
            }
        });
    }

    public void StopCure() {
    }

    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.getWindow().setFlags(1024, 1024);
        this.getWindow().setFlags(128, 128);
        this.setContentView(R.layout.activity_item);
        this.findAndInitViews();
    }

    public void onDestroy() {
        super.onDestroy();
        ItemActivity.theOnly = null;
    }

    public void onPause() {
        super.onPause();
        DataOut.Register_Card_Pay = 0;
    }

    public void onResume() {
        super.onResume();
        if (ControlThread.theOnly.returnToMainActivity) {
            ItemActivity.theOnly = null;
            this.finish();
            return;
        }
        ItemActivity.theOnly = this;
        this.StaticReturnTime = System.currentTimeMillis();
    }
}
