/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.mm;

import cynoodle.core.discord.GEntityManager;
import cynoodle.core.discord.MEntityManager;
import cynoodle.core.entities.EntityType;
import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

@MIdentifier("base:mm")
public final class MakeMeModule extends Module {
    private MakeMeModule() {}

    private static final EntityType<MakeMe> TYPE_MAKE_ME = EntityType.of(MakeMe.class);
    private static final EntityType<MakeMeGroup> TYPE_GROUP = EntityType.of(MakeMeGroup.class);
    private static final EntityType<MakeMeStatus> TYPE_STATUS = EntityType.of(MakeMeStatus.class);

    // ===

    private GEntityManager<MakeMe> makeMeManager;
    private GEntityManager<MakeMeGroup> groupManager;
    private MEntityManager<MakeMeStatus> statusManager;

    // ===

    @Override
    protected void start() {
        super.start();

        this.makeMeManager = new GEntityManager<>(TYPE_MAKE_ME);
        this.groupManager = new GEntityManager<>(TYPE_GROUP);
        this.statusManager = new MEntityManager<>(TYPE_STATUS);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    // ===

    @Nonnull
    public GEntityManager<MakeMe> getMakeMeManager() {
        return this.makeMeManager;
    }

    @Nonnull
    public GEntityManager<MakeMeGroup> getGroupManager() {
        return this.groupManager;
    }

    @Nonnull
    public MEntityManager<MakeMeStatus> getStatusManager() {
        return this.statusManager;
    }
}
