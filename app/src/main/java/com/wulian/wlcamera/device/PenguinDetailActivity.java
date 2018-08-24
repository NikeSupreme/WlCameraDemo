package com.wulian.wlcamera.device;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
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
import com.wulian.wlcamera.customview.YuntaiButton;
import com.wulian.wlcamera.customview.YuntaiButtonLandscape;
import com.wulian.wlcamera.device.album.AlbumGridActivity;
import com.wulian.wlcamera.device.setting.CameraSettingActivity;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by hxc on 2017/7/4.
 * 企鹅机详情界面
 */

public class PenguinDetailActivity extends BaseTitleActivity {

    private static final String TAG = "PenguinDetailActivity";
    protected final String PROCESS = "icamProcess";
    private static final String QUERY = "QUERY";

    private static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private static final int SHOWSPEED_INTERVAL = 3000;// 速度间隔为3秒
    private static final int TALK_PORTRAIT = 1;//竖屏按下说话
    private static final int TALK_LANDSCAPE = 2;// 横屏按下说话

    private ICamDeviceBean iCamDeviceBean;
    private float mDensity;
    private int mHiddenViewMeasuredHeight;
    private int registerExpTime = 0;

    private PinchLayout layout_video_container;
    private FrameLayout main_container;
    private YuntaiButton yt_penguin;
    private YuntaiButtonLandscape yt_penguin_landscape;
    private ImageView iv_arrow;
    private View layout_video_loading, layout_video_reload, layout_video_offline;
    private TextView tv_network_speed, tv_hold_speek;
    private ViEAndroidGLES20 view_video;
    private AngleMeter angleMeter;
    private SoundPool soundPool;
    private int snapshot_sound_id;

    private View btn_snapshot;
    private ImageView btn_sound_switch, btn_brightness, iv_snapshot, btn_fullscreen;
    private ImageView iv_hold_speak;
    private TextView btn_definition;
    private DefinitionChoosePop definitionChoosePop;
    private BrightnessSetPop brightnessSetPop;
    private GestureDetector gestureDetector;
    private int definitionValue = 3;
    private int brightnessValue = 50;
    private int minWidth, maxWidth;
    private int widthRatio = 16, heightRatio = 9;
    private boolean isShowLandscapeView = true;
    private boolean isDuplexSpeech = false;//是否双向语音通话中
    private boolean isShowLimitsDialog = false;

    private Handler handler = new Handler(Looper.getMainLooper());

    private Bitmap saveLastBitmap;//保存当前图库图片的引用，方便更换的时候回收

    //横竖屏切换相关view
    private View layout_portrait, layout_portrait_bottom, layout_landscape;
    private TextView btn_definition_landscape, tv_network_speed_landscape;
    private ImageView btn_sound_switch_landscape, btn_snapshot_landscape, iv_hold_speak_landscape, btn_fullscreen_landscape;
    private FrameLayout layoutBrightness;
    private Button btnIknown;
    private ImageView ivBrightness;

    public static boolean hasInit = false;
    private boolean isPause = false;
    private boolean isRadioOpen = false;
    private boolean isPlayAndRecord = false;
    private boolean isFirstCreate = false;
    private boolean isControling = false;
    private boolean isLandscape = false;
    private boolean isQueryHistory = false;
    private boolean canAdjustBrightness = false;

    private long saveReceivedDataSize = 0;
    private static final int YUNTAI_CONTROL = 1;
    private YuntaiButton.Direction curDirection;
    private YuntaiButtonLandscape.Direction curDirectionLandscape;
    private Runnable autoPullRunnable;
    private String cameraDefinition;

    private TranslateAnimation animation;

    private String deviceDomain;
    private String sipDomain;
    private String sipUid;
    private String userSipPwd;
    private String deviceId;
    /**
     * 0 loading，1 断开，2 播放, 3 离线
     */
    private int videoPlayState;

    public boolean isShared = false;

    Handler ytHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case YUNTAI_CONTROL:
                    curDirection = (YuntaiButton.Direction) msg.obj;
                    switch (curDirection) {
                        case left:
                            yuntai_left();
                            break;
                        case up:
                            yuntai_up();
                            break;
                        case right:
                            yuntai_right();
                            break;
                        case down:
                            yuntai_down();
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    Handler ytHandlerLandscape = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case YUNTAI_CONTROL:
                    curDirectionLandscape = (YuntaiButtonLandscape.Direction) msg.obj;
                    switch (curDirectionLandscape) {
                        case left:
                            yuntai_left();
                            break;
                        case up:
                            yuntai_up();
                            break;
                        case right:
                            yuntai_right();
                            break;
                        case down:
                            yuntai_down();
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public static void start(Context context, String deviceId, String userSipPwd, String sipUid, String deviceDomain, String sipDomain) {
        if (deviceId.startsWith("CG") &&deviceId.length() >= 11) {
            deviceId= deviceId.substring(0, 11);
        }
        context.startActivity(new Intent(context, PenguinDetailActivity.class)
                .putExtra("deviceId", deviceId)
                .putExtra("userSipPwd", userSipPwd)
                .putExtra("sipUid", sipUid)
                .putExtra("deviceDomain", deviceDomain)
                .putExtra("sipDomain", sipDomain));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_penguin_detail, true);
        EventBus.getDefault().register(this);
        CameraUtil.setHasVideoActivityRunning(true);
        WLog.i("android.os.Build.MODEL", Build.MODEL);
    }

    @Override
    public boolean enableSwipeBack() {
        return false;
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
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
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
        super.onDestroy();
    }

    private void stopWork() {
        IPCController.closeAllVideoAsync(new IPCResultCallBack() {
            @Override
            public void getResult(int i) {
                WLog.i(PROCESS, "挂断视频流：" + i);
                if (i == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (videoPlayState != 3) {//离线不改变状态
                                videoPlayState = 1;
                            }
                            handler.removeCallbacksAndMessages(null);
                            tv_network_speed.setText("0KB/s");
                            tv_network_speed_landscape.setText("0KB/s");
                        }
                    });
                }
            }
        });
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
            //这个标志位是因为关闭权限弹框是会走onresume回调，造成多次呼叫引发问题
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
        setToolBarTitleAndRightImg(getString(R.string.Penguin), R.drawable.icon_cateye_setting);
    }

    @Override
    protected void initView() {
        main_container = (FrameLayout) findViewById(R.id.main_container);
        layout_video_container = (PinchLayout) findViewById(R.id.layout_video_container);
        layout_video_loading = findViewById(R.id.layout_video_loading);
        layout_video_reload = findViewById(R.id.layout_video_reload);
        layout_video_offline = findViewById(R.id.layout_video_offline);
        yt_penguin = (YuntaiButton) findViewById(R.id.yt_penguin);
        yt_penguin_landscape = (YuntaiButtonLandscape) findViewById(R.id.yt_penguin_landscape);
        iv_arrow = (ImageView) findViewById(R.id.iv_arrows);
        angleMeter = (AngleMeter) findViewById(R.id.anglemeter);
        angleMeter.setMaxAngle("100°");
        view_video = new ViEAndroidGLES20(this);
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
        tv_hold_speek = (TextView) findViewById(R.id.tv_hold_speek);
        btn_snapshot = findViewById(R.id.btn_snapshot);
        iv_snapshot = (ImageView) findViewById(R.id.iv_snapshot);
        btn_sound_switch = (ImageView) findViewById(R.id.btn_sound_switch);
        btn_brightness = (ImageView) findViewById(R.id.btn_brightness);
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
        tv_network_speed_landscape.setAlpha(0.5f);
        layoutBrightness = (FrameLayout) findViewById(R.id.layout_brightness_tips);
        ivBrightness = (ImageView) findViewById(R.id.iv_brightness_tip);
        btnIknown = (Button) findViewById(R.id.btn_i_known);
    }

    @Override
    protected void initData() {
        Bundle bd = getIntent().getExtras();
        if (bd == null) {
            return;
        }
        isFirstCreate = true;
        deviceId = getIntent().getStringExtra("deviceId");
        userSipPwd = getIntent().getStringExtra("userSipPwd");
        deviceDomain = getIntent().getStringExtra("deviceDomain");
        sipUid = getIntent().getStringExtra("sipUid");
        sipDomain = getIntent().getStringExtra("sipDomain");
        mDensity = getResources().getDisplayMetrics().density;
        mHiddenViewMeasuredHeight = (int) (mDensity * 50 + 0.5);
        updateLoadingState(0);

        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        snapshot_sound_id = soundPool.load(this, R.raw.snapshot, 1);

        setRadioOpen(isRadioOpen);

        checkPermission();
        autoPullRunnable = new Runnable() {
            @Override
            public void run() {
                pullDownAnimation();
            }
        };
        handler.postDelayed(autoPullRunnable, 1000);

        btn_definition.setText(DefinitionBean.getNameResByValue(this, definitionValue));
        showSnapshot();

        gestureDetector = new GestureDetector(this, new CameraGestureListener(this, new CameraGestureListener.MyGestureListener() {
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
        iv_arrow.setOnClickListener(this);
        iv_hold_speak.setOnClickListener(this);
        iv_hold_speak_landscape.setOnClickListener(this);
        yt_penguin.setOnDirectionLisenter(new MyDirection());
        btnIknown.setOnClickListener(this);
        yt_penguin_landscape.setOnDirectionLisenter(new MyDirectionLandscape());

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
        layout_landscape.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);// 手势双击
                return true;// 自定义方向判断
            }
        });

    }

    class MyDirection implements YuntaiButton.OnDirectionLisenter {
        @Override
        public void directionLisenter(YuntaiButton.Direction direction) {
            yuntai_stop();
            ytHandler.removeMessages(YUNTAI_CONTROL);
            if (direction != YuntaiButton.Direction.none) {
                ytHandler.sendMessageDelayed(
                        Message.obtain(ytHandler, YUNTAI_CONTROL, direction),
                        500);
            }
        }
    }

    class MyDirectionLandscape implements YuntaiButtonLandscape.OnDirectionLisenter {
        @Override
        public void directionLisenter(YuntaiButtonLandscape.Direction direction) {
            yuntai_stop();
            ytHandlerLandscape.removeMessages(YUNTAI_CONTROL);
            if (direction != YuntaiButtonLandscape.Direction.none) {
                ytHandlerLandscape.sendMessageDelayed(
                        Message.obtain(ytHandlerLandscape, YUNTAI_CONTROL, direction),
                        500);
            }
        }
    }


    @Override
    public void onClickView(View v) {
        if (videoPlayState != 3) {//离线不能点击
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
                            IPCMsgController.MsgConfigCSC(deviceId,
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
                                IPCMsgController.MsgConfigEncode(deviceId,
                                        deviceDomain, definitionValue - 1);//0,1,2
                            } else {
                                IPCMsgController.MsgConfigEncode(deviceId,
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
                    IPCMsgController.MsgConfigEncode(deviceId,
                            deviceDomain, definitionValue - 1);//0,1,2
                } else {
                    IPCMsgController.MsgConfigEncode(deviceId,
                            deviceDomain, definitionValue);//1,2,3
                }
            } else if (v == layout_video_reload) {
                updateLoadingState(0);
                IPCController.closeAllVideoAsyncRefresh(new IPCResultCallBack() {
                    @Override
                    public void getResult(int i) {
                        if (i == 0) {
                            WLog.i(PROCESS, "关闭视频流并重置呼叫" + i);
                        }
                    }
                });
                makeCall();
            } else if (v == iv_hold_speak) {
                beginTalk(TALK_PORTRAIT);
            } else if (v == iv_hold_speak_landscape) {
                beginTalk(TALK_LANDSCAPE);
            }
        }
        if (v == iv_snapshot) {
            Intent intent = new Intent(this, AlbumGridActivity.class);
            intent.putExtra("devId", deviceId);
            startActivity(intent);
        } else if (v.getId() == R.id.img_right) {
            ICamDeviceBean iCamDeviceBean = new ICamDeviceBean();
            iCamDeviceBean.did = deviceId;
            iCamDeviceBean.sdomain = sipDomain;
            Intent intent = new Intent(this, CameraSettingActivity.class);
            intent.putExtra("ICamDeviceBean", iCamDeviceBean);
            startActivityForResult(intent, 1);
        } else if (v == iv_arrow) {
            if (yt_penguin.getVisibility() == View.GONE) {
                handler.removeCallbacks(autoPullRunnable);
                pullDownAnimation();
            } else {
                pushupAnimation();
            }
        } else if (v == btn_fullscreen_landscape) {
            exitFullscreen();
        } else if (v == btn_fullscreen) {
            performFullscreen();
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
            yt_penguin_landscape.setVisibility(View.GONE);
            btn_definition_landscape.setVisibility(View.GONE);
            tv_network_speed_landscape.setVisibility(View.VISIBLE);
            isShowLandscapeView = false;
        } else {
            btn_sound_switch_landscape.setVisibility(View.VISIBLE);
            btn_snapshot_landscape.setVisibility(View.VISIBLE);
            iv_hold_speak_landscape.setVisibility(View.VISIBLE);
            btn_fullscreen_landscape.setVisibility(View.VISIBLE);
            yt_penguin_landscape.setVisibility(View.VISIBLE);
            btn_definition_landscape.setVisibility(View.VISIBLE);
            tv_network_speed_landscape.setVisibility(View.VISIBLE);
            isShowLandscapeView = true;
        }
    }

    private void initSip() {
        if (MainApplication.getApplication().hasRegisterSipAccount &&
                TextUtils.equals(preference.getCurrentSipSuid(), sipUid)) {
            WLog.i(PROCESS, "已经注册sip账号，直接呼叫");
            makeCall();
        } else if (MainApplication.getApplication().hasInitSip &&
                !MainApplication.getApplication().hasRegisterSipAccount) {
            WLog.i(PROCESS, "已经初始化sip，但未注册sip账号");
            startRegister();
        } else if (MainApplication.getApplication().hasInitSip && !TextUtils.equals(preference.getCurrentSipSuid(), sipUid)) {
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
                            preference.saveCurrentSipSuid(sipUid);
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
        if (videoPlayState != 3) {//离线不启动视频
            setRender();
            updateLoadingState(0);
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
                                WLog.i(PROCESS, "账号注册异常重新注册次数超过5次需手动刷新" + registerExpTime);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateLoadingState(1);
                                    }
                                });
                                registerExpTime = 0;
                            }
                        }
                    }, deviceId, deviceDomain);
                }
            });
        }
    }

    private void beginTalk(int mode) {
        if (isDuplexSpeech) {//关闭双向语音
            setRadioOpen(false);
            if (mode == TALK_PORTRAIT) {
                iv_hold_speak.setImageResource(R.drawable.icon_hold_speek);
            } else {
                iv_hold_speak_landscape.setImageResource(R.drawable.btn_hold_fullscreen);
            }
            tv_hold_speek.setText(getString(R.string.Click_Call));
            btn_sound_switch.setClickable(true);
            btn_sound_switch_landscape.setClickable(true);
            IPCController.stopPlayAndRecordAudioAsync(new IPCResultCallBack() {
                @Override
                public void getResult(int i) {
                    WLog.i(TAG, "getResult: stop result = " + i);
                }
            });
            isDuplexSpeech = false;
        } else {//开启双向语音
            VibratorUtil.holdSpeakVibration();
            isDuplexSpeech = true;
            setRadioOpen(true);
            if (mode == TALK_PORTRAIT) {
                iv_hold_speak.setImageResource(R.drawable.penguin_speak_animlist);
                AnimationDrawable animationDrawable = (AnimationDrawable) iv_hold_speak.getDrawable();
                animationDrawable.start();
            } else {
                iv_hold_speak_landscape.setImageResource(R.drawable.penguin_speak_landscape_animlist);
                AnimationDrawable animationDrawable = (AnimationDrawable) iv_hold_speak_landscape.getDrawable();
                animationDrawable.start();
            }
            tv_hold_speek.setText(getString(R.string.Click_Hang_Up));
            btn_sound_switch.setClickable(false);
            btn_sound_switch_landscape.setClickable(false);
            IPCController.playAndRecordAudioAsync(new IPCResultCallBack() {
                @Override
                public void getResult(int i) {
                }
            });
        }
    }

    /**
     * 配置亮度以及查询设备信息
     */
    private void configAndQueryCameraInfo() {
        IPCMsgController.MsgConfigCSC(deviceId,
                deviceDomain, brightnessValue, 50, 50, 50);
        IPCMsgController.MsgQueryDeviceDescriptionInfo(deviceId,
                deviceDomain);//此处查询为了获得dpis针对720P和1080P设置不同的清晰度
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IPCcameraXmlMsgEvent event) {
        if (event.getCode() != 0) {
            SipDataReturn(false, event.getApiType(), event.getMessage(),
                    event.getDestURI(), String.valueOf(event.getCode()));
        } else {
            SipDataReturn(true, event.getApiType(), event.getMessage(),
                    event.getDestURI(), String.valueOf(event.getCode()));
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
                IPCMsgController.MsgConfigEncode(deviceId,
                        deviceDomain, definitionValue);//1,2,3
            } else {//720P随便看
                cameraDefinition = "720P";
                IPCMsgController.MsgConfigEncode(deviceId,
                        deviceDomain, definitionValue - 1);//0,1,2
            }
        }
    }

    private Runnable requestSpeedTask = new Runnable() {
        @Override
        public void run() {
            IPCController.getCallSpeedInfo();
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
                }
            });
        } else {
            btn_sound_switch.setImageResource(R.drawable.icon_cateye_sound_off);
            btn_sound_switch_landscape.setImageResource(R.drawable.btn_sound_fullscreen_off);
            IPCController.stopPlayAudioAsync(new IPCResultCallBack() {
                @Override
                public void getResult(int i) {
                }
            });
        }
    }

    /**
     * 视频背景显示上一次退出时的截图
     */
    private void showSnapshot() {
        String snapshotPath = FileUtil.getLastFramePath();
        String fileName = deviceId + ".jpg";
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
        String savePath = FileUtil.getSnapshotPath() + "/" + deviceId;
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
            String savePath = FileUtil.getSnapshotPath() + "/" + deviceId;
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
        boolean isFirstFullScreen = preference.getIsFrisCameraFullScreen();
        if (isFirstFullScreen) {
            preference.setIsFrisCameraFullScreen(false);
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

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏

        if (isDuplexSpeech) {
            iv_hold_speak.setImageResource(R.drawable.penguin_speak_animlist);
            iv_hold_speak_landscape.setImageResource(R.drawable.penguin_speak_landscape_animlist);
            AnimationDrawable animationDrawable = (AnimationDrawable) iv_hold_speak_landscape.getDrawable();
            animationDrawable.start();
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
        if (isDuplexSpeech) {
            iv_hold_speak.setImageResource(R.drawable.penguin_speak_animlist);
            iv_hold_speak_landscape.setImageResource(R.drawable.penguin_speak_landscape_animlist);
            AnimationDrawable animationDrawable = (AnimationDrawable) iv_hold_speak.getDrawable();
            animationDrawable.start();
        }
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
                    WLog.i(PROCESS, "sdk挂断重呼");
                    updateLoadingState(0);
                    IPCController.closeAllVideoAsyncRefresh(new IPCResultCallBack() {
                        @Override
                        public void getResult(int i) {
                            if (i == 0) {
                                WLog.i(PROCESS, "关闭视频流并重置呼叫" + i);
                                makeCall();
                            }
                        }
                    });
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
                showSpeed(event.getMessage());
                break;
            case HANDLE_RTC_CALL_DQ_TYPE:
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
                    String fileName = deviceId + ".jpg";
                    File savePathFile = new File(savePath);
                    if (!savePathFile.exists()) {
                        savePathFile.mkdirs();
                    }
                    FileUtil.saveBitmapToJpeg(event.getmVideoBitmap(), savePath, fileName);
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


    private void pullDownAnimation() {
        iv_hold_speak.setVisibility(View.GONE);
        tv_hold_speek.setVisibility(View.GONE);
        yt_penguin.setVisibility(View.VISIBLE);
        animation = new TranslateAnimation(0, 0, -200, 0);
        animation.setDuration(1000);//设置动画持续时间
        yt_penguin.startAnimation(animation);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                iv_arrow.setImageResource(R.drawable.arrow_up_white);
            }
        }, 1000);
    }

    private void pushupAnimation() {
        yt_penguin.setVisibility(View.GONE);
        iv_hold_speak.setVisibility(View.VISIBLE);
        tv_hold_speek.setVisibility(View.VISIBLE);
        animation = new TranslateAnimation(0, 0, 200, 0);
        animation.setDuration(1000);//设置动画持续时间
        iv_hold_speak.startAnimation(animation);
        tv_hold_speek.startAnimation(animation);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                iv_arrow.setImageResource(R.drawable.arrow_down_white);
            }
        }, 1000);
    }

    private ValueAnimator createDropAnimator(final View v, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                int value = (int) arg0.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = value;
                v.setLayoutParams(layoutParams);

            }
        });
        return animator;
    }

    public void yuntai_stop() {
        stopMove();
    }

    public void yuntai_left() {
        if (isControling) {
            return;
        }
        isControling = true;
        IPCMsgController.MsgControlPTZMovement(-1, 0);
    }

    public void yuntai_down() {
        if (isControling) {
            return;
        }
        isControling = true;
        IPCMsgController.MsgControlPTZMovement(0, -1);
    }

    public void yuntai_right() {
        if (isControling) {
            return;
        }
        isControling = true;
        IPCMsgController.MsgControlPTZMovement(1, 0);
    }

    public void yuntai_up() {
        if (isControling) {
            return;
        }
        isControling = true;
        IPCMsgController.MsgControlPTZMovement(0, 1);
    }

    private void stopMove() {
        if (isControling) {
            isControling = false;
            IPCMsgController.MsgControlPTZMovement(0, 0);
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
