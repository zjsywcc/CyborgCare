package com.moecheng.cyborgcare.util;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.moecheng.cyborgcare.R;

import java.lang.reflect.Field;


/**
 * Created by wangchengcheng on 2017/11/20.
 */
@SuppressWarnings("NewApi")
public class Compat {

    private static final int VERSION = Build.VERSION.SDK_INT;

    public static int getColor(Context context, int id) {
        if (VERSION >= Build.VERSION_CODES.M) {
            return context.getResources().getColor(id,null);
        } else {
            return context.getResources().getColor(id);
        }
    }

    public static String getString(Context context, int id) {
        return context.getResources().getString(id);
    }

    /**
     * 计算颜色值
     *
     * @param color color值
     * @param alpha alpha值
     * @return 最终颜色
     */
    public static int calculateColorWithAlpha(int color, int alpha) {
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }

    /**
     * 获取状态栏高度
     *
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Activity activity) {
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return activity.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 修复 Dialog 的样式
     * 必须在 Dialog 显示后调用
     */
    public static void fixDialogStyle(Dialog dialog) {
        if (dialog == null) {
            return;
        }
        try {
            Resources res = dialog.getContext().getResources();
            int dialogTitleId = res.getIdentifier("android:id/alertTitle", null, null);
            int dialogDividerId = res.getIdentifier("android:id/titleDivider", null, null);
            int dialogMsgId = res.getIdentifier("android:id/message", null, null);
            int dialogBtn1Id = res.getIdentifier("android:id/button1", null, null);

            TextView title = (TextView) dialog.findViewById(dialogTitleId);
            View divider = dialog.findViewById(dialogDividerId);
            TextView msg = (TextView) dialog.findViewById(dialogMsgId);

            if (title != null) {
                title.setTextColor(getColor(dialog.getContext(), R.color.colorPrimary));
            }

            if (divider != null) {
                divider.setBackgroundColor(getColor(dialog.getContext(), R.color.colorPrimary));
            }

            if (msg != null) {
                msg.setLineSpacing(res.getDimension(R.dimen.l_dialog_content), 1);
                if (VERSION < 21) {
                    msg.setPadding(msg.getPaddingLeft(), msg.getPaddingTop(), msg.getPaddingRight(),
                            (int) res.getDimension(R.dimen.p_dialog_content_bottom));
                } else {
                    msg.setTextColor(ContextCompat.getColor(dialog.getContext(), R.color.textColorSecondary));
                }
            }

            View btn1 = dialog.findViewById(dialogBtn1Id);
            if (btn1 != null) {
                ViewGroup btnParent = (ViewGroup) btn1.getParent();
                btnParent.setMotionEventSplittingEnabled(false);
                View btns1 = btnParent.getChildAt(0);
                View btns2 = btnParent.getChildAt(1);
                View btns3 = btnParent.getChildAt(2);
                if (VERSION < 21) {
                    btns1.setBackgroundResource(R.drawable.button_selector);
                    btns2.setBackgroundResource(R.drawable.button_selector);
                    btns3.setBackgroundResource(R.drawable.button_selector);
                }
            }

            if (dialog instanceof DatePickerDialog) {
                fixDatePicker(((DatePickerDialog) dialog).getDatePicker());
            } else if (dialog instanceof TimePickerDialog) {
                fixTimePicker((TimePickerDialog) dialog);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void fixTimePicker(TimePickerDialog dialog) {
        if (VERSION >= 21) {
            return;
        }

        try {
            Field pickerField = dialog.getClass().getDeclaredField("mTimePicker");
            TimePicker picker = (TimePicker) pickerField.get(dialog);
            Field[] fields = new Field[3];
            try {
                fields[0] = TimePicker.class.getDeclaredField("mHourSpinner");
            }
            catch (NoSuchFieldException e) {
                fields[0] = TimePicker.class.getDeclaredField("mHourPicker");
            }
            try {
                fields[1] = TimePicker.class.getDeclaredField("mMinuteSpinner");
            }
            catch (NoSuchFieldException e) {
                fields[1] = TimePicker.class.getDeclaredField("mMinutePicker");
            }
            try {
                fields[2] = TimePicker.class.getDeclaredField("mAmPmSpinner");
            }
            catch (NoSuchFieldException e) {
                fields[2] = TimePicker.class.getDeclaredField("mAmPmPicker");
            }
            for (Field f : fields) {
                f.setAccessible(true);
                fixNumberPicker((NumberPicker) f.get(picker));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void fixDatePicker(DatePicker picker) {
        if (VERSION >= 21) {
            return;
        }
        try {
            Field[] fields = new Field[3];
            try {
                fields[0] = DatePicker.class.getDeclaredField("mDaySpinner");
            }
            catch (NoSuchFieldException e) {
                fields[0] = DatePicker.class.getDeclaredField("mDayPicker");
            }
            try {
                fields[1] = DatePicker.class.getDeclaredField("mMonthSpinner");
            }
            catch (NoSuchFieldException e) {
                fields[1] = DatePicker.class.getDeclaredField("mMonthPicker");
            }
            try {
                fields[2] = DatePicker.class.getDeclaredField("mYearSpinner");
            }
            catch (NoSuchFieldException e) {
                fields[2] = DatePicker.class.getDeclaredField("mYearPicker");
            }
            for (Field f : fields) {
                f.setAccessible(true);
                fixNumberPicker((NumberPicker) f.get(picker));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void fixNumberPicker(NumberPicker picker) {
        if (picker == null) return;
        try {
            Field f = NumberPicker.class.getDeclaredField("mSelectionDivider");
            f.setAccessible(true);
            f.set(picker, new ColorDrawable(getColor(picker.getContext(), R.color.colorPrimary)));

            Field virtualBg = NumberPicker.class.getDeclaredField("mVirtualButtonPressedDrawable");
            virtualBg.setAccessible(true);
            Drawable draw = new ColorDrawable(getColor(picker.getContext(), R.color.colorPrimary));
            draw.setAlpha(125);
            virtualBg.set(picker, draw);
            picker.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
