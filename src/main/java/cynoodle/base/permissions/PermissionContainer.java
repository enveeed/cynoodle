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

import cynoodle.mongo.IBsonArray;
import cynoodle.mongo.IBsonArrayCodec;
import cynoodle.mongo.fluent.Codec;
import cynoodle.mongo.fluent.FluentArray;
import org.bson.BSONException;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A container for {@link PermissionEntry PermissionEntries}.
 */
public final class PermissionContainer implements IBsonArray {

    PermissionContainer() {}

    // ===

    private Map<Long, PermissionEntry> permissions = new HashMap<>();

    // === PERMISSIONS ===

    /**
     * Get all permission entries this container contains.
     * @return permission entry set
     */
    @Nonnull
    public Set<PermissionEntry> getPermissions() {
        return Set.copyOf(this.permissions.values());
    }

    //

    /**
     * Set the given permission, either to allow the permission
     * or deny the permission.
     * @param permission the permission
     * @param allow true to allow, false to deny
     */
    public void setPermission(Permission permission, boolean allow) {
        this.setEntry(permission, allow);
    }

    /**
     * Set the given permission to <b>allow</b>.
     * @param permission the permission
     */
    public void setPermission(Permission permission) {
        setPermission(permission, true);
    }

    /**
     * Unset the given permission.
     * @param permission the permission
     */
    public void unsetPermission(Permission permission) {
        this.unsetEntry(permission);
    }

    // ===

    /**
     * Check if this container contains the given permission,
     * that is it is set with either allow or deny and not unset.
     * @param permission the permission
     * @return true if set to allow or deny, false if unset
     */
    public boolean contains(Permission permission) {
        return this.permissions.containsKey(permission.getID());
    }

    //

    public boolean allows(Permission permission) {
        if(!contains(permission)) return false;
        else return this.permissions.get(permission.getID()).isAllowed();
    }

    public boolean denies(Permission permission) {
        if(!contains(permission)) return false;
        else return this.permissions.get(permission.getID()).isDenied();
    }

    // ===

    private void setEntry(Permission permission, boolean allow) {
        this.permissions.put(permission.getID(), new PermissionEntry(permission, allow));
    }

    private void unsetEntry(Permission permission) {
        this.permissions.remove(permission.getID());
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentArray data) throws BSONException {

        // TODO improve

        // NOTE: Uses getPermissionID() for performance, but does not check if it actually exists (empty entries possible!!!)

        this.permissions = data.collect()
                .as(Codec.load(PermissionEntry.codec()))
                .toMapOr(PermissionEntry::getPermissionID, this.permissions);

    }

    @Nonnull
    @Override
    public FluentArray toBson() throws BSONException {

        FluentArray data = FluentArray.wrapNew();

        data.insert()
                .as(Codec.store(PermissionEntry.codec()))
                .atEnd(this.permissions.values());

        return data;
    }

    // ===

    @Nonnull
    static Codec<PermissionContainer> codec() {
        return new IBsonArrayCodec<>(PermissionContainer::new);
    }
}
