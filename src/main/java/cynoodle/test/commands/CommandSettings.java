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

package cynoodle.test.commands;

import cynoodle.mongodb.IBsonDocument;
import cynoodle.mongodb.IBsonDocumentCodec;
import cynoodle.mongodb.fluent.Codec;
import cynoodle.mongodb.fluent.FluentDocument;
import org.bson.BSONException;

import javax.annotation.Nonnull;

/**
 * Settings for a single command, within and not to be confused with {@link CommandsSettings}.
 */
public final class CommandSettings implements IBsonDocument {
    private CommandSettings() {}

    // ===

    /**
     * The command identifier.
     */
    private String command;

    //

    /**
     * Enable / disable the command.
     */
    private boolean enabled = true;

    // ===

    CommandSettings(@Nonnull String command) {
        this.command = command;
    }

    // ===

    @Nonnull
    public String getCommand() {
        return this.command;
    }

    //

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument data) throws BSONException {
        this.command = data.getAt("command").asString().value();

        this.enabled = data.getAt("enabled").asBoolean().or(this.enabled);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BSONException {
        FluentDocument data = FluentDocument.wrapNew();

        data.setAt("command").asString().to(this.command);

        data.setAt("enabled").asBoolean().to(this.enabled);

        return data;
    }

    // ===

    @Nonnull
    public static Codec<CommandSettings> codec() {
        return new IBsonDocumentCodec<>(CommandSettings::new);
    }
}
