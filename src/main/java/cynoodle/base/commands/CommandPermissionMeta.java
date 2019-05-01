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

import cynoodle.base.permissions.PermissionMeta;
import cynoodle.mongo.IBsonDocument;
import cynoodle.mongo.IBsonDocumentCodec;
import cynoodle.mongo.fluent.Codec;
import cynoodle.mongo.fluent.FluentDocument;
import org.bson.BSONException;

import javax.annotation.Nonnull;

/**
 * {@link PermissionMeta} implementation which includes information about the command
 * the permission is for.
 */
public final class CommandPermissionMeta implements PermissionMeta, IBsonDocument {

    private String identifier;

    // ===

    public CommandPermissionMeta(@Nonnull String commandIdentifier) {
        this.identifier = commandIdentifier;
    }

    private CommandPermissionMeta() {}

    // ===

    @Nonnull
    public String getIdentifier() {
        return this.identifier;
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument data) throws BSONException {

        this.identifier = data.getAt("identifier").asString().value();

    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BSONException {
        FluentDocument data = FluentDocument.wrapNew();

        data.setAt("identifier").asString().to(this.identifier);

        return data;
    }

    // ===

    @Nonnull
    static Codec<CommandPermissionMeta> codec() {
        return new IBsonDocumentCodec<>(CommandPermissionMeta::new);
    }
}
