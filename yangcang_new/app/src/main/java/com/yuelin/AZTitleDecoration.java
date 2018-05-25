package com.yuelin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.pilot.common.utils.TextDrawUtils;

public class AZTitleDecoration extends RecyclerView.ItemDecoration {

    private TextPaint mTitleTextPaint;
    private Paint mBackgroundPaint;
    private TitleAttributes mTitleAttributes;

    public AZTitleDecoration(TitleAttributes attributes) {
        mTitleAttributes = attributes;
        mTitleTextPaint = new TextPaint();
        mTitleTextPaint.setAntiAlias(true);
        mTitleTextPaint.setTextSize(mTitleAttributes.mTextSize);
        mTitleTextPaint.setColor(mTitleAttributes.mTextColor);
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(mTitleAttributes.mBackgroundColor);
    }

    /**
     * 绘制标题
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (parent.getAdapter() == null || !(parent.getAdapter() instanceof AZBaseAdapter)) {
            return;
        }
        AZBaseAdapter adapter = (AZBaseAdapter) parent.getAdapter();
        if (adapter.getDataList() == null || adapter.getDataList().isEmpty()) {
            return;
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            final View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);
            if (titleAttachView(child, parent)) {
                drawTitleItem(c, parent, child, adapter.getSortLetters(position));
            }
        }
    }

    /**
     * 绘制悬浮标题
     */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (parent.getAdapter() == null || !(parent.getAdapter() instanceof AZBaseAdapter)) {
            return;
        }
        AZBaseAdapter adapter = (AZBaseAdapter) parent.getAdapter();
        if (adapter.getDataList() == null || adapter.getDataList().isEmpty()) {
            return;
        }
        View firstView = parent.getChildAt(0);
        int firstAdapterPosition = parent.getChildAdapterPosition(firstView);
        c.save();
        //找到下一个标题对应的adapter position
        int nextLetterAdapterPosition = adapter.getNextSortLetterPosition(firstAdapterPosition);
        if (nextLetterAdapterPosition != -1) {
            //下一个标题view index
            int nextLettersViewIndex = nextLetterAdapterPosition - firstAdapterPosition;
            if (nextLettersViewIndex < parent.getChildCount()) {
                View nextLettersView = parent.getChildAt(nextLettersViewIndex);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) nextLettersView.getLayoutParams();
                int nextToTop = nextLettersView.getLeft() - params.leftMargin - parent.getPaddingLeft();
                if (nextToTop < mTitleAttributes.mItemHeight * 2) {
                    Log.e("itemmmma", nextToTop + ".");
                    //有重叠
                    c.translate(nextToTop - mTitleAttributes.mItemHeight * 2, 0);
                }
            }
        }
        mBackgroundPaint.setColor(mTitleAttributes.mBackgroundColor);

        c.drawRect(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getPaddingRight() + mTitleAttributes.mItemHeight,
                parent.getHeight(), mBackgroundPaint);

        mTitleTextPaint.setTextSize(mTitleAttributes.mTextSize);
        mTitleTextPaint.setColor(mTitleAttributes.mTextColor);

        if ((parent.getRight() - parent.getWidth() - mTitleAttributes.mTextPadding / 2 - mTitleAttributes.mItemHeight / 2) > 0) {
            c.drawText(adapter.getSortLetters(firstAdapterPosition),
                    parent.getRight() - parent.getWidth() - mTitleAttributes.mTextPadding / 2 - mTitleAttributes.mItemHeight / 2,
                    TextDrawUtils.getTextBaseLineByCenter(parent.getPaddingTop() + mTitleAttributes.mItemHeight / 2, mTitleTextPaint),
                    mTitleTextPaint);
        } else {
            c.drawText(adapter.getSortLetters(firstAdapterPosition),
                    parent.getPaddingLeft() + firstView.getPaddingLeft() + mTitleAttributes.mTextPadding + mTitleAttributes.mTextPadding / 2 - 1,
                    TextDrawUtils.getTextBaseLineByCenter(parent.getPaddingTop() + mTitleAttributes.mItemHeight / 2, mTitleTextPaint),
                    mTitleTextPaint);
        }
        c.restore();
    }

    /**
     * 设置空出绘制标题的区域
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (titleAttachView(view, parent)) {
            outRect.set(mTitleAttributes.mItemHeight, 0, 0, 0);
        } else {
            super.getItemOffsets(outRect, view, parent, state);
        }
    }

    /**
     * 绘制标题信息
     */
    private void drawTitleItem(Canvas c, RecyclerView parent, View child, String letters) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        //绘制背景

        c.drawRect(child.getLeft() - parent.getPaddingLeft() - mTitleAttributes.mItemHeight, 0,
                child.getRight() - params.rightMargin - child.getWidth(),
                parent.getHeight() - parent.getPaddingBottom(), mBackgroundPaint);
        float textCenterY = child.getRight() - params.rightMargin - child.getWidth() * 2;
        //绘制标题文字
        c.drawText(letters, child.getRight() - child.getWidth() - mTitleAttributes.mTextPadding / 2 - mTitleAttributes.mItemHeight / 2,
                TextDrawUtils.getTextBaseLineByCenter(parent.getPaddingTop() + mTitleAttributes.mItemHeight / 2, mTitleTextPaint), mTitleTextPaint);
    }

    /**
     * 判断指定view的上方是否要空出绘制标题的位置
     *
     * @param view   　指定的view
     * @param parent 父view
     */
    private boolean titleAttachView(View view, RecyclerView parent) {
        if (parent.getAdapter() == null || !(parent.getAdapter() instanceof AZBaseAdapter)) {
            return false;
        }
        AZBaseAdapter adapter = (AZBaseAdapter) parent.getAdapter();
        if (adapter.getDataList() == null || adapter.getDataList().isEmpty()) {
            return false;
        }
        int position = parent.getChildAdapterPosition(view);
        //第一个一定要空出区域 + 每个都和前面一个去做判断，不等于前一个则要空出区域
        return position == 0 ||
                null != adapter.getDataList().get(position) && !adapter.getSortLetters(position).equals(adapter.getSortLetters(position - 1));

    }

    public static class TitleAttributes {

        Context mContext;
        /**
         * 标题高度
         */
        int mItemHeight;
        /**
         * 文字的padding
         */
        int mTextPadding;
        /**
         * 标题文字大小
         */
        int mTextSize;
        /**
         * 标题文字颜色
         */
        int mTextColor;
        /**
         * 标题背景
         */
        int mBackgroundColor;

        public TitleAttributes(Context context) {
            mContext = context;
            mItemHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics());
            mTextPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics());
            mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, context.getResources().getDisplayMetrics());
            mTextColor = Color.parseColor("#FF000000");
            mBackgroundColor = Color.parseColor("#0e000000");
        }

        public TitleAttributes setItemHeight(int heightDp) {
            mItemHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightDp,
                    mContext.getResources().getDisplayMetrics());
            return this;
        }

        public TitleAttributes setTextPadding(int paddingDp) {
            mTextPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingDp,
                    mContext.getResources().getDisplayMetrics());
            return this;
        }

        public TitleAttributes setTextSize(int sizeSp) {
            mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sizeSp, mContext.getResources().getDisplayMetrics());
            return this;
        }

        public TitleAttributes setTextColor(int color) {
            mTextColor = color;
            return this;
        }

        public TitleAttributes setBackgroundColor(int color) {
            mBackgroundColor = color;
            return this;
        }


    }
}
