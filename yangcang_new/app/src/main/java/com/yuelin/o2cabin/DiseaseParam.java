package com.yuelin.o2cabin;

import org.json.*;

public class DiseaseParam
{
    public int Stage1_PSI;
    public int Stage1_Time;
    public int Stage2_PSI;
    public int Stage2_Time;
    public int Stage3_PSI;
    public int Stage3_Time;
    public int Stage4_PSI;
    public int Stage4_Time;
    public int TotalTime;
    public float Value;
    
    public boolean PaserFromJsonObject(final JSONObject jsonObject) {
        try {
            this.Value = (float)jsonObject.getDouble("value");
            this.Stage1_PSI = jsonObject.getInt("psi1");
            this.Stage1_Time = jsonObject.getInt("time1");
            this.Stage2_PSI = jsonObject.getInt("psi2");
            this.Stage2_Time = jsonObject.getInt("time2");
            this.Stage3_PSI = jsonObject.getInt("psi3");
            this.Stage3_Time = jsonObject.getInt("time3");
            this.Stage4_PSI = jsonObject.getInt("psi4");
            this.Stage4_Time = jsonObject.getInt("time4");
            this.TotalTime = this.Stage1_Time + this.Stage2_Time + this.Stage3_Time + this.Stage4_Time;
            return true;
        }
        catch (Exception ex) {
            MyLog.log(ex);
            return false;
        }
    }
    
    public JSONObject ToJSONObject() {
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", (double)this.Value);
            jsonObject.put("psi1", this.Stage1_PSI);
            jsonObject.put("time1", this.Stage1_Time);
            jsonObject.put("psi2", this.Stage2_PSI);
            jsonObject.put("time2", this.Stage2_Time);
            jsonObject.put("psi3", this.Stage3_PSI);
            jsonObject.put("time3", this.Stage3_Time);
            jsonObject.put("psi4", this.Stage1_PSI);
            jsonObject.put("time4", this.Stage1_Time);
            return jsonObject;
        }
        catch (Exception ex) {
            MyLog.log(ex);
            return null;
        }
    }
}
