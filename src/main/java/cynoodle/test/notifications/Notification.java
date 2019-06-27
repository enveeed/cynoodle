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

package cynoodle.test.notifications;

import cynoodle.discord.DiscordPointer;

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
