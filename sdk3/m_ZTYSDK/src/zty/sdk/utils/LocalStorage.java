package zty.sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorage {

    public static final String STORAGE_NAME = "ZTY";

    private SharedPreferences sharedPreferences;

    private LocalStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
    }

    public static LocalStorage getInstance(Context context) {
        return new LocalStorage(context);
    }

    public boolean hasKey(String key) {
        return !StringUtil.isEmpty(key)
                && sharedPreferences.contains(key);
    }

    public void putString(String key, String value) {

        if (StringUtil.isEmpty(key)
        		/* || StringUtil.isEmpty(value)*/)
            return;
        sharedPreferences.edit().putString(key, value).commit();

    }

    /**
     * 
     * @param key
     * @param defaultValues
     * @return
     */
    public String getString(String key, String... defaultValues) {

        if (StringUtil.isEmpty(key)) return null;
        String defaultValue =
                defaultValues.length >= 1 ?
                        defaultValues[0] : "";
        return sharedPreferences.getString(key, defaultValue);

    }

}
