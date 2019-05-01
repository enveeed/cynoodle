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

import cynoodle.module.MIdentifier;
import cynoodle.module.Module;

import javax.annotation.Nonnull;

@MIdentifier(PermissionsModule.IDENTIFIER)
public final class PermissionsModule extends Module {
    private PermissionsModule() {}

    static final String IDENTIFIER = "base:permissions";

    // ===

    private Permissions permissions = null;

    // ===

    @Override
    protected void start() {
        super.start();

        this.permissions = new Permissions();
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    // ===

    @Nonnull
    public Permissions getPermissions() {
        if(this.permissions == null)
            throw new IllegalStateException("PermissionsModule must be started before accessing permissions!");
        return this.permissions;
    }

    // ===

    @Nonnull
    public static PermissionsModule get() {
        return get(PermissionsModule.class);
    }
}
