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

import cynoodle.mongo.fluent.Codec;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Function;

/**
 * Immutable type information for {@link Permission Permissions}.
 */
public final class PermissionType {

    private final String name;
    private final Codec<? extends PermissionMeta> metaCodec;
    private final Function<Permission, String> formatter;

    // ===

    private PermissionType(@Nonnull String name,
                           @Nonnull Codec<? extends PermissionMeta> metaCodec,
                           @Nonnull Function<Permission, String> formatter) {
        this.name = name;
        this.metaCodec = metaCodec;
        this.formatter = formatter;
    }

    // ===

    /**
     * Get the name of this type.
     * @return the type name
     */
    @Nonnull
    public String getName() {
        return this.name;
    }

    /**
     * Get the codec for the {@link PermissionMeta} of
     * the {@link Permission Permissions} of this type.
     * @return the meta codec
     */
    @Nonnull
    public Codec<? extends PermissionMeta> getMetaCodec() {
        return this.metaCodec;
    }

    /**
     * Get the display name of the given permission.
     * The permission must be of this type.
     * @param permission the permission
     * @return the display name of the permission
     * @throws IllegalArgumentException if the permission was not of this type
     */
    @Nonnull
    public String getDisplayName(@Nonnull Permission permission)
            throws IllegalArgumentException {
        if(!Objects.equals(permission.getPermissionTypeName(), this.name))
            throw new IllegalArgumentException("Permission type mismatch: Got permission of "
                    + permission.getPermissionTypeName() + " but expected " + this.name + "!");
        return this.formatter.apply(permission);
    }

    // ===

    @Nonnull
    public static PermissionType of(@Nonnull String name,
                                    @Nonnull Codec<? extends PermissionMeta> metaCodec,
                                    @Nonnull Function<Permission, String> formatter) {
        return new PermissionType(name, metaCodec, formatter);
    }
}
