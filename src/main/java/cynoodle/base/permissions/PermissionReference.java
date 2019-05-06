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

import cynoodle.entities.EntityReference;
import cynoodle.mongo.fluent.Codec;
import org.bson.BSONException;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Convenience wrapper for referencing {@link Permission Permissions}.
 */
public final class PermissionReference {

    private final EntityReference<Permission> reference;

    // ===

    private PermissionReference(@Nonnull EntityReference<Permission> reference) {
        this.reference = reference;
    }

    // ===

    @Nonnull
    public Optional<Permission> get() {
        return reference.get();
    }

    @Nonnull
    public Permission require() {
        return reference.require();
    }

    // ===

    @Nonnull
    public static PermissionReference of(@Nonnull Permission permission) {
        return new PermissionReference(permission.reference(Permission.class));
    }

    // ===

    @Nonnull
    public static Codec<PermissionReference> codec() {
        return new Codec<>() {
            @Override
            public PermissionReference load(BsonValue bson) throws BSONException {
                return new PermissionReference(Permission.referenceCodec().load(bson));
            }

            @Override
            public BsonValue store(PermissionReference object) throws BSONException {
                return Permission.referenceCodec().store(object.reference);
            }
        };
    }
}
