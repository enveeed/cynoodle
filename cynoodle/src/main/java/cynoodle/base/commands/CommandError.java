/*
 * cynoodle, a bot for the chat platform Discord
 *
 * Copyright (C) 2019 enveeed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * All trademarks are the property of their respective owners, including, but not limited to Discord Inc.
 */

package cynoodle.base.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

/**
 * An error for command execution.
 * @see Command#execute(Context)
 */
public final class CommandError {

    public static final Type DEFAULT
            = newType(":warning:", new Color(0xFFCC4D));
    public static final Type FATAL
            = newType(":x:", new Color(0xDD2E44));

    public static final Type PERMISSION
            = newType(":no_entry:", new Color(0xDD2E44));

    // ===

    private final Type type;
    private final String message;

    @Nullable
    private final String title;

    //

    private CommandError(@Nonnull Type type, @Nonnull String message,
                         @Nullable String title) {
        this.type = type;
        this.message = message;

        this.title = title;
    }

    // ===

    @Nonnull
    public Type getType() {
        return this.type;
    }

    @Nonnull
    public String getMessage() {
        return this.message;
    }

    //

    @Nullable
    public String getTitle() {
        return this.title;
    }

    // ===

    @Nonnull
    public MessageEmbed asEmbed() {

        EmbedBuilder out = new EmbedBuilder();

        //

        StringBuilder content = new StringBuilder();

        content.append(this.type.icon).append(" \u200b ");
        if(this.title != null) content.append("**").append(this.title).append("**\n\n");
        content.append(this.message).append("\n\n");

        out.setDescription(content.toString());

        out.setColor(this.type.color);

        //

        return out.build();
    }

    //

    @Nonnull
    public CommandException asException() {
        return new CommandException(this);
    }

    public void throwAsException() throws CommandException {
        throw asException();
    }

    // ===

    @Nonnull
    public static CommandError newError(@Nonnull Type type, @Nonnull String message) {
        return new CommandError(type, message, null);
    }

    @Nonnull
    public static CommandError newError(@Nonnull String message) {
        return new CommandError(DEFAULT, message, null);
    }

    @Nonnull
    public static CommandError newError(@Nonnull Type type, @Nonnull String message,
                                        @Nonnull String title) {
        return new CommandError(type, message, title);
    }

    @Nonnull
    public static CommandError newError(@Nonnull String message,
                                        @Nonnull String title) {
        return new CommandError(DEFAULT, message, title);
    }

    // ===

    /**
     * A type of error, with icon and color.
     */
    public static final class Type {

        private final String icon;
        private final Color color;

        // ===

        Type(@Nonnull String icon, @Nonnull Color color) {
            this.icon = icon;
            this.color = color;
        }

        // ===

        @Nonnull
        public String getIcon() {
            return this.icon;
        }

        @Nonnull
        public Color getColor() {
            return this.color;
        }
    }

    //

    @Nonnull
    public static Type newType(@Nonnull String icon, @Nonnull Color color) {
        return new Type(icon, color);
    }
}
