package com.yuelin.o2cabin;

import android.os.*;
import android.app.*;

public class HttpTask extends AsyncTask<String, Integer, String>
{
    Activity _context;
    
    public HttpTask(final Activity context) {
        this._context = context;
    }
    
    protected String doInBackground(final String... array) {
        try {
            return Tools.HttpPost(ControlThread.theOnly.pathBase, array[0]);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    protected void onCancelled() {
        super.onCancelled();
    }
    
    protected void onPostExecute(final String s) {
        if (this._context == MainActivity.theOnly) {
            ((MainActivity)this._context).OnHttpReturn(s);
            return;
        }
        if (this._context == ItemActivity.theOnly) {
            ((ItemActivity)this._context).OnHttpReturn(s);
        }
    }
}
