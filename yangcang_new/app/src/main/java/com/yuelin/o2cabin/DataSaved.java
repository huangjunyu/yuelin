package com.yuelin.o2cabin;

import android.util.Log;
import android.widget.Toast;

import org.json.*;

import java.util.*;

public class DataSaved {
    public static HashMap<Integer, Disease> Diseases;
    public static JSONArray OfflineData;

    static {
        DataSaved.Diseases = new HashMap<Integer, Disease>();
    }
    public static boolean AddOrReplaceDiseases(final JSONArray jsonArray) {
        DataSaved.Diseases = new HashMap<Integer, Disease>();
        if (jsonArray == null) {
            return false;
        } else {
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    Disease disease = new Disease();
                    disease.PaserFromJsonObject(jsonArray.getJSONObject(i));
                    DataSaved.Diseases.put(disease.id, disease);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public static int GetDiseasePrice(final int n, final int n2) {
        try {
            final StringBuilder sb = new StringBuilder();
            sb.append("DiseasePrice");
            sb.append(n);
            return Local.GetIntgerValue(sb.toString(), n2);
        } catch (Exception ex) {
            MyLog.log(ex);
            return n2;
        }
    }

    public static int GetDiseaseUpdateDate() {
        return Local.GetIntgerValue("DiseaseUpdateDate", 20160901);
    }

    public static String GetFactoryID() {
        final StringBuilder sb = new StringBuilder();
        sb.append(GetFactoryID1());
        sb.append("-");
        sb.append(GetFactoryID2());
        sb.append("-");
        sb.append(GetFactoryID3());
        sb.append("-");
        sb.append(GetFactoryID4());
        return sb.toString();
    }

    public static String GetFactoryID1() {
        return Local.GetStringValue("FactoryID1", "YL01");
    }

    public static String GetFactoryID2() {
        return Local.GetStringValue("FactoryID2", "HYK");
    }

    public static String GetFactoryID3() {
        return Local.GetStringValue("FactoryID3", "00000000");
    }

    public static String GetFactoryID4() {
        return Local.GetStringValue("FactoryID4", "0000");
    }

    public static long GetPositionUploadTime() {
        return Local.GetLongValue("PositionUploadTime");
    }

    public static float GetPositionX() {
        return Local.GetFloatValue("PositionX", 0.0f);
    }

    public static float GetPositionY() {
        return Local.GetFloatValue("PositionY", 0.0f);
    }

    public static int GetServerDueDate() {
        return Local.GetIntgerValue("ServerDueDate", 20161001);
    }

    public static int GetServerLock() {
        return Local.GetIntgerValue("ServerLock", 0);
    }

    public static int GetServersListUpdateDate() {
        return Local.GetIntgerValue("ServersUpdateDate", 20170901);
    }

    public static int GetTotalRuntimeMinutes() {
        return Local.GetIntgerValue("TotalRuntimeMinutes", 0);
    }

    public static int Get_CPS_Sync_Pressure() {
        return Local.GetIntgerValue("CPS_Sync_Pressure", 10);
    }

    public static int Get_Cabin_Pressure_MAX_Value() {
        return Local.GetIntgerValue("Cabin_Pressure_MAX_Value", 45);
    }

    public static int Get_Cabin_Pressure_MIN_Value() {
        return Local.GetIntgerValue("Cabin_Pressure_MIN_Value", 30);
    }

    public static int Get_Cabin_Pressure_Modify_Value() {
        return Local.GetIntgerValue("Cabin_Pressure_Modify_Value", 100);
    }

    public static int Get_Cabin_Temp_Modify_Value() {
        return Local.GetIntgerValue("Cabin_Temp_Modify_Value", 100);
    }

    public static int Get_Cps_Sync_Time() {
        return Local.GetIntgerValue("Cps_Sync_Time", 50);
    }

    public static int Get_Disinfec_Time() {
        return Local.GetIntgerValue("Disinfec_Time", 50);
    }

    public static int Get_Double_Valve_Work_Time() {
        return Local.GetIntgerValue("Double_Valve_Work_Time", 4);
    }

    public static int Get_O2_Concentration_Modify_Value() {
        return Local.GetIntgerValue("O2_Concentration_Modify_Value", 100);
    }

    public static int Get_O2_MAX_Value() {
        return Local.GetIntgerValue("O2_MAX_Value", 40);
    }

    public static int Get_O2_MIN_Value() {
        return Local.GetIntgerValue("O2_MIN_Value", 22);
    }

    public static int Get_O2_Modify_Value() {
        return Local.GetIntgerValue("O2_Modify_Value", 100);
    }

    public static int Get_Single_Valve_Work_Time() {
        return Local.GetIntgerValue("Single_Valve_Work_Time", 40);
    }

    public static int Get_Stage1_PSI() {
        return Local.GetIntgerValue("Stage1_PSI", 15);
    }

    public static int Get_Stage1_Time() {
        return Local.GetIntgerValue("Stage1_Time", 10);
    }

    public static int Get_Stage2_PSI() {
        return Local.GetIntgerValue("Stage2_PSI", 20);
    }

    public static int Get_Stage2_Time() {
        return Local.GetIntgerValue("Stage2_Time", 10);
    }

    public static int Get_Stage3_PSI() {
        return Local.GetIntgerValue("Stage3_PSI", 25);
    }

    public static int Get_Stage3_Time() {
        return Local.GetIntgerValue("Stage3_Time", 10);
    }

    public static int Get_Stage4_PSI() {
        return Local.GetIntgerValue("Stage4_PSI", 33);
    }

    public static int Get_Stage4_Time() {
        return Local.GetIntgerValue("Stage4_Time", 30);
    }

    public static int Get_Stop_O2_Pressure() {
        return Local.GetIntgerValue("Stop_O2_Pressure", 9);
    }

    public static int Get_Unit_Price() {
        return Local.GetIntgerValue("Unit_Price", 250);
    }

    public static boolean InitDiseases() {
        try {
            final String getStringValue = Local.GetStringValue("Diseases");
            final StringBuilder sb = new StringBuilder();
            sb.append("SavedJsonString:");
            sb.append(getStringValue);
            MyLog.d("InitDiseases", sb.toString());
            if (getStringValue == null) {
                return true;
            }
            if (getStringValue.length() == 0) {
                return true;
            }
            final JSONArray jsonArray = new JSONArray(getStringValue);
            AddOrReplaceDiseases(jsonArray);
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("jsonArray = ");
            sb2.append(jsonArray.length());
            MyLog.d("InitDiseases", sb2.toString());
            return true;
        } catch (Exception ex) {
            MyLog.log(ex);
            return false;
        }
    }

    public static boolean LoadOfflineData() {
        try {
            final String getStringValue = Local.GetStringValue("OfflineData");
            if (getStringValue == null || getStringValue.length() == 0) {
                DataSaved.OfflineData = new JSONArray();
                MyLog.d("LoadOfflineData", "data is null ");
                return true;
            }
            final JSONArray offlineData = new JSONArray(getStringValue);
            if (offlineData != null) {
                DataSaved.OfflineData = offlineData;
                final StringBuilder sb = new StringBuilder();
                sb.append("jsonArray = ");
                sb.append(offlineData.length());
                MyLog.d("LoadOfflineData", sb.toString());
                return true;
            }
            DataSaved.OfflineData = new JSONArray();
            MyLog.d("LoadOfflineData", "jsonArray is null ");
            return true;
        } catch (Exception ex) {
            MyLog.log(ex);
            return false;
        }
    }

    public static void SetDiseasePrice(final int n, final int n2) {
        try {
            final StringBuilder sb = new StringBuilder();
            sb.append("DiseasePrice");
            sb.append(n);
            Local.SetIntgerValue(sb.toString(), n2);
        } catch (Exception ex) {
            MyLog.log(ex);
        }
    }

    public static void SetFactoryID1(final String s) {
        Local.SetStringValue("FactoryID1", s);
    }

    public static void SetFactoryID2(final String s) {
        Local.SetStringValue("FactoryID2", s);
    }

    public static void SetFactoryID3(final String s) {
        Local.SetStringValue("FactoryID3", s);
    }

    public static void SetFactoryID4(final String s) {
        Local.SetStringValue("FactoryID4", s);
    }

    public static void SetPositionUploadTime(final long n) {
        Local.SetLongValue("PositionUploadTime", n);
    }

    public static void SetPositionX(final float n) {
        Local.SetFloatValue("PositionX", n);
    }

    public static void SetPositionY(final float n) {
        Local.SetFloatValue("PositionY", n);
    }

    public static void SetServerDueDate(final int n) {
        Local.SetIntgerValue("ServerDueDate", n);
    }

    public static void SetServerLock(final int n) {
        Local.SetIntgerValue("ServerLock", n);
    }

    public static void SetTotalRuntimeMinutes(final int n) {
        Local.SetIntgerValue("TotalRuntimeMinutes", n);
    }

    public static void Set_CPS_Sync_Pressure(final int n) {
        Local.SetIntgerValue("CPS_Sync_Pressure", n);
    }

    public static void Set_Cabin_Pressure_MAX_Value(final int n) {
        Local.SetIntgerValue("Cabin_Pressure_MAX_Value", n);
    }

    public static void Set_Cabin_Pressure_MIN_Value(final int n) {
        Local.SetIntgerValue("Cabin_Pressure_MIN_Value", n);
    }

    public static void Set_Cabin_Pressure_Modify_Value(final int n) {
        Local.SetIntgerValue("Cabin_Pressure_Modify_Value", n);
    }

    public static void Set_Cabin_Temp_Modify_Value(final int n) {
        Local.SetIntgerValue("Cabin_Temp_Modify_Value", n);
    }

    public static void Set_Cps_Sync_Time(final int n) {
        Local.SetIntgerValue("Cps_Sync_Time", n);
    }

    public static void Set_Disinfec_Time(final int n) {
        Local.SetIntgerValue("Disinfec_Time", n);
    }

    public static void Set_Double_Valve_Work_Time(final int n) {
        Local.SetIntgerValue("Double_Valve_Work_Time", n);
    }

    public static void Set_O2_Concentration_Modify_Value(final int n) {
        Local.SetIntgerValue("O2_Concentration_Modify_Value", n);
    }

    public static void Set_O2_MAX_Value(final int n) {
        Local.SetIntgerValue("O2_MAX_Value", n);
    }

    public static void Set_O2_MIN_Value(final int n) {
        Local.SetIntgerValue("O2_MIN_Value", n);
    }

    public static void Set_O2_Modify_Value(final int n) {
        Local.SetIntgerValue("O2_Modify_Value", n);
    }

    public static void Set_Single_Valve_Work_Time(final int n) {
        Local.SetIntgerValue("Single_Valve_Work_Time", n);
    }

    public static void Set_Stage1_PSI(final int n) {
        Local.SetIntgerValue("Stage1_PSI", n);
    }

    public static void Set_Stage1_Time(final int n) {
        Local.SetIntgerValue("Stage1_Time", n);
    }

    public static void Set_Stage2_PSI(final int n) {
        Local.SetIntgerValue("Stage2_PSI", n);
    }

    public static void Set_Stage2_Time(final int n) {
        Local.SetIntgerValue("Stage2_Time", n);
    }

    public static void Set_Stage3_PSI(final int n) {
        Local.SetIntgerValue("Stage3_PSI", n);
    }

    public static void Set_Stage3_Time(final int n) {
        Local.SetIntgerValue("Stage3_Time", n);
    }

    public static void Set_Stage4_PSI(final int n) {
        Local.SetIntgerValue("Stage4_PSI", n);
    }

    public static void Set_Stage4_Time(final int n) {
        Local.SetIntgerValue("Stage4_Time", n);
    }

    public static void Set_Stop_O2_Pressure(final int n) {
        Local.SetIntgerValue("Stop_O2_Pressure", n);
    }

    public static void Set_Unit_Price(final int n) {
        Local.SetIntgerValue("Unit_Price", n);
    }
}