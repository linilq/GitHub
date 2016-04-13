package zty.sdk.listener;
@Deprecated
public interface GameSDKPaymentListener {

    public void onPayFinished(int amount);

    public void onPayCancelled();

}
