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

package cynoodle.module;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

/**
 * Immutable representation of a module identifier, consisting out of a group name and a module name.
 *
 * The format is <code>group:module</code>.
 */
public final class ModuleIdentifier {

    public final static Pattern NAME_REGEX = Pattern.compile("^[a-zA-Z0-9_-]+$");

    // ===

    private final String group;
    private final String name;

    // ===

    private ModuleIdentifier(@Nonnull String group, @Nonnull String name) {
        this.group = group;
        this.name = name;
    }

    // ===

    @Nonnull
    public String getGroup() {
        return this.group;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    // ===

    @Nonnull
    @Override
    public String toString() {
        return this.group + ":" + this.name;
    }

    // ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModuleIdentifier that = (ModuleIdentifier) o;

        if (!getGroup().equals(that.getGroup())) return false;
        return getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        int result = getGroup().hashCode();
        result = 31 * result + getName().hashCode();
        return result;
    }


    // ===

    /**
     * Create a module identifier out of a group and module name
     * @param group the group name
     * @param name the module name
     * @return a new module identifier
     * @throws IllegalArgumentException if the group or module name is invalid
     */
    @Nonnull
    public static ModuleIdentifier of(@Nonnull String group, @Nonnull String name) throws IllegalArgumentException {
        if(!NAME_REGEX.matcher(group).find()) throw new IllegalArgumentException("Illegal identifier: group name is invalid: " + group);
        if(!NAME_REGEX.matcher(name).find()) throw new IllegalArgumentException("Illegal identifier: module name is invalid: " + name);
        return new ModuleIdentifier(group, name);
    }

    //

    /**
     * Parse a module identifier
     * @param identifier the identifier in string form
     * @return a new module identifier representing the input
     * @throws IllegalArgumentException if the group or module name is invalid or if the identifier format is invalid
     */
    @Nonnull
    public static ModuleIdentifier parse(@Nonnull String identifier) throws IllegalArgumentException {
        String[] split = identifier.split(":");
        if(split.length != 2) throw new IllegalArgumentException("Illegal identifier format: "+identifier);
        var group = split[0];
        var name = split[1];
        return of(group, name);
    }

}
