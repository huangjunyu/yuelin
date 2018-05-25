package com.yuelin.o2cabin;

import android.app.*;
import android.content.pm.PackageManager;
import android.os.*;
import android.view.*;
import android.content.*;
import android.widget.*;
import android.text.*;

public class PasswordActivity extends Activity
{
    private TextView versionNameTv;
    private void setFullScreen() {
        this.findViewById(R.id.fullscreen).setSystemUiVisibility(4871);
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.getWindow().setFlags(1024, 1024);
        this.getWindow().setFlags(128, 128);
        this.setContentView(R.layout.activity_password);
        versionNameTv = (TextView)findViewById(R.id.activity_pwd_version_name);
        try {
            versionNameTv.setText("版本号：" + PasswordActivity.this.getPackageManager().getPackageInfo(PasswordActivity.this.getPackageName(),0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        try {
            ((ImageButton)this.findViewById(R.id.enter)).setOnClickListener((View.OnClickListener)new View.OnClickListener() {
                public void onClick(final View view) {
                    PasswordActivity.this.startActivity(new Intent((Context)PasswordActivity.this, (Class)ParameterActivity.class));
                }
            });
            ((EditText)this.findViewById(R.id.password)).addTextChangedListener((TextWatcher)new TextWatcher() {
                public void afterTextChanged(final Editable editable) {
                }
                
                public void beforeTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
                }
                
                public void onTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("s=");
                    sb.append((Object)charSequence);
                    MyLog.d("onTextChanged", sb.toString());
                    if (DataOut.GetPassword().equals(charSequence.toString())) {
                        PasswordActivity.this.startActivity(new Intent((Context)PasswordActivity.this, (Class)ParameterActivity.class));
                    }
                }
            });
            ((ImageButton)this.findViewById(R.id.back)).setOnClickListener((View.OnClickListener)new View.OnClickListener() {
                public void onClick(final View view) {
                    PasswordActivity.this.finish();
                }
            });
        }
        catch (Exception ex) {
            MyLog.log(ex);
        }
    }
    
    public void onResume() {
        super.onResume();
    }
}
