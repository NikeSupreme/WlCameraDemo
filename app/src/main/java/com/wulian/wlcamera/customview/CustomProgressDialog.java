package com.wulian.wlcamera.customview;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulian.wlcamera.R;
import com.wulian.wlcamera.utils.ToastUtil;


/**
 * Created by syf on 2017/2/23.
 */
public class CustomProgressDialog extends Dialog {
    public static final int DELAYMILLIS_5 = 5000;
    public static final int DELAYMILLIS_10 = 10000;
    public static final int DELAYMILLIS_15 = 15000;
    public static final int DELAYMILLIS_20 = 20000;
    public static final int DELAYMILLIS_25 = 25000;
    public static final int DELAYMILLIS_30 = 30000;
    public static final int DELAYMILLIS_40 = 40000;
    public static final int DELAYMILLIS_120 = 120000;
    public static final int MSG_DISMISS_DELAY = 1221;
    public static boolean isCancle = true;
    public static final int resultOk = 0;
    public static final int resultFail = -1;
    public static final int resultCancel = -2;
    private final Activity activity;
    private ImageView mImageView;
    private TextView mTextView;
    private Button mButton;
    private Animation animation;
    private Runnable dialogRunnable = new Runnable() {
        public void run() {
            CustomProgressDialog.isCancle = false;
            CustomProgressDialog.this.dismissProgressDialog(-1);
        }
    };
    private final Handler childHandler = new Handler(Looper.getMainLooper());
    private CustomProgressDialog.OnDialogDismissListener newDialogDismissListener = new CustomProgressDialog.OnDialogDismissListener() {
        public void onDismiss(CustomProgressDialog progressDialog, int result) {
            if(-1 == result) {
                try {
//                    CustomProgressDialog.this.showErrorDialog();
                    ToastUtil.single(getContext(),R.string.Http_Time_Out);
                } catch (Exception var4) {
                }

                CustomProgressDialog.isCancle = true;
            }

        }
    };
    private final OnCancelListener onCancelListener = new OnCancelListener() {
        public void onCancel(DialogInterface dialog) {
            CustomProgressDialog.this.dismissProgressDialog(-2);
        }
    };


    public CustomProgressDialog(Activity activity) {
        super(activity, R.style.customProgressDialog);
        this.activity = activity;
        this.initDialog(false);
    }

    public CustomProgressDialog(Activity activity, Boolean cancle) {
        super(activity, R.style.customProgressDialog);
        this.activity = activity;
        this.initDialog(cancle);
    }

    private void initDialog(Boolean isCancle) {
        View contentView = this.initContentView();
        this.setContentView(contentView);
        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.flags = 2;
        lp.dimAmount = 0.6F;
        lp.gravity = 17;
        this.setCancelable(isCancle);
        this.setOnCancelListener(this.onCancelListener);
    }

    private View initContentView() {
        View contentView = View.inflate(this.activity, R.layout.general_progress_dialog, (ViewGroup)null);
        this.mImageView = (ImageView)contentView.findViewById(R.id.loadingProgress);
        this.mTextView = (TextView)contentView.findViewById(R.id.textView_loadingmsg);
        this.mButton = (Button)contentView.findViewById(R.id.button_cancel);
        this.mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                CustomProgressDialog.this.cancel();
            }
        });
        this.animation = AnimationUtils.loadAnimation(this.activity, R.anim.progressdialog_anim);
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        this.animation.setInterpolator(linearInterpolator);
        return contentView;
    }

    public void setProgressDrawable(int res) {
        this.setProgressDrawable(this.getActivity().getResources().getDrawable(res));
    }

    public void setProgressDrawable(Drawable drawable) {
        this.mImageView.setImageDrawable(drawable);
    }

    public void setCancleDialog(String string) {
        this.mButton.setText(string);
        this.mButton.setVisibility(View.VISIBLE);
    }

    public void setCancleButtonBackground(int res) {
        this.mButton.setBackgroundResource(res);
    }

    public void setCancleButtonTextSize(float size) {
        this.mButton.setTextSize(size);
    }

    public void setCancleButtonTextColor(int color) {
        this.mButton.setTextColor(color);
    }

    public Activity getActivity() {
        return this.activity;
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus) {
            this.mImageView.startAnimation(this.animation);
        } else {
            super.onWindowFocusChanged(hasFocus);
        }

    }

    public void setOnDialogDismissListener(CustomProgressDialog.OnDialogDismissListener newDialogDismissListener) {
        this.newDialogDismissListener = newDialogDismissListener;
    }

    private void postDialogDelayRunnableMsg(int timeOut) {
        this.childHandler.postDelayed(this.dialogRunnable, (long)timeOut);
    }

    public void showDialog(String key) {
        this.showDialog(key, (String)null, activity.getResources().getInteger(R.integer.http_timeout));
    }

    public void showDialog(String key, String message) {
        this.showDialog(key, message, activity.getResources().getInteger(R.integer.http_timeout));
    }

    public void showDialog(String key, int timeOut) {
        this.showDialog(key, (String)null, timeOut);
    }

    public void showDialog(String key, String message, int timeOut) {
        try {
            if(!this.isShowing()) {
                this.postDialogDelayRunnableMsg(timeOut);
                this.show();
                if(!TextUtils.isEmpty(message)){
                    this.mTextView.setText(message);
                }
                this.mTextView.setGravity(17);
            } else if(!TextUtils.equals(message, this.mTextView.getText().toString())) {
                if(!TextUtils.isEmpty(message)){
                    this.mTextView.setText(message);
                }
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    public void dismissProgressDialog() {
        this.dismissProgressDialog(0);
    }

    public void dismissProgressDialog(int keyCode) {
        if(this.isShowing()) {
            try {
                this.childHandler.removeCallbacks(this.dialogRunnable);
                this.newDialogDismissListener.onDismiss(this, keyCode);
                this.dismiss();
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }

    }

    public interface OnDialogDismissListener {
        void onDismiss(CustomProgressDialog var1, int var2);
    }
}
