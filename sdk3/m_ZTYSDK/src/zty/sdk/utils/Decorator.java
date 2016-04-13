package zty.sdk.utils;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.widget.ImageView;

public class Decorator {

    public static void setBackground(View view, Drawable drawable) {
        view.setBackgroundDrawable(drawable);
    }

    public static void setImage(ImageView imageView, Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }


    public static void setStateImage(View view, Drawable drawable1, Drawable drawable2, Drawable drawable3) {

        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, drawable2);
        stateListDrawable.addState(new int[]{android.R.attr.state_focused, android.R.attr.state_enabled}, drawable2);
        stateListDrawable.addState(new int[]{android.R.attr.state_enabled}, drawable1);
        stateListDrawable.addState(new int[]{android.R.attr.state_focused}, drawable2);
        stateListDrawable.addState(new int[]{}, drawable3);
        view.setBackgroundDrawable(stateListDrawable);

    }

    public static void setStateImage(View view, Drawable drawable1, Drawable drawable2, Drawable drawable3, Drawable drawable4) {

        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_selected, android.R.attr.state_enabled}, drawable4);
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, drawable2);
        stateListDrawable.addState(new int[]{android.R.attr.state_focused, android.R.attr.state_enabled}, drawable2);
        stateListDrawable.addState(new int[]{android.R.attr.state_enabled}, drawable1);
        stateListDrawable.addState(new int[]{android.R.attr.state_focused}, drawable2);
        stateListDrawable.addState(new int[]{}, drawable3);
        view.setBackgroundDrawable(stateListDrawable);

    }
   

}
