package com.example.bai1training.base;

import android.content.Context;
import android.util.TypedValue;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by cuonglv on 8/16/2021
 */
public class Converter {

    private static DecimalFormat digitalNumberDecimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);

    private static String a = "ĂẮẶẰÂẤẦẬăắằặâấầậÁÀẠáàạẲẴẳẵẨẪẩẫẢÃảã";
    private static String e = "éèẹÉÈẸÊẾỀỆêếềệẺẼẻẽỂỄểễ";
    private static String i = "íìịÍÌỊỈĨỉĩ";
    private static String o = "ÓÒỌóòọÔỐỒỘôốồộƠỚỜỢơớờợỎÕỏõỔỖổỗỞỖởỡ";
    private static String u = "ÚÙỤúùụƯỨỪỰưứừựỦŨủũỬỮửữ";
    private static String y = "ÝỲỴýỳỵỶỸỷỹ";
    private static String d = "Đđ";

    /**
     * convert 1000 to 1.000đ
     *
     * @param value
     * @return
     */
    public static String convertThousandMoneyToStringCommaDong(int value) {
        NumberFormat formatter = new DecimalFormat("#,###");
        StringBuilder builder = new StringBuilder();
        if (value == 0)
            return builder.append("0").append("đ").toString();
        if (value >= 1000) {
            builder.append(formatter.format(value).replace(",", ".")).append("đ");
        } else builder.append(value).append("đ");
        return builder.toString();
    }

    /**
     * convert 1000 to 1.000
     *
     * @param value
     * @return
     */
    public static String convertThousandMoneyToStringComma(int value) {
        NumberFormat formatter = new DecimalFormat("#,###");
        StringBuilder builder = new StringBuilder();
        if (value == 0)
            return builder.append("0").toString();
        if (value >= 1000) {
            builder.append(formatter.format(value).replace(",", "."));
        } else builder.append(value);
        return builder.toString();
    }

    /**
     * convert 1000 to 1.000đ
     *
     * @param value
     * @return
     */
    public static String convertThousandMoneyToStringCommaDong(Double value) {
        NumberFormat formatter = new DecimalFormat("#,###");
        StringBuilder builder = new StringBuilder();
        if (value == 0)
            return builder.append("0").append("đ").toString();
        if (value >= 1000) {
            builder.append(formatter.format(value).replace(",", ".")).append("đ");
        } else builder.append(value).append("đ");
        return builder.toString();
    }

    /**
     * convert 1234 to 1.2k
     *
     * @param value
     * @return
     */
    public static String convertThousandSaleToString(int value) {
        StringBuilder builder = new StringBuilder();
        if (value == 0) return builder.append("0").toString();
        if (value >= 1000 && value <= 1099) {
            builder.append(value / 1000);
            builder.append("k");
        } else if (value > 1099) {
            builder.append(value / 1000);
            builder.append(",");
            builder.append(value % 1000 / 100);
            builder.append("k");
        } else builder.append(value);
        return builder.toString();
    }

    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static Double getOneDigitalNumberAfterComma(Double value) {
        digitalNumberDecimalFormat.applyPattern("#.#");
        digitalNumberDecimalFormat.setRoundingMode(RoundingMode.FLOOR);
        return Double.valueOf(digitalNumberDecimalFormat.format(value));
    }

    public static String convertStringUTF8(String str) {
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            tmp.append(convertCharUTF8(str.charAt(i)));
        }
        return tmp.toString();
    }

    public static boolean hasUTF8(char ch) {
        String tmp = "" + ch;
        return a.contains(tmp) || e.contains(tmp) || i.contains(tmp)
                || o.contains(tmp) || u.contains(tmp) || y.contains(tmp) || d.contains(tmp);
    }

    public static char convertCharUTF8(char ch) {
        String tmp = "" + ch;
        if (a.contains(tmp)) return 'A';
        else if (e.contains(tmp)) return 'E';
        else if (i.contains(tmp)) return 'I';
        else if (o.contains(tmp)) return 'O';
        else if (u.contains(tmp)) return 'U';
        else if (y.contains(tmp)) return 'Y';
        else if (d.contains(tmp)) return 'D';
        else return Character.toUpperCase(ch);
    }

    public static  String convertStringToDate(String date) {
        String displayDate = "";
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
            Date newDate = null;
            newDate = spf.parse(date);
            spf = new SimpleDateFormat("dd/MM/yyyy");
            date = spf.format(newDate);
            displayDate = date;
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            displayDate = date;
        }
        return displayDate;
    }
}