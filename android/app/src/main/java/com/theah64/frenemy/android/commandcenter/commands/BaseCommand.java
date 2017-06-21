package com.theah64.frenemy.android.commandcenter.commands;

import android.content.Context;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by theapache64 on 14/9/16,7:48 PM.
 */
public abstract class BaseCommand {

    private final String[] args;
    private CommandLine cmd;

    BaseCommand(String command) throws CommandException, ParseException, CommandHelp {

        if (command != null && !command.isEmpty()) {

            this.args = command.split(" ");

            if (args.length > 1 && getOptions() == null) {
                throw new CommandException("Invalid argument : " + args[1]);
            }

            if (getOptions() != null && (args.length == 1 || (args.length == 2 && args[1].equals("--help")))) {
                if (getOptions() != null) {
                    HelpFormatter formatter = new HelpFormatter();

                    StringWriter out = new StringWriter();
                    PrintWriter pw = new PrintWriter(out);

                    formatter.printHelp(pw, 80, args[0], "", getOptions(),
                            formatter.getLeftPadding(), formatter.getDescPadding(), "", true);
                    pw.flush();

                    throw new CommandHelp(out.toString());
                } else {
                    throw new CommandException("No help found");
                }
            }

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

    public abstract void handle(final Context context, final Callback callback) throws IOException;

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

    public static class CommandHelp extends Exception {
        public CommandHelp(String s) {
            super(s);
        }
    }

}
