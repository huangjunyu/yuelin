package com.yuelin.o2cabin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pilot.common.utils.PinyinUtils;
import com.yuelin.AZItemEntity;
import com.yuelin.AZTitleDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

public class ListActivity_new extends Activity {
    ArrayList<Integer> IDs;
    HashMap<Button, Integer> diseaseButton;
    TextView pageinfo;
    private RecyclerView mRecyclerView;
    private ItemAdapter   mAdapter;
    private AZSideBarView mBarList;
    public ListActivity_new() {
        this.IDs = new ArrayList<Integer>();
        this.diseaseButton = new HashMap<Button, Integer>();
    }

    private void findAndInitViews() {
        try {
            mRecyclerView = findViewById(R.id.recycler_list);
            mBarList = findViewById(R.id.bar_list);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(ListActivity_new.this,LinearLayoutManager.HORIZONTAL,false));
            mRecyclerView.addItemDecoration(new AZTitleDecoration(new AZTitleDecoration.TitleAttributes(ListActivity_new.this)));
            this.IDs = new ArrayList<Integer>();
            final Iterator<Map.Entry<Integer, Disease>> iterator = DataSaved.Diseases.entrySet().iterator();
            while (iterator.hasNext()) {
                this.IDs.add(iterator.next().getKey());
            }
            final ImageButton imageButton = (ImageButton) this.findViewById(R.id.back);
            if (imageButton != null) {
                imageButton.setOnClickListener((View.OnClickListener) new View.OnClickListener() {
                    public void onClick(final View view) {
                        DataOut.Register_Cure = 0;
                        ListActivity_new.this.finish();
                    }
                });
            }
        } catch (Exception ex) {
            MyLog.log(ex);
        }
    }
    private void initEvent() {
        mBarList.setOnLetterChangeListener(new AZSideBarView.OnLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                int position = mAdapter.getSortLettersFirstPosition(letter);
                if (position != -1) {
                    if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                        LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                        manager.scrollToPositionWithOffset(position, 0);
                    } else {
                        mRecyclerView.getLayoutManager().scrollToPosition(position);
                    }
                }
            }
        });
    }
    private void initData() {

        this.IDs = new ArrayList<Integer>();
        List<Disease> diseases = new ArrayList<Disease>();
        final Iterator<Map.Entry<Integer, Disease>> iterator = DataSaved.Diseases.entrySet().iterator();
        while (iterator.hasNext()) {
            this.IDs.add(iterator.next().getKey());
        }
        for (int i = 0;i < this.IDs.size();i++){
            Log.e("eoooaaa",DataSaved.Diseases.get(this.IDs.get(i)).name);
            diseases.add(DataSaved.Diseases.get(this.IDs.get(i)));
        }
        List<Disease> dateList = fillData(diseases);
        Collections.sort(dateList, new LettersComparator());
        mRecyclerView.setAdapter(mAdapter = new ItemAdapter(dateList));
        mAdapter.setItemClickListener(new ItemAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ControlThread.theOnly.diseaseName = mAdapter.getDataList().get(position).name;
                ControlThread.theOnly.diseaseID = (int) mAdapter.getDataList().get(position).id;
                ListActivity_new.this.startActivity(new Intent((Context) ListActivity_new.this,  ItemActivity.class));
            }
        });
    }
    private List<Disease> fillData(List<Disease> date) {
        List<Disease> sortList = new ArrayList<>();
        for (Disease aDate : date) {
            AZItemEntity<String> item = new AZItemEntity<>();
//            item.setValue(aDate);
            //汉字转换成拼音
            String pinyin = PinyinUtils.getPingYin(aDate.name);
            //取第一个首字母
            String letters = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (letters.matches("[A-Z]")) {
                aDate.setSortLetter(letters.toUpperCase());
            } else {
                aDate.setSortLetter("#");
            }
            sortList.add(aDate);
        }
        return sortList;

    }
    private void setFullScreen() {
        this.findViewById(R.id.fullscreen).setSystemUiVisibility(4871);
    }

    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.getWindow().setFlags(1024, 1024);
        this.getWindow().setFlags(128, 128);
        this.setContentView(R.layout.activity_list_new);
        this.findAndInitViews();
        this.setFullScreen();
        initEvent();
        initData();
    }

    public void onResume() {
        super.onResume();
        if (ControlThread.theOnly.returnToMainActivity) {
            this.finish();
        }
    }
}
