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
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * A registry to store {@link PermissionType} instances.
 */
public final class PermissionTypeRegistry {
    PermissionTypeRegistry() {}

    // ===

    private final Map<String, PermissionType> registry = new HashMap<>();

    // ===

    /**
     * Register the given {@link PermissionType}.
     * @param type the type
     * @throws IllegalArgumentException if this type is already registered.
     */
    public void register(@Nonnull PermissionType type)
        throws IllegalArgumentException {
        String key = type.getKey();
        if(this.registry.containsKey(key))
            throw new IllegalArgumentException("There is already a PermissionType registered with key \"" + key + "\"!");
        this.registry.put(key, type);
    }

    // ===

    /**
     * Check if the registry contains a {@link PermissionType} by its key.
     * @param key the permission type key
     * @return true if it contains it, false otherwise
     */
    public boolean contains(@Nonnull String key) {
        return this.registry.containsKey(key);
    }

    /**
     * @see #contains(String)
     * @param type the type
     * @return true if it contains it, false otherwise
     */
    public boolean contains(@Nonnull PermissionType type) {
        return this.registry.containsKey(type.getKey());
    }

    // ===

    /**
     * Get the {@link PermissionType} registered by its key.
     * @param key the key
     * @return optional containing the type, empty if not registered
     */
    @Nonnull
    public Optional<PermissionType> get(@Nonnull String key) {
        return Optional.ofNullable(this.registry.get(key));
    }

    /**
     * Get the {@link PermissionType} registered by its key,
     * throwing if not registered.
     * @param key the key
     * @return the permission type
     * @throws NoSuchElementException if that type is not registered
     */
    @Nonnull
    public PermissionType require(@Nonnull String key) throws NoSuchElementException {
        return get(key)
                .orElseThrow(() -> new NoSuchElementException("There is no PermissionType with key \"" + key + "\"!"));
    }

}
