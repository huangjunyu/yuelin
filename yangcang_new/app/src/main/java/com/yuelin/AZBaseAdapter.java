package com.yuelin;

import android.support.v7.widget.RecyclerView;

import com.yuelin.o2cabin.Disease;

import java.util.List;

public abstract class AZBaseAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

	protected List<Disease> mDataList;

	public AZBaseAdapter(List<Disease> dataList) {
		mDataList = dataList;
	}

	public List<Disease> getDataList() {
		return mDataList;
	}

	public void setDataList(List<Disease> dataList) {
		mDataList = dataList;
		notifyDataSetChanged();
	}

	public String getSortLetters(int position) {
		if (mDataList == null || mDataList.isEmpty()) {
			return null;
		}
		return mDataList.get(position).getSortLetter();
	}

	public int getSortLettersFirstPosition(String letters) {
		if (mDataList == null || mDataList.isEmpty()) {
			return -1;
		}
		int position = -1;
		for (int index = 0; index < mDataList.size(); index++) {
			if (mDataList.get(index).getSortLetter().equals(letters)) {
				position = index;
				break;
			}
		}
		return position;
	}

	public int getNextSortLetterPosition(int position) {
		if (mDataList == null || mDataList.isEmpty() || mDataList.size() <= position + 1) {
			return -1;
		}
		int resultPosition = -1;
		for (int index = position + 1; index < mDataList.size(); index++) {
			if (!mDataList.get(position).getSortLetter().equals(mDataList.get(index).getSortLetter())) {
				resultPosition = index;
				break;
			}
		}
		return resultPosition;
	}

	@Override
	public int getItemCount() {
		return mDataList == null ? 0 : mDataList.size();
	}
}
