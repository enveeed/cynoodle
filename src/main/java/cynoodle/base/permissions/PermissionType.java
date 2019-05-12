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

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Function;

/**
 * A {@link PermissionType} describes display names
 */
public final class PermissionType {

    private final String key;

    private final String name;

    // TODO replace those suppliers with a singular meta data supplier
    private final Function<Permission, String> nameSupplier;
    private final Function<Permission, String> descriptionSupplier;

    // ===

    public PermissionType(@Nonnull String key, @Nonnull String name,
                          @Nonnull Function<Permission, String> nameSupplier,
                          @Nonnull Function<Permission, String> descriptionSupplier) {
        this.key = key;
        this.name = name;
        this.nameSupplier = nameSupplier;
        this.descriptionSupplier = descriptionSupplier;
    }


    // ===

    @Nonnull
    public String getKey() {
        return this.key;
    }

    //

    // NOTE: This should be replaced by an i18n-able version
    @Nonnull
    public String getName() {
        return this.name;
    }

    //

    // NOTE: This should be replaced by an i18n-able version
    @Nonnull
    public String getPermissionName(@Nonnull Permission permission)
        throws IllegalArgumentException {
        ensureLegalPermission(permission);
        return this.nameSupplier.apply(permission);
    }

    // NOTE: This should be replaced by an i18n-able version
    @Nonnull
    String getPermissionDescription(@Nonnull Permission permission)
        throws IllegalArgumentException {
        ensureLegalPermission(permission);
        return this.descriptionSupplier.apply(permission);
    }

    // ===

    private void ensureLegalPermission(@Nonnull Permission permission)
            throws IllegalArgumentException {
        if(!Objects.equals(permission.getPermissionTypeKey(), this.getKey()))
            throw new IllegalArgumentException("Permission does not belong to this type! Expected \"" +
                    this.getKey() + "\" but got Permission " + permission.getID() + " of \"" + permission.getPermissionTypeKey() + "\"!");
    }

}
