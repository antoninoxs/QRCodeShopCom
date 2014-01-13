package it.torvergata.mp;

import it.torvergata.mp.activity.tab.TabsFragmentActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
 
import com.google.android.gcm.GCMBaseIntentService;
import static it.torvergata.mp.Const.SENDER_ID;
import static it.torvergata.mp.Const.displayMessage;
  
public class GCMIntentService extends GCMBaseIntentService {
    public GCMIntentService() {
        super(SENDER_ID);
    }
    @Override
    protected void onRegistered(Context context, String registrationId){
        ServerUtilities.register(context, registrationId);
    }
    @Override
    protected void onUnregistered(Context context, String registrationId){
        ServerUtilities.unregister(context, registrationId);
    }
    @Override
    protected void onMessage(Context context, Intent intent) {
        String message = intent.getExtras().getString("price");
        displayMessage(context, message);
        generateNotification(context, message);
    }
    @Override
    protected void onDeletedMessages(Context context, int total) {
    }
    @Override
    public void onError(Context context, String errorId) {
    }
    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        return super.onRecoverableError(context, errorId);
    }
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        
        String[] items = message.split(",");
        String state=items[1];
        String stateOrderText=GenericFunctions.convertOrderState(Integer.parseInt(state));
        
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context,TabsFragmentActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        long[] pattern = {100,1000};
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(icon).setContentTitle(title).setContentText("Aggiornamento stato ordine: "+stateOrderText);
        mBuilder.setContentIntent(intent).setVibrate(pattern).setSound(uri);
        notificationManager.notify(0, mBuilder.build());
        
//        Notification notification = new Notification(icon, message, when);
//        notification.defaults |= Notification.DEFAULT_VIBRATE;
//        String title = context.getString(R.string.app_name);
//        Intent notificationIntent = new Intent(context,MainActivity1.class);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
//        notification.setLatestEventInfo(context, title, message, intent);
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        notification.defaults |= Notification.DEFAULT_SOUND;
//        notificationManager.notify(0, notification);
    }
}