package com.yuelin.o2cabin;

import android.app.*;
import android.widget.*;
import android.view.*;
import android.os.*;

public class ParameterActivity extends Activity
{
    ImageButton back;
    EditText text1;
    EditText text10;
    EditText text2;
    EditText text3;
    EditText text4;
    EditText text5;
    EditText text6;
    EditText text7;
    EditText text8;
    EditText text9;
    
    private void findAndInitViews() {
        try {
            this.text1 = (EditText)this.findViewById(R.id.input1);
            this.text2 = (EditText)this.findViewById(R.id.input2);
            this.text3 = (EditText)this.findViewById(R.id.input3);
            this.text4 = (EditText)this.findViewById(R.id.input4);
            this.text5 = (EditText)this.findViewById(R.id.input5);
            this.text6 = (EditText)this.findViewById(R.id.input6);
            this.text7 = (EditText)this.findViewById(R.id.input7);
            this.text8 = (EditText)this.findViewById(R.id.input8);
            this.text9 = (EditText)this.findViewById(R.id.input9);
            this.text10 = (EditText)this.findViewById(R.id.input10);
            ((ImageButton)this.findViewById(R.id.back)).setOnClickListener((View.OnClickListener)new View.OnClickListener() {
                public void onClick(final View view) {
                    ParameterActivity.this.saveData();
                    ParameterActivity.this.finish();
                }
            });
            this.loadData();
        }
        catch (Exception ex) {
            MyLog.log(ex);
        }
    }
    
    private void saveData() {
        DataSaved.Set_O2_Concentration_Modify_Value(Integer.parseInt(this.text1.getText().toString()));
        DataSaved.Set_Cabin_Pressure_Modify_Value(Integer.parseInt(this.text2.getText().toString()));
        DataSaved.Set_CPS_Sync_Pressure((int)(10.0 * Double.parseDouble(this.text3.getText().toString())));
        DataSaved.Set_Stop_O2_Pressure((int)(10.0 * Double.parseDouble(this.text4.getText().toString())));
        DataSaved.Set_Cabin_Pressure_MAX_Value((int)(10.0 * Double.parseDouble(this.text5.getText().toString())));
        DataSaved.Set_Cabin_Pressure_MIN_Value((int)(10.0 * Double.parseDouble(this.text6.getText().toString())));
        final int n = (int)(60.0 * Double.parseDouble(this.text7.getText().toString()));
        DataSaved.SetTotalRuntimeMinutes(n);
        if (MainActivity.theOnly != null) {
            MainActivity.theOnly.time.setText((CharSequence)Tools.GetDisplayString(n / 6));
        }
        DataSaved.Set_Single_Valve_Work_Time((int)(10.0 * Double.parseDouble(this.text8.getText().toString())));
        DataSaved.Set_Double_Valve_Work_Time((int)(10.0 * Double.parseDouble(this.text9.getText().toString())));
        DataSaved.Set_Disinfec_Time((int)(10.0 * Double.parseDouble(this.text10.getText().toString())));
    }
    
    private void setFullScreen() {
        this.findViewById(R.id.fullscreen).setSystemUiVisibility(4871);
    }
    
    public void loadData() {
        final EditText text1 = this.text1;
        final StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(DataSaved.Get_O2_Concentration_Modify_Value());
        text1.setText((CharSequence)sb.toString());
        final EditText text2 = this.text2;
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("");
        sb2.append(DataSaved.Get_Cabin_Pressure_Modify_Value());
        text2.setText((CharSequence)sb2.toString());
        this.text3.setText((CharSequence)Tools.GetDisplayString(DataSaved.Get_CPS_Sync_Pressure()));
        this.text4.setText((CharSequence)Tools.GetDisplayString(DataSaved.Get_Stop_O2_Pressure()));
        this.text5.setText((CharSequence)Tools.GetDisplayString(DataSaved.Get_Cabin_Pressure_MAX_Value()));
        this.text6.setText((CharSequence)Tools.GetDisplayString(DataSaved.Get_Cabin_Pressure_MIN_Value()));
        this.text7.setText((CharSequence)Tools.GetDisplayString(DataSaved.GetTotalRuntimeMinutes() / 6));
        this.text8.setText((CharSequence)Tools.GetDisplayString(DataSaved.Get_Single_Valve_Work_Time()));
        this.text9.setText((CharSequence)Tools.GetDisplayString(DataSaved.Get_Double_Valve_Work_Time()));
        this.text10.setText((CharSequence)Tools.GetDisplayString(DataSaved.Get_Disinfec_Time()));
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.getWindow().setFlags(1024, 1024);
        this.getWindow().setFlags(128, 128);
        this.setContentView(R.layout.activity_parameter);
        this.findAndInitViews();
    }
    
    public void onResume() {
        super.onResume();
    }
}
