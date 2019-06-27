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

package cynoodle.test.permissions;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * A permission string.
 * <p>
 *     Must be in the format of dot separated lowercase alphanumeric strings.
 * </p>
 * <p>
 *     Valid examples:</br>
 *     <code>permission.node.something</code>, <code>permission</code>
 * </p>
 */
public final class Permission {

    /**
     * The regex any full permission must match.
     */
    private static final Pattern REGEX
            = Pattern.compile("(?:^\\w+|\\w+\\.\\w+)+$");
    /**
     * The regex any individual node must match.
     */
    private static final Pattern REGEX_NODE
            = Pattern.compile("^\\w+$");

    // ===

    private final String[] nodes;

    // ===

    private Permission(@Nonnull String[] nodes) {
        this.nodes = nodes;
    }

    // ===

    /**
     * Get the single nodes of this permission.
     * @return array containing the nodes
     */
    @Nonnull
    public String[] nodes() {
        return this.nodes.clone();
    }

    /**
     * Get the length of this permission,
     * that is the amount of nodes it has. It always has at least 1.
     * @return the length
     */
    public int length() {
        return this.nodes.length;
    }

    // ===

    public boolean has(@Nonnull Member member, boolean fallback) {
        Permissions permissions = Permissions.get();
        return permissions.test(member, this, fallback);
    }

    public boolean has(@Nonnull Member member) {
        Permissions permissions = Permissions.get();
        return permissions.test(member, this);
    }

    // ===

    @Nonnull
    public String toString() {
        return Joiner.on('.').join(this.nodes);
    }

    // ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Permission that = (Permission) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(nodes, that.nodes);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(nodes);
    }

    // ===

    /**
     * Create a permission using the given nodes.
     * @param nodes the nodes to make up the permission
     * @return a new permission
     * @throws IllegalArgumentException if there were no nodes given or the nodes did not match the expected format
     */
    @Nonnull
    public static Permission of(@Nonnull String... nodes) throws IllegalArgumentException {
        if(nodes.length == 0)
            throw new IllegalArgumentException("Permission must consist of at least one node.");

        // validate nodes
        for (int i = 0; i < nodes.length; i++) {
            if(!REGEX_NODE.matcher(nodes[i]).matches())
                throw new IllegalArgumentException("Invalid node: " + nodes[i]);
        }

        return new Permission(nodes);
    }

    @Nonnull
    public static Permission of(@Nonnull String permission) throws IllegalArgumentException {
        if(!REGEX.matcher(permission).matches())
            throw new IllegalArgumentException("Invalid permission: " + permission);

        // split into nodes and collect into array
        String[] nodes = Iterables.toArray(Splitter.on('.').split(permission), String.class);

        return of(nodes);
    }
}
