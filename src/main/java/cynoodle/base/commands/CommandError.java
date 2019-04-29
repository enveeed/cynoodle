/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.base.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * A detailed, exceptional error for command execution.
 */
public final class CommandError extends Exception {

    /**
     * The title, a short text indicating what the issue is about.
     */
    private final String title;

    /**
     * The message, describing the error.
     */
    private final String message;

    /**
     * The icon string (e.g. an emoji).
     */
    private final String icon;

    /**
     * The color for the error embed.
     */
    private final Color color;

    /**
     * Optional flags for the error.
     */
    private final EnumSet<Flag> flags;

    // ===

    private CommandError(@Nonnull Builder builder) {

        this.title      = builder.title;
        this.message    = builder.message;
        this.icon       = builder.icon;
        this.color      = builder.color;
        this.flags      = builder.flags;
    }

    // ===

    @Nullable
    public String getTitle() {
        return this.title;
    }

    // NOTE: This overrides Exception.getMessage()
    @Nullable
    public String getMessage() {
        return this.message;
    }

    @Nullable
    public String getIcon() {
        return this.icon;
    }

    @Nullable
    public Color getColor() {
        return this.color;
    }

    //

    public boolean hasFlag(@Nonnull Flag flag) {
        return flags.contains(flag);
    }

    // ===

    /**
     * Create an error embed for this error.
     * @return the error message embed.
     */
    @Nonnull
    public MessageEmbed asEmbed() {

        EmbedBuilder embed = new EmbedBuilder();

        //

        StringBuilder content = new StringBuilder();

        if(this.icon != null) content.append(this.icon).append(" \u200b ");
        if(this.title != null) content.append("**").append(this.title).append("**\n\n");
        if(this.message != null) content.append(this.message).append("\n\n");

        if(this.hasFlag(Flag.DISPLAY_USAGE)) {
            // TODO append usage
        }

        // TODO more flags

        if(content.length() == 0) content.append("Unknown error.");

        embed.setDescription(content.toString());

        if(color != null) embed.setColor(color);

        //

        return embed.build();
    }

    // ===

    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    // ===

    public static class Builder {

        private String title = null;
        private String message = null;
        private String icon = null;
        private Color color = null;

        private final EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);

        // ===

        @Nonnull
        public Builder withTitle(@Nullable String title) {
            this.title = title;
            return this;
        }

        @Nonnull
        public Builder withMessage(@Nullable String message) {
            this.message = message;
            return this;
        }

        @Nonnull
        public Builder withIcon(@Nullable String icon) {
            this.icon = icon;
            return this;
        }

        @Nonnull
        public Builder withColor(@Nullable Color color) {
            this.color = color;
            return this;
        }

        @Nonnull
        public Builder withFlags(@Nonnull Flag... flags) {
            this.flags.addAll(Arrays.asList(flags));
            return this;
        }

        // ===

        @Nonnull
        public CommandError build() {
            return new CommandError(this);
        }
    }

    // ===

    /**
     * Optional flags.
     */
    public enum Flag {

        /**
         * Display simple usage information.
         */
        DISPLAY_USAGE,
        ;

    }

}
