package com.wulian.wlcamera.device.album.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;
import com.wulian.wlcamera.R;
import com.wulian.wlcamera.device.album.ImageItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ImageGridAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter {

	private Context context;
	private List<ImageItem> dataList;
	private Map<String, ImageItem> selectMap = new HashMap<String, ImageItem>();
	private LayoutInflater mInflater;

	public ImageGridAdapter(Context context, List<ImageItem> list) {
		this.context = context;
		dataList = list;
		mInflater = LayoutInflater.from(context);
	}

	public void setDataList(List<ImageItem> dataList) {
		this.dataList = dataList;
	}

	@Override
	public int getCount() {
		int count = 0;
		if (dataList != null) {
			count = dataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class Holder {
		private ImageView iv;
		private ImageView selected;
		private TextView text;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;

		if (convertView == null) {
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.item_image_grid, parent, false);
			holder.iv = (ImageView) convertView.findViewById(R.id.image);
			holder.selected = (ImageView) convertView.findViewById(R.id.isselected);
			holder.text = (TextView) convertView.findViewById(R.id.item_image_grid_text);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final ImageItem item = dataList.get(position);

		holder.iv.setTag(item.imagePath);

		ImageLoader.getInstance().displayImage("file://" + item.imagePath, holder.iv);
	//	Picasso.with(context).load("file://" + item.imagePath).into(holder.iv);

		boolean isChecked = getImageItemCheckStatus(item);
		if (isChecked) {
			holder.text.setVisibility(View.VISIBLE);
			holder.selected.setVisibility(View.VISIBLE);
		} else {
			holder.selected.setVisibility(View.INVISIBLE);
			holder.text.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	public void clearSelect() {
		selectMap.clear();
		notifyDataSetChanged();
	}

	public void selectAll() {
		for (int i = 0; i < dataList.size(); i++) {
			ImageItem item = dataList.get(i);
			selectMap.put(item.imagePath, item);
		}
		notifyDataSetChanged();
	}

	public Map<String, ImageItem> getSelectMap() {
		return selectMap;
	}

	public boolean getImageItemCheckStatus(ImageItem item) {
		if (selectMap.get(item.imagePath) != null) {
			return true;
		}
		return false;
	}

	public void check(ImageItem item, boolean check) {
		if (check) {
			selectMap.put(item.imagePath, item);
		} else {
			selectMap.remove(item.imagePath);
		}

		notifyDataSetChanged();
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder mHeaderHolder;
		if (convertView == null) {
			mHeaderHolder = new HeaderViewHolder();
			convertView = mInflater.inflate(R.layout.layout_header, parent, false);
			mHeaderHolder.mTextView = (TextView) convertView.findViewById(R.id.header);
			convertView.setTag(mHeaderHolder);
		} else {
			mHeaderHolder = (HeaderViewHolder) convertView.getTag();
		}

		mHeaderHolder.mTextView.setText(dataList.get(position).time);

		return convertView;
	}

	class HeaderViewHolder {
		public TextView mTextView;
	}

	@Override
	public long getHeaderId(int position) {
		return dataList.get(position).headerId;
	}

}
