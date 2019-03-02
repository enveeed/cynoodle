/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.command;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Arrays;
import java.util.EnumSet;


public final class CommandException extends Exception {

    /**
     * The title, a short text indicating what the issue is about.
     */
    private final String title;

    /**
     * The icon string (e.g. an emoji).
     */
    private final String icon;

    /**
     * The color for the error embed.
     */
    private final Color color;

    /**
     * If usage help should be displayed.
     */
    private final EnumSet<Flag> flags;

    // ===

    private CommandException(@Nonnull Builder builder) {
        super(builder.message, builder.cause);
        this.title  = builder.title;
        this.icon   = builder.icon;
        this.color  = builder.color;
        this.flags  = builder.flags;
    }

    // ===

    @Nullable
    public String getTitle() {
        return this.title;
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

        private Exception cause = null;

        private final EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);

        // ===

        private Builder() {}

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
        public Builder withCause(@Nullable Exception cause) {
            this.cause = cause;
            return this;
        }

        @Nonnull
        public Builder withFlags(@Nonnull Flag... flags) {
            this.flags.addAll(Arrays.asList(flags));
            return this;
        }

        // ===

        @Nonnull
        public CommandException build() {
            return new CommandException(this);
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
