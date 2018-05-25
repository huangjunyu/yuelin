package com.yuelin.o2cabin;

import android.widget.*;
import android.content.pm.*;
import android.net.*;
import android.app.*;
import android.content.*;
import android.view.*;
import android.os.*;

import java.net.*;
import java.io.*;

public class Update
{
    private static final int DOWNLOAD = 1;
    private static final int DOWNLOAD_FINISH = 2;
    private boolean cancelUpdate;
    String downloadUrl;
    private Context mContext;
    private Dialog mDownloadDialog;
    private Handler mHandler;
    private ProgressBar mProgress;
    private String mSavePath;
    private int progress;
    
    public Update(final Context mContext) {
        this.cancelUpdate = false;
        this.mHandler = new Handler() {
            public void handleMessage(final Message message) {
                switch (message.what) {
                    default: {}
                    case 2: {
                        Update.this.installApk();
                    }
                    case 1: {
                        Update.this.mProgress.setProgress(Update.this.progress);
                    }
                }
            }
        };
        this.mContext = mContext;
    }
    
    private void downloadApk() {
        new downloadApkThread().start();
    }
    
    private int getVersionCode(final Context context) {
        try {
            return context.getPackageManager().getPackageInfo("com.huizhan.health", 0).versionCode;
        }
        catch (PackageManager.NameNotFoundException ex) {
            ex.printStackTrace();
            return 0;
        }
    }
    
    private void installApk() {
        final File file = new File(this.mSavePath, "DeviceApp.apk");
        if (!file.exists()) {
            return;
        }
        final Intent intent = new Intent("android.intent.action.VIEW");
        final StringBuilder sb = new StringBuilder();
        sb.append("file://");
        sb.append(file.toString());
        intent.setDataAndType(Uri.parse(sb.toString()), "application/vnd.android.package-archive");
        this.mContext.startActivity(intent);
    }
    
    private void showDownloadDialog() {
        try {
            MyLog.d("Update", "showDownloadDialog 001");
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.mContext);
            alertDialogBuilder.setTitle("正在更新");
            final View inflate = LayoutInflater.from(this.mContext).inflate(R.layout.softupdate_progress, (ViewGroup)null);
            this.mProgress = (ProgressBar)inflate.findViewById(R.id.update_progress);
            MyLog.d("Update", "showDownloadDialog 002");
            alertDialogBuilder.setView(inflate);
            MyLog.d("Update", "showDownloadDialog 003");
            alertDialogBuilder.setNegativeButton("取消", (DialogInterface.OnClickListener)new DialogInterface.OnClickListener() {
                public void onClick(final DialogInterface dialogInterface, final int n) {
                    dialogInterface.dismiss();
                    Update.this.cancelUpdate = true;
                }
            });
            MyLog.d("Update", "showDownloadDialog 004");
            (this.mDownloadDialog = (Dialog)alertDialogBuilder.create()).show();
            MyLog.d("Update", "showDownloadDialog 005");
            this.downloadApk();
            MyLog.d("Update", "showDownloadDialog 006");
        }
        catch (Exception ex) {
            MyLog.log(ex);
        }
    }
    
    private void showNoticeDialog(final String downloadUrl, final String message, final int n) {
        this.downloadUrl = downloadUrl;
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.mContext);
        alertDialogBuilder.setTitle(mContext.getString(R.string.soft_update_title));
        alertDialogBuilder.setMessage("是否更新到最新版本？");
        alertDialogBuilder.setPositiveButton(mContext.getString(R.string.soft_update_updatebtn), (DialogInterface.OnClickListener)new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                dialogInterface.dismiss();
                MyLog.d("Update", "showNoticeDialog 001");
                Update.this.showDownloadDialog();
                MyLog.d("Update", "showNoticeDialog 002");
            }
        });
        alertDialogBuilder.setNegativeButton(mContext.getString(R.string.soft_update_later), (DialogInterface.OnClickListener)new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int n) {
                dialogInterface.dismiss();
            }
        });
        ((Dialog)alertDialogBuilder.create()).show();
    }
    
    void update(final String s, final String s2, final int n) {
        this.showNoticeDialog(s, s2, n);
    }
    
    private class downloadApkThread extends Thread
    {
        @Override
        public void run() {
            try {
                if (Environment.getExternalStorageState().equals("mounted")) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append(Environment.getExternalStorageDirectory());
                    sb.append("/");
                    final String string = sb.toString();
                    final Update this$0 = Update.this;
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append(string);
                    sb2.append("download");
                    this$0.mSavePath = sb2.toString();
                    final HttpURLConnection httpURLConnection = (HttpURLConnection)new URL(Update.this.downloadUrl).openConnection();
                    httpURLConnection.connect();
                    final int contentLength = httpURLConnection.getContentLength();
                    final InputStream inputStream = httpURLConnection.getInputStream();
                    final File file = new File(Update.this.mSavePath);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    final FileOutputStream fileOutputStream = new FileOutputStream(new File(Update.this.mSavePath, "DeviceApp.apk"));
                    final byte[] array = new byte[1024];
                    int n = 0;
                    do {
                        final int read = inputStream.read(array);
                        n += read;
                        Update.this.progress = (int)(100.0f * (n / contentLength));
                        Update.this.mHandler.sendEmptyMessage(1);
                        if (read <= 0) {
                            Update.this.mHandler.sendEmptyMessage(2);
                            break;
                        }
                        fileOutputStream.write(array, 0, read);
                    } while (!Update.this.cancelUpdate);
                    fileOutputStream.close();
                    inputStream.close();
                }
            } catch (MalformedURLException ex2) {
                MyLog.log(ex2);
            } catch (IOException ex) {
                MyLog.log(ex);
            }
            Update.this.mDownloadDialog.dismiss();
        }
    }
}
