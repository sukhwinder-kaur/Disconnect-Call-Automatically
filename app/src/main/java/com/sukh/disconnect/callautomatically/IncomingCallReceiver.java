package com.sukh.disconnect.callautomatically;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import java.io.IOException;
import java.lang.reflect.Method;

public class IncomingCallReceiver extends BroadcastReceiver {
    String incomingNumber = "";
    AudioManager audioManager;
    TelephonyManager telephonyManager;
    Context context;
    private MediaPlayer mediaPlayer;

    public void onReceive(Context context, Intent intent) {
        // Get AudioManager
        this.context = context;
        mediaPlayer = new MediaPlayer();
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // Get TelephonyManager
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                // Get incoming number
                incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            }
        }

        if (!incomingNumber.equals("")) {
            // Get an instance of ContentResolver
           /* ContentResolver cr=context.getContentResolver();
            // Fetch the matching number
            Cursor numbers=cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,  new  String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID,ContactsContract.CommonDataKinds.Phone.NUMBER},  ContactsContract.CommonDataKinds.Phone.NUMBER +"=?", new String[]{incomingNumber},  null);
            if(numbers.getCount()<=0){ // The incoming number is not  found in the contacts list*/
            // Turn on the mute
            //audioManager.setStreamMute(AudioManager.STREAM_RING,  true);
            // Reject the call
            //rejectCall();
            // Send the rejected message ton app
            //startApp(context,incomingNumber);
            // }

            //audioManager.setStreamMute(AudioManager.STREAM_RING, true);
            rejectCall();
            //startApp(context, incomingNumber);
           // acceptCall();
        }
    }


    private void startApp(Context context, String number) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("number", "Rejected incoming number:" + number);
        context.startActivity(intent);
    }

    private void rejectCall() {
        try {

            // Get the getITelephony() method
            Class<?> classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method method = classTelephony.getDeclaredMethod("getITelephony");
            // Disable access check
            method.setAccessible(true);
            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = method.invoke(telephonyManager);
            // Get the endCall method from ITelephony
            Class<?> telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void acceptCall() {
        try {
           /* // Get the getITelephony() method
            Class<?> classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method method = classTelephony.getDeclaredMethod("getITelephony");
            // Disable access check
            method.setAccessible(true);
            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = method.invoke(telephonyManager);
            // Get the endCall method from ITelephony
            Class<?> telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("answerRingingCall");
            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);*/
            Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
            buttonUp.putExtra(Intent.EXTRA_KEY_EVENT,
                    new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
            context.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
            //playAudio(R.raw.a);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    private void playAudio(int resid) {
        AssetFileDescriptor afd = context.getResources().openRawResourceFd(resid);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
            afd.close();
        } catch (IllegalArgumentException e) {
            Log.w("Rahul Log",e.getMessage());
        } catch (IllegalStateException e) {
            Log.w("Rahul Log", e.getMessage());
        } catch (IOException e) {
            Log.w("Rahul Log", e.getMessage());
        }
    }

}
