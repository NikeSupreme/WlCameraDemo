/**
 * Project Name:  iCam
 * File Name:     AlbumPicActivity.java
 * Package Name:  com.wulian.icam.view.album
 * @Date:         2015年3月27日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.wlcamera.device.album;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.wulian.wlcamera.BaseTitleActivity;
import com.wulian.wlcamera.R;
import com.wulian.wlcamera.bean.AlbumEntity;
import com.wulian.wlcamera.customview.CustomViewPager;
import com.wulian.wlcamera.customview.ProgressDialogManager;
import com.wulian.wlcamera.customview.WLDialog;
import com.wulian.wlcamera.device.album.adapter.PagerViewAdapter;
import com.wulian.wlcamera.tools.ImageLoader;
import com.wulian.wlcamera.utils.AlbumUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * @ClassName: AlbumPicActivity
 * @Function: 相册左右滑动预览
 * @Date: 2015年3月27日
 * @author: yuanjs
 * @email: yuanjsh@wuliangroup.cn
 */
public class AlbumPicActivity extends BaseTitleActivity implements OnClickListener {
	private WLDialog.Builder builder;
	private WLDialog dialog;
	private CustomViewPager viewPager;
	private AlbumEntity albumEntity;
	private int targetPositon;
	private PagerViewAdapter albumAdapter;
	private TextView album_count;
	private int currentPostion;// 当前显示的图片位置
	private ImageLoader mLoader;
	private List<String> allJpgFilePathList; // 当前文件夹下所有图片路径
	private AlbumUtils mAlbumUtil;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:// 加载结束
				updatePositionAndTitle(targetPositon);
				break;
			case 1:// 删除照片
				int position = (int) msg.obj;// 刚刚删除的位置
				int newSize = allJpgFilePathList.size();
				if (newSize > 0) {
					if (position == newSize)// 刚删除的是尾部照片
						updatePositionAndTitle(position - 1);// 最后下标相应少一个
					else
						updatePositionAndTitle(position);
				} else {
					AlbumPicActivity.this.finish();
				}
				ProgressDialogManager.getDialogManager().dimissDialog("DELETE_PHOTO", 0);
				break;
			default:
				break;
			}
			initAdapter();
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_album_pic, true);
		getAllDir();
	}

	private void initAdapter() {
		if (allJpgFilePathList != null && allJpgFilePathList.size() > 0) {
			if (albumAdapter == null) {
				albumAdapter = new PagerViewAdapter(this, null, mLoader, viewPager);
				albumAdapter.setSourceData(allJpgFilePathList);
				viewPager.setAdapter(albumAdapter);
				viewPager.setCurrentItem(targetPositon, true);
			} else {
				albumAdapter = (PagerViewAdapter) viewPager.getAdapter();
				albumAdapter.setSourceData(allJpgFilePathList);
				albumAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	protected void initData() {
		super.initData();
		viewPager.setPageMargin(this.getResources().getDimensionPixelSize(R.dimen.default_edittext_height));
		mAlbumUtil = new AlbumUtils(this);
		albumEntity = (AlbumEntity) getIntent().getExtras().getSerializable("AlbumEntity");
		targetPositon = getIntent().getIntExtra("position", 0);
		if (albumEntity.getDeviceName() != null) {
			setToolBarTitleAndRightImg(albumEntity.getDeviceName(), R.drawable.ic_delete);
		} else {
			setToolBarTitleAndRightImg(albumEntity.getFileName(), R.drawable.ic_delete);
		}
		mLoader = new ImageLoader(this);
	}

	@Override
	protected void initView() {
		super.initView();
		viewPager = (CustomViewPager) this.findViewById(R.id.album_pic);
		album_count = (TextView) this.findViewById(R.id.album_count);

	}

	@Override
	protected void initListeners() {
		super.initListeners();
		getImgRight().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDeleteDialog();
			}
		});
	}

	/**
	 * @MethodName: deletePic
	 * @Function: 删除当前图片
	 * @author: yuanjs
	 * @date: 2015年4月2日
	 * @email: yuanjsh@wuliangroup.cn
	 * @param position
	 */
	private void deletePic(final int position) {
		ProgressDialogManager.getDialogManager().showDialog("DELETE_PHOTO", this, null, null, getResources().getInteger(R.integer.http_timeout));
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 删除缓存、文件、列表
				mLoader.deleteBitmapFromMemCache(allJpgFilePathList.get(position));
				mAlbumUtil.deletePicByPath(allJpgFilePathList.get(position));
				allJpgFilePathList.remove(position);
				Message msg = mHandler.obtainMessage();
				msg.what = 1;
				msg.obj = position;
				mHandler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * @MethodName: getAllDir
	 * @Function: 初始化该文件夹下所有图片路径
	 * @author: yuanjs
	 * @date: 2015年4月1日
	 * @email: yuanjsh@wuliangroup.cn
	 */
	private void getAllDir() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (allJpgFilePathList != null) {
					allJpgFilePathList.clear();
				}
				allJpgFilePathList = mAlbumUtil.loadJpgs(albumEntity.getPath());
				mHandler.sendEmptyMessage(0);
			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLoader.cancelTask();
		mLoader.deletAllBitmapFromMemCache();
	}

	public void updatePositionAndTitle(int positon) {
		int size = allJpgFilePathList.size();
		long time = new File(allJpgFilePathList.get(positon)).lastModified();
		String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).format(new Date(time));
		album_count.setText(positon + 1 + "/" + size);
		currentPostion = positon;
	}

	private void showDeleteDialog() {
		builder = new WLDialog.Builder(AlbumPicActivity.this);
		builder.setMessage(getString(R.string.CateEye_Album_delete_this_photo_confirm))
				.setCancelOnTouchOutSide(false)
				.setDismissAfterDone(false)
				.setPositiveButton(getResources().getString(R.string.Sure))
				.setNegativeButton(getResources().getString(R.string.Cancel))
				.setListener(new WLDialog.MessageListener() {
					@Override
					public void onClickPositive(View view, String msg) {
						dialog.dismiss();
						deletePic(currentPostion);
					}

					@Override
					public void onClickNegative(View view) {
						dialog.dismiss();
					}
				});
		dialog = builder.create();
		if (!dialog.isShowing()) {
			dialog.show();
		}

	}

}
