package com.moecheng.cyborgcare.bluetooth.format;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

import com.moecheng.cyborgcare.BuildConfig;

/**
 * Created by wangchengcheng on 2017/11/29.
 */

public class Utils {

    private static final String TAG = "SPP_TERMINAL";

    /**
     * 将调试消息输出到日志的一般方法
     */
    public static void log(String message) {
        if (BuildConfig.DEBUG) {
            if (message != null) Log.i(TAG, message);
        }
    }
    // ============================================================================


    /**
     * 将十六进制命令转换为字符串进行显示
     */
    public static String printHex(String hex) {
        StringBuilder sb = new StringBuilder();
        int len = hex.length();
        try {
            for (int i = 0; i < len; i += 2) {
                sb.append("0x").append(hex.substring(i, i + 2)).append(" ");
            }
        } catch (NumberFormatException e) {
            log("printHex NumberFormatException: " + e.getMessage());

        } catch (StringIndexOutOfBoundsException e) {
            log("printHex StringIndexOutOfBoundsException: " + e.getMessage());
        }
        return sb.toString();
    }
    // ============================================================================


    /**
     * 将输入的ASCII命令翻译成十六进制字节
     * @param hex - 十六进制
     * @return - byte数组
     */
    public static byte[] toHex(String hex) {
        int len = hex.length();
        byte[] result = new byte[len];
        try {
            int index = 0;
            for (int i = 0; i < len; i += 2) {
                result[index] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
                index++;
            }
        } catch (NumberFormatException e) {
            log("toHex NumberFormatException: " + e.getMessage());

        } catch (StringIndexOutOfBoundsException e) {
            log("toHex StringIndexOutOfBoundsException: " + e.getMessage());
        }
        return result;
    }
    // ============================================================================


    /**
     * 该方法将两个数组合并为一个
     */
    public static byte[] concat(byte[] A, byte[] B) {
        byte[] C = new byte[A.length + B.length];
        System.arraycopy(A, 0, C, 0, A.length);
        System.arraycopy(B, 0, C, A.length, B.length);
        return C;
    }
    // ============================================================================


    /**
     * 模
     */
    public static int mod(int x, int y) {
        int result = x % y;
        return result < 0 ? result + y : result;
    }
    // ============================================================================

    /**
     * 计算校验和
     */
    public static String calcModulo256(String command)
    {
        int crc = 0;
        for (int i = 0; i< command.length(); i++) {
            crc += (int)command.charAt(i);
        }
        return Integer.toHexString(Utils.mod(crc, 256));
    }
    // ============================================================================

    /**
     * 使用正确的颜色为文本着色
     */
    public static String mark(String text, String color) {
        return "<font color=" + color + ">" + text + "</font>";
    }
    // ============================================================================

    /**
     * 获取存储在配置中的String
     */
    public static String getPrefence(Context context, String item) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(item, TAG);
    }
    // ============================================================================


    /**
     * 从设置获取标志
     */
    public static boolean getBooleanPrefence(Context context, String tag) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getBoolean(tag, true);
    }
    // ============================================================================


    /**
     * 过滤输入字段
     */
    // ============================================================================
    public static class InputFilterHex implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (!Character.isDigit(source.charAt(i))
                        && source.charAt(i) != 'A' && source.charAt(i) != 'D'
                        && source.charAt(i) != 'B' && source.charAt(i) != 'E'
                        && source.charAt(i) != 'C' && source.charAt(i) != 'F'
                        ) {
                    return "";
                }
            }
            return null;
        }
    }
    // ============================================================================
}
