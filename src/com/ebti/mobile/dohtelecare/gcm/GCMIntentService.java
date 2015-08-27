package com.ebti.mobile.dohtelecare.gcm;



import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.ebti.mobile.dohtelecare.R;
import com.ebti.mobile.dohtelecare.activity.LoginActivity;
import com.google.android.gcm.GCMBaseIntentService;
import static  com.ebti.mobile.dohtelecare.gcm.CommonUtilities.displayMessage;


public class GCMIntentService extends GCMBaseIntentService{

    private static final String INFO = "info";
    private static  int notice_id= 0;

    @Override
    protected void onError(Context arg0, String arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        // TODO Auto-generated method stub
        /*Called when your server sends a message to GCM, and GCM delivers it to the device.
         *  If the message has a payload, its contents are available as extras in the intent.*/
        Log.i("info", "onMessage");
        Log.i("info", "loginID:" + intent.getExtras().getString("loginID"));
        Log.i("info", "message:" + intent.getExtras().getString("message"));
        displayMessage(context,intent.getExtras());

        generateNotification(context, intent.getExtras().getString("message"),intent);

    }

    private  static void generateNotification(Context context, String message,
            Intent intent_GCM) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);

        String title = context.getString(R.string.app_name);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(when);
        calendar.add(Calendar.SECOND, 10);

        SimpleDateFormat sdFormat = new SimpleDateFormat("MM/dd HH:mm:ss");

        RemoteViews contentView = new RemoteViews( context.getPackageName() , R.layout.custom_notification );
        contentView.setTextViewText(R.id.title, title );
        contentView.setTextViewText(R.id.getTime, sdFormat.format(calendar.getTime())+"" );
        contentView.setTextViewText(R.id.text, message );
        notification.contentView = contentView;

//		Intent notificationIntent = new Intent(context, LoginActivity.class);
        Intent notificationIntent = new Intent();

//		if (intent_GCM.getExtras() != null) {
//			notificationIntent.putExtras(intent_GCM.getExtras());
//		}

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
        notification.contentIntent = intent;

//        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(notice_id++, notification);
    }

    @Override
    protected void onRegistered(Context context, String registerID) {
        // TODO Auto-generated method stub
        Log.i(INFO, "on Registered ID: \n" + registerID );

    }

    @Override
    protected void onUnregistered(Context context, String registerID) {
        // TODO Auto-generated method stub
        Log.i(INFO, "on Unregistered" );
//		if( GCMRegistrar.isRegisteredOnServer(context) )
//		{
//
//		}
    }

}
