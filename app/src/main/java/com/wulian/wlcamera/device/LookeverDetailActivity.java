package com.wulian.wlcamera.device;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulian.sdk.android.ipc.rtcv2.IPCController;
import com.wulian.sdk.android.ipc.rtcv2.IPCMsgApiType;
import com.wulian.sdk.android.ipc.rtcv2.IPCMsgController;
import com.wulian.sdk.android.ipc.rtcv2.IPCResultCallBack;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCCallStateMsgEvent;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCOnReceivedMsgEvent;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCVideoFrameMsgEvent;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCcameraXmlMsgEvent;
import com.wulian.sdk.android.ipc.rtcv2.message.messagestate.MsgCallState;
import com.wulian.sdk.android.ipc.rtcv2.message.messagestate.MsgReceivedType;
import com.wulian.sdk.android.ipc.rtcv2.utils.IPCGetFrameFunctionType;
import com.wulian.webrtc.ViEAndroidGLES20;
import com.wulian.wlcamera.BaseTitleActivity;
import com.wulian.wlcamera.MainApplication;
import com.wulian.wlcamera.R;
import com.wulian.wlcamera.bean.DefinitionBean;
import com.wulian.wlcamera.bean.DeviceDetailMsg;
import com.wulian.wlcamera.bean.ICamDeviceBean;
import com.wulian.wlcamera.customview.AngleMeter;
import com.wulian.wlcamera.customview.BrightnessSetPop;
import com.wulian.wlcamera.customview.CameraGestureListener;
import com.wulian.wlcamera.customview.DefinitionChoosePop;
import com.wulian.wlcamera.customview.PinchLayout;
import com.wulian.wlcamera.customview.ProgressDialogManager;
import com.wulian.wlcamera.device.album.AlbumGridActivity;
import com.wulian.wlcamera.device.setting.CameraSettingActivity;
import com.wulian.wlcamera.tools.Preference;
import com.wulian.wlcamera.utils.CameraUtil;
import com.wulian.wlcamera.utils.DisplayUtil;
import com.wulian.wlcamera.utils.FileUtil;
import com.wulian.wlcamera.utils.LanguageUtil;
import com.wulian.wlcamera.utils.SizeUtil;
import com.wulian.wlcamera.utils.ToastUtil;
import com.wulian.wlcamera.utils.VibratorUtil;
import com.wulian.wlcamera.utils.WLog;
import com.wulian.wlcamera.utils.XmlHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by huxc on 2017/5/4.
 * 随便看详情界面
 */

public class LookeverDetailActivity extends BaseTitleActivity {

    private static final String KEY_ICAM_DEVICE_BEAN = "icam_device_bean";
    private static final String PROCESS = "icamProcess";

    private static final String QUERY = "QUERY";

    private static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private static final int SHOWSPEED_INTERVAL = 3000;// 速度间隔为3秒
    private static final String TAG = "LookeverDetailActivity";

    private ViEAndroidGLES20 view_video;
    private AngleMeter angleMeter;
    private GestureDetector mGestureDetector;
    private SoundPool soundPool;
    private DefinitionChoosePop definitionChoosePop;
    private BrightnessSetPop brightnessSetPop;
    private FrameLayout main_container;

    private PinchLayout layout_video_container;
    private View layout_video_loading, layout_video_reload, layout_video_offline;
    private View btn_snapshot;
    private View btn_hold_speak;
    private FrameLayout layoutBrightness;
    private Button btnIknown;
    private TextView tv_network_speed, tv_hold_speak;
    private TextView btn_definition;
    private ImageView btn_sound_switch, btn_brightness, iv_snapshot, btn_fullscreen;
    private ImageView iv_hold_speak;
    private ImageView ivBrightness;

    private int definitionValue = 3;
    private int snapshot_sound_id;
    private int brightnessValue = 50;
    private int minWidth, maxWidth;
    private int widthRatio = 16, heightRatio = 9;
    private int registerExpTime = 0;
    private boolean isLandscape = false;
    private boolean isShowLandscapeView = true;
    private boolean canAdjustBrightness = false;


    private Handler handler = new Handler(Looper.getMainLooper());

    private Bitmap saveLastBitmap;//保存当前图库图片的引用，方便更换的时候回收

    //横竖屏切换相关view
    private View layout_portrait, layout_portrait_bottom, layout_landscape;
    private TextView btn_definition_landscape, tv_network_speed_landscape;
    private ImageView btn_sound_switch_landscape, btn_snapshot_landscape, iv_hold_speak_landscape, btn_fullscreen_landscape;

    public static boolean hasInit = false;
    private boolean isPause = false;
    private boolean isRadioOpen = false;
    private boolean isPlayAndRecord = false;
    private boolean isFirstCreate = false;
    private boolean isShowLimitsDialog = false;
    private long saveReceivedDataSize = 0;
    private boolean isQueryHistory = false;
    private String cameraDefinition;
    private String deviceId;
    private String deviceDomain;
    private String sipDomain;
    private String sipUid;
    private String userSipPwd;
    private String uniqueDeviceId = "cmic078e50294d415774";


    /**
     * 0 loading，1 断开，2 播放, 3 离线
     */
    private int videoPlayState;

    public boolean isShared = false;

    public static void start(Context context, String deviceId, String userSipPwd, String sipUid, String deviceDomain, String sipDomain) {
        context.startActivity(new Intent(context, LookeverDetailActivity.class)
                .putExtra("deviceId", deviceId)
                .putExtra("userSipPwd", userSipPwd)
                .putExtra("sipUid", sipUid)
                .putExtra("deviceDomain", deviceDomain)
                .putExtra("sipDomain", sipDomain));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_lookever_detail, true);
        CameraUtil.setHasVideoActivityRunning(true);
    }


    @Override
    protected void onResume() {
        super.onResume();
        isPause = false;
        showLastSnapshot();
        if (videoPlayState != 2 && !isShowLimitsDialog) {
            initSip();
        }
    }

    @Override
    protected void onPause() {
        if (videoPlayState == 2) {
            IPCController.getRenderFrame("hello", IPCGetFrameFunctionType.FRAME_MAIN_THUNBNAIL);
        }
        isPause = true;
        super.onPause();
    }

    @Override
    protected void onStop() {
        isQueryHistory = false;
        if (isPlayAndRecord) {
            IPCController.stopPlayAndRecordAudioAsync(null);
            isPlayAndRecord = false;
        }
        tv_network_speed.setText("0KB/s");
        tv_network_speed_landscape.setText("0KB/s");
        tv_network_speed_landscape.setAlpha(0.5f);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        IPCController.closeAllVideoAsync(new IPCResultCallBack() {
            @Override
            public void getResult(int i) {
            }
        });
        IPCController.setRender("", null);
        layout_video_container.removeView(view_video);
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        CameraUtil.setHasVideoActivityRunning(false);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                PERMISSION_RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{PERMISSION_RECORD_AUDIO},
                    PERMISSION_REQUEST_CODE);
        } else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            //这个标志位是因为关闭权限弹框是会走onResume回调，造成多次呼叫引发问题
            isShowLimitsDialog = true;
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                // Permission Denied
                ToastUtil.singleCenter(this, R.string.Toast_Permission_Denied);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setToolBarTitleAndRightImg(getString(R.string.Lookever), R.drawable.icon_cateye_setting);
    }

    @Override
    protected void initView() {
        main_container = (FrameLayout) findViewById(R.id.main_container);
        layout_video_container = (PinchLayout) findViewById(R.id.layout_video_container);
        layout_video_loading = findViewById(R.id.layout_video_loading);
        layout_video_reload = findViewById(R.id.layout_video_reload);
        layout_video_offline = findViewById(R.id.layout_video_offline);
        layoutBrightness = (FrameLayout) findViewById(R.id.layout_brightness_tips);
        ivBrightness = (ImageView) findViewById(R.id.iv_brightness_tip);
        btnIknown = (Button) findViewById(R.id.btn_i_known);

        angleMeter = (AngleMeter) findViewById(R.id.anglemeter);
        angleMeter.setMaxAngle("100°");
        view_video = new ViEAndroidGLES20(this);
        view_video.setZOrderOnTop(true);
        view_video.setZOrderMediaOverlay(true);

        DisplayMetrics displayMetrics = SizeUtil.getScreenSize(getApplicationContext());
        int deviceWidth = displayMetrics.widthPixels;
        int cameraPreviewWidth = deviceWidth;// 根据布局中的上下比例
        int cameraPreviewHeight = (int) ((float) cameraPreviewWidth
                * heightRatio / widthRatio);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(cameraPreviewWidth, cameraPreviewHeight);
        layoutParams.gravity = Gravity.CENTER;
        layout_video_container.addView(view_video, 0, layoutParams);
        view_video.setKeepScreenOn(true);

        tv_network_speed = (TextView) findViewById(R.id.tv_network_speed);
        tv_hold_speak = (TextView) findViewById(R.id.tv_hold_speak);
        btn_snapshot = findViewById(R.id.btn_snapshot);
        iv_snapshot = (ImageView) findViewById(R.id.iv_snapshot);
        btn_sound_switch = (ImageView) findViewById(R.id.btn_sound_switch);
        btn_brightness = (ImageView) findViewById(R.id.btn_brightness);
        btn_hold_speak = findViewById(R.id.btn_hold_speak);
        iv_hold_speak = (ImageView) findViewById(R.id.iv_hold_speak);
        btn_definition = (TextView) findViewById(R.id.btn_definition);
        btn_fullscreen = (ImageView) findViewById(R.id.btn_fullscreen);
        btn_definition.setAlpha(0.5f);
        btn_fullscreen.setAlpha(0.5f);

        //横竖屏切换相关view
        layout_portrait = findViewById(R.id.layout_portrait);
        layout_portrait_bottom = findViewById(R.id.layout_portrait_bottom);
        layout_landscape = findViewById(R.id.layout_landscape);
        btn_definition_landscape = (TextView) findViewById(R.id.btn_definition_landscape);
        tv_network_speed_landscape = (TextView) findViewById(R.id.tv_network_speed_landscape);
        btn_sound_switch_landscape = (ImageView) findViewById(R.id.btn_sound_switch_landscape);
        btn_snapshot_landscape = (ImageView) findViewById(R.id.btn_snapshot_landscape);
        iv_hold_speak_landscape = (ImageView) findViewById(R.id.iv_hold_speak_landscape);
        btn_fullscreen_landscape = (ImageView) findViewById(R.id.btn_fullscreen_landscape);
        btn_fullscreen_landscape.setAlpha(0.5f);
    }

    @Override
    protected void initData() {
        isFirstCreate = true;
        deviceId = getIntent().getStringExtra("deviceId");
        userSipPwd = getIntent().getStringExtra("userSipPwd");
        deviceDomain = getIntent().getStringExtra("deviceDomain");
        sipUid = getIntent().getStringExtra("sipUid");
        sipDomain = getIntent().getStringExtra("sipDomain");
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        snapshot_sound_id = soundPool.load(this, R.raw.snapshot, 1);

        setRadioOpen(isRadioOpen);

        checkPermission();

        btn_definition.setText(DefinitionBean.getNameResByValue(this, definitionValue));
        showSnapshot();
        mGestureDetector = new GestureDetector(this, new CameraGestureListener(this, new CameraGestureListener.MyGestureListener() {
            @Override
            public void OnBrightChanged(float brightness) {
                if (canAdjustBrightness) {
                    setBrightness(brightness);
                }
            }

            @Override
            public void onSingleTouchConfirmed() {
                if (isLandscape) {
                    hideOrShowLandscapeView();
                }
            }
        }));
        updateLoadingState(0);
    }

    @Override
    protected void initListeners() {
        btn_snapshot.setOnClickListener(this);
        iv_snapshot.setOnClickListener(this);
        btn_sound_switch.setOnClickListener(this);
        btn_brightness.setOnClickListener(this);
        layout_video_reload.setOnClickListener(this);
        btn_definition.setOnClickListener(this);
        btn_fullscreen.setOnClickListener(this);

        btn_definition_landscape.setOnClickListener(this);
        btn_sound_switch_landscape.setOnClickListener(this);
        btn_snapshot_landscape.setOnClickListener(this);
        btn_fullscreen_landscape.setOnClickListener(this);
        btnIknown.setOnClickListener(this);

        layout_landscape.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mGestureDetector.onTouchEvent(motionEvent);// 手势双击
                return true;// 自定义方向判断
            }
        });

        btn_hold_speak.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (videoPlayState == 2) {//播放状态才能点击
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            iv_hold_speak.setImageResource(R.drawable.icon_hold_speek_on);
                            tv_hold_speak.setText(R.string.Cateye_In_Call);
                            IPCController.recordAudioAsync(new IPCResultCallBack() {
                                @Override
                                public void getResult(int i) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            setRadioOpen(false);
                                        }
                                    });
                                    VibratorUtil.holdSpeakVibration();
                                }
                            });
                        }
                        break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL: {
                            iv_hold_speak.setImageResource(R.drawable.icon_hold_speek);
                            tv_hold_speak.setText(R.string.CateEye_Detail_Hold_Speek);
                            IPCController.stopRecordAudioAsync(new IPCResultCallBack() {
                                @Override
                                public void getResult(int i) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            setRadioOpen(true);
                                        }
                                    });
                                }
                            });
                        }
                        break;
                    }
                }
                return true;
            }
        });


        iv_hold_speak_landscape.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        iv_hold_speak_landscape.setImageResource(R.drawable.btn_hold_fullscreen_pre);
                        IPCController.recordAudioAsync(new IPCResultCallBack() {
                            @Override
                            public void getResult(int i) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setRadioOpen(false);
                                    }
                                });
                                VibratorUtil.holdSpeakVibration();
                            }
                        });
                    }
                    break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        iv_hold_speak_landscape.setImageResource(R.drawable.btn_hold_fullscreen);
                        IPCController.stopRecordAudioAsync(new IPCResultCallBack() {
                            @Override
                            public void getResult(int i) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setRadioOpen(true);
                                    }
                                });
                            }
                        });
                    }
                    break;
                }
                return true;
            }
        });
        layout_video_container.setOnChildViewLocationChangedListener(new PinchLayout.OnChildViewLocationChangedListener() {
            @Override
            public void childViewMoveScaleX(float ratio) {
                angleMeter.refreshAngle(ratio);
            }

            @Override
            public void minSize(boolean isMinSize) {
                angleMeter.setVisibility(isMinSize ? View.GONE : View.VISIBLE);
            }
        });
    }

    @Override
    public void onClickView(View v) {
//        super.onClick(v);
        if (videoPlayState != 3) {
            if (v == btn_snapshot || v == btn_snapshot_landscape) {
                if (videoPlayState == 2) {
                    IPCController.getRenderFrame("hello", IPCGetFrameFunctionType.FRAME_PLAY_THUMBNAIL);
                    btn_snapshot.setEnabled(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btn_snapshot.setEnabled(true);
                        }
                    }, 1000);
                }
            } else if (v == btn_sound_switch || v == btn_sound_switch_landscape) {
                if (videoPlayState == 2) {
                    setRadioOpen(!isRadioOpen);
                }
            } else if (v == btn_brightness) {
                if (brightnessSetPop == null) {
                    brightnessSetPop = new BrightnessSetPop(this, new BrightnessSetPop.OnValueChangedListener() {
                        @Override
                        public void onValueChanged(int value) {
                            brightnessValue = value;
                            IPCMsgController.MsgConfigCSC(uniqueDeviceId,
                                    deviceDomain, brightnessValue, 50, 50, 50);
                        }

                        @Override
                        public void onDismiss() {
                            btn_definition.setVisibility(View.VISIBLE);
                        }
                    });
                }
                brightnessSetPop.showUpRise(btn_brightness, brightnessValue);
            } else if (v == btn_definition) {
                if (definitionChoosePop == null) {
                    definitionChoosePop = new DefinitionChoosePop(this, new DefinitionChoosePop.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(DefinitionBean bean) {
                            definitionValue = bean.value;
                            btn_definition.setText(bean.name);
                            btn_definition_landscape.setText(bean.name);
                            if (!TextUtils.isEmpty(cameraDefinition) && "720P".equals(cameraDefinition)) {
                                IPCMsgController.MsgConfigEncode(uniqueDeviceId,
                                        deviceDomain, definitionValue - 1);//0,1,2
                            } else {
                                IPCMsgController.MsgConfigEncode(uniqueDeviceId,
                                        deviceDomain, definitionValue);//1,2,3
                            }

                        }
                    });
                }
                definitionChoosePop.showUpRise(btn_definition, definitionValue);
            } else if (v == btn_definition_landscape) {
                if (definitionValue < 3) {
                    definitionValue += 1;
                } else {
                    definitionValue = 1;
                }
                btn_definition.setText(DefinitionBean.getNameResByValue(this, definitionValue));
                btn_definition_landscape.setText(DefinitionBean.getNameResByValue(this, definitionValue));
                if (!TextUtils.isEmpty(cameraDefinition) && "720P".equals(cameraDefinition)) {
                    IPCMsgController.MsgConfigEncode(uniqueDeviceId,
                            deviceDomain, definitionValue - 1);//0,1,2
                } else {
                    IPCMsgController.MsgConfigEncode(uniqueDeviceId,
                            deviceDomain, definitionValue);//1,2,3
                }

            } else if (v == layout_video_reload) {
                updateLoadingState(0);
                reloadView();
            }
        }
        if (v == iv_snapshot) {
            Intent intent = new Intent(this, AlbumGridActivity.class);
            intent.putExtra("devId", uniqueDeviceId);
            startActivity(intent);
        } else if (v.getId() == R.id.img_right) {
            ICamDeviceBean iCamDeviceBean = new ICamDeviceBean();
            iCamDeviceBean.did = uniqueDeviceId;
            iCamDeviceBean.uniqueDeviceId = uniqueDeviceId;
            iCamDeviceBean.sdomain = sipDomain;
            Intent intent = new Intent(this, CameraSettingActivity.class);
            intent.putExtra("ICamDeviceBean", iCamDeviceBean);
            startActivityForResult(intent, 1);
        } else if (v == btn_fullscreen) {
            performFullscreen();
        } else if (v == btn_fullscreen_landscape) {
            exitFullscreen();
        } else if (v == btnIknown) {
            canAdjustBrightness = true;
            setLandscapeViewEnable(true);
            layoutBrightness.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            this.finish();
        }
    }

    private void hideOrShowLandscapeView() {
        if (isShowLandscapeView) {
            btn_sound_switch_landscape.setVisibility(View.GONE);
            btn_snapshot_landscape.setVisibility(View.GONE);
            iv_hold_speak_landscape.setVisibility(View.GONE);
            btn_fullscreen_landscape.setVisibility(View.GONE);
            btn_definition_landscape.setVisibility(View.GONE);
            tv_network_speed_landscape.setVisibility(View.VISIBLE);
            isShowLandscapeView = false;
        } else {
            btn_sound_switch_landscape.setVisibility(View.VISIBLE);
            btn_snapshot_landscape.setVisibility(View.VISIBLE);
            iv_hold_speak_landscape.setVisibility(View.VISIBLE);
            btn_fullscreen_landscape.setVisibility(View.VISIBLE);
            btn_definition_landscape.setVisibility(View.VISIBLE);
            tv_network_speed_landscape.setVisibility(View.VISIBLE);
            isShowLandscapeView = true;
        }
    }

    //当已经注册sip账号且没有切换sip账号是不需要重新注册
    private void initSip() {
        if (MainApplication.getApplication().hasRegisterSipAccount
                && TextUtils.equals(Preference.getPreferences().getCurrentSipSuid(), sipUid)) {
            WLog.i(PROCESS, "已经注册sip账号，直接呼叫");
            makeCall();
        } else if (MainApplication.getApplication().hasInitSip &&
                !MainApplication.getApplication().hasRegisterSipAccount) {
            WLog.i(PROCESS, "已经初始化sip，但未注册sip账号");
            startRegister();
        } else if (MainApplication.getApplication().hasInitSip && !TextUtils.equals(Preference.getPreferences().getCurrentSipSuid(), sipUid)) {
            WLog.i(PROCESS, "摄像机和锁之间切换，需要重新注册sip");
            startRegister();
        } else {
            IPCResultCallBack initRTCAsyncCallback = new IPCResultCallBack() {
                @Override
                public void getResult(int result) {
                    hasInit = result == 0 ? true : false;
                    WLog.i(PROCESS, "未初始化也未注册，此刻开始初始化" + hasInit);
                    if (hasInit) {
                        MainApplication.getApplication().hasInitSip = hasInit;
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startRegister();
                        }
                    }, 500);
                }
            };
            IPCController.initRTCAsync(initRTCAsyncCallback);
        }
    }


    private void startRegister() {
        IPCResultCallBack ipcResultCallBack = new IPCResultCallBack() {
            @Override
            public void getResult(int i) {
                boolean isRegisterAccount = i == 0 ? true : false;
                WLog.i(PROCESS, "注册sip账号: " + isRegisterAccount);
                if (isRegisterAccount) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Preference.getPreferences().saveCurrentSipSuid(sipUid);
                            MainApplication.getApplication().hasRegisterSipAccount = true;
                            WLog.i(PROCESS, "注册成功后呼叫");
                            makeCall();
                        }
                    }, 500);
                }
            }
        };
        IPCController.registerAccountAsync(ipcResultCallBack, sipUid, userSipPwd, sipDomain);
    }


    private void setRender() {
        if (isFirstCreate) {
            isFirstCreate = false;
            WLog.i(PROCESS, "设置渲染器");
            IPCController.setRender("", view_video);
            IPCController.setRenderFlag(deviceDomain);
        }
    }

    private void makeCall() {
        updateLoadingState(0);
        setRender();
        handler.post(new Runnable() {
            @Override
            public void run() {
                configAndQueryCameraInfo();
                IPCController.makeCallAsync(new IPCResultCallBack() {
                    @Override
                    public void getResult(int i) {
                        WLog.i(PROCESS, "发起视频呼叫结果: " + i);
                        if (i != 0 && i != 4) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateLoadingState(1);
                                }
                            });
                        } else if (i == 4 && registerExpTime < 5) {
                            WLog.i(PROCESS, "账号注册异常重新注册" + registerExpTime);
                            registerExpTime++;
                            startRegister();
                        } else if (i == 4 && registerExpTime == 5) {
                            WLog.i(PROCESS, "账号注册异常超过5次需手动刷新" + registerExpTime);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateLoadingState(1);
                                }
                            });
                            registerExpTime = 0;
                        }
                    }
                }, uniqueDeviceId, deviceDomain);
            }
        });
    }


    private void reloadView() {
        IPCController.closeAllVideoAsync(new IPCResultCallBack() {
            @Override
            public void getResult(int i) {
                if (i == 0) {
                    WLog.i(PROCESS, "关闭视频流并重置呼叫" + i);
                    makeCall();
                }
            }
        });

    }

    /**
     * 配置亮度以及查询设备信息
     */
    private void configAndQueryCameraInfo() {
//        IPCMsgController.MsgNotifyRtmp(uniqueDeviceId, deviceDomain, 0);
        IPCMsgController.MsgConfigCSC(uniqueDeviceId,
                deviceDomain, brightnessValue, 50, 50, 50);
        IPCMsgController.MsgQueryDeviceDescriptionInfo(uniqueDeviceId,
                deviceDomain);//此处查询为了获得dpis针对720P和1080P设置不同的清晰度
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IPCcameraXmlMsgEvent event) {
        if (event.getCode() != 0) {
            SipDataReturn(false, event.getApiType(), event.getMessage(),
                    event.getDestURI(), String.valueOf(event.getCode()));
            Log.i("sip", "fail---" + "apiType = " + event.getApiType() + "msg = " + event.getMessage());
        } else {
            SipDataReturn(true, event.getApiType(), event.getMessage(),
                    event.getDestURI(), String.valueOf(event.getCode()));
            Log.i("sip", "success---" + "apiType = " + event.getApiType() + "msg = " + event.getMessage());
        }
    }

    protected void SipDataReturn(boolean isSuccess, IPCMsgApiType apiType,
                                 String xmlData, String from, String code) {
        if (isSuccess) {
            Log.i("hxc", apiType + "");
            switch (apiType) {
                case QUERY_DEVICE_DESCRIPTION_INFO:
                    DeviceDetailMsg detailMsg = XmlHandler
                            .getDeviceDetailMsg(xmlData);
                    if (detailMsg != null) {
                        String dpis = detailMsg.getDpis();
                        WLog.i(TAG, "SipDataReturn: " + detailMsg.getDpis());
                        if (!TextUtils.isEmpty(dpis)) {
                            setDefaultDpis(dpis);
                        }
                    }
                    break;
                case QUERY_STORAGE_STATUS:
                    ProgressDialogManager.getDialogManager().dimissDialog(QUERY, 0);
                    if (!isPause && isQueryHistory) {
                        Pattern pstatus = Pattern
                                .compile("<storage.*status=\"(\\d)\"\\s+.*/?>(</storage>)?");
                        Matcher matchers = pstatus.matcher(xmlData);
                        if (matchers.find()) {
                            String status = matchers.group(1).trim();
                            if ("1".equals(status)) {
                                ToastUtil.singleCenter(this, R.string.No_SD_Look_Back);
                            } else if ("2".equals(status)) {
//                                stopWork();
                                IPCController.changeReplay(uniqueDeviceId, deviceDomain);
//                                LookeverReplayHardActivity.start(this, iCamGetSipInfoBean.suid, deviceDomain, iCamDeviceBean);
                            }
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 设置默认清晰度
     *
     * @param dpis
     */
    private void setDefaultDpis(String dpis) {
        String[] dpisArray = new String[3];
        dpisArray = dpis.split(",");
        if (dpisArray != null && dpisArray.length == 3) {
            if ("640x480".equals(dpisArray[0])) {//1080P随便看
                cameraDefinition = "1080P";
                IPCMsgController.MsgConfigEncode(uniqueDeviceId,
                        deviceDomain, definitionValue);//1,2,3
            } else {//720P随便看
                cameraDefinition = "720P";
                IPCMsgController.MsgConfigEncode(uniqueDeviceId,
                        deviceDomain, definitionValue - 1);//0,1,2
            }
        }
    }


    private Runnable requestSpeedTask = new Runnable() {
        @Override
        public void run() {
            IPCController.getCallSpeedInfo();
            WLog.i(TAG, "获取实时码率");
            handler.postDelayed(this, SHOWSPEED_INTERVAL);
        }
    };

    private void showSpeed(String speedInfo) {
        if (!TextUtils.isEmpty(speedInfo)) {
            long dataSize = 0;
            long delatDataSize = 0;
            try {
                dataSize = Long.parseLong(speedInfo);
                delatDataSize = dataSize - saveReceivedDataSize;
                delatDataSize = (delatDataSize > 0 ? delatDataSize : 0)
                        / (SHOWSPEED_INTERVAL / 1000);
                saveReceivedDataSize = dataSize;
                WLog.i(TAG, "底层推上来的码率: " + dataSize);
                WLog.i(TAG, "计算出的码率: " + delatDataSize);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            long speed = delatDataSize / 1000;
            tv_network_speed.setText("" + (speed > 0 ? speed : 0) + "KB/s");
            tv_network_speed_landscape.setText("" + (speed > 0 ? speed : 0) + "KB/s");
        }
    }

    private void setRadioOpen(boolean isOpen) {
        this.isRadioOpen = isOpen;
        if (isOpen) {
            btn_sound_switch.setImageResource(R.drawable.icon_cateye_sound_on);
            btn_sound_switch_landscape.setImageResource(R.drawable.btn_sound_fullscreen_on);
            IPCController.playAudioAsync(new IPCResultCallBack() {
                @Override
                public void getResult(int i) {
                    WLog.i("playAudioAsync result:" + i);
                }
            });
        } else {
            btn_sound_switch.setImageResource(R.drawable.icon_cateye_sound_off);
            btn_sound_switch_landscape.setImageResource(R.drawable.btn_sound_fullscreen_off);
            IPCController.stopPlayAudioAsync(new IPCResultCallBack() {
                @Override
                public void getResult(int i) {
                    WLog.i("stopPlayAudioAsync result:" + i);
                }
            });
        }
    }

    /**
     * 视频背景显示上一次退出时的截图
     */
    private void showSnapshot() {
        String snapshotPath = FileUtil.getLastFramePath();
        String fileName = uniqueDeviceId + ".jpg";
        String path = snapshotPath + "/" + fileName;
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                view_video.setBackground(new BitmapDrawable(getResources(), bitmap));
                return;
            }
        }
        view_video.setBackgroundResource(R.drawable.camera_default_bg1);
    }

    private void showLastSnapshot() {
        String savePath = FileUtil.getSnapshotPath() + "/" + uniqueDeviceId;
        File savePathFile = new File(savePath);
        String[] bmpFiles = savePathFile.list();
        if (bmpFiles != null && bmpFiles.length > 0) {
            String bmpFile = bmpFiles[bmpFiles.length - 1];
            Bitmap bitmap = BitmapFactory.decodeFile(savePath + "/" + bmpFile);
            iv_snapshot.setImageBitmap(bitmap);
        } else {
            iv_snapshot.setImageResource(R.drawable.icon_image_gallery);
        }
    }

    private void saveBitmap(final Bitmap bitmap) {
        if (bitmap != null) {
            String savePath = FileUtil.getSnapshotPath() + "/" + uniqueDeviceId;
            File savePathFile = new File(savePath);
            if (!savePathFile.exists()) {
                savePathFile.mkdirs();
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmsss");
            String time = simpleDateFormat.format(System.currentTimeMillis());
            String fileName = time + ".jpg";
            FileUtil.saveBitmapToJpeg(bitmap, savePath, fileName);
            DisplayUtil.snapAnimotion(this, main_container, view_video, iv_snapshot, bitmap, new DisplayUtil.onCompleteListener() {
                @Override
                public void onComplete() {
                    iv_snapshot.setImageBitmap(bitmap);
                }
            });
            if (soundPool != null) {
                soundPool.play(snapshot_sound_id, 1.0f, 1.0f, 0, 0, 1);
            }
        }
    }

    /**
     * 设置loadingview 状态
     *
     * @param state 0 loading，1 断开，2 播放,3 离线
     */
    private void updateLoadingState(int state) {
        videoPlayState = state;
        if (state == 0) {
            layout_video_reload.setVisibility(View.GONE);
            layout_video_loading.setVisibility(View.VISIBLE);
            layout_video_offline.setVisibility(View.GONE);
        } else if (state == 1) {
            layout_video_reload.setVisibility(View.VISIBLE);
            layout_video_loading.setVisibility(View.GONE);
            layout_video_offline.setVisibility(View.GONE);
        } else if (state == 2) {
            layout_video_reload.setVisibility(View.GONE);
            layout_video_loading.setVisibility(View.GONE);
            layout_video_offline.setVisibility(View.GONE);
        } else if (state == 3) {
            layout_video_reload.setVisibility(View.GONE);
            layout_video_loading.setVisibility(View.GONE);
            layout_video_offline.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 进入全屏
     */
    private void performFullscreen() {
        boolean isFirstFullScreen = Preference.getPreferences().getIsFrisCameraFullScreen();
        if (isFirstFullScreen) {
            Preference.getPreferences().setIsFrisCameraFullScreen(false);
            if (!LanguageUtil.isChina()) {
                ivBrightness.setImageResource(R.drawable.icon_brightness_tip_en);
                btnIknown.setBackgroundResource(R.drawable.icon_i_know_en);
            } else {
                ivBrightness.setImageResource(R.drawable.icon_brightness_tip_cn);
                btnIknown.setBackgroundResource(R.drawable.icon_i_know_cn);
            }
            setLandscapeViewEnable(false);
            layoutBrightness.setVisibility(View.VISIBLE);
        } else {
            canAdjustBrightness = true;
            layoutBrightness.setVisibility(View.GONE);
        }
        layout_video_container.setScaleEnable(false);
        isLandscape = true;
        getmToolBarHelper().setToolBarVisible(false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        layout_portrait.setVisibility(View.GONE);
        layout_portrait_bottom.setVisibility(View.GONE);
        layout_landscape.setVisibility(View.VISIBLE);


        if (brightnessSetPop != null && brightnessSetPop.isShowing()) {
            brightnessSetPop.dismiss();
        }
        if (definitionChoosePop != null && definitionChoosePop.isShowing()) {
            definitionChoosePop.dismiss();
        }
    }

    /**
     * 退出全屏
     */
    private void exitFullscreen() {
        layout_video_container.setScaleEnable(true);
        isLandscape = false;
        getmToolBarHelper().setToolBarVisible(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(params);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        layout_portrait.setVisibility(View.VISIBLE);
        layout_portrait_bottom.setVisibility(View.VISIBLE);
        layout_landscape.setVisibility(View.GONE);

        layout_video_container.setChildViewLocationCenter();
    }


    private void setLandscapeViewEnable(boolean flag) {
        iv_hold_speak_landscape.setEnabled(flag);
        btn_definition_landscape.setEnabled(flag);
        btn_fullscreen_landscape.setEnabled(flag);
        btn_sound_switch_landscape.setEnabled(flag);
        btn_snapshot_landscape.setEnabled(flag);
    }


    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            exitFullscreen();
        } else {
            super.onBackPressed();
        }
    }


    private int retryCount = 0;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IPCCallStateMsgEvent event) {
        MsgCallState callState = MsgCallState.getMsgCallState(event
                .getCallState());
        switch (callState) {
            case STATE_ESTABLISHED:
                WLog.i(PROCESS, "##建立连接了");
                break;
            case STATE_TERMINATED:
                WLog.i(PROCESS, "##挂断了");
                tv_network_speed.setText("0KB/s");
                tv_network_speed_landscape.setText("0KB/s");
                if (retryCount < 2) {
                    updateLoadingState(0);
                    WLog.i(PROCESS, "sdk挂断重呼");
                    makeCall();
                    retryCount += 1;
                } else {
                    updateLoadingState(1);
                }
                handler.removeCallbacks(requestSpeedTask);
                break;
            case STATE_VIDEO_INCOMING:
                updateLoadingState(2);
                WLog.i(PROCESS, "##视频流来了");
                view_video.setBackground(null);
                retryCount = 0;
                handler.removeCallbacks(requestSpeedTask);
                handler.postDelayed(requestSpeedTask, SHOWSPEED_INTERVAL);
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IPCOnReceivedMsgEvent event) {
        switch (MsgReceivedType.getMsgReceivedTypeByID(event.getRtcType())) {
            case HANDLE_RTC_CALL_SPEED_TYPE:
//                WLog.i("##摄像头速率");
                showSpeed(event.getMessage());
                break;
            case HANDLE_RTC_CALL_DQ_TYPE:
                WLog.i("##处理DQ信息");
                WLog.i("DQ信息-->" + event.getMessage());
//                dq_message = event.getMessage();
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IPCVideoFrameMsgEvent event) {
        WLog.d("PML", "End time is:" + System.currentTimeMillis());
        IPCGetFrameFunctionType type = event.getType();
        switch (type) {
            case FRAME_MAIN_THUNBNAIL:
                Bitmap bitmap = event.getmVideoBitmap();
                if (bitmap != null && !bitmap.isRecycled()) {
                    String savePath = FileUtil.getLastFramePath();
                    String fileName = uniqueDeviceId + ".jpg";
                    File savePathFile = new File(savePath);
                    if (!savePathFile.exists()) {
                        savePathFile.mkdirs();
                    }
                    FileUtil.saveBitmapToJpeg(event.getmVideoBitmap(), savePath, fileName);
//                    bitmap.recycle();
                }
                break;
            case FRAME_PLAY_THUMBNAIL:

                WLog.i("#Thread-->" + Thread.currentThread().getName());
                WLog.i("收到抓拍图片");
                if (event.getmVideoBitmap() == null) {
                    WLog.i("抓拍图片为空");
                } else {
                    saveBitmap(event.getmVideoBitmap());
                }
                break;

        }
    }


    public void setBrightness(float brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
        } else if (lp.screenBrightness < 0.2) {
            lp.screenBrightness = (float) 0.2;
        }
        getWindow().setAttributes(lp);
    }

}
