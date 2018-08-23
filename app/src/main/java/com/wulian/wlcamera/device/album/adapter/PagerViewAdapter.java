/**
 * Project Name:  iCam
 * File Name:     PagerViewAdapter.java
 * Package Name:  com.wulian.icam.adpter
 * @Date:         2015年4月2日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.wlcamera.device.album.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wulian.wlcamera.R;
import com.wulian.wlcamera.customview.CustomViewPager;
import com.wulian.wlcamera.customview.photoView.PhotoView;
import com.wulian.wlcamera.customview.photoView.PhotoViewAttacher;
import com.wulian.wlcamera.device.album.AlbumPicActivity;
import com.wulian.wlcamera.tools.ImageLoader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * @ClassName: PagerViewAdapter
 * @Function: 自定义PagerAdapter适配器
 * @date: 2015年4月2日
 * @author: yuanjs
 * @email: yuanjsh@wuliangroup.cn
 */
public class PagerViewAdapter extends PagerAdapter implements
        OnPageChangeListener {
	private List<String> imageUrls = new ArrayList<String>();
	private Context mContext;
	private ImageLoader mLoader;
	private DisplayMetrics metrics = null;
	private LayoutInflater inflater;
	private CustomViewPager viewPager;
	private String type;
	RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT);

	public PagerViewAdapter(Context mContext, String type, ImageLoader mLoader,
                            CustomViewPager viewPager) {
		this.mContext = mContext;
		this.type = type;
		this.inflater = LayoutInflater.from(mContext);
		this.mLoader = mLoader;
		metrics = getDeviceSize(mContext);
		this.viewPager = viewPager;
		this.viewPager.setOnPageChangeListener(this);
	}

	public void setSourceData(List<String> imageThumbUrls) {
		if (imageUrls != null) {
			imageUrls.clear();
		}
		imageUrls.addAll(imageThumbUrls);
	}

	@Override
	public int getCount() {
		return imageUrls.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == (View) arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
	}

	boolean isFirst = true;// 第一次加载该图片
	RectF initRectF = new RectF();// 图片原始RectF
	boolean flag = false;// true表示pagerView不能切换图片
	ViewGroup container;// 图片容器

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		FrameLayout imageLayout = (FrameLayout) inflater.inflate(
				R.layout.item_pager_image, container, false);
		final PhotoView imageView = (PhotoView) imageLayout
				.findViewById(R.id.image);
		String[] strMetrics = getImageViewMetrics(imageView).split("#");
		int width = 0;
		int height = 0;
		if (strMetrics != null) {
			width = Integer.parseInt(strMetrics[0]);
			height = Integer.parseInt(strMetrics[1]);
		}
		imageView.setMaximumScale(8.0f);// 设置最大放大尺寸
		// imageView.setOnMatrixChangeListener(new MyMatrixChangedListener());
		final ProgressBar spinner = (ProgressBar) imageLayout
				.findViewById(R.id.loading);
		if (imageUrls.size() == 0) {
			imageView.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.device_default));
		} else {
			String dir = imageUrls.get(position);
			imageView.setTag(dir);
			com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage("file://" + dir, imageView,
					new ImageLoadingListener(){
						@Override
						public void onLoadingStarted(String s, View view) {

						}

						@Override
						public void onLoadingFailed(final String s, final View view, FailReason failReason) {
							/**added by huihui 2016-06-29
							 *handle Image can't be decoded imageloader
							 */
							new Handler().postDelayed(new Runnable(){
								public void run() {
									com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(s, (ImageView) view);
//							WulianLog.e("snap path" + s , failReason.toString());
								}
							}, 1000);
						}

						@Override
						public void onLoadingComplete(String s, View view, Bitmap bitmap) {
								spinner.setVisibility(View.GONE);
								imageView.setImageBitmap(bitmap);
						}

						@Override
						public void onLoadingCancelled(String s, View view) {

						}
					});
			container.addView(imageLayout);
		}
		this.container = container;
		return imageLayout;
	}

	/**
	 * @MethodName: setPhotoViewMatrix
	 * @Function: 遍历容器，将里面所有图片尺寸还原
	 * @author: yuanjs
	 * @date: 2015年5月27日
	 * @email: yuanjsh@wuliangroup.cn
	 * @param container
	 *            pagerView容器
	 */
	private void setPhotoViewMatrix(ViewGroup container) {
		int childCount = container.getChildCount();
		if (childCount > 0) {
			for (int i = 0; i < childCount; i++) {
				View view = container.getChildAt(i);
				if (view instanceof FrameLayout) {
					int y = ((FrameLayout) view).getChildCount();
					if (y > 0) {
						for (int j = 0; j < y; j++) {
							View childView = ((FrameLayout) view).getChildAt(j);
							if (childView instanceof PhotoView) {
								((PhotoView) childView).setScale(1.0f);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @ClassName: MyMatrixChangedListener
	 * @Function: 用于监听图片是否经过缩放,缩放的话就不能切换图片
	 * @date: 2015年5月27日
	 * @author: yuanjs
	 * @email: yuanjsh@wuliangroup.cn
	 */
	private class MyMatrixChangedListener implements
			PhotoViewAttacher.OnMatrixChangedListener {
		@Override
		public void onMatrixChanged(RectF arg0) {
			if (isFirst) {
				initRectF.set(arg0);
				isFirst = false;
			}
			if (initRectF != null && initRectF.equals(arg0)) {
				flag = false;
			} else {
				flag = true;
			}
			viewPager.setNoScroll(flag);
		}
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// WulianLog.i("setOnPageChangeListener", "onPageScrollStateChanged");
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// WulianLog.i("setOnPageChangeListener", "onPageScrolled");
	}

	@Override
	public void onPageSelected(int position) {
		if (container != null) {// viewPager.setCurrentItem时，container可能还没好呢。
			setPhotoViewMatrix(container);
		}
			((AlbumPicActivity) mContext).updatePositionAndTitle(position);
	}

	@Override
	public void finishUpdate(ViewGroup container) {
		super.finishUpdate(container);
	}

	/**
	 * @Function 获取屏幕尺寸
	 * @author Wangjj
	 * @date 2014年10月5日
	 * @param context
	 * @return
	 */
	public static DisplayMetrics getDeviceSize(Context context) {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metrics);
		return metrics;
	}

	/**
	 * 根据ImageView获得适当的压缩的宽和高
	 *
	 * @param imageView
	 * @return
	 */
	public static String getImageViewMetrics(ImageView imageView) {
		String imageSize = null;
		final DisplayMetrics displayMetrics = imageView.getContext()
				.getResources().getDisplayMetrics();
		final ViewGroup.LayoutParams params = imageView.getLayoutParams();

		int width = params.width == ViewGroup.LayoutParams.WRAP_CONTENT ? 0 : imageView
				.getWidth(); // Get actual image width
		if (width <= 0)
			width = params.width; // Get layout width parameter
		if (width <= 0)
			width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check
		// maxWidth
		// parameter
		if (width <= 0)
			width = displayMetrics.widthPixels;

		int height = params.height == ViewGroup.LayoutParams.WRAP_CONTENT ? 0 : imageView
				.getHeight(); // Get actual image height
		if (height <= 0)
			height = params.height; // Get layout height parameter
		if (height <= 0)
			height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check
		// maxHeight
		// parameter
		if (height <= 0)
			height = displayMetrics.heightPixels;
		return width + "#" + height;
	}

	/**
	 * 反射获得ImageView设置的最大宽度和高度
	 *
	 * @param object
	 * @param fieldName
	 * @return
	 */
	private static int getImageViewFieldValue(Object object, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = (Integer) field.get(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
		}
		return value;
	}
}
