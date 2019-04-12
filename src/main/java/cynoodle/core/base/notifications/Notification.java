/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.notifications;

import cynoodle.core.discord.DiscordPointer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;

public final class Notification {

    private final String identifier;

    private final DiscordPointer context;
    private final String[] variables;

    // ===

    private Notification(@Nonnull String identifier,
                         @Nullable DiscordPointer context,
                         @Nonnull String[] variables) {
        this.identifier = identifier;
        this.context = context;
        this.variables = variables;
    }

    // ===

    @Nonnull
    public String getIdentifier() {
        return this.identifier;
    }

    //

    @Nonnull
    public Optional<DiscordPointer> getContext() {
        return Optional.ofNullable(this.context);
    }

    @Nonnull
    public String[] getVariables() {
        return this.variables;
    }

    // ===

    @Nonnull
    public static Notification of(@Nonnull String identifier, @Nullable DiscordPointer context, @Nonnull String... variables) {
        return new Notification(identifier, context, variables);
    }

    @Nonnull
    public static Notification of(@Nonnull String identifier, @Nonnull String... variables) {
        return new Notification(identifier, null, variables);
    }

    // ===

    // TODO improvements, maybe move to somewhere else
    @Nonnull
    public String format(@Nonnull String message,
                         @Nonnull String[] variableNames,
                         @Nonnull String[] variables) {
        if(variableNames.length != variables.length)
            throw new IllegalArgumentException("Variable names length vs. variables length mismatch!");

        String out = message;

        for (int i = 0; i < variableNames.length; i++) {
            out = out.replaceAll("\\{" + variableNames[i] + "\\}", variables[i]);
        }

        return out;
    }

    //

    @Override
    public String toString() {
        return "Notification{" +
                "identifier='" + identifier + '\'' +
                ", context=" + context +
                ", variables=" + Arrays.toString(variables) +
                '}';
    }
}
