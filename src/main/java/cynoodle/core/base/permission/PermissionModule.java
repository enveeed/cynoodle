/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.permission;

import cynoodle.core.discord.GEntityManager;
import cynoodle.core.entities.EntityType;
import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

@MIdentifier("base:permission")
public final class PermissionModule extends Module {
    private PermissionModule() {}

    // ===

    private static final EntityType<Permission> TYPE_PERMISSION = EntityType.of(Permission.class);

    // ===

    private GEntityManager<Permission> permissionManager;

    // ===

    @Override
    protected void start() {
        super.start();

        this.permissionManager = new GEntityManager<>(TYPE_PERMISSION);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    // ===

    @Nonnull
    public GEntityManager<Permission> getPermissionManager() {
        return this.permissionManager;
    }
}
