package io.interact.mohamedbenarbia.benmycontacts.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import io.interact.mohamedbenarbia.benmycontacts.Interaction.UserInteraction;

/**
 * Register as a broadcast receiver service that listens in background to incoming and outgoing call events.
 */
public class CallInteractionsListener extends BroadcastReceiver {
    final String LOG_TAG = CallInteractionsListener.class.getName();

    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static long callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;  //because the passed incoming is only valid in ringing


    @Override
    public void onReceive(Context context, Intent intent) {

        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        } else {
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                state = TelephonyManager.CALL_STATE_IDLE;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                state = TelephonyManager.CALL_STATE_RINGING;
            }


            onCallStateChanged(context, state, number);
        }
    }


    protected void onIncomingCallStarted(Context ctx, String number, long start) {
        Log.e(LOG_TAG,"Call from: "+number) ;
        UserInteraction interaction = new UserInteraction("", "call", "", "" + start, "INBOUND", number, "");
        Intent mServiceIntent = new Intent(ctx, NewInteractionHandlerService.class);
        mServiceIntent.putExtra("interaction", interaction.toString());
        ctx.startService(mServiceIntent);

    }

    protected void onOutgoingCallStarted(Context ctx, String number, long start) {
        Log.e(LOG_TAG,"Call to: "+number) ;
        UserInteraction interaction = new UserInteraction("", "call", "", "" + start, "OUTBOUND", "", number);
        Intent mServiceIntent = new Intent(ctx, NewInteractionHandlerService.class);
        mServiceIntent.putExtra("interaction", interaction.toString());
        ctx.startService(mServiceIntent);
    }

    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    public void onCallStateChanged(Context context, int state, String number) {
        if (lastState == state) {
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = System.currentTimeMillis();
                savedNumber = number;
                onIncomingCallStarted(context, number, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    callStartTime = System.currentTimeMillis();
                    onOutgoingCallStarted(context, savedNumber, callStartTime);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    //onMissedCall(context, savedNumber, callStartTime);
                } else if (isIncoming) {
                    //onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                } else {
                    // onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                }
                break;
        }
        lastState = state;
    }
}
