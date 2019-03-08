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

import static cynoodle.core.base.command.CommandError.Flag.DISPLAY_USAGE;

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
    public static CommandError simple(@Nonnull String message) {
        return CommandError.builder()
                .withTitle("Command Error")
                .withMessage(message)
                .withIcon(ICON_EXTERNAL)
                .withColor(COLOR_MODERATE)
                .build();
    }

    // === INPUT ===

    @Nonnull
    public static CommandError parsingFailed(@Nonnull ParsingException exception) {

        String message = exception.getMessage();
        if(message == null) message = "Failed to parse input!";

        return CommandError.builder()
                .withTitle("Parsing Failed")
                .withMessage(message)
                .withIcon(ICON_EXTERNAL)
                .withColor(COLOR_MODERATE)
                .withFlags(DISPLAY_USAGE)
                .build();
    }

    @Nonnull
    public static CommandError missingParameter(@Nonnull String name) {
        return CommandError.builder()
                .withTitle("Invalid Input")
                .withMessage("Missing parameter `" + name + "`!")
                .withIcon(ICON_EXTERNAL)
                .withColor(COLOR_MODERATE)
                .withFlags(DISPLAY_USAGE)
                .build();
    }

    // === DISCORD ===

    @Nonnull
    public static CommandError insufficientDiscordPermissions(@Nonnull Permission permission) {
        return CommandError.builder()
                .withTitle("Insufficient Discord Permissions")
                .withMessage("The bot account misses permission \"" + permission.getName()+"\"!")
                .withIcon(ICON_EXTERNAL)
                .withColor(COLOR_SEVERE)
                .build();
    }

    // === INTERNAL ===

    @Nonnull
    public static CommandError internalError() {
        return CommandError.builder()
                .withTitle("Unexpected Internal Error")
                .withMessage("This was probably not your fault, please try again later.")
                .withIcon(ICON_INTERNAL)
                .withColor(COLOR_SEVERE)
                .build();
    }

    // === PERMISSIONS ===

    @Nonnull
    public static CommandError permissionUndefined() {
        return CommandError.builder()
                .withTitle("Permission Error")
                .withMessage("There is no permission set for this command.")
                .withIcon(ICON_PERMISSION)
                .withColor(COLOR_SEVERE)
                .build();
    }

    @Nonnull
    public static CommandError permissionInsufficient(@Nonnull cynoodle.core.base.permission.Permission permission) {
        return CommandError.builder()
                .withTitle("Insufficient Permissions")
                .withMessage(permission.getMessageError().isPresent() ?
                        permission.getMessageError().orElseThrow() : "Sorry, I can't let you do that.")
                .withIcon(ICON_PERMISSION)
                .withColor(COLOR_SEVERE)
                .build();
    }
}
