package io.interact.mohamedbenarbia.benmycontacts.services;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import io.interact.mohamedbenarbia.benmycontacts.Interaction.UserInteraction;

/**
 * Tracks only outgoing sms
 */
public class OutgoingSmsObserver extends ContentObserver {
    String LOG_TAG=OutgoingSmsObserver.class.getName();

    String last_id=null;

    /**
     * Constant from Android SDK
     */
    private static final int MESSAGE_TYPE_SENT = 2;
    Context context;
    public OutgoingSmsObserver(Handler handler,Context c) {
        super(handler);
        context = c;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        Log.e(LOG_TAG,"noticed a new outgoing sms ");
        Uri uriSMSURI = Uri.parse("content://sms");
        Cursor cur = context.getContentResolver().query(uriSMSURI,
                null, null, null, null);

        if (cur.moveToNext()) {
            String protocol = cur.getString(cur.getColumnIndex("protocol"));
            int type = cur.getInt(cur.getColumnIndex("type"));
            String current_id=cur.getString(cur.getColumnIndex("_id"));
            Log.e(LOG_TAG,"current "+current_id +" last "+last_id);
            String status=cur.getString(cur.getColumnIndex("status"));
            Log.e(LOG_TAG,"status "+status);
            // Only processing outgoing sms event & only when it
            // is sent successfully (available in SENT box).
            if (protocol == null && type == MESSAGE_TYPE_SENT && !current_id.equals(last_id) ) {
                int dateColumn = cur.getColumnIndex("date");
                int addressColumn = cur.getColumnIndex("address");
                //get the indexes of the needed columns
                String to = cur.getString(addressColumn);
                long now = cur.getLong(dateColumn);
                Log.e(LOG_TAG,"noticed a new outgoing sms towards "+to);
                //trigger a new interaction event for an outgoing sms
                UserInteraction inter = new UserInteraction("", "sms", "", "" + now, "OUTBOUND", "",to);
                Intent mServiceIntent = new Intent(context, NewInteractionHandlerService.class);
                mServiceIntent.putExtra("interaction",inter.toString());
                context.startService(mServiceIntent);
                last_id=current_id;
                //don't forget to close the cursor
                cur.close();
            }else {
                //do nothing
            }

        }


    }
}
