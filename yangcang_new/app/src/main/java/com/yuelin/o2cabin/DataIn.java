package com.yuelin.o2cabin;

public class DataIn
{
    public short Register_Cabin_O2_Concentration;
    public int Register_CardID;
    public int Register_Card_Balance;
    public short Register_Card_Pay_Status;
    public short Register_Kpa;
    public short Register_O2_Concentration;
    public short Register_PM25;
    public short Register_Reserved;

    public DataIn() {
        this.Register_Card_Pay_Status = 0;
        this.Register_Card_Balance = 0;
        this.Register_Kpa = 0;
        this.Register_Cabin_O2_Concentration = 0;
        this.Register_O2_Concentration = 0;
        this.Register_PM25 = 0;
        this.Register_CardID = 0;
        this.Register_Reserved = 0;
    }
}
