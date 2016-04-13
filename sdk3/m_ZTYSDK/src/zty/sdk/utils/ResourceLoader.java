package zty.sdk.utils;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.InputStream;

import zty.sdk.game.Constants;

public class ResourceLoader {

    public static InputStream getInputStream(String resourceName) {

        String resource = "mobi/zty/sdk/assets/" + resourceName;
        try {
            return ResourceLoader.class.getClassLoader().getResourceAsStream(resource);
        } catch (Exception ex) {
            Log.e(Constants.TAG, "no resource file:" + resource);
        }
        return null;

    }

    public static Drawable getBitmapDrawable(String resourceName) {

        Drawable drawable = null;
        String resource = "mobi/zty/sdk/assets/" + resourceName;
        try {
            InputStream is = ResourceLoader.class.getClassLoader().getResourceAsStream(resource);
            drawable = BitmapDrawable.createFromStream(is, resource);
        } catch (Exception ex) {
            Log.e(Constants.TAG, "no resource file:" + resource);
        }
        return drawable;

    }

    public static Drawable getBitmapDrawableFromAssets(Context context, String resourceName) {

        Drawable drawable = null;
        try {
            InputStream is = context.getAssets().open(resourceName);
            drawable = is != null ? BitmapDrawable.createFromStream(is, resourceName) : null;
        } catch (Exception ex) {
            Log.e(Constants.TAG, "no resource file from assets:" + resourceName);
        }
        
        return drawable;

    }

    public static Drawable getNinePatchDrawable(String resourceName) {

        Drawable drawable = null;
        String resource = "mobi/zty/sdk/assets/" + resourceName;
        try {
            InputStream is = ResourceLoader.class.getClassLoader().getResourceAsStream(resource);
            drawable = NinePatchTool.decodeDrawableFromStream(is);
        } catch (Exception ex) {
            Log.e(Constants.TAG, "no resource file:" + resource);
        }
        return drawable;

    }

}
