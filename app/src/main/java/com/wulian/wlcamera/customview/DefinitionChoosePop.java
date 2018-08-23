package com.wulian.wlcamera.customview;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wulian.wlcamera.R;
import com.wulian.wlcamera.bean.DefinitionBean;
import com.wulian.wlcamera.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huxc on 2017/6/8.
 * 清晰度选择popwindow
 */

public class DefinitionChoosePop extends PopupWindow {

    private Context context;
    private ListView mListView;
    private DefinitionListAdapter adapter;
    private OnItemSelectedListener listener;
    private View rootView;

    private int textViewHeight = 28;

    private int selectedValue;

    public interface OnItemSelectedListener {
        void onItemSelected(DefinitionBean bean);
    }

    public DefinitionChoosePop(@NonNull Context context, @NonNull OnItemSelectedListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);
        textViewHeight = DisplayUtil.dip2Pix(context, 28);
        rootView = LayoutInflater.from(context).inflate(R.layout.popupwindow_definition_choose, null);
        setContentView(rootView);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        mListView = (ListView) rootView.findViewById(R.id.listview);
        adapter = new DefinitionListAdapter(context);
        mListView.setAdapter(adapter);
    }

    public void showUpRise(View view, int selectedValue) {
        this.selectedValue = selectedValue;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        int[] anchorPosition = new int[2];
        view.getLocationInWindow(anchorPosition);
        int anchorX = anchorPosition[0];
        int anchorY = anchorPosition[1];
        showAtLocation(view, Gravity.TOP | Gravity.LEFT, anchorX + view.getWidth() / 3, anchorY - textViewHeight * 3);
    }

    class DefinitionListAdapter extends BaseAdapter {

        private List<DefinitionBean> list = new ArrayList<>();

        public DefinitionListAdapter(Context context) {
            list.add(new DefinitionBean(context, 1));
            list.add(new DefinitionBean(context, 2));
            list.add(new DefinitionBean(context, 3));
        }

        public List<DefinitionBean> getData() {
            return list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                TextView textView = new TextView(context);
                textView.setHeight(textViewHeight);
                textView.setBackgroundResource(R.color.black);
                textView.setAlpha(0.5f);
                textView.setTextSize(14);
                textView.setGravity(Gravity.CENTER);
                textView.setMaxLines(1);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int p = (int) v.getTag();
                        DefinitionBean bean = list.get(p);
                        listener.onItemSelected(bean);
                        dismiss();
                    }
                });
                convertView = textView;
            }
            TextView textView = (TextView) convertView;
            textView.setTag(position);
            DefinitionBean bean = list.get(position);
            textView.setText(bean.name);
            if (selectedValue == bean.value) {
                textView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                textView.setTextColor(context.getResources().getColor(R.color.white));
            }
            return convertView;
        }
    }

}
