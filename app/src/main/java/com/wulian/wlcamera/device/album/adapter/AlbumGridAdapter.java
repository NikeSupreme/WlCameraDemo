package com.wulian.wlcamera.device.album.adapter;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.wulian.wlcamera.R;
import com.wulian.wlcamera.bean.AlbumEntity;
import com.wulian.wlcamera.device.album.AlbumGridActivity;
import com.wulian.wlcamera.device.album.AlbumPicActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AlbumGridAdapter extends BaseAdapter {

	private final static int MAX = 9;

	private Context mContext;
	private List<String> mPictureUrlList;
	private LayoutInflater inflater;
	private AlbumEntity albumEntity;
	private boolean showAll;
	private boolean isDeleteModel = false;
	private DeleteListener deleteListener;
	private Map<String, String> selectMap = new HashMap<String, String>();
	DisplayImageOptions displayImageOptions;

	public AlbumGridAdapter(Context mContext, AlbumEntity albumEntity,
                            boolean showAll) {
		this.mContext = mContext;
		this.albumEntity = albumEntity;
		this.mPictureUrlList = albumEntity.getPics();
		inflater = LayoutInflater.from(mContext);
		this.showAll = showAll;
		displayImageOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(false) // 设置图片不缓存于内存中
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565) // 设置图片的质量
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT) // 设置图片的缩放类型，该方法可以有效减少内存的占用
				.build();
	}

	@Override
	public int getCount() {
		return mPictureUrlList == null ? 0
				: (showAll ? mPictureUrlList.size()
						: (mPictureUrlList.size() > MAX ? MAX : mPictureUrlList
								.size()));
	}

	@Override
	public Object getItem(int position) {
		return mPictureUrlList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_image_nogrid, null);
			viewHolder = new ViewHolder();
			viewHolder.item_layout = (LinearLayout) convertView
					.findViewById(R.id.item_layout);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			viewHolder.item_tv_count = (TextView) convertView
					.findViewById(R.id.item_tv_count);
			viewHolder.selected = (ImageView) convertView
					.findViewById(R.id.isselected);
			viewHolder.text = (TextView) convertView
					.findViewById(R.id.item_image_grid_text);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		ImageLoader.getInstance().displayImage(
				"file://" + mPictureUrlList.get(position), viewHolder.image,
				displayImageOptions, new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {

					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {

					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {

					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {

					}
				}, new ImageLoadingProgressListener() {
					@Override
					public void onProgressUpdate(String imageUri, View view,
							int current, int total) {

					}
				});

		final boolean isLastItem = position == (getCount() - 1);

		if (showAll) {

			viewHolder.item_layout.setVisibility(View.GONE);

		} else {

			if (isLastItem && mPictureUrlList.size() > MAX) {
				viewHolder.item_tv_count.setText(String.valueOf(mPictureUrlList
						.size()));
				viewHolder.item_layout.setVisibility(View.VISIBLE);

			} else {
				viewHolder.item_layout.setVisibility(View.GONE);
			}
		}

		boolean isChecked = getImageItemCheckStatus(mPictureUrlList
				.get(position));
		if (isChecked) {
			viewHolder.text.setVisibility(View.VISIBLE);
			viewHolder.selected.setVisibility(View.VISIBLE);
		} else {
			viewHolder.selected.setVisibility(View.INVISIBLE);
			viewHolder.text.setVisibility(View.INVISIBLE);
		}

		viewHolder.image.setOnClickListener(new OnClickListener() {

			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void onClick(View v) {

				if (isDeleteModel) {

					if (isLastItem && mPictureUrlList.size() > MAX) {

					} else {

						check(mPictureUrlList.get(position),
								!getImageItemCheckStatus(mPictureUrlList
										.get(position)));
						if (deleteListener != null) {
							deleteListener.onSelectChange(mPictureUrlList
									.get(position));
						}

					}

					return;
				}

				if (isLastItem && mPictureUrlList.size() > MAX) {
					mContext.startActivity(new Intent(mContext,
							AlbumGridActivity.class).putExtra("AlbumEntity",
							albumEntity));
				} else {
					final Intent i = new Intent(mContext,
							AlbumPicActivity.class);
					i.putExtra("AlbumEntity", albumEntity);
					i.putExtra("position", position);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						ActivityOptions options = ActivityOptions
								.makeScaleUpAnimation(v, 0, 0, v.getWidth(),
										v.getHeight());
						mContext.startActivity(i, options.toBundle());
					} else {
						mContext.startActivity(i);
					}
				}
			}
		});

		viewHolder.image.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				if (isLastItem && mPictureUrlList.size() > MAX) {

				} else {

					check(mPictureUrlList.get(position),
							!getImageItemCheckStatus(mPictureUrlList
									.get(position)));
					if (deleteListener != null) {
						deleteListener.onSelectChange(mPictureUrlList
								.get(position));
					}

				}
				return false;
			}
		});

		return convertView;
	}

	class ViewHolder {
		LinearLayout item_layout;
		ImageView image;
		TextView item_tv_count;
		ImageView selected;
		TextView text;
	}

	public void clearSelect() {
		selectMap.clear();
		notifyDataSetChanged();
	}

	public void selectAll() {
		for (int i = 0; i < mPictureUrlList.size(); i++) {
			String item = mPictureUrlList.get(i);
			selectMap.put(item, item);
		}
		notifyDataSetChanged();
	}

	public Map<String, String> getSelectMap() {
		return selectMap;
	}

	public boolean getImageItemCheckStatus(String item) {
		if (selectMap.get(item) != null) {
			return true;
		}
		return false;
	}

	public void check(String item, boolean check) {
		if (check) {
			selectMap.put(item, item);
		} else {
			selectMap.remove(item);
		}

		notifyDataSetChanged();
	}

	public int getSize() {

		return mPictureUrlList == null ? 0 : mPictureUrlList.size();
	}

	public void setModel(boolean model) {
		isDeleteModel = model;
	}

	public void setDeleteListener(DeleteListener deleteListener) {
		this.deleteListener = deleteListener;
	}

	public interface DeleteListener {
		public void onSelectChange(String result);
	}

}
