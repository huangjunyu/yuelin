package com.yuelin.o2cabin;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuelin.AZBaseAdapter;
import com.yuelin.AZItemEntity;

import java.util.List;

public class ItemAdapter extends AZBaseAdapter<Disease, ItemAdapter.ItemHolder> {
	private MyItemClickListener mItemClickListener;
	public ItemAdapter(List<Disease> dataList) {
		super(dataList);
	}

	@Override
	public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adapter, parent, false), mItemClickListener);
//		return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adapter, parent, false));
	}

	@Override
	public void onBindViewHolder(ItemHolder holder, int position) {
//		holder.mTextName.setText(mDataList.get(position).name);
		String nameStr = mDataList.get(position).name;
		String titleStr = "";
		for (int i = 0;i < mDataList.get(position).name.length();i++){
			titleStr += nameStr.substring(i,i+1) + "\n";
		}
		holder.mTextName.setText(titleStr);
	}

	static class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		private MyItemClickListener mListener;
		TextView mTextName;

		ItemHolder(View itemView, MyItemClickListener myItemClickListener) {
			super(itemView);
			mTextName = itemView.findViewById(R.id.text_item_name);

			//将全局的监听赋值给接口
			this.mListener = myItemClickListener;
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (mListener != null) {
				mListener.onItemClick(v, getLayoutPosition());
			}
		}
	}
	/**
	 * 创建一个回调接口
	 */
	public interface MyItemClickListener {
		void onItemClick(View view, int position);
	}

	/**
	 * 在activity里面adapter就是调用的这个方法,将点击事件监听传递过来,并赋值给全局的监听
	 *
	 * @param myItemClickListener
	 */
	public void setItemClickListener(MyItemClickListener myItemClickListener) {
		this.mItemClickListener = myItemClickListener;
	}
}
