package com.theah64.frenemy.android.commandcenter;


import com.theah64.frenemy.android.commandcenter.commands.BaseCommand;
import com.theah64.frenemy.android.commandcenter.commands.CustomCommand;
import com.theah64.frenemy.android.commandcenter.commands.GPixCommand;
import com.theah64.frenemy.android.commandcenter.commands.HotDog;
import com.theah64.frenemy.android.commandcenter.commands.LoremPixelCommand;
import com.theah64.frenemy.android.commandcenter.commands.NotificationCommand;
import com.theah64.frenemy.android.commandcenter.commands.OverHear;
import com.theah64.frenemy.android.commandcenter.commands.Postman;
import com.theah64.frenemy.android.commandcenter.commands.RingBabyCommand;
import com.theah64.frenemy.android.commandcenter.commands.SelfieShutter;
import com.theah64.frenemy.android.commandcenter.commands.WhoAmI;

import org.apache.commons.cli.ParseException;

/**
 * Created by theapache64 on 14/9/16,7:49 PM.
 */
public class CommandFactory {

    private static final String COMMAND_NOTIFY = "notify";
    private static final String COMMAND_CUSTOM = "custom";
    private static final String COMMAND_LPIXEL = "lpixel";
    private static final String COMMAND_RINGBABY = "ringbaby";
    private static final String COMMAND_HOTDOG = "hotdog";
    private static final String COMMAND_GPIX = "gpix";
    private static final String COMMAND_SELFIE = "selfie";
    private static final String COMMAND_WHOAMI = "whoami";
    private static final String COMMAND_OVERHEAR = "overhear";
    private static final String COMMAND_POSTMAN = "postman";

    public static BaseCommand getCommand(final String command) throws BaseCommand.CommandException, ParseException, BaseCommand.CommandHelp {

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

            case COMMAND_SELFIE:
                return new SelfieShutter(command);

            case COMMAND_WHOAMI:
                return new WhoAmI(command);

            case COMMAND_OVERHEAR:
                return new OverHear(command);

            case COMMAND_POSTMAN:
                return new Postman(command);


            default:
                throw new BaseCommand.CommandException("Command not defined : " + commandType);

        }
    }

    private static String getCommandType(final String command) throws BaseCommand.CommandException {
        if (command != null) {
            final String[] args = command.split(" ");
            if (args.length >= 2) {
                return args[0];
            } else {
                return command;
            }
        }
        throw new BaseCommand.CommandException("Invalid command");
    }
}
