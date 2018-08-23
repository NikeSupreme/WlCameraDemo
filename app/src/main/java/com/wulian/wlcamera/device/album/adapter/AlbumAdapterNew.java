/**
 * Project Name:  Z_BitmapfunTest
 * File Name:     ImageListViewAdapter.java
 * Package Name:  com.test.bitmap
 *
 * @Date: 2015年3月30日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.wlcamera.device.album.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.wulian.wlcamera.R;
import com.wulian.wlcamera.bean.AlbumEntity;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AlbumAdapterNew extends BaseAdapter {
    private Context mContext;
    private List<AlbumEntity> albumList;
    private LayoutInflater inflater;
    private Map<AlbumEntity, AlbumGridAdapter> map = new HashMap<>();
    private AlbumGridAdapter.DeleteListener deleteListener;

    // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
    // Locale.ENGLISH);
    // SimpleDateFormat df1 = new SimpleDateFormat("MM-dd", Locale.ENGLISH);
    // SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

    public AlbumAdapterNew(Context mContext) {
        this.mContext = mContext;

        inflater = LayoutInflater.from(mContext);
        // mListView.setOnScrollListener(this);//与pulllistview事件冲突
    }

    public void setSourceData(List<AlbumEntity> albumEntities) {
        map.clear();
        this.albumList = albumEntities;
    }

    @Override
    public int getCount() {
        return albumList == null ? 0 : albumList.size();
    }

    @Override
    public Object getItem(int position) {
        return albumList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_album_grid, null);
            viewHolder = new ViewHolder();
            viewHolder.item_tv = (TextView) convertView.findViewById(R.id.item_tv);
            viewHolder.item_gv = (GridView) convertView.findViewById(R.id.item_gv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        AlbumEntity entity = albumList.get(position);

        String name = entity.getDeviceName();
        if (!TextUtils.isEmpty(name)) {
            viewHolder.item_tv.setText(name);
        } else {
            viewHolder.item_tv.setText(entity.getFileName());
        }

        AlbumGridAdapter adapter = new AlbumGridAdapter(mContext, entity, false);//getCount() == 1
        adapter.setDeleteListener(deleteListener);
        map.put(entity, adapter);


        viewHolder.item_gv.setAdapter(adapter);

        return convertView;
    }

    public void clearSelect() {
        Iterator<Map.Entry<AlbumEntity, AlbumGridAdapter>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<AlbumEntity, AlbumGridAdapter> entry = it.next();
            AlbumGridAdapter value = entry.getValue();
            value.clearSelect();
        }
    }

    public void selectAll() {
        Iterator<Map.Entry<AlbumEntity, AlbumGridAdapter>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<AlbumEntity, AlbumGridAdapter> entry = it.next();
            AlbumGridAdapter value = entry.getValue();
            value.selectAll();
        }
    }

    public int getSelectsize() {
        int size = 0;
        Iterator<Map.Entry<AlbumEntity, AlbumGridAdapter>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<AlbumEntity, AlbumGridAdapter> entry = it.next();
            AlbumGridAdapter value = entry.getValue();
            size += value.getSelectMap().size();
        }
        return size;
    }

    public int getTotal() {
        int count = 0;
        Iterator<Map.Entry<AlbumEntity, AlbumGridAdapter>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<AlbumEntity, AlbumGridAdapter> entry = it.next();
            AlbumGridAdapter value = entry.getValue();
            count += value.getSize();
        }
        return count;
    }

    public void delete() {

        Iterator<Map.Entry<AlbumEntity, AlbumGridAdapter>> iterator1 = map.entrySet().iterator();
        while (iterator1.hasNext()) {
            Map.Entry<AlbumEntity, AlbumGridAdapter> entry1 = iterator1.next();
            AlbumGridAdapter value1 = entry1.getValue();

            Iterator<Map.Entry<String, String>> iterator2 = value1.getSelectMap().entrySet().iterator();
            while (iterator2.hasNext()) {
                Map.Entry<String, String> entry2 = iterator2.next();
                String path = entry2.getValue();
                File file = new File(path);
                if (file != null && file.exists()) {
                    file.delete();
                }
            }
        }
    }

    public void setModel(boolean model) {

        Iterator<Map.Entry<AlbumEntity, AlbumGridAdapter>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<AlbumEntity, AlbumGridAdapter> entry = iterator.next();
            AlbumGridAdapter value = entry.getValue();
            value.setModel(model);
        }
    }

    public void setDeleteListener(AlbumGridAdapter.DeleteListener deleteListener) {

        this.deleteListener = deleteListener;
    }

    class ViewHolder {
        TextView item_tv;
        GridView item_gv;
    }

}
