package io.interact.mohamedbenarbia.benmycontacts.services;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * Service that enables the outgoing sms observer
 */
public class SmsService extends Service {

    OutgoingSmsObserver myobserver;
    String LOG_TAG=SmsService.class.getName();
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(LOG_TAG, "on create");

        myobserver= new OutgoingSmsObserver(new Handler(), this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //register an observer to listen for outgoing sms
        ContentResolver contentResolver = this.getContentResolver();
        contentResolver.registerContentObserver(Uri.parse("content://sms"), true, myobserver);

        return START_STICKY;

    }

    /**
     * we don't need to bind anything so we return null
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
