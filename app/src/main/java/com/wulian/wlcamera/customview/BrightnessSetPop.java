package com.wulian.wlcamera.customview;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.wulian.wlcamera.R;
import com.wulian.wlcamera.utils.DisplayUtil;


/**
 * Created by zbl on 2017/6/9.
 * 亮度选择popwindow
 */

public class BrightnessSetPop extends PopupWindow {

    private Context context;
    private VerticalSeekBar seekbar;
    private OnValueChangedListener listener;
    private View rootView;

    private int viewWidth = 40;
    private int viewHeight = 200;

    private int selectedValue;

    public interface OnValueChangedListener {
        void onValueChanged(int value);

        void onDismiss();
    }

    public BrightnessSetPop(@NonNull Context context, @NonNull final OnValueChangedListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);
        viewWidth = DisplayUtil.dip2Pix(context, 40);
        viewHeight = DisplayUtil.dip2Pix(context, 200);
        rootView = LayoutInflater.from(context).inflate(R.layout.popupwindow_brightness_set, null);
        setContentView(rootView);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        seekbar = (VerticalSeekBar) rootView.findViewById(R.id.seekbar);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                BrightnessSetPop.this.listener.onValueChanged(seekBar.getProgress());
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                BrightnessSetPop.this.listener.onDismiss();
            }
        });
    }

    public void showUpRise(View view, int value) {
        this.selectedValue = value;
        if (seekbar != null) {
            seekbar.setProgress(value);
        }

        int[] anchorPosition = new int[2];
        view.getLocationInWindow(anchorPosition);
        int anchorX = anchorPosition[0];
        int anchorY = anchorPosition[1];
        showAtLocation(view, Gravity.TOP | Gravity.LEFT, anchorX + (view.getWidth() - viewWidth) / 2, anchorY - viewHeight);
    }
}
