package com.wulian.wlcamera.device.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wulian.sdk.android.ipc.rtcv2.IPCMsgController;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCcameraXmlMsgEvent;
import com.wulian.wlcamera.BaseTitleActivity;
import com.wulian.wlcamera.R;
import com.wulian.wlcamera.bean.ICamDeviceBean;
import com.wulian.wlcamera.customview.ProgressDialogManager;
import com.wulian.wlcamera.tools.IcamMsgEventHandler;
import com.wulian.wlcamera.utils.CameraUtil;
import com.wulian.wlcamera.utils.ConfigUtil;
import com.wulian.wlcamera.utils.ConstantsUtil;
import com.wulian.wlcamera.utils.ToastUtil;
import com.wulian.wlcamera.utils.WLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by hxc on 2017/6/7.
 * func:安全防护设置界面
 */

public class CameraProtectSettingActivity extends BaseTitleActivity implements IcamMsgEventHandler, View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private String spMoveArea;
    private int spMoveSensitivity;
    private String spMoveWeekday;
    private String spMoveTime;
    private TextView tvProtectTime;
    private TextView tvProtectArea;
    private SeekBar sbMove;
    private Button btnStartProtect;

    private String deviceId;
    private String sipDomain;
    public static final String DAY_EVERY = "7,1,2,3,4,5,6,";
    public static final String DAY_WORKDAY = "1,2,3,4,5,";
    public static final String TIME_ALL_DAY = "00,00,23,59";
    public static final String TIME_DAY = "08,00,20,00";
    public static final String TIME_NIGHT = "20,00,08,00";
    public static final int REQUESTCODE_MOVE_TIME = 1;
    public static final int REQUESTCODE_MOVE_AREA = 2;
    public static final int REQUESTCODE_COVER_TIME = 3;
    public static final int REQUESTCODE_COVER_AREA = 4;
    public static final String START_PROTECT = "START_PROTECT";
    private int sipokcount = 0;

    private ICamDeviceBean iCamDeviceBean;
    private SharedPreferences sp;
    private RelativeLayout rlProtectTime;
    private RelativeLayout rlProtectArea;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_protect_setting, true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitle(getResources().getString(R.string.Safety_Fortification));
    }

    @Override
    protected void initView() {
        super.initView();
        tvProtectTime = (TextView) findViewById(R.id.tv_protect_time);
        tvProtectArea = (TextView) findViewById(R.id.tv_protect_area);
        sbMove = (SeekBar) findViewById(R.id.sb_move);
        btnStartProtect = (Button) findViewById(R.id.btn_start_protect);
        rlProtectTime = (RelativeLayout) findViewById(R.id.rl_protect_time);
        rlProtectArea = (RelativeLayout) findViewById(R.id.rl_protect_area);
    }

    @Override
    protected void initData() {
        super.initData();
        iCamDeviceBean = (ICamDeviceBean) getIntent().getSerializableExtra("ICamDeviceBean");
        deviceId = iCamDeviceBean.deviceId;
        sipDomain = iCamDeviceBean.deviceDomain;
        sp = getSharedPreferences(ConfigUtil.SP_CONFIG, MODE_PRIVATE);
        initMoveData();
        updateView();
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        btnStartProtect.setOnClickListener(this);
        sbMove.setOnSeekBarChangeListener(this);
        rlProtectTime.setOnClickListener(this);
        rlProtectArea.setOnClickListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int id = seekBar.getId();
        if (id == R.id.sb_move) {
            spMoveSensitivity = seekBar.getProgress();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_start_protect:
                String weekdayMove[] = spMoveWeekday.split(",");
                String timeMove[] = spMoveTime.split(",");
                // 时间跨夜，进行分割
                try {
                    if ((Integer.parseInt(timeMove[0]) > Integer.parseInt(timeMove[2]))
                            || ((Integer.parseInt(timeMove[0]) == Integer
                            .parseInt(timeMove[2])) && Integer
                            .parseInt(timeMove[1]) > Integer
                            .parseInt(timeMove[3]))) {
                        timeMove = new String[]{timeMove[0], timeMove[1], "23", "59",
                                "0", "0", timeMove[2], timeMove[3]};
                    }
                    String areaMove[] = spMoveArea.split(";");
                    // 时间、星期，区域
                    if (timeMove.length <= 0) {
                        Toast.makeText(this, R.string.Set_Protection_Time, Toast.LENGTH_SHORT).show();
                        return;

                    }
                    if (weekdayMove.length <= 0) {
                        Toast.makeText(this, "请设置移动布防星期", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (areaMove.length <= 0) {
                        Toast.makeText(this, R.string.Set_Protection_Area, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    IPCMsgController.MsgConfigMovementDetection(
                            deviceId, sipDomain, true,
                            sbMove.getProgress(), areaMove);
                    String[] strWeek = new String[weekdayMove.length];
                    for (int i = 0; i < weekdayMove.length; i++) {
                        StringBuilder sbWeek = new StringBuilder(weekdayMove[i]);
                        if (timeMove.length == 4) {
                            sbWeek.append(",");
                            CameraUtil.formatSingleNum(timeMove);// 补零
                            sbWeek.append(timeMove[0]);
                            sbWeek.append(":");
                            sbWeek.append(timeMove[1]);
                            sbWeek.append("-");
                            sbWeek.append(timeMove[2]);
                            sbWeek.append(":");
                            sbWeek.append(timeMove[3]);
                        } else if (timeMove.length == 8) {
                            sbWeek.append(",");
                            CameraUtil.formatSingleNum(timeMove);// 补零
                            sbWeek.append(timeMove[0]);
                            sbWeek.append(":");
                            sbWeek.append(timeMove[1]);
                            sbWeek.append("-");
                            sbWeek.append(timeMove[2]);
                            sbWeek.append(":");
                            sbWeek.append(timeMove[3]);
                            sbWeek.append(",");
                            sbWeek.append(timeMove[4]);
                            sbWeek.append(":");
                            sbWeek.append(timeMove[5]);
                            sbWeek.append("-");
                            sbWeek.append(timeMove[6]);
                            sbWeek.append(":");
                            sbWeek.append(timeMove[7]);
                        }
                        strWeek[i] = sbWeek.toString();
                    }
                    IPCMsgController.MsgConfigLinkageArming(deviceId,
                            sipDomain, true, strWeek);
                    ProgressDialogManager.getDialogManager().showDialog(START_PROTECT, this, null, null, 10000);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.rl_protect_time:
                startActivityForResult(
                        (new Intent(this, CameraProtectTimeActivity.class)).putExtra(
                                "time", spMoveTime).putExtra("weekday",
                                spMoveWeekday), REQUESTCODE_MOVE_TIME);
                break;
            case R.id.rl_protect_area:
                startActivityForResult(
                        (new Intent(this, CameraProtectAreaActivity.class))
                                .putExtra("type", REQUESTCODE_MOVE_AREA)
                                .putExtra("area", spMoveArea)
                                .putExtra("ICamDeviceBean", iCamDeviceBean), REQUESTCODE_MOVE_AREA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUESTCODE_MOVE_TIME:
                if (resultCode == RESULT_OK) {
                    spMoveWeekday = data.getStringExtra("weekday");
                    spMoveTime = data.getStringExtra("time");
                    updateView();
                }
                break;
            case REQUESTCODE_MOVE_AREA:
                if (resultCode == RESULT_OK) {
                    spMoveArea = data.getStringExtra("area");
                    updateView();
                }
                break;
            default:
                break;
        }
    }

    private void initMoveData() {
        spMoveArea = sp.getString(deviceId + ConfigUtil.MOVE_AREA,
                ";");
        spMoveSensitivity = sp.getInt(deviceId
                + ConfigUtil.MOVE_SENSITIVITY, 50);
        spMoveWeekday = sp.getString(deviceId
                + ConfigUtil.MOVE_WEEKDAY, "");
        spMoveTime = sp.getString(deviceId + ConfigUtil.MOVE_TIME,
                "");
        // TODO 过滤，如果跨夜，则截取,维持成四段
        String[] time = spMoveTime.split(",");
        if (time.length > 4 && time.length == 8) {
            spMoveTime = time[0] + "," + time[1] + "," + time[6] + ","
                    + time[7];
        }
        if (TextUtils.isEmpty(spMoveWeekday))
            spMoveWeekday = DAY_EVERY;

        if (TextUtils.isEmpty(spMoveTime))
            spMoveTime = TIME_ALL_DAY;
    }

    private void updateView() {
        int length = spMoveArea.split(";").length;
        if (length <= 0) {
            tvProtectArea.setText(getResources().getString(R.string.Not_set));
        } else {
            tvProtectArea.setText(getResources().getString(R.string.Have_Set));
        }

        sbMove.setProgress(spMoveSensitivity);
        String weekday_move = CameraUtil.convertWeekday(this, spMoveWeekday);
        String time_move = CameraUtil.convertTime(this, spMoveTime);
        tvProtectTime.setText(weekday_move + " " + time_move);
    }

    private void commitSP() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(deviceId + ConfigUtil.MOVE_SENSITIVITY,
                sbMove.getProgress());// 灵敏度
        editor.putBoolean(deviceId + ConfigUtil.IS_MOVE_DETECTION,
                true);// 启用
        WLog.i("hxctest", "commitSP = " + true);
        editor.putString(deviceId + ConfigUtil.MOVE_AREA,
                spMoveArea);// 区域
        editor.putString(deviceId + ConfigUtil.MOVE_WEEKDAY,
                spMoveWeekday);
        editor.putString(deviceId + ConfigUtil.MOVE_TIME,
                spMoveTime);
        editor.commit();
        WLog.i("safeProtect", "---CommitSp--->");
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IPCcameraXmlMsgEvent event) {
        if (event.getCode() != 0) {
            Log.i("SettingSipMSg", "fail---" + "apiType = " + event.getApiType() + "msg = " + event.getMessage());
            switch (event.getApiType()) {
                case CONFIG_MOVEMENT_DETECTION:
                case CONFIG_LINKAGE_ARMING:
                    ToastUtil.show(this, getString(R.string.Setting_Fail));
                    break;
            }
        } else {
            Log.i("SettingSipMSg", "success---" + "apiType = " + event.getApiType() + "msg = " + event.getMessage());
            switch (event.getApiType()) {
                case CONFIG_MOVEMENT_DETECTION:
                    if (CameraUtil.getParamFromXml(event.getMessage(), "status").equalsIgnoreCase(
                            "ok")) {
                        sipokcount++;
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.Setting_Fail), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CONFIG_LINKAGE_ARMING:
                    if (CameraUtil.getParamFromXml(event.getMessage(), "status").equalsIgnoreCase(
                            "ok")) {
                        sipokcount++;
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.Setting_Fail), Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
            if (sipokcount == 2) {
                ConstantsUtil.START_PROTECT_AREA = true;
                commitSP();
                ProgressDialogManager.getDialogManager().dimissDialog(START_PROTECT, 0);
                Toast.makeText(this, getResources().getString(R.string.Setting_Success), Toast.LENGTH_SHORT).show();
                sipokcount = 0;
                setResult(RESULT_OK);
                finish();
            }
        }
    }

}
