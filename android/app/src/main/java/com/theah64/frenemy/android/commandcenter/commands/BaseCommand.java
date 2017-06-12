package com.theah64.frenemy.android.commandcenter.commands;

import android.content.Context;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by theapache64 on 14/9/16,7:48 PM.
 */
public abstract class BaseCommand {

    private final String[] args;
    private CommandLine cmd;

    BaseCommand(String command) throws CommandException, ParseException {
        if (command != null && !command.isEmpty()) {

            if (command.contains("help")) {
                if (getOptions() != null) {
                    HelpFormatter formatter = new HelpFormatter();

                    StringWriter out = new StringWriter();
                    PrintWriter pw = new PrintWriter(out);

                    formatter.printHelp(pw, 80, command.split("help")[0], getOptions(),
                            formatter.getLeftPadding(), formatter.getDescPadding(), "test-footer", true);
                    pw.flush();

                    throw new CommandException(out.toString());
                } else {
                    throw new CommandException("No help found");
                }
            }

            this.args = command.split(" ");
            //Valid command syntax, check for options
            if (getOptions() != null) {
                final CommandLineParser parser = new DefaultParser();
                this.cmd = parser.parse(getOptions(), args);
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
