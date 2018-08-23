package com.wulian.wlcamera.device.album;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wulian.wlcamera.R;
import com.wulian.wlcamera.bean.AlbumEntity;
import com.wulian.wlcamera.customview.PullListView;
import com.wulian.wlcamera.customview.WLDialog;
import com.wulian.wlcamera.device.album.adapter.AlbumAdapterNew;
import com.wulian.wlcamera.device.album.adapter.AlbumGridAdapter;
import com.wulian.wlcamera.utils.AlbumUtils;
import com.wulian.wlcamera.utils.FileUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


/**
 * 作者: chao
 * 时间: 2017/5/5
 * 描述:
 * 联系方式: 805901025@qq.com
 */

public class AlbumActivity extends AppCompatActivity implements AlbumGridAdapter.DeleteListener {

    private WLDialog.Builder builder;
    private WLDialog dialog;
    private PullListView albumListView;
    private TextView tv_album_empty; // 无相册时的视图
    private List<AlbumEntity> albumList = Collections.synchronizedList(new ArrayList<AlbumEntity>()); // 相册集
    private AlbumAdapterNew adapter; // 相册适配器
    private ImageView titlebar_back;
    int totalCount = 0;// 图片数目总和
    private AlbumUtils mAlbumUtil;
    private boolean isRefeshing = false;

    private static final int MODEL_NORMAL = 0;
    private static final int MODEL_DELETE = 1;
    private TextView tv_select;
    private TextView titlebar_title;
    private TextView titlebar_choose;
    private LinearLayout opLayout;
    private int currentModel = MODEL_NORMAL;
    private boolean isDeleteAll = false;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            initAdapter();
        }
    };

    private Handler mSubHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cateye_album);

        mAlbumUtil = new AlbumUtils(this);

        HandlerThread handlerThread = new HandlerThread("readsd");
        handlerThread.start();//创建一个HandlerThread并启动它
        mSubHandler = new Handler(handlerThread.getLooper());

        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initAlbumList();// 刷新最新的数据
    }

    private void initView() {

        tv_album_empty = (TextView) findViewById(R.id.tv_album_empty);
        albumListView = (PullListView) findViewById(R.id.lv_more_album);
        albumListView.setOnRefreshListener(new PullListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                changeCurrentModel(MODEL_NORMAL);
                initAlbumList();
            }
        });

        titlebar_back = (ImageView) findViewById(R.id.titlebar_back);
        titlebar_choose = ((TextView) findViewById(R.id.titlebar_choose));
        titlebar_title = (TextView) findViewById(R.id.titlebar_title);
        opLayout = (LinearLayout) findViewById(R.id.opLayout);
        final TextView select_all = (TextView) findViewById(R.id.select_all);

        select_all.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isDeleteAll) {

                    adapter.clearSelect();
                    select_all.setTextColor(Color.WHITE);

                } else {

                    adapter.selectAll();
                    select_all.setTextColor(Color.parseColor("#0ec0c0"));

                }

                isDeleteAll = !isDeleteAll;

                titlebar_choose.setText(adapter.getSelectsize() + "/"
                        + adapter.getTotal());

            }
        });

        findViewById(R.id.delete_photo).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (adapter.getSelectsize() == 0) {

                            Toast.makeText(AlbumActivity.this,
                                    R.string.CateEye_Album_no_one_select,
                                    Toast.LENGTH_SHORT).show();

                            return;
                        }
                        builder = new WLDialog.Builder(AlbumActivity.this);
                        builder.setMessage(getString(R.string.CateEye_Album_delete_these_photo_confirm))
                                .setCancelOnTouchOutSide(false)
                                .setDismissAfterDone(false)
                                .setPositiveButton(getResources().getString(R.string.Sure))
                                .setNegativeButton(getResources().getString(R.string.Cancel))
                                .setListener(new WLDialog.MessageListener() {
                                    @Override
                                    public void onClickPositive(View view, String msg) {
                                        dialog.dismiss();
                                        adapter.delete();
                                        initAlbumList();
                                        adapter.clearSelect();

                                        onKeyBack();
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
                });

        findViewById(R.id.cancel).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        onKeyBack();
                        adapter.clearSelect();
                        select_all.setTextColor(Color.WHITE);
                    }
                });

        titlebar_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (onKeyBack()){
                    finish();
                }else {
                    adapter.clearSelect();
                    select_all.setTextColor(Color.WHITE);
                }
            }
        });

        tv_select = (TextView) findViewById(R.id.tv_select);
        tv_select.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                changeCurrentModel(MODEL_DELETE);

                titlebar_choose.setText(adapter.getSelectsize() + "/"
                        + adapter.getTotal());

            }
        });

    }

    private void initAlbumList() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return;
        }
        if (isRefeshing) {
            return;
        }
        isRefeshing = true;
//        if (devices != null && devices.size() > 0) {// 确保已经加载了类表
            synchronized (albumList) {
                albumList.clear();
                // 2、不是add、remove，而是新内存空间
                albumList.addAll(mAlbumUtil.getAlbums(FileUtil.getSnapshotPath()));

                removeOthers();
                updateNickNameForAlbum();

                if (albumList != null) {
                    for (AlbumEntity entity : albumList) {
                        loadJpgs(entity);
                    }
                }
            }

            /** 刷新界面 */
            mHandler.sendEmptyMessage(0);
//			initAdapter();
//        }
        isRefeshing = false;
    }

    private void initAdapter() {
        albumListView.onRefreshComplete();
        albumListView.updateRefreshTime();
        if (albumList.size() == 0) {
            // albumListView.setEmptyView(tv_album_empty);// 无法下拉刷新
            tv_album_empty.setVisibility(View.VISIBLE);
        } else {
            tv_album_empty.setVisibility(View.GONE);
        }
        if (adapter == null) {
            adapter = new AlbumAdapterNew(this);
            adapter.setSourceData(albumList);
            albumListView.setAdapter(adapter);
        } else {
            // 步骤：1、清空旧数据->2、获取新数据->3.设置新数据->4、刷新
            adapter.notifyDataSetChanged();// 4、刷新
            // 有个错误没有捕捉到:在子线程通知adapter更新
        }
        adapter.setDeleteListener(this);
    }

    public void loadJpgs(AlbumEntity ae) {
        File[] jpgImgs = new File(ae.getPath()).listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                return filename.toLowerCase(Locale.ENGLISH).endsWith(".jpg");
            }
        });

        // 对图片排序
        Arrays.sort(jpgImgs, new Comparator<File>() {

            @Override
            public int compare(File lhs, File rhs) {
                if (lhs.lastModified() > rhs.lastModified())
                    return -1;// 较新的靠前排
                else if (lhs.lastModified() < rhs.lastModified()) {
                    return 1;
                }
                return 0;
            }
        });

        List<String> pics = new ArrayList<>();

        for (int i = 0; i < jpgImgs.length; i++) {

            pics.add(jpgImgs[i].getPath());

        }

        ae.setPics(pics);

    }

    // 模式切换
    public void changeCurrentModel(int model) {

        currentModel = model;
        if (model == MODEL_NORMAL) {
            tv_select.setVisibility(View.VISIBLE);
            opLayout.setVisibility(View.GONE);
            titlebar_title.setVisibility(View.VISIBLE);
            titlebar_choose.setVisibility(View.INVISIBLE);
            adapter.setModel(false);
        } else if (model == MODEL_DELETE) {
            opLayout.setVisibility(View.VISIBLE);
            tv_select.setVisibility(View.GONE);
            titlebar_title.setVisibility(View.INVISIBLE);
            titlebar_choose.setVisibility(View.VISIBLE);
            adapter.setModel(true);
        }
    }

    /**
     * @Function 移除不在设备列表的相册
     * @author Wangjj
     * @date 2015年6月16日
     */

    public void removeOthers() {
//        ArrayList<Device> devices = new ArrayList<>(MainApplication.getApplication().getWifiDeviceCache().getDevices());
//        if (devices != null && devices.size() > 0) {
//            List<AlbumEntity> albumListOther = new ArrayList<AlbumEntity>();
//            for (AlbumEntity ae : albumList) {
//                if (!isInDevielist(devices, ae.getFileName())) {
//                    albumListOther.add(ae);
//                }
//            }
//            albumList.removeAll(albumListOther);
//        }
    }

//    private boolean isInDevielist(List<Device> devices, String dirName) {
//        for (Device device : devices) {
//            if (device.devID.equalsIgnoreCase(dirName)) {
//                return true;
//            }
//        }
//        return false;
//    }

    private void updateNickNameForAlbum() {
        for (int i = 0, albumSize = albumList.size(); i < albumSize; i++) {// 遍历相册,优化相册名
            updateNickName(albumList.get(i));
        }
    }

    private void updateNickName(AlbumEntity albumEntity) {
//        ArrayList<Device> devices = new ArrayList<>(MainApplication.getApplication().getWifiDeviceCache().getDevices());
//        if (devices != null) {
//            for (Device device : devices) {
//                if (device.devID.equalsIgnoreCase(
//                        albumEntity.getFileName())) {
//                    albumEntity.setDeviceName(device.name);
//                    break;
//                }
//            }
//        }
    }

    public boolean onKeyBack() {

        if (currentModel == MODEL_DELETE) {
            isDeleteAll = false;
            changeCurrentModel(MODEL_NORMAL);
            adapter.clearSelect();
            titlebar_title.setVisibility(View.VISIBLE);
            titlebar_choose.setVisibility(View.INVISIBLE);
            return false;
        }

        return true;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mSubHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onSelectChange(String result) {
        changeCurrentModel(MODEL_DELETE);
        isDeleteAll = false;
        titlebar_choose.setText(adapter.getSelectsize() + "/"
                + adapter.getTotal());
    }
}
