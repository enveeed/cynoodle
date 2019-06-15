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

import cynoodle.mongodb.IBsonDocument;
import cynoodle.mongodb.IBsonDocumentCodec;
import cynoodle.mongodb.fluent.Codec;
import cynoodle.mongodb.fluent.FluentDocument;
import org.bson.BSONException;

import javax.annotation.Nonnull;

/**
 * An immutable Entry for a permission filter into a {@link PermissionContainer},
 * to either <b>allow</b> or <b>deny</b> matching permissions.
 */
public final class PermissionEntry implements IBsonDocument {
    private PermissionEntry() {}

    // ===

    private String filter;
    private boolean status;

    // ===

    PermissionEntry(@Nonnull String filter, boolean status) {
        this.filter = filter;
        this.status = status;
    }

    // ===

    /**
     * Get the permission filter of this Entry.
     * @return the permission filter
     */
    @Nonnull
    public String getFilter() {
        return this.filter;
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
        this.filter = data.getAt("filter").asString().or(this.filter);
        this.status = data.getAt("status").asBoolean().or(this.status);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BSONException {
        FluentDocument data = FluentDocument.wrapNew();

        data.setAt("filter").asString().to(this.filter);
        data.setAt("status").asBoolean().to(this.status);

        return data;
    }

    // ===

    static Codec<PermissionEntry> codec() {
        return new IBsonDocumentCodec<>(PermissionEntry::new);
    }
}
