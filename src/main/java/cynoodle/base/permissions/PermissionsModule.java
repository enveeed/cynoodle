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

import com.google.common.flogger.FluentLogger;
import cynoodle.module.MIdentifier;
import cynoodle.module.Module;

import javax.annotation.Nonnull;

@MIdentifier(PermissionsModule.IDENTIFIER)
public final class PermissionsModule extends Module {
    private PermissionsModule() {}

    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    static final String IDENTIFIER = "base:permissions";

    // ===

    private Permissions permissions = null;

    // ===

    @Override
    protected void start() {
        super.start();

        // initialize permissions instance
        this.permissions = new Permissions();

        // setup events
        // TODO replace this with actual cynoodle API event listener registration
        noodle().getDiscord().getAPI().addEventListener(new PermissionsEventHandler());

        // TODO test if something was deleted while we were offline and remove those permissions (role + member)

        // delete orphans
        LOG.atFine().log("Deleting orphan permissions ...");
        this.permissions.deleteOrphanPermissions();
        LOG.atFine().log("Done.");
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
