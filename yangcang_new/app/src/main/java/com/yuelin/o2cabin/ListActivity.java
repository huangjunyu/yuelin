package com.yuelin.o2cabin;

import android.app.*;
import android.widget.*;
import android.view.*;
import android.content.*;

import java.util.*;

import android.os.*;

public class ListActivity extends Activity {
    ArrayList<Integer> IDs;
    ImageButton back;
    int currentPage;
    HashMap<Button, Integer> diseaseButton;
    TextView[] diseaseNames;
    ImageButton enter;
    ImageButton nextPage;
    TextView pageinfo;
    ImageButton prevPage;
    int totalPage;

    public ListActivity() {
        this.IDs = new ArrayList<Integer>();
        this.currentPage = -1;
        this.totalPage = 1;
        this.diseaseButton = new HashMap<Button, Integer>();
        this.diseaseNames = new TextView[8];
    }

    private void findAndInitViews() {
        try {
            this.diseaseNames[0] = (TextView) this.findViewById(R.id.text1);
            this.diseaseNames[1] = (TextView) this.findViewById(R.id.text2);
            this.diseaseNames[2] = (TextView) this.findViewById(R.id.text3);
            this.diseaseNames[3] = (TextView) this.findViewById(R.id.text4);
            this.diseaseNames[4] = (TextView) this.findViewById(R.id.text5);
            this.diseaseNames[5] = (TextView) this.findViewById(R.id.text6);
            this.diseaseNames[6] = (TextView) this.findViewById(R.id.text7);
            this.diseaseNames[7] = (TextView) this.findViewById(R.id.text8);
            this.IDs = new ArrayList<Integer>();
            final Iterator<Map.Entry<Integer, Disease>> iterator = DataSaved.Diseases.entrySet().iterator();
            while (iterator.hasNext()) {
                this.IDs.add(iterator.next().getKey());
            }
            this.totalPage = 7 + this.IDs.size() >> 3;
            this.prevPage = (ImageButton) this.findViewById(R.id.prev);
            if (this.prevPage != null) {
                this.prevPage.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
                    public void onClick(final View view) {
                        ListActivity.this.gotoPage(-1 + ListActivity.this.currentPage);
                    }
                });
            }
            this.nextPage = (ImageButton) this.findViewById(R.id.next);
            if (this.nextPage != null) {
                this.nextPage.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
                    public void onClick(final View view) {
                        ListActivity.this.gotoPage(1 + ListActivity.this.currentPage);
                    }
                });
            }
            final ImageButton imageButton = (ImageButton) this.findViewById(R.id.back);
            if (imageButton != null) {
                imageButton.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
                    public void onClick(final View view) {
                        DataOut.Register_Cure = 0;
                        ListActivity.this.finish();
                    }
                });
            }
            final ImageButton imageButton2 = (ImageButton) this.findViewById(R.id.enter);
            if (imageButton2 != null) {
                MyLog.d("ListActivity", "Set enter.setOnClickListener");
                imageButton2.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
                    public void onClick(final View view) {
                        MyLog.d("ListActivity", "Enter enter.setOnClickListener");
                        ListActivity.this.startActivity(new Intent((Context) ListActivity.this, (Class) ItemActivity.class));
                    }
                });
            }
            this.pageinfo = (TextView) this.findViewById(R.id.pageinfo);
            this.gotoPage(0);
        } catch (Exception ex) {
            MyLog.log(ex);
        }
    }

    private void setFullScreen() {
        this.findViewById(R.id.fullscreen).setSystemUiVisibility(4871);
    }

    void gotoPage(int currentPage) {
        int i = 0;
        if (currentPage < 0) {
            currentPage = 0;
        }
        if (currentPage > -1 + this.totalPage) {
            currentPage = -1 + this.totalPage;
        }
        if (this.currentPage != currentPage) {
            this.currentPage = currentPage;
            if (this.currentPage == 0) {
                this.prevPage.setVisibility(View.GONE);
            } else {
                this.prevPage.setVisibility(View.VISIBLE);
            }
            if (this.currentPage >= -1 + this.totalPage) {
                this.nextPage.setVisibility(View.GONE);
            } else {
                this.nextPage.setVisibility(View.VISIBLE);
            }
            final TextView pageinfo = this.pageinfo;
            final StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(1 + this.currentPage);
            sb.append("/");
            sb.append(this.totalPage);
            pageinfo.setText((CharSequence) sb.toString());
            while (i < 8) {
                final int n = i + 8 * this.currentPage;
                if (n >= DataSaved.Diseases.size()) {
                    this.diseaseNames[i].setText((CharSequence) "");
                    this.diseaseNames[i].setOnClickListener((View.OnClickListener) null);
                } else {
                    this.diseaseNames[i].setTag((Object) this.IDs.get(n));
                    this.diseaseNames[i].setText((CharSequence) DataSaved.Diseases.get(this.IDs.get(n)).name);
                    this.diseaseNames[i].setOnClickListener((View.OnClickListener) new View.OnClickListener() {
                        public void onClick(final View view) {
                            final TextView textView = (TextView) view;
                            if (textView != null) {
                                ControlThread.theOnly.diseaseName = textView.getText().toString();
                                ControlThread.theOnly.diseaseID = (int) textView.getTag();
                                ListActivity.this.startActivity(new Intent((Context) ListActivity.this, (Class) ItemActivity.class));
                            }
                        }
                    });
                }
                ++i;
            }
        }
    }

    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.getWindow().setFlags(1024, 1024);
        this.getWindow().setFlags(128, 128);
        this.setContentView(R.layout.activity_list);
        this.findAndInitViews();
        this.setFullScreen();
    }

    public void onResume() {
        super.onResume();
        if (ControlThread.theOnly.returnToMainActivity) {
            this.finish();
        }
    }
}
