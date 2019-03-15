/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.profiles;

import cynoodle.core.base.commands.CommandsModule;
import cynoodle.core.base.commands.CommandRegistry;
import cynoodle.core.discord.UEntityManager;
import cynoodle.core.entities.EntityType;
import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.MRequires;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

@MIdentifier("base:profiles")
@MRequires("base:commands")
@MRequires("base:local")
@MRequires("base:fm")
public final class ProfilesModule extends Module {
    private ProfilesModule() {}

    private final static EntityType<Profile> TYPE_PROFILE = EntityType.of(Profile.class);

    private UEntityManager<Profile> profileManager;

    // ===

    @Override
    protected void start() {
        super.start();

        this.profileManager = new UEntityManager<>(TYPE_PROFILE);

        CommandRegistry registry = Module.get(CommandsModule.class).getRegistry();

        registry.register(ProfileCommand.class);
        registry.register(ProfileEditCommand.class);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    // ===

    @Nonnull
    public UEntityManager<Profile> getProfileManager() {
        return this.profileManager;
    }
}
