package zty.sdk.utils;

import android.content.Context;
import android.util.TypedValue;

public class MetricUtil {

    public static int getDip(Context context, float param) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, param, context.getResources().getDisplayMetrics());
    }

    /**
    * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
    */
    public static int dip2px(Context context, float dpValue) {
      final float scale = context.getResources().getDisplayMetrics().density;
      return (int) (dpValue * scale + 0.5f);
    }

    /**
    * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
    */
    public static int px2dip(Context context, float pxValue) {
      final float scale = context.getResources().getDisplayMetrics().density;
      int ret = (int) (pxValue / scale + 0.5f);
      return ret;
    } 

}
