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

package cynoodle.base.permissions;

import cynoodle.mongo.IBsonDocument;
import cynoodle.mongo.IBsonDocumentCodec;
import cynoodle.mongo.fluent.Codec;
import cynoodle.mongo.fluent.FluentDocument;
import org.bson.BSONException;

import javax.annotation.Nonnull;

/**
 * An immutable Entry for a permission into a {@link PermissionContainer},
 * to either <b>allow</b> or <b>deny</b> it.
 */
public final class PermissionEntry implements IBsonDocument {
    private PermissionEntry() {}

    // ===

    private String permission;
    private boolean status;

    // ===

    PermissionEntry(@Nonnull String permission, boolean status) {
        this.permission = permission;
        this.status = status;
    }

    // ===

    /**
     * Get the permission of this Entry.
     * @return the permission
     */
    @Nonnull
    public String getPermission() {
        return this.permission;
    }

    //

    /**
     * Check if this permission entry is set to <b>allow</b>.
     * @return true if allowed, false otherwise
     */
    public boolean isAllowed() {
        return this.status;
    }

    /**
     * Check if this permission entry is set to <b>deny</b>.
     * @return true if denied, false otherwise
     */
    public boolean isDenied() {
        return !isAllowed();
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument data) throws BSONException {
        this.permission = data.getAt("permission").asString().or(this.permission);
        this.status = data.getAt("status").asBoolean().or(this.status);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BSONException {
        FluentDocument data = FluentDocument.wrapNew();

        data.setAt("permission").asString().to(this.permission);
        data.setAt("status").asBoolean().to(this.status);

        return data;
    }

    // ===

    static Codec<PermissionEntry> codec() {
        return new IBsonDocumentCodec<>(PermissionEntry::new);
    }
}
