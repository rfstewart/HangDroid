package edu.slcc.rstewart.hangdroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class IncomingSms extends BroadcastReceiver{
  final SmsManager sms = SmsManager.getDefault();

  public IncomingSms(){
  }

  @Override
  public void onReceive(Context ctx,Intent I){
    final Bundle B = I.getExtras();
    if(B != null){
      try {
        final Object[] pdus = (Object[])B.get("pdus");
        String fmt = B.getString("format");

        for(int i = 0; i < pdus.length; i++){
          SmsMessage sms = SmsMessage.createFromPdu((byte[])pdus[i]);
          String phono = sms.getDisplayOriginatingAddress();
          String msg = sms.getDisplayMessageBody();
          HDUtils.alert(ctx, "Text message from: " + phono);
          HDUtils.saveSms(msg, phono, ctx);
        }
      } catch(Exception e){
        Log.d("SMS Error",e.getMessage());}
    }
  }
}
