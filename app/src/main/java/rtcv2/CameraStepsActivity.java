package rtcv2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.wulian.sdk.android.ipc.rtcv2.IPCController;
import com.wulian.sdk.android.ipc.rtcv2.IPCResultCallBack;
import com.wulian.sdk.android.ipc.rtcv2.message.IPCAccountRegisterMsgEvent;
import com.wulian.wlcamera.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class CameraStepsActivity extends Activity implements OnClickListener {
    private Button btn_initRTC;
    private Button btn_destroyRTC;

    private Button btn_RtcNetChanged;
    private Button btn_RegisterAccount;
    private Button btn_unRegisterAccount;

    private Button btn_PlayVideo1;
    private Button btn_RePlayVideo1;
    private Button btn_DeviceSetting1;

    private String deviceId;
    private String deviceDomain;
    private String sipDomain;
    private String sipUid;
    private String userSipPwd;
    private String uniqueDeviceId = "cmic078e50294d415774";

    public static void start(Context context, String deviceId, String userSipPwd, String sipUid, String deviceDomain, String sipDomain) {
        if (deviceId.startsWith("CG") &&deviceId.length() >= 11) {
            deviceId= deviceId.substring(0, 11);
        }
        context.startActivity(new Intent(context, CameraStepsActivity.class)
                .putExtra("deviceId", deviceId)
                .putExtra("userSipPwd", userSipPwd)
                .putExtra("sipUid", sipUid)
                .putExtra("deviceDomain", deviceDomain)
                .putExtra("sipDomain", sipDomain));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_steps);
        initView();
        initData();
        setListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(IPCAccountRegisterMsgEvent event) {
        System.out.printf(
                "IPCAccountRegisterMsgEvent time is:"
                        + System.currentTimeMillis());
        System.out.printf("IPC Account Register is:" + event.getDestURI() + ";"
                + event.getCallState() + ";" + event.getCallCode());
    }

    private void initView() {
        btn_initRTC = (Button) findViewById(R.id.btn_initRTC);
        btn_destroyRTC = (Button) findViewById(R.id.btn_destroyRTC);
        btn_RtcNetChanged = (Button) findViewById(R.id.btn_RtcNetChanged);
        btn_RegisterAccount = (Button) findViewById(R.id.btn_RegisterAccount);
        btn_unRegisterAccount = (Button) findViewById(R.id.btn_unRegisterAccount);

        btn_PlayVideo1 = (Button) findViewById(R.id.btn_PlayVideo1);
        btn_RePlayVideo1 = (Button) findViewById(R.id.btn_RePlayVideo1);
        btn_DeviceSetting1 = (Button) findViewById(R.id.btn_DeviceSetting1);

    }

    private void initData() {
        deviceId = getIntent().getStringExtra("deviceId");
        userSipPwd = getIntent().getStringExtra("userSipPwd");
        deviceDomain = getIntent().getStringExtra("deviceDomain");
        sipUid = getIntent().getStringExtra("sipUid");
        sipDomain = getIntent().getStringExtra("sipDomain");
        uniqueDeviceId = deviceId;
    }

    private void setListener() {
        btn_initRTC.setOnClickListener(this);
        btn_destroyRTC.setOnClickListener(this);
        btn_RtcNetChanged.setOnClickListener(this);
        btn_RegisterAccount.setOnClickListener(this);
        btn_unRegisterAccount.setOnClickListener(this);

        btn_PlayVideo1.setOnClickListener(this);
        btn_RePlayVideo1.setOnClickListener(this);
        btn_DeviceSetting1.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_initRTC:
                String codeVersion = IPCController.getCoreRTCVersion();
                Log.d("PML", "codeVersion is:" + codeVersion);
                IPCController.setLog(true);
                IPCController.initRTCAsync(null);
                // IPCController.initRTCAsync(null);
                // mViEAndroidGLES20.setVisibility(View.VISIBLE);
                // IPCController.setRender("", (ViEAndroidGLES20)
                // mViEAndroidGLES20);
                // IPCController.setRenderFlag(et_DesEndPoint.getText().toString().trim());
                break;
            case R.id.btn_destroyRTC:
                IPCController.destroyRTCAsync(null);
                //	IPCController.destroyRTC();
                break;
            case R.id.btn_RtcNetChanged:
                IPCController.RtcNetChanged();
                break;
            case R.id.btn_RegisterAccount:
                IPCResultCallBack registerAccountAsyncCallback = new IPCResultCallBack() {
                    @Override
                    public void getResult(int result) {
                        System.out.printf("registerAccountAsyncCallback result is:"
                                + (result == 0 ? "TRUE" : "FALSE"));
                    }
                };
                IPCController.registerAccountAsync(registerAccountAsyncCallback,
                        sipUid, userSipPwd, sipDomain
                                .toString().trim());
                break;
            case R.id.btn_unRegisterAccount:
                IPCController.unRegisterAccountAsync(null);
                break;
            case R.id.btn_PlayVideo1: {
                Intent it = new Intent(this, PlayVideoActivity.class);
                it.putExtra("DestID", deviceId);
                it.putExtra("DestPoint", deviceDomain);
                startActivity(it);
            }
            break;
            case R.id.btn_RePlayVideo1: {
                Intent it = new Intent(this, ReplayVideoActivity.class);
                it.putExtra("DestID", deviceId);
                it.putExtra("DestPoint", deviceDomain);
                startActivity(it);
            }
            break;
            case R.id.btn_DeviceSetting1: {
                Intent it = new Intent(this, DeviceSettingActivity.class);
                it.putExtra("DestID", deviceId);
                it.putExtra("DestPoint", deviceDomain);
                startActivity(it);
            }
            break;

            default:
                break;
        }
    }
}
