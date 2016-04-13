package zty.sdk.views;

import zty.sdk.utils.Helper;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

public class ExDialog extends Dialog
{
  private Context context;
  private Window window = null;

  public ExDialog(Context paramContext, String paramString)
  {
    super(paramContext, Helper.getResStyle(paramContext, paramString));
    this.context = paramContext;
    init();
  }

  private float getDensity(Context paramContext)
  {
    return paramContext.getResources().getDisplayMetrics().density;
  }

  private void init()
  {
    requestWindowFeature(1);
    float f = getDensity(this.context);
    this.window = getWindow();
    WindowManager.LayoutParams localLayoutParams = this.window.getAttributes();
    localLayoutParams.width = (int)(200.0F * f);
    localLayoutParams.height = (int)(200.0F * f);
    //localLayoutParams.alpha = 1.0F;
    //localLayoutParams.gravity = 16;
    this.window.setAttributes(localLayoutParams);
  }
}