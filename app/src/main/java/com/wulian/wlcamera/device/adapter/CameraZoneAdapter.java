package com.wulian.wlcamera.device.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulian.wlcamera.R;
import com.wulian.wlcamera.bean.ZoneBean;
import com.wulian.wlcamera.utils.LanguageUtil;

import java.util.List;


public class CameraZoneAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ZoneBean> sortData;
    private static final String TAG = "CameraZoneAdapter";
    private int selectedPosition = -5; //默认一个参数


    public interface OnItemClickListener {
        void onItemClick(View view, int position, ZoneBean bean);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public CameraZoneAdapter() {
    }

    public void setData(List<ZoneBean> sortData) {
        this.sortData = sortData;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_camera_zone, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof ItemHolder) {
            final ZoneBean bean = sortData.get(viewHolder.getAdapterPosition());
            if(LanguageUtil.isChina()){
                ((ItemHolder) viewHolder).tvAreaName.setText(bean.cn);
            }else{
                ((ItemHolder) viewHolder).tvAreaName.setText(bean.en);
            }
            if (mOnItemClickListener != null) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = viewHolder.getLayoutPosition();
                        mOnItemClickListener.onItemClick(viewHolder.itemView, pos,bean);
                        selectedPosition = position; //选择的position赋值给参数，
                        notifyItemChanged(selectedPosition);//刷新当前点击item
                    }
                });
            }
            viewHolder.itemView.setSelected(selectedPosition == position);
            if (selectedPosition == position) {
                ((ItemHolder) viewHolder).ivChecked.setVisibility(View.VISIBLE);
            } else {
                ((ItemHolder) viewHolder).ivChecked.setVisibility(View.INVISIBLE);
            }
        }

    }

    public ZoneBean get(int position) {
        return sortData == null ? null : sortData.get(position);
    }

    @Override
    public int getItemCount() {
        return sortData == null ? 0 : sortData.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        private TextView tvAreaName;
        private ImageView ivChecked;

        public ItemHolder(View itemView) {
            super(itemView);
            tvAreaName = (TextView) itemView.findViewById(R.id.tv_area_name);
            ivChecked = (ImageView) itemView.findViewById(R.id.iv_checked);
        }
    }

    public void setSelectItem(int selectItem) {
        this.selectedPosition = selectItem;
    }

}
