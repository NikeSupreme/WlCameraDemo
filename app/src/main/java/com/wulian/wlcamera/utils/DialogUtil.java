package com.wulian.wlcamera.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulian.wlcamera.R;
import com.wulian.wlcamera.customview.WLDialog;

import java.util.Locale;

/**
 * Created by hxc on 2017/5/9.
 */

public class DialogUtil {

    /**
     * V6通用Dialog (pos 和 neg)
     * @param mContext
     * @param title
     * @param message
     * @param ok
     * @param cancel
     * @param listen
     * @return
     */
    public static WLDialog showCommonDialog(Context mContext,
                                            String title, String message, String ok, String cancel,
                                            WLDialog.MessageListener listen){
        WLDialog dialog = null;
        WLDialog.Builder builder = new WLDialog.Builder(mContext);
        builder.setCancelOnTouchOutSide(false)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(ok)
                .setNegativeButton(cancel)
                .setListener(listen);
        dialog = builder.create();
        return dialog;
    }
    /**
     * 设置对话框的宽度
     **/
    public static void changeDialogWidth(Dialog dialog, Context mContext) {
        if (null != dialog) {
            WindowManager wm = (WindowManager) mContext
                    .getSystemService(Context.WINDOW_SERVICE);
            int screenWidth = wm.getDefaultDisplay().getWidth();
            WindowManager.LayoutParams params = dialog.getWindow()
                    .getAttributes();
            int width = mContext.getResources().getDimensionPixelSize(
                    R.dimen.register_margin_2);
            params.width = screenWidth - 2 * width;
            dialog.getWindow().setAttributes(params);
        }
    }

    /**
     * V6通用提示Dialog（底部只有一个button）
     * @param mContext
     * @param tip
     * @param message
     * @param ok
     * @param listen
     * @return
     */
    public static WLDialog showTipsDialog(Context mContext,
                                            String tip, String message, String ok,
                                            WLDialog.MessageListener listen){
        WLDialog dialog = null;
        WLDialog.Builder builder = new WLDialog.Builder(mContext);
        builder.setCancelOnTouchOutSide(false)
                .setCancelable(false)
                .setTitle(tip)
                .setMessage(message)
                .setPositiveButton(ok)
                .setListener(listen);
        dialog = builder.create();
        return dialog;
    }




    public static Dialog showProtectAreaTipDialog(Context mContext,
                                             boolean isCancel,
                                             final View.OnClickListener okOnclick) {
        View layout = LayoutInflater.from(mContext).inflate(
                R.layout.protect_area_tips, null);
        Dialog dialog = new Dialog(mContext, R.style.dialog_style_v5);
        dialog.setContentView(layout);
        dialog.setCancelable(isCancel);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        changeDialogWidth(dialog, mContext);
        TextView okBtn = (TextView) layout.findViewById(R.id.btn_positive);
        // 绑定事件
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != okOnclick) {
                    okOnclick.onClick(v);
                }
            }
        });
        return dialog;
    }

    public static WLDialog showWifiHintDialog(Context mContext, WLDialog.MessageListener listen) {
        WLDialog dialog = null;
        WLDialog.Builder builder = new WLDialog.Builder(mContext);
        builder.setCancelOnTouchOutSide(false)
                .setCancelable(false)
                .setMessage(mContext.getString(R.string.CateyePlayVideo_Download_Hint))
                .setPositiveButton(mContext.getResources().getString(R.string.CateyePlayVideo_Play))
                .setNegativeButton(mContext.getResources().getString(R.string.Cancel))
                .setListener(listen);
        dialog = builder.create();
        return dialog;
    }

    public static WLDialog showConfigOrBindDialog(Context mContext,String msg,String posMsg,String negMSg, WLDialog.MessageListener listen) {
        WLDialog dialog = null;
        WLDialog.Builder builder = new WLDialog.Builder(mContext);
        builder.setCancelOnTouchOutSide(false)
                .setCancelable(false)
                .setMessage(msg)
                .setPositiveButton(posMsg)
                .setNegativeButton(negMSg)
                .setListener(listen);
        dialog = builder.create();
        return dialog;
    }

    public static WLDialog showUnknownDeviceTips(Context mContext, WLDialog.MessageListener listen,String msg) {
        WLDialog dialog = null;
        WLDialog.Builder builder = new WLDialog.Builder(mContext);
        builder.setCancelOnTouchOutSide(false)
                .setCancelable(false)
                .setMessage(msg)
                .setPositiveButton(mContext.getString(R.string.Sure))
                .setListener(listen);
        dialog = builder.create();
        return dialog;
    }

    public static WLDialog showOtherUserBindTips(Context mContext, WLDialog.MessageListener listen,String msg) {
        WLDialog dialog = null;
        WLDialog.Builder builder = new WLDialog.Builder(mContext);
        builder.setCancelOnTouchOutSide(false)
                .setCancelable(false)
                .setMessage(msg)
                .setPositiveButton(mContext.getString(R.string.Tip_I_Known))
                .setListener(listen);
        dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        return dialog;
    }


}
