package com.yuelin.o2cabin;

import tw.com.prolific.driver.pl2303.*;

import android.hardware.usb.*;
import android.content.*;

public class SerialController {
    private static final String ACTION_USB_PERMISSION = "com.prolific.pl2303hxdsimpletest.USB_PERMISSION";
    private static final String TAG = "SerialController";
    static final byte[] buffer;
    static int bufferSize;
    public static SerialController theOnly;
    private PL2303Driver.BaudRate baudrate;
    private PL2303Driver.DataBits dataBits;
    private PL2303Driver.FlowControl flowControl;
    private PL2303Driver.Parity parity;
    public boolean running;
    private PL2303Driver serial;
    SerialServiceThread serialThread;
    private PL2303Driver.StopBits stopBits;

    static {
        buffer = new byte[10240];
    }

    public SerialController() {
        this.serial = null;
        this.baudrate = PL2303Driver.BaudRate.B9600;
        this.dataBits = PL2303Driver.DataBits.D8;
        this.parity = PL2303Driver.Parity.NONE;
        this.stopBits = PL2303Driver.StopBits.S1;
        this.flowControl = PL2303Driver.FlowControl.OFF;
        this.running = true;
        this.serialThread = null;
        SerialController.theOnly = this;
    }

    public PL2303Driver GetSerial() {
        return this.serial;
    }

    public boolean Init() {
        MyLog.d("SerialController", "Enter Init");
        boolean b = false;
        try {
            this.serial = new PL2303Driver((UsbManager) MyApplication.theOnly.getSystemService("usb"), (Context) MyApplication.theOnly, "com.prolific.pl2303hxdsimpletest.USB_PERMISSION");
            if (!this.serial.PL2303USBFeatureSupported()) {
                MyLog.d("SerialController", "No Support USB host API");
                this.serial = null;
                b = false;
            } else {
                b = true;
            }
        } catch (Exception ex) {
            MyLog.log(ex);
        }
        MyLog.d("SerialController", "Leave Init");
        return b;
    }

    public boolean IsConnected() {
        return this.serial.isConnected();
    }

    public void NotifyData(final byte[] array) {
        int i = 0;
        int n;
        for (int j = 0; j < array.length; j = n) {
            final byte[] buffer = SerialController.buffer;
            final int bufferSize = SerialController.bufferSize;
            SerialController.bufferSize = bufferSize + 1;
            n = j + 1;
            MyLog.d("outof", "outof" + bufferSize);
            buffer[bufferSize] = array[j];
        }
        if (SerialController.bufferSize >= 8 && SerialController.buffer[1] == 3) {
            final byte[] getBytesSend = DataOut.GetBytesSend();
            final StringBuilder sb = new StringBuilder();
            sb.append("Price=");
            sb.append(DataOut.Register_Total_Pay);
            MyLog.d("SerialController", sb.toString());
            SerialController.theOnly.Write(getBytesSend, getBytesSend.length);
            SerialController.bufferSize -= 8;
            while (i < SerialController.bufferSize) {
                SerialController.buffer[i] = SerialController.buffer[i + 8];
                ++i;
            }
        } else if (SerialController.bufferSize >= 29 && SerialController.buffer[1] == 16) {
            final DataIn parse = DataOut.Parse(SerialController.buffer, 29);
            final byte[] array2 = {1, 16, 0, -56, 0, 10, -63, -16};
            SerialController.theOnly.Write(array2, array2.length);
            SerialController.bufferSize -= 29;
            for (int k = 0; k < SerialController.bufferSize; ++k) {
                SerialController.buffer[k] = SerialController.buffer[k + 29];
            }
            if (parse != null) {
                ControlThread.theOnly.lastDataInTime = System.currentTimeMillis();
                ControlThread.theOnly.lastDataIn2 = parse;
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("indata.Card_Pay_Status =");
                sb2.append(parse.Register_Card_Pay_Status);
                MyLog.d("SerialController", sb2.toString());
                final StringBuilder sb3 = new StringBuilder();
                sb3.append("indata.Card_Balance =");
                sb3.append(parse.Register_Card_Balance);
                MyLog.d("SerialController", sb3.toString());
                final StringBuilder sb4 = new StringBuilder();
                sb4.append("indata.Kpa =");
                sb4.append(parse.Register_Kpa);
                MyLog.d("SerialController", sb4.toString());
                final StringBuilder sb5 = new StringBuilder();
                sb5.append("indata.Register_Cabin_O2_Concentration =");
                sb5.append(parse.Register_Cabin_O2_Concentration);
                MyLog.d("SerialController", sb5.toString());
                final StringBuilder sb6 = new StringBuilder();
                sb6.append("indata.O2_Concentration =");
                sb6.append(parse.Register_O2_Concentration);
                MyLog.d("SerialController", sb6.toString());
                final StringBuilder sb7 = new StringBuilder();
                sb7.append("indata.PM25 =");
                sb7.append(parse.Register_PM25);
                MyLog.d("SerialController", sb7.toString());
                final StringBuilder sb8 = new StringBuilder();
                sb8.append("indata.CardID =");
                sb8.append(parse.Register_CardID);
                MyLog.d("SerialController", sb8.toString());
                if (parse.Register_Card_Pay_Status == 1 && !ControlThread.theOnly.stopedByUser && DataOut.Register_Cure == 0) {
                    ControlThread.theOnly.startTime = System.currentTimeMillis();
                    ControlThread.theOnly.addedMinutes = 0;
                    ControlThread.theOnly.lastSeconds = 0L;
                    ControlThread.theOnly.lastCureHeartTime = 0L;
                    ControlThread.theOnly.needRunMinutes = DataOut.Register_Total_Time;
                    MyLog.d("SerialController", "\u8bbe\u7f6e\u5f00\u673a003\uff0cDataOut.Register_Cure = 1");
                    DataOut.Register_Cure = 1;
                    DataOut.Register_Card_Pay = 0;
                    try {
                        Thread.sleep(100L);
                    } catch (Exception ex) {
                    }
                    if (ItemActivity.theOnly != null) {
                        ItemActivity.theOnly.StartCure(parse);
                    }
                }
                if (DataOut.Register_Cure == 1) {
                    ControlThread.theOnly.lastDataIn = parse;
                    if (parse.Register_O2_Concentration >= 210 && parse.Register_O2_Concentration <= 1000) {
                        ControlThread.theOnly.O2[ControlThread.theOnly.O2Count++ % 20] = parse.Register_O2_Concentration;
                    }
                    if (ControlThread.theOnly.O2Count >= 20) {
                        int n2 = -10000;
                        int n3 = 10000;
                        int n4 = 0;
                        while (i < 20) {
                            if (ControlThread.theOnly.O2[i] > n2) {
                                n2 = ControlThread.theOnly.O2[i];
                            }
                            if (ControlThread.theOnly.O2[i] < n3) {
                                n3 = ControlThread.theOnly.O2[i];
                            }
                            n4 += ControlThread.theOnly.O2[i];
                            ++i;
                        }
                        ControlThread.theOnly.O2Value = (short) (0.5 + (n4 - n2 - n3) / 18.0);
                        if (ControlThread.theOnly.O2Value > ControlThread.theOnly.maxDataIn.Register_O2_Concentration) {
                            ControlThread.theOnly.maxDataIn.Register_O2_Concentration = ControlThread.theOnly.O2Value;
                        }
                    }
                    if (StatusActivity.theOnly != null) {
                        StatusActivity.theOnly.OnData(parse);
                    }
                }
                if (parse.Register_Kpa > ControlThread.theOnly.maxDataIn.Register_Kpa) {
                    ControlThread.theOnly.maxDataIn.Register_Kpa = parse.Register_Kpa;
                }
                if (parse.Register_Cabin_O2_Concentration > ControlThread.theOnly.maxDataIn.Register_Cabin_O2_Concentration) {
                    ControlThread.theOnly.maxDataIn.Register_Cabin_O2_Concentration = parse.Register_Cabin_O2_Concentration;
                }
                if (parse.Register_PM25 > ControlThread.theOnly.maxDataIn.Register_PM25) {
                    ControlThread.theOnly.maxDataIn.Register_PM25 = parse.Register_PM25;
                }
            } else {
                MyLog.d("SerialController", "indata is null , parse error !");
            }
        }
    }

    public boolean Open(final int n) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Enter Open(");
        sb.append(n);
        sb.append(")");
        MyLog.d("SerialController", sb.toString());
        if (SerialController.theOnly.GetSerial() == null) {
            return false;
        }
        int n2 = 0;
        boolean b = false;
        do {
            if (!SerialController.theOnly.GetSerial().isConnected()) {
                if (!SerialController.theOnly.GetSerial().enumerate()) {
                    MyLog.d("SerialController", "no more serial devices found !");
                    return false;
                }
                MyLog.d("SerialController", "serial enumerate succeeded!");
            }
            MyLog.d("SerialController", "serial attached");
            if (SerialController.theOnly.GetSerial().isConnected()) {
                MyLog.d("SerialController", "openUsbSerial : isConnected ");
                if (n != 9600) {
                    if (n != 19200) {
                        if (n != 115200) {
                            this.baudrate = PL2303Driver.BaudRate.B9600;
                        } else {
                            this.baudrate = PL2303Driver.BaudRate.B115200;
                        }
                    } else {
                        this.baudrate = PL2303Driver.BaudRate.B19200;
                    }
                } else {
                    this.baudrate = PL2303Driver.BaudRate.B9600;
                }
                final StringBuilder sb2 = new StringBuilder();
                sb2.append("baudRate:");
                sb2.append(n);
                MyLog.d("SerialController", sb2.toString());
                if (!SerialController.theOnly.GetSerial().InitByBaudRate(this.baudrate, 700)) {
                    if (!SerialController.theOnly.GetSerial().PL2303Device_IsHasPermission()) {
                        MyLog.d("SerialController", "cannot open, maybe no permission");
                    }
                    if (SerialController.theOnly.GetSerial().PL2303Device_IsHasPermission() && !SerialController.theOnly.GetSerial().PL2303Device_IsSupportChip()) {
                        MyLog.d("SerialController", "cannot open, maybe this chip has no support, please use PL2303HXD / RA / EA chip.");
                    }
                    return false;
                }
                MyLog.d("SerialController", "connected.");
                b = true;
            } else {
                MyLog.d("SerialController", "openUsbSerial : is not Connected ");
                try {
                    Thread.sleep(1000L);
                    ++n2;
                } catch (Exception ex) {
                    MyLog.log(ex);
                }
            }
        } while (n2 < 60 && !b);
        MyLog.d("SerialController", "Leave openUsbSerial");
        if (b) {//如果已接通
            (this.serialThread = new SerialServiceThread()).start();
        }
        return b;
    }

    public byte[] Read() {
        final byte[] array = new byte[128];
        if (SerialController.theOnly.GetSerial() == null) {
            return null;
        }
        if (!SerialController.theOnly.GetSerial().isConnected()) {
            return null;
        }
        final int read = SerialController.theOnly.GetSerial().read(array);
        if (read < 0) {
            MyLog.d("SerialController", "Fail to bulkTransfer(read data)");
            return null;
        }
        byte[] array2 = null;
        if (read > 0) {
            array2 = new byte[read];
            for (int i = 0; i < read; ++i) {
                array2[i] = array[i];
            }
        }
        try {
            Thread.sleep(10L);
            return array2;
        } catch (InterruptedException ex) {
            MyLog.log(ex);
            return array2;
        }
    }

    public void Stop() {
        this.running = false;
        try {
            Thread.sleep(50L);
        } catch (Exception ex) {
        }
    }

    public void Write(final byte[] array, final int n) {
        if (SerialController.theOnly.GetSerial() == null) {
            return;
        }
        if (!SerialController.theOnly.GetSerial().isConnected()) {
            return;
        }
        final int write = SerialController.theOnly.GetSerial().write(array, n);
        if (write < 0) {
            final StringBuilder sb = new StringBuilder();
            sb.append("setup2: fail to controlTransfer: ");
            sb.append(write);
            MyLog.d("SerialController", sb.toString());
            return;
        }
        try {
            Thread.sleep(50L);
        } catch (InterruptedException ex) {
            MyLog.log(ex);
        }
    }

    public class SerialServiceThread extends Thread {//以下全是轮询
        @Override
        public void run() {
            MyLog.d("SerialController", "Enter SerialServiceThread.run()");
            super.run();
            while (SerialController.this.running) {
                final byte[] read = SerialController.this.Read();
                if (read != null && read.length > 0) {
                    MyLog.d("oldreadooo", "ooo" + read.length);
                    SerialController.this.NotifyData(read);
                }
                try {
                    Thread.sleep(10L);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
