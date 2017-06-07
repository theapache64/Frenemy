package com.theah64.frenemy.android.commandcenter;


import com.theah64.frenemy.android.commandcenter.commands.BaseCommand;
import com.theah64.frenemy.android.commandcenter.commands.CustomCommand;
import com.theah64.frenemy.android.commandcenter.commands.GPixCommand;
import com.theah64.frenemy.android.commandcenter.commands.HotDog;
import com.theah64.frenemy.android.commandcenter.commands.LoremPixelCommand;
import com.theah64.frenemy.android.commandcenter.commands.NotificationCommand;
import com.theah64.frenemy.android.commandcenter.commands.RingBabyCommand;

import org.apache.commons.cli.ParseException;

/**
 * Created by theapache64 on 14/9/16,7:49 PM.
 */
public class CommandFactory {


    public static final String COMMAND_NOTIFY = "notify";
    public static final String COMMAND_CUSTOM = "custom";
    public static final String COMMAND_LPIXEL = "lpixel";
    public static final String COMMAND_RINGBABY = "ringbaby";
    public static final String COMMAND_HOTDOG = "hotdog";
    public static final String COMMAND_GPIX = "gpix";

    public static BaseCommand getCommand(final String command) throws BaseCommand.CommandException, ParseException {

        final String commandType = getCommandType(command);

        switch (commandType) {

            case COMMAND_NOTIFY:
                return new NotificationCommand(command);

            case COMMAND_CUSTOM:
                return new CustomCommand(command);

            case COMMAND_LPIXEL:
                return new LoremPixelCommand(command);

            case COMMAND_RINGBABY:
                return new RingBabyCommand(command);

            case COMMAND_HOTDOG:
                return new HotDog(command);

            case COMMAND_GPIX:
                return new GPixCommand(command);

            default:
                throw new BaseCommand.CommandException("Command not defined " + commandType);

        }
    }

    private static String getCommandType(final String command) throws BaseCommand.CommandException {
        if (command != null) {
            final String[] args = command.split(" ");
            if (args.length >= 2) {
                return args[0];
            }
        }
        throw new BaseCommand.CommandException("Invalid command");
    }
}
