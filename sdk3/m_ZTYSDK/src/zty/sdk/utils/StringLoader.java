package zty.sdk.utils;

import java.util.HashMap;
import java.util.Map;

import zty.sdk.model.StringValue;

public class StringLoader {

    private static Map<StringValue, String> stringMap =
            new HashMap<StringValue, String>();

    static {
        stringMap.put(StringValue.initialize, "初始化中……");
        stringMap.put(StringValue.login, "正在登录……");
        stringMap.put(StringValue.register, "正在注册……");
        stringMap.put(StringValue.pay, "支付中……");
    }

    public static String getString(StringValue value) {
        return stringMap.get(value);
    }

}
