package com.yuelin.o2cabin;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class DisinfectActivity extends Activity
{
    static DisinfectActivity theOnly = null;
    int currentIndex = -1;
    ViewFlipper flipper;
    ImageView[] images = new ImageView[4];
    int oldIndex = -1;
    long startTickCount = -1L;
    long startTime = -1L;
    long stopTickCount = -1L;

    public static void OnTimer_Static(long paramLong)
    {
        try
        {
            if (theOnly != null)
                theOnly.OnTimer(paramLong);
            return;
        }
        catch (Exception localException)
        {
            MyLog.log(localException);
        }
    }

    private void findAndInitViews()
    {
        try
        {
            synchronized (DataOut.Lock)
            {
                DataOut.Register_Disinfect = 1;
                this.startTime = System.currentTimeMillis();
                this.flipper = ((ViewFlipper)findViewById(R.id.activity_disinfect_flipper));
                this.currentIndex = 0;
//                this.flipper.setInAnimation(this, 2131034126);
                this.flipper.setFlipInterval(6000);
                this.flipper.startFlipping();
                this.oldIndex = 3;
                this.currentIndex = 0;
                this.stopTickCount = System.currentTimeMillis();
                return;
            }
        }
        catch (Exception localException)
        {
            MyLog.log(localException);
        }
    }

    public void OnTimer(long paramLong)
    {
        if ((this.startTime > 0L) && (paramLong - this.startTime >= 300000L))
        {
            long l = (paramLong - this.startTime) / 1000L;
            this.startTime = paramLong;
            runOnUiThread(new Runnable()
            {
                public void run()
                {
                    DisinfectActivity.this.finish();
                }
            });
        }
    }

    public void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        theOnly = this;
        getWindow().setFlags(1024, 1024);
        getWindow().setFlags(128, 128);
        setContentView(R.layout.activity_disinfect);
        findAndInitViews();
    }

    public void onDestroy()
    {
        synchronized (DataOut.Lock)
        {
            DataOut.Register_Disinfect = 0;
            this.startTime = -1L;
            theOnly = null;
            super.onDestroy();
            return;
        }
    }

    public void onResume()
    {
        super.onResume();
    }
}
