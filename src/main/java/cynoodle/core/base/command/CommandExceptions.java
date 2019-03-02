/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.command;

import cynoodle.core.api.text.ParsingException;
import net.dv8tion.jda.core.Permission;

import javax.annotation.Nonnull;

import java.awt.*;

import static cynoodle.core.base.command.CommandException.Flag.DISPLAY_USAGE;

/**
 * Static suppliers for different common {@link CommandException CommandExceptions}.
 */
public final class CommandExceptions {
    private CommandExceptions() {}

    private static final String ICON_EXTERNAL = "⚠️";
    private static final String ICON_INTERNAL = "❌";
    private static final String ICON_PERMISSION = "\uD83D\uDEAB";

    private static final Color COLOR_MODERATE = new Color(0xFFCC4D);
    private static final Color COLOR_SEVERE = new Color(0xDD2E44);

    // === GENERAL ===

    @Nonnull
    public static CommandException simple(@Nonnull String message) {
        return CommandException.builder()
                .withTitle("Command Error")
                .withMessage(message)
                .withIcon(ICON_EXTERNAL)
                .withColor(COLOR_MODERATE)
                .build();
    }

    // === INPUT ===

    @Nonnull
    public static CommandException parsingFailed(@Nonnull ParsingException exception) {

        String message = exception.getMessage();
        if(message == null) message = "Failed to parse input!";

        return CommandException.builder()
                .withTitle("Parsing Failed")
                .withMessage(message)
                .withIcon(ICON_EXTERNAL)
                .withColor(COLOR_MODERATE)
                .withFlags(DISPLAY_USAGE)
                .build();
    }

    @Nonnull
    public static CommandException missingParameter(@Nonnull String name) {
        return CommandException.builder()
                .withTitle("Invalid Input")
                .withMessage("Missing parameter `" + name + "`!")
                .withIcon(ICON_EXTERNAL)
                .withColor(COLOR_MODERATE)
                .withFlags(DISPLAY_USAGE)
                .build();
    }

    // === DISCORD ===

    @Nonnull
    public static CommandException insufficientDiscordPermissions(@Nonnull Permission permission) {
        return CommandException.builder()
                .withTitle("Insufficient Discord Permissions")
                .withMessage("The bot account misses permission \"" + permission.getName()+"\"!")
                .withIcon(ICON_EXTERNAL)
                .withColor(COLOR_SEVERE)
                .build();
    }

    // === INTERNAL ===

    @Nonnull
    public static CommandException internalError() {
        return CommandException.builder()
                .withTitle("Unexpected Internal Error")
                .withMessage("This was probably not your fault, please try again later.")
                .withIcon(ICON_INTERNAL)
                .withColor(COLOR_SEVERE)
                .build();
    }
}
