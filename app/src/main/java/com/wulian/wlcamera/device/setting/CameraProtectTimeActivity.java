package com.wulian.wlcamera.device.setting;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wulian.wlcamera.BaseTitleActivity;
import com.wulian.wlcamera.R;
import com.wulian.wlcamera.customview.wheel.NumericWheelAdapter;
import com.wulian.wlcamera.customview.wheel.WheelView;
import com.wulian.wlcamera.utils.CameraUtil;

import java.util.Calendar;
import java.util.Locale;


/**
 * Created by hxc on 2017/6/8.
 * func:防护时间界面
 */

public class CameraProtectTimeActivity extends BaseTitleActivity implements View.OnClickListener {
    private ImageView ivAlldayYes;
    private LinearLayout llAllday;
    private ImageView ivDayYes;
    private LinearLayout llDay;
    private ImageView ivNightYes;
    private LinearLayout llNight;
    private TextView tvUserDefined;
    private ImageView ivUserDefine;
    private LinearLayout llUserDefined;
    private CheckBox cbWorkday;
    private Button btnSure;

    public static final String TIME_ALL_DAY = "00,00,23,59";
    public static final String TIME_DAY = "08,00,20,00";
    public static final String TIME_NIGHT = "20,00,08,00";

    public static final String DAY_EVERY = "7,1,2,3,4,5,6,";
    public static final String DAY_WORKDAY = "1,2,3,4,5,";

    private static final int NONE = 0;
    private static final int ALLDAY = 1;
    private static final int DAY = 2;
    private static final int NIGHT = 3;
    private static final int USER_DEFINED = 4;
    private static final int EVERY = 1;
    private static final int WORKDAY = 2;

    private String moveTime;
    private String moveWeekday;
    private int moveTimeType = NONE;
    private int moveWeekdayType = EVERY;

    private Dialog mTimePeriodDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_protect_time, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getResources().getString(R.string.Protective_Time));
    }

    @Override
    protected void initView() {
        super.initView();
        ivAlldayYes = (ImageView) findViewById(R.id.iv_allday_yes);
        ivDayYes = (ImageView) findViewById(R.id.iv_day_yes);
        ivNightYes = (ImageView) findViewById(R.id.iv_night_yes);
        ivUserDefine = (ImageView) findViewById(R.id.iv_user_define);
        llAllday = (LinearLayout) findViewById(R.id.ll_allday);
        llDay = (LinearLayout) findViewById(R.id.ll_day);
        llNight = (LinearLayout) findViewById(R.id.ll_night);
        llUserDefined = (LinearLayout) findViewById(R.id.ll_user_defined);
        cbWorkday = (CheckBox) findViewById(R.id.cb_workday);
        tvUserDefined = (TextView) findViewById(R.id.tv_user_defined);
        btnSure = (Button) findViewById(R.id.btn_sure);
    }


    @Override
    protected void initData() {
        super.initData();
        moveTime = getIntent().getStringExtra("time");
        moveWeekday = getIntent().getStringExtra("weekday");
        if (TextUtils.isEmpty(moveTime)) {
            moveTimeType = ALLDAY;
            moveTime = TIME_ALL_DAY;
        } else if (moveTime.equalsIgnoreCase(TIME_ALL_DAY)) {
            moveTimeType = ALLDAY;
        } else if (moveTime.equalsIgnoreCase(TIME_DAY)) {
            moveTimeType = DAY;
        } else if (moveTime.equalsIgnoreCase(TIME_NIGHT)) {
            moveTimeType = NIGHT;
        } else {
            moveTimeType = USER_DEFINED;
        }

        seleteItem();

        if (TextUtils.isEmpty(moveWeekday)) {
            moveWeekdayType = EVERY;
        } else if (moveWeekday.equalsIgnoreCase(DAY_WORKDAY)) {
            moveWeekdayType = WORKDAY;
        } else {
            moveWeekdayType = EVERY;
        }

        if (moveWeekdayType == EVERY)
            cbWorkday.setChecked(false);
        else
            cbWorkday.setChecked(true);

    }


    @Override
    protected void initListeners() {
        super.initListeners();
        ivAlldayYes.setOnClickListener(this);
        llAllday.setOnClickListener(this);
        ivDayYes.setOnClickListener(this);
        llDay.setOnClickListener(this);
        ivNightYes.setOnClickListener(this);
        llNight.setOnClickListener(this);
        tvUserDefined.setOnClickListener(this);
        ivUserDefine.setOnClickListener(this);
        llUserDefined.setOnClickListener(this);
        cbWorkday.setOnClickListener(this);
        btnSure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_allday:
                moveTimeType = ALLDAY;
                moveTime = TIME_ALL_DAY;
                seleteItem();
                break;
            case R.id.ll_day:
                moveTimeType = DAY;
                moveTime = TIME_DAY;
                seleteItem();
                break;
            case R.id.ll_night:
                moveTimeType = NIGHT;
                moveTime = TIME_NIGHT;
                seleteItem();
                break;
            case R.id.ll_user_defined:
                showPopwindow(moveTime);
                break;
            case R.id.btn_sure:
                if (cbWorkday.isChecked())
                    moveWeekday = DAY_WORKDAY;
                else
                    moveWeekday = DAY_EVERY;
                Intent it = new Intent();
                it.putExtra("time", moveTime);
                it.putExtra("weekday", moveWeekday);
                setResult(RESULT_OK, it);
                finish();
                break;
        }
    }

    private void seleteItem() {
        llAllday.setBackgroundResource(R.drawable.bg_timeset_normal);
        llDay.setBackgroundResource(R.drawable.bg_timeset_normal);
        llNight.setBackgroundResource(R.drawable.bg_timeset_normal);
        llUserDefined.setBackgroundResource(R.drawable.bg_timeset_normal);

        ivAlldayYes.setVisibility(View.GONE);
        ivDayYes.setVisibility(View.GONE);
        ivNightYes.setVisibility(View.GONE);
        ivUserDefine.setVisibility(View.GONE);

        switch (moveTimeType) {
            case ALLDAY:
                ivAlldayYes.setVisibility(View.VISIBLE);
                break;
            case DAY:
                ivDayYes.setVisibility(View.VISIBLE);
                break;
            case NIGHT:
                ivNightYes.setVisibility(View.VISIBLE);
                break;
            case USER_DEFINED:
                ivUserDefine.setVisibility(View.VISIBLE);
                tvUserDefined.setText(CameraUtil.convertTime(this,moveTime));
                break;
        }
    }


    /**
     * 显示popupWindow
     */
    private void showPopwindow(String defaultTime) {
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.custom_common_timeperiod_alertdialog, null);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setFocusable(true);
        TextView btnCancel = (TextView) contentView.findViewById(R.id.tvCancel);
        TextView btnSure = (TextView) contentView.findViewById(R.id.tv_Sure);
        final WheelView start_time_hour = (WheelView) contentView
                .findViewById(R.id.start_time_hour);
        final WheelView start_time_min = (WheelView) contentView
                .findViewById(R.id.start_time_min);
        final WheelView end_time_hour = (WheelView) contentView
                .findViewById(R.id.end_time_hour);
        final WheelView end_time_min = (WheelView) contentView
                .findViewById(R.id.end_time_min);

        start_time_hour.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
        start_time_min.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        end_time_hour.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
        end_time_min.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        Calendar calender = Calendar.getInstance(Locale.getDefault());
        start_time_hour.setCurrentItem(calender.get(Calendar.HOUR_OF_DAY));
        start_time_min.setCurrentItem(calender.get(Calendar.MINUTE));
        end_time_hour.setCurrentItem(calender.get(Calendar.HOUR_OF_DAY));
        end_time_min.setCurrentItem(calender.get(Calendar.MINUTE));
        start_time_hour.setCyclic(true);
        start_time_min.setCyclic(true);
        end_time_hour.setCyclic(true);
        end_time_min.setCyclic(true);

        if (!TextUtils.isEmpty(defaultTime)) {
            String timeNum[] = defaultTime.split(",");
            if (timeNum.length == 4) {
                start_time_hour.setCurrentItem(Integer.parseInt(timeNum[0]),
                        false);
                start_time_min.setCurrentItem(Integer.parseInt(timeNum[1]),
                        false);
                end_time_hour.setCurrentItem(Integer.parseInt(timeNum[2]),
                        false);
                end_time_min
                        .setCurrentItem(Integer.parseInt(timeNum[3]), false);
            }
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = start_time_hour.getCurrentItem() + ","
                        + start_time_min.getCurrentItem() + ","
                        + end_time_hour.getCurrentItem() + ","
                        + end_time_min.getCurrentItem();
                String time[] = result.split(",");
                moveTime = result;
                moveTimeType = USER_DEFINED;
                seleteItem();
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(contentView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }
}
