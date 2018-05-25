package com.yuelin.o2cabin;

public class DataOut
{
    public static final Object Lock;
    public static short Register_Card_Pay;
    public static short Register_Cure;
    public static short Register_Disinfect;
    public static short Register_Stage1_PSI;
    public static short Register_Stage1_Time;
    public static short Register_Stage2_PSI;
    public static short Register_Stage2_Time;
    public static short Register_Stage3_PSI;
    public static short Register_Stage3_Time;
    public static short Register_Stage4_PSI;
    public static short Register_Stage4_Time;
    public static short Register_Total_Pay;
    public static short Register_Total_Time;
    public static short Register_Unit_Price;

    static {
        Lock = new Object();
        DataOut.Register_Disinfect = 0;
        DataOut.Register_Card_Pay = 0;
        DataOut.Register_Cure = 0;
        DataOut.Register_Stage1_Time = 0;
        DataOut.Register_Stage1_PSI = 0;
        DataOut.Register_Stage2_Time = 0;
        DataOut.Register_Stage2_PSI = 0;
        DataOut.Register_Stage3_Time = 0;
        DataOut.Register_Stage3_PSI = 0;
        DataOut.Register_Stage4_Time = 0;
        DataOut.Register_Stage4_PSI = 0;
        DataOut.Register_Total_Time = 0;
        DataOut.Register_Total_Pay = 0;
        DataOut.Register_Unit_Price = 1;
    }

    public static byte[] GetBytesSend() {
        final byte[] array = new byte[55];
        array[0] = 1;
        array[1] = 3;
        array[2] = 50;
        WriteData(array, 3, DataOut.Register_Disinfect);
        WriteData(array, 5, DataOut.Register_Card_Pay);
        WriteData(array, 7, DataOut.Register_Cure);
        WriteData(array, 9, DataOut.Register_Stage1_Time);
        WriteData(array, 11, DataOut.Register_Stage1_PSI);
        WriteData(array, 13, DataOut.Register_Stage2_Time);
        WriteData(array, 15, DataOut.Register_Stage2_PSI);
        WriteData(array, 17, DataOut.Register_Stage3_Time);
        WriteData(array, 19, DataOut.Register_Stage3_PSI);
        WriteData(array, 21, DataOut.Register_Stage4_Time);
        WriteData(array, 23, DataOut.Register_Stage4_PSI);
        WriteData(array, 25, DataOut.Register_Total_Time);
        WriteData(array, 27, DataOut.Register_Total_Pay);
        WriteData(array, 29, (short)DataSaved.Get_O2_Concentration_Modify_Value());
        WriteData(array, 31, (short)DataSaved.Get_Cabin_Pressure_Modify_Value());
        WriteData(array, 33, (short)DataSaved.Get_O2_Modify_Value());
        WriteData(array, 35, (short)DataSaved.Get_O2_MAX_Value());
        WriteData(array, 37, (short)DataSaved.Get_O2_MIN_Value());
        WriteData(array, 39, (short)DataSaved.Get_CPS_Sync_Pressure());
        WriteData(array, 41, (short)DataSaved.Get_Stop_O2_Pressure());
        WriteData(array, 43, (short)DataSaved.Get_Cabin_Pressure_MAX_Value());
        WriteData(array, 45, (short)DataSaved.Get_Cabin_Pressure_MIN_Value());
        WriteData(array, 47, (short)DataSaved.Get_Single_Valve_Work_Time());
        WriteData(array, 49, (short)DataSaved.Get_Double_Valve_Work_Time());
        WriteData(array, 51, (short)DataSaved.Get_Unit_Price());
        final byte[] array2 = new byte[3];
        Tools.CRC16(array, 53, array2);
        array[53] = array2[0];
        array[54] = array2[1];
        return array;
    }

    //
    public static int GetCureMinutes(final int sex, final int age, final int weight, final int height, final int minute) {
        int temp;
        if (sex == 0) {
            temp = (int)(90 * (weight * minute) / (20 * (height * height) / 10000.0 * (100.0 - 0.3 * age)));
        }
        else {
            temp = (int)(93 * (weight * minute) / (22 * (height * height) / 10000.0 * (100.0 - 0.3 * age)));
        }
        if (temp < 30) {
            temp = 30;
        }
        if (temp > 120) {
            temp = 120;
        }
        return temp;
    }

    public static String GetPassword() {
        return "123456";
    }

    public static void OnTimer(final long n) {
    }

    public static DataIn Parse(final byte[] array, final int n) {
        if (n != 29) {
            return null;
        }
        final DataIn dataIn = new DataIn();
        dataIn.Register_Card_Pay_Status = ReadShort(array, 7);
        dataIn.Register_Card_Balance = ReadInt(array, 9);
        dataIn.Register_Kpa = ReadShort(array, 13);
        dataIn.Register_Cabin_O2_Concentration = ReadShort(array, 15);
        dataIn.Register_O2_Concentration = ReadShort(array, 17);
        dataIn.Register_PM25 = ReadShort(array, 19);
        dataIn.Register_CardID = ReadInt(array, 21);
        dataIn.Register_Reserved = ReadShort(array, 25);
        return dataIn;
    }

    private static int ReadInt(final byte[] array, final int n) {
        return 0x0 | array[n] << 24 | (0xFF0000 & array[n + 1] << 16) | (0xFF00 & array[n + 2] << 8) | (0xFF & array[n + 3]);
    }

    private static short ReadShort(final byte[] array, final int n) {
        return (short)((short)(0x0 | (short)(array[n] << 8)) | (short)(0xFF & array[n + 1]));
    }

    private static void WriteData(final byte[] array, final int n, final short n2) {
        array[n] = (byte)(0xFF & n2 >> 8);
        array[n + 1] = (byte)(n2 & 0xFF);
    }
}