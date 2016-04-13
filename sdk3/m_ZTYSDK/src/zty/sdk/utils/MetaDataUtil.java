package zty.sdk.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class MetaDataUtil {

    public static String getString(Context context, String key, String defaultValue) {

        try {

            PackageManager packageManager = context.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo appi = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                if (appi != null) {
                    Bundle bundle = appi.metaData;
                    if (bundle != null) {
                        Object value = bundle.get(key);
                        return (value == null) ? defaultValue : String.valueOf(value);
                    }
                }
            }
            return defaultValue;

        } catch (PackageManager.NameNotFoundException e) {
            return defaultValue;
        }

    }

    public static int getInt(Context context, String key, int defaultValue) {
        try {
            return Integer.parseInt(getString(context, key, String.valueOf(defaultValue)));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

}
