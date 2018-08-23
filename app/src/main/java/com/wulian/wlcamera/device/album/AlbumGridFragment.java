package com.wulian.wlcamera.device.album;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wulian.wlcamera.R;
import com.wulian.wlcamera.WLFragment;
import com.wulian.wlcamera.bean.AlbumEntity;
import com.wulian.wlcamera.customview.WLDialog;
import com.wulian.wlcamera.device.album.adapter.ImageGridAdapter;
import com.wulian.wlcamera.utils.FileUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * 作者: chao
 * 时间: 2017/5/5
 * 描述: 相册展开页面
 * 联系方式: 805901025@qq.com
 */

public class AlbumGridFragment extends WLFragment implements
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    private WLDialog.Builder builder;
    private WLDialog dialog;
    private static final int MODEL_NORMAL = 0;
    private static final int MODEL_DELETE = 1;
    private TextView tv_album_empty; // 无相册时的视图
    private TextView select_all;
    private TextView delete_photo;
    private RelativeLayout opLayout;
    private GridView mGridView;
    private ImageGridAdapter adapter;
    private String[] mSnapShot;
    private String fileName;
    private AlbumEntity mAlbumEntity;
    private List<ImageItem> dataList = new ArrayList<>();
    private int currentModel = MODEL_NORMAL;
    private boolean isDeleteAll = false;
    private Map<String, Integer> mHeaderIdMap = new HashMap<String, Integer>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private int mHeaderId = 1;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            initAdapter();
        }
    };
    /**
     * @param path
     * @Function 加载所有jpg图片
     * @author Wangjj
     * @date 2015年6月23日
     */

    public void loadJpgs(String path) {
        File albumDir = new File(path);
        if (!albumDir.exists()) {
            albumDir.mkdirs();
        }
        File[] jpgImgs = new File(path).listFiles(new FilenameFilter() {
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
        mSnapShot = new String[jpgImgs.length];
        dataList.clear();
        mHeaderIdMap.clear();
        mHeaderId = 1;

        for (int i = 0; i < jpgImgs.length; i++) {
            File tmp = jpgImgs[i];
            mSnapShot[i] = tmp.getAbsolutePath();
            ImageItem imageItem = new ImageItem();
            imageItem.imagePath = tmp.getAbsolutePath();
            imageItem.time = dateFormat.format(new Date(tmp.lastModified()));
            dataList.add(generateHeaderId(imageItem));
        }

    }

    private ImageItem generateHeaderId(ImageItem item) {

        String ymd = item.time;
        if (!mHeaderIdMap.containsKey(ymd)) {
            item.headerId = mHeaderId;
            mHeaderIdMap.put(ymd, mHeaderId);
            mHeaderId++;
        } else {
            item.headerId = mHeaderIdMap.get(ymd);
        }

        return item;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int layoutResID() {
        return R.layout.fragment_cateye_album;
    }

    @Override
    protected void initTitle(View v) {
        super.initTitle(v);
        setLeftImageAndEvent(R.drawable.icon_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onKeyBack();

                AlbumGridFragment.this.getActivity().finish();
            }
        });
        mTvTitle.setText(getString(R.string.CateEye_Album_Tittle));
        setRightText(getString(R.string.CateEye_Album_Tittle_Right)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCurrentModel(MODEL_DELETE);

                setLeftText(adapter.getSelectMap().size() + "/"
                        + adapter.getCount());
            }
        });
    }

    @Override
    public void initView(View view) {
        select_all = (TextView) view.findViewById(R.id.select_all);
        delete_photo = (TextView) view.findViewById(R.id.delete_photo);
        opLayout = (RelativeLayout) view.findViewById(R.id.opLayout);
        tv_album_empty = (TextView) view.findViewById(R.id.tv_album_empty);
//        titlebar_title.setText(mAlbumEntity.getDeviceName());
        mGridView = (GridView) view.findViewById(R.id.gridView);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new ImageGridAdapter(getActivity(), dataList);
        mGridView.setAdapter(adapter);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mGridView.setOnItemClickListener(this);
        mGridView.setOnItemLongClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        String devId = getActivity().getIntent().getStringExtra("devId");
        if (TextUtils.isEmpty(devId)){
            return;
        }
        fileName = FileUtil.getSnapshotPath() + "/" + devId;
        mAlbumEntity = new AlbumEntity();
        mAlbumEntity.setPath(fileName);
//        updateNickName(mAlbumEntity);
    }

    @Override
    public void initListener() {
        super.initListener();
        select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isDeleteAll) {

                    adapter.clearSelect();
                    select_all.setTextColor(Color.WHITE);

                } else {

                    adapter.selectAll();
                    select_all.setTextColor(Color.parseColor("#8dd652"));

                }

                isDeleteAll = !isDeleteAll;

                setLeftText(adapter.getSelectMap().size() + "/"
                        + adapter.getCount());

            }
        });

        delete_photo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final Map<String, ImageItem> selectMap = adapter.getSelectMap();

                if (selectMap.size() == 0) {
                    Toast.makeText(getActivity(), R.string.CateEye_Album_no_one_select,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                builder = new WLDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.CateEye_Album_delete_these_photo_confirm))
                        .setCancelOnTouchOutSide(false)
                        .setDismissAfterDone(false)
                        .setPositiveButton(getResources().getString(R.string.Sure))
                        .setNegativeButton(getResources().getString(R.string.Cancel))
                        .setListener(new WLDialog.MessageListener() {
                            @Override
                            public void onClickPositive(View view, String msg) {
                                dialog.dismiss();
                                List<ImageItem> selectList = new ArrayList<ImageItem>();
                                Iterator<Map.Entry<String, ImageItem>> it = selectMap
                                        .entrySet().iterator();
                                while (it.hasNext()) {
                                    Map.Entry<String, ImageItem> entry = it
                                            .next();
                                    ImageItem key = entry.getValue();
                                    selectList.add(key);
                                }

                                for (ImageItem item : selectList) {
                                    File file = new File(item.imagePath);
                                    if (file != null && file.exists()) {
                                        file.delete();
                                    }
                                }
                                dataList.clear();
                                loadJpgs(fileName);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        loadJpgs(fileName);
        adapter.setDataList(dataList);
        initAdapter();
        // if (mSnapShot.length == 0) {
        // AlbumGridFragment.this.getActivity().finish();
        // }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

        ImageItem item = dataList.get(position);

        if (currentModel == MODEL_DELETE) {

            isDeleteAll = false;
            adapter.check(item, !adapter.getImageItemCheckStatus(item));
            mHandler.sendEmptyMessage(0);
            setLeftText(adapter.getSelectMap().size() + "/"
                    + adapter.getCount());

            return;
        }

        final Intent i = new Intent(getActivity(), AlbumPicActivity.class);
        i.putExtra("AlbumEntity", mAlbumEntity);
        i.putExtra("position", position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // makeThumbnailScaleUpAnimation() looks kind of ugly here as
            // the
            // loading spinner may
            // show plus the thumbnail image in GridView is cropped. so
            // using
            // makeScaleUpAnimation() instead.
            ActivityOptions options = ActivityOptions.makeScaleUpAnimation(v,
                    0, 0, v.getWidth(), v.getHeight());
            getActivity().startActivity(i, options.toBundle());
        } else {
            startActivity(i);
        }

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {

        changeCurrentModel(MODEL_DELETE);

        ImageItem item = dataList.get(position);

        adapter.check(item, !adapter.getImageItemCheckStatus(item));
        mHandler.sendEmptyMessage(0);
        setLeftText(adapter.getSelectMap().size() + "/"
                + adapter.getCount());

        return true;
    }

    // 模式切换
    public void changeCurrentModel(int model) {
        currentModel = model;
        if (model == MODEL_NORMAL) {
            setRightText(getString(R.string.CateEye_Album_Tittle_Right)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeCurrentModel(MODEL_DELETE);

                    setLeftText(adapter.getSelectMap().size() + "/"
                            + adapter.getCount());
                }
            });
            mTvTitle.setVisibility(View.VISIBLE);
            setLeftTextGone();
            opLayout.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.slide_out_bottom));
            opLayout.setVisibility(View.GONE);
        } else if (model == MODEL_DELETE) {
            setRightText(getString(R.string.Cancel)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onKeyBack();
                    adapter.clearSelect();
                    select_all.setTextColor(Color.WHITE);
                }
            });
            mTvTitle.setVisibility(View.INVISIBLE);
            setLeftText(adapter.getSelectMap().size() + "/"
                    + adapter.getCount());
            opLayout.setVisibility(View.VISIBLE);
            opLayout.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.slide_in_bottom));
        }

    }

    public boolean onKeyBack() {

        if (currentModel == MODEL_DELETE) {
            isDeleteAll = false;
            changeCurrentModel(MODEL_NORMAL);
            adapter.clearSelect();
            mHandler.sendEmptyMessage(0);
            mTvTitle.setVisibility(View.VISIBLE);
            setLeftTextGone();
            return false;
        }

        return true;

    }

//    private void updateNickName(AlbumEntity albumEntity) {
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
//    }

    private void initAdapter() {
        adapter.notifyDataSetChanged();
        if (dataList.size() == 0) {
            // albumListView.setEmptyView(tv_album_empty);// 无法下拉刷新
            tv_album_empty.setVisibility(View.VISIBLE);
        } else {
            tv_album_empty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

}
