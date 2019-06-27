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

import cynoodle.mongodb.IBsonDocument;
import cynoodle.mongodb.IBsonDocumentCodec;
import cynoodle.mongodb.fluent.Codec;
import cynoodle.mongodb.fluent.FluentDocument;
import org.bson.BSONException;

import javax.annotation.Nonnull;

/**
 * Properties for a specific {@link Command} on a guild.
 */
public final class CommandProperties implements IBsonDocument {

    private CommandProperties() {}

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument data) throws BSONException {

    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BSONException {
        return null;
    }

    // ===

    @Nonnull
    public static Codec<CommandProperties> codec() {
        return new IBsonDocumentCodec<>(CommandProperties::new);
    }
}
