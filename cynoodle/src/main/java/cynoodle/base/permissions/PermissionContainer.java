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

import cynoodle.mongodb.IBsonArray;
import cynoodle.mongodb.IBsonArrayCodec;
import cynoodle.mongodb.fluent.Codec;
import cynoodle.mongodb.fluent.FluentArray;
import org.bson.BSONException;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A container for {@link PermissionEntry PermissionEntries}.
 */
public final class PermissionContainer implements IBsonArray {

    // TODO use a graph to support inheritance using wildcards

    PermissionContainer() {}

    // ===

    private Map<String, PermissionEntry> permissions = new HashMap<>();

    // === PERMISSIONS ===

    /**
     * Get all permission entries this container contains.
     * @return permission entry set
     */
    @Nonnull
    public Set<PermissionEntry> getEntries() {
        return Set.copyOf(this.permissions.values());
    }

    //

    /**
     * Set the given permission filter, either to allow the matching permissions
     * or deny them.
     * @param filter the permission filter
     * @param allow true to allow, false to deny
     */
    public void set(String filter, boolean allow) {
        this.setEntry(filter, allow);
    }

    /**
     * Set the given permission filter to <b>allow</b>.
     * @param filter the permission filter
     */
    public void set(String filter) {
        set(filter, true);
    }

    /**
     * Unset the given permission filter.
     * @param filter the permission filter
     */
    public void unset(String filter) {
        this.unsetEntry(filter);
    }

    // === TODO change the following for filters

    /**
     * Check if this container contains an entry which matches the given permission,
     * which is set to either allow or deny and not unset.
     * @param permission the permission
     * @return true if there is a matching entry set to allow or deny, false if unset
     */
    public boolean contains(Permission permission) {
        // TODO support wildcard matching, currently we only match for equality
        return this.permissions.containsKey(permission.toString());
    }

    //

    public boolean allows(Permission permission) {
        // TODO support wildcard matching, currently we only match for equality
        if(!contains(permission)) return false;
        else return this.permissions.get(permission.toString()).isAllowed();
    }

    public boolean denies(Permission permission) {
        // TODO support wildcard matching, currently we only match for equality
        if(!contains(permission)) return false;
        else return this.permissions.get(permission.toString()).isDenied();
    }

    // ===

    private void setEntry(String filter, boolean allow) {
        this.permissions.put(filter, new PermissionEntry(filter, allow));
    }

    private void unsetEntry(String filter) {
        this.permissions.remove(filter);
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentArray data) throws BSONException {
        this.permissions = data.collect()
                .as(Codec.load(PermissionEntry.codec()))
                .toMapOr(PermissionEntry::getFilter, this.permissions);

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
