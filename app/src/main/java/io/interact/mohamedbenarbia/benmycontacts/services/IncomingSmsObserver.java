package io.interact.mohamedbenarbia.benmycontacts.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import io.interact.mohamedbenarbia.benmycontacts.Interaction.UserInteraction;

/**
 * tracks only incoming sms
 */
public class IncomingSmsObserver extends BroadcastReceiver {
    String LOG_TAG=IncomingSmsObserver.class.getName();

   @Override
   public void onReceive(Context context, Intent intent)
   {
       //---get the SMS message passed in---
       Bundle bundle = intent.getExtras();
       SmsMessage[] msgs = null;
       String str = "";
       if (bundle != null)
       {
           //---retrieve the SMS message received---
           Object[] pdus = (Object[]) bundle.get("pdus");
           msgs = new SmsMessage[pdus.length];
           for (int i=0; i<msgs.length; i++){
               msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
           }
           //---display the new SMS message---
           UserInteraction inter = new UserInteraction("", "sms", "", "" + System.currentTimeMillis(), "INBOUND", msgs[0].getOriginatingAddress(), "");
           Intent mServiceIntent = new Intent(context, NewInteractionHandlerService.class);
           mServiceIntent.putExtra("interaction",inter.toString());
           context.startService(mServiceIntent);
       }
   }
}
