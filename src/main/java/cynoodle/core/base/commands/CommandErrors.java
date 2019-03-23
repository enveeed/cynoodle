/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.commands;

import cynoodle.core.api.text.Options;
import cynoodle.core.api.text.ParsingException;
import net.dv8tion.jda.core.Permission;

import javax.annotation.Nonnull;
import java.awt.*;

import static cynoodle.core.base.commands.CommandError.Flag.DISPLAY_USAGE;

/**
 * Static suppliers for different common {@link CommandError CommandErrors}.
 */
public final class CommandErrors {
    private CommandErrors() {}

    private static final String ICON_EXTERNAL = "⚠️";
    private static final String ICON_INTERNAL = "❌";
    private static final String ICON_PERMISSION = "\uD83D\uDEAB";

    private static final Color COLOR_MODERATE = new Color(0xFFCC4D);
    private static final Color COLOR_SEVERE = new Color(0xDD2E44);

    // === GENERAL ===

    @Nonnull
    public static CommandError simple(@Nonnull Command command, @Nonnull String message) {
        return CommandError.builder(command)
                .withTitle("Command Error")
                .withMessage(message)
                .withIcon(ICON_EXTERNAL)
                .withColor(COLOR_MODERATE)
                .build();
    }

    // === INPUT ===

    @Nonnull
    public static CommandError commandParsingFailed(@Nonnull Command command, @Nonnull ParsingException exception) {

        String message = exception.getMessage();
        if(message == null) message = "Failed to parse input!";

        return CommandError.builder(command)
                .withTitle("Command Parsing Failed")
                .withMessage("Failed to parse command:\n" + message)
                .withIcon(ICON_EXTERNAL)
                .withColor(COLOR_MODERATE)
                .withFlags(DISPLAY_USAGE)
                .build();
    }

    @Nonnull
    public static CommandError parameterParsingFailed(@Nonnull Command command, @Nonnull String name, @Nonnull ParsingException exception) {

        String message = exception.getMessage();
        if(message == null) message = "Failed to parse input!";

        return CommandError.builder(command)
                .withTitle("Parameter Parsing Failed")
                .withMessage("Failed to parse parameter `" + name + "`:\n" + message)
                .withIcon(ICON_EXTERNAL)
                .withColor(COLOR_MODERATE)
                .withFlags(DISPLAY_USAGE)
                .build();
    }

    @Nonnull
    public static CommandError optionParsingFailed(@Nonnull Command command, @Nonnull Options.Option option, @Nonnull ParsingException exception) {

        String message = exception.getMessage();
        if(message == null) message = "Failed to parse input!";

        return CommandError.builder(command)
                .withTitle("Option Parsing Failed")
                .withMessage("Failed to parse option `" + option.getLong() + "`:\n" + message)
                .withIcon(ICON_EXTERNAL)
                .withColor(COLOR_MODERATE)
                .withFlags(DISPLAY_USAGE)
                .build();
    }

    @Nonnull
    public static CommandError parameterMissing(@Nonnull Command command, @Nonnull String name) {
        return CommandError.builder(command)
                .withTitle("Invalid Input")
                .withMessage("Missing parameter `" + name + "`!")
                .withIcon(ICON_EXTERNAL)
                .withColor(COLOR_MODERATE)
                .withFlags(DISPLAY_USAGE)
                .build();
    }

    // === DISCORD ===

    @Nonnull
    public static CommandError insufficientDiscordPermissions(@Nonnull Command command, @Nonnull Permission permission) {
        return CommandError.builder(command)
                .withTitle("Insufficient Discord Permissions")
                .withMessage("The bot account misses permission \"" + permission.getName()+"\"!")
                .withIcon(ICON_EXTERNAL)
                .withColor(COLOR_SEVERE)
                .build();
    }

    // === INTERNAL ===

    @Nonnull
    public static CommandError internalError(@Nonnull Command command) {
        return CommandError.builder(command)
                .withTitle("Unexpected Internal Error")
                .withMessage("This was probably not your fault, please try again later.")
                .withIcon(ICON_INTERNAL)
                .withColor(COLOR_SEVERE)
                .build();
    }

    // === PERMISSIONS ===

    @Nonnull
    public static CommandError permissionInsufficient(@Nonnull Command command) {
        return CommandError.builder(command)
                .withTitle("Insufficient Permissions")
                .withMessage("I'm afraid I can't do that, Dave.")
                .withIcon(ICON_PERMISSION)
                .withColor(COLOR_SEVERE)
                .build();
    }
}
