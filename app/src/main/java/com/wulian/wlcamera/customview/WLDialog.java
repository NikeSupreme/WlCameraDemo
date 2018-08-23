package com.wulian.wlcamera.customview;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wulian.wlcamera.R;
import com.wulian.wlcamera.utils.StringUtil;

import static android.content.Context.WINDOW_SERVICE;

public class WLDialog extends Dialog {
    public static final String TAG = WLDialog.class.getSimpleName();

    private Context context;

    public WLDialog(Context context) {
        super(context);
        this.context = context;
    }

    public WLDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public Context getActivityContext() {
        return context;
    }

    public interface MessageListener {
        void onClickPositive(View var1, String msg);

        void onClickNegative(View var1);
    }

    public static class Builder {
        private static final int MAX_EDITTEXT_LENGTH = 20;
        private Context mContext;
        private String mTitleText;
        private String subTitleText;
        private String mMessageText;
        private String mPositiveButtonText;
        private String mNegativeButtonText;
        private String mEditTextHintText;
        private String mEditTextText;
        private View mContentView;
        private TextView positiveButton;
        private View editTextView;
        private EditText etUserInfo;
        private WLDialog.MessageListener mListener;
        private boolean cancelOnTouchOutSide = true;
        private boolean cancelable = true;
        private int mWidth = -1;
        private int mHeight = -1;
        private float mWidthPercent = 0.8F;
        private float mHeightPercent = -1.0F;
        private boolean isDismission = true;
        private boolean inputPassword = false;

        public Builder(Context context) {
            this.mContext = context;
        }

        public WLDialog.Builder setSubTitleText(String mBottomTitleText) {
            this.subTitleText = mBottomTitleText;
            return this;
        }

        public WLDialog.Builder setSubTitleText(int mBottomTitleText) {
            this.subTitleText = this.mContext.getString(mBottomTitleText);
            return this;
        }

        public WLDialog.Builder setTitle(int title) {
            this.mTitleText = this.mContext.getString(title);
            return this;
        }

        public WLDialog.Builder setTitle(String title) {
            this.mTitleText = title;
            return this;
        }

        public WLDialog.Builder setDismissAfterDone(boolean isDismiss) {
            this.isDismission = isDismiss;
            return this;
        }

        public WLDialog.Builder setMessage(String message) {
            this.mMessageText = message;
            return this;
        }

        public WLDialog.Builder setMessage(int message) {
            this.mMessageText = this.mContext.getString(message);
            return this;
        }

        public boolean isCancelOnTouchOutSide() {
            return this.cancelOnTouchOutSide;
        }

        public WLDialog.Builder setCancelOnTouchOutSide(boolean cancelOnTouchOutSide) {
            this.cancelOnTouchOutSide = cancelOnTouchOutSide;
            return this;
        }

        public WLDialog.Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public WLDialog.Builder setContentView(int resLayout) {
            View view = LayoutInflater.from(this.mContext).inflate(resLayout, (ViewGroup) null);
            this.mContentView = view;
            return this;
        }

        public WLDialog.Builder setContentView(View v) {
            this.mContentView = v;
            return this;
        }

        public WLDialog.Builder setPositiveButton(int positiveButtonText) {
            this.mPositiveButtonText = this.mContext.getString(positiveButtonText);
            return this;
        }

        public WLDialog.Builder setPositiveButton(String positiveButtonText) {
            this.mPositiveButtonText = positiveButtonText;
            return this;
        }

        public WLDialog.Builder setNegativeButton(int negativeButtonText) {
            this.mNegativeButtonText = this.mContext.getString(negativeButtonText);
            return this;
        }

        public WLDialog.Builder setNegativeButton(String negativeButtonText) {
            this.mNegativeButtonText = negativeButtonText;
            return this;
        }

        public WLDialog.Builder setEditTextHint(int mEditTextHintText) {
            this.mEditTextHintText = this.mContext.getString(mEditTextHintText);
            return this;
        }

        public WLDialog.Builder setEditTextHint(String mEditTextHintText) {
            this.mEditTextHintText = mEditTextHintText;
            return this;
        }

        public WLDialog.Builder inputPassword(boolean inputPassword) {
            this.inputPassword = inputPassword;
            return this;
        }

        public WLDialog.Builder setEditTextText(int mEditTextText) {
            this.mEditTextText = this.mContext.getString(mEditTextText);
            return this;
        }

        public WLDialog.Builder setEditTextText(String mEditTextText) {
            this.mEditTextText = mEditTextText;
            return this;
        }

        public WLDialog.MessageListener getListener() {
            return this.mListener;
        }

        public WLDialog.Builder setListener(WLDialog.MessageListener listener) {
            this.mListener = listener;
            return this;
        }

        public int getWidth() {
            return this.mWidth;
        }

        public WLDialog.Builder setWidth(int width) {
            this.mWidth = width;
            return this;
        }

        public int getHeight() {
            return this.mHeight;
        }

        public WLDialog.Builder setHeight(int height) {
            this.mHeight = height;
            return this;
        }

        public float getWidthPercent() {
            return this.mWidthPercent;
        }

        public WLDialog.Builder setWidthPercent(float widthPercent) {
            this.mWidthPercent = widthPercent;
            return this;
        }

        public float getHeightPercent() {
            return this.mHeightPercent;
        }

        public WLDialog.Builder setHeightPercent(float heightPercent) {
            this.mHeightPercent = heightPercent;
            return this;
        }

        public WLDialog create() {
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            WLDialog dialog = new WLDialog(this.mContext, R.style.dialog_style_v5);
            View view = inflater.inflate(R.layout.layout_dialog_v6, (ViewGroup) null);
            dialog.setContentView(view, new ViewGroup.LayoutParams(-1, -1));
            this.initTitle(dialog, view);
            this.initSubTitle(dialog, view);
            this.initContent(dialog, view);
            this.initButton(dialog, view);
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager manager = (WindowManager) this.mContext.getSystemService(WINDOW_SERVICE);
            manager.getDefaultDisplay().getMetrics(dm);
            this.initSize(dialog, dm.widthPixels, dm.heightPixels);
            dialog.setOnDismissListener(new OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    Builder.this.hideInput(Builder.this.mContext);
                }
            });
            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(this.cancelOnTouchOutSide);
            dialog.setCancelable(this.cancelable);
            return dialog;
        }

        private void initSubTitle(WLDialog dialog, View view) {
            TextView titleBottomTextView = (TextView) view.findViewById(R.id.dialog_tv_title_bottom);
            if (TextUtils.isEmpty(this.subTitleText)) {
                titleBottomTextView.setVisibility(View.GONE);
            } else {
                titleBottomTextView.setVisibility(View.VISIBLE);
                titleBottomTextView.setText(this.subTitleText);
            }

        }

        private void initTitle(WLDialog dialog, View view) {
            TextView titleTextView = (TextView) view.findViewById(R.id.dialog_tv_title);
            if (TextUtils.isEmpty(this.mTitleText)) {
                titleTextView.setVisibility(View.GONE);
            } else {
                titleTextView.setText(this.mTitleText);
            }

        }

        private void initContent(WLDialog dialog, View view) {
            if (!TextUtils.isEmpty(this.mMessageText)) {
                TextView lcontentLayout = (TextView) view.findViewById(R.id.dialog_tv_message);
                lcontentLayout.setText(this.mMessageText);
            } else if (!TextUtils.isEmpty(this.mEditTextHintText) ||
                    !TextUtils.isEmpty(this.mEditTextText)) {
                initEditTextView();
                LinearLayout lcontentLayout1 = (LinearLayout) view.findViewById(R.id.dialog_layout_content);
                lcontentLayout1.removeAllViews();
                lcontentLayout1.addView(this.editTextView, new ViewGroup.LayoutParams(-1, -1));
            } else if (this.mContentView != null) {
                LinearLayout lcontentLayout1 = (LinearLayout) view.findViewById(R.id.dialog_layout_content);
                lcontentLayout1.removeAllViews();
                lcontentLayout1.addView(this.mContentView, new ViewGroup.LayoutParams(-1, -1));
            }

        }

        private void initButton(final WLDialog dialog, final View view) {
            boolean noButton = true;
            positiveButton = (TextView) view.findViewById(R.id.dialog_btn_positive);
            if (this.mPositiveButtonText != null) {
                noButton = false;

                positiveButton.setText(this.mPositiveButtonText);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (Builder.this.mListener != null) {
                            if (!TextUtils.isEmpty(mEditTextHintText) ||
                                    !TextUtils.isEmpty(mEditTextText)) {
                                Builder.this.mListener.onClickPositive(view, etUserInfo.getEditableText().toString().trim());
                            } else {
                                Builder.this.mListener.onClickPositive(view, null);
                            }

                        }

                        if (Builder.this.isDismission) {
                            dialog.dismiss();
                        }

                    }
                });
            } else {
                positiveButton.setVisibility(View.GONE);
            }

            TextView negativeButton = (TextView) view.findViewById(R.id.dialog_btn_negative);
            if (this.mNegativeButtonText != null) {
                noButton = false;
                negativeButton.setText(this.mNegativeButtonText);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (Builder.this.mListener != null) {
                            Builder.this.mListener.onClickNegative(view);
                        }

                        dialog.dismiss();
                    }
                });
            } else {
                negativeButton.setVisibility(View.GONE);
            }

            View divider = view.findViewById(R.id.dialog_divider);
            if (!TextUtils.isEmpty(this.mNegativeButtonText) && !TextUtils.isEmpty(this.mPositiveButtonText)) {
                divider.setVisibility(View.VISIBLE);
            } else if (TextUtils.isEmpty(this.mNegativeButtonText) && TextUtils.isEmpty(this.mPositiveButtonText)) {
                divider.setVisibility(View.GONE);
            } else {
                divider.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(this.mNegativeButtonText)) {
                    negativeButton.setBackgroundResource(R.drawable.dialog_btn);
                } else {
                    positiveButton.setBackgroundResource(R.drawable.dialog_btn);
                }
            }

            if (noButton) {
                view.findViewById(R.id.dialog_layout_btn).setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(this.mEditTextHintText) &&
                    TextUtils.isEmpty(this.mEditTextText)) {
                setPositiveClickable(false);
            } else {
                setPositiveClickable(true);
            }
        }

        public void hideInput(Context context) {
            try {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && imm.isActive()) {
                    Activity activity = (Activity) context;
                    imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
                }
            } catch (Exception var4) {
                ;
            }

        }

        private void initSize(WLDialog dialog, int screenWidth, int screenHeight) {
            WindowManager.LayoutParams winParams = dialog.getWindow().getAttributes();
            if (this.mHeight > 0) {
                winParams.height = this.mHeight;
            } else if (this.mHeightPercent > 0.0F) {
                winParams.height = (int) (this.mHeightPercent * (float) screenHeight);
            }

            if (this.mWidth > 0) {
                winParams.width = this.mWidth;
            } else if (this.mWidthPercent > 0.0F) {
                winParams.width = (int) (this.mWidthPercent * (float) screenWidth);
            }

            dialog.getWindow().setAttributes(winParams);
        }

        private void initEditTextView() {
            editTextView = LayoutInflater.from(mContext).inflate(R.layout.dialog_user_info, null);
            etUserInfo = (EditText) editTextView.findViewById(R.id.et_user_info);
            //屏蔽空格
            InputFilter filter = new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    String s = StringUtil.deleteEmoji(source.toString());
                    if (TextUtils.equals(s, source)) {
                        return null;
                    }
                    return s;
                }
            };
            etUserInfo.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(MAX_EDITTEXT_LENGTH)});
            if (!TextUtils.isEmpty(mEditTextHintText)) {
                etUserInfo.setHint(mEditTextHintText);
            }
            if (!TextUtils.isEmpty(mEditTextText)) {
                etUserInfo.setText(mEditTextText);
                etUserInfo.setSelection(etUserInfo.length());
            }
            if (inputPassword) {
                etUserInfo.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                etUserInfo.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            etUserInfo.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Drawable drawable = etUserInfo.getCompoundDrawables()[2];
                    if (drawable == null) {
                        // don't have end drawable
                        return false;
                    }

                    // 点击了 输入框中 右边的 x
                    if (motionEvent.getX() > etUserInfo.getWidth()
                            - etUserInfo.getPaddingRight()
                            - drawable.getIntrinsicWidth()) {
                        etUserInfo.setText("");
                        return true;
                    }
                    return false;
                }
            });
            etUserInfo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (TextUtils.isEmpty(s)) {
                        setPositiveClickable(false);
                    } else {
                        setPositiveClickable(true);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        private void setPositiveClickable(boolean clickable) {
            if (clickable) {
                positiveButton.setClickable(true);
                positiveButton.setTextColor(mContext.getResources().getColor(R.color.v6_text_green));
            } else {
                positiveButton.setClickable(false);
                positiveButton.setTextColor(mContext.getResources().getColor(R.color.v6_text_gray_light));
            }
        }
    }
}
