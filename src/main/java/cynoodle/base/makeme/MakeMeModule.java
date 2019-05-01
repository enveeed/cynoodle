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

package cynoodle.base.makeme;

import cynoodle.base.commands.CommandRegistry;
import cynoodle.base.commands.CommandsModule;
import cynoodle.discord.GEntityManager;
import cynoodle.discord.MEntityManager;
import cynoodle.entities.EntityType;
import cynoodle.module.MIdentifier;
import cynoodle.module.MRequires;
import cynoodle.module.Module;

import javax.annotation.Nonnull;

@MIdentifier("base:makeme")
@MRequires("base:commands")
public final class MakeMeModule extends Module {
    private MakeMeModule() {}

    private static final EntityType<MakeMe> ENTITY_MAKE_ME = EntityType.of(MakeMe.class);
    private static final EntityType<MakeMeGroup> ENTITY_GROUP = EntityType.of(MakeMeGroup.class);
    private static final EntityType<MakeMeStatus> ENTITY_STATUS = EntityType.of(MakeMeStatus.class);

    // ===

    private GEntityManager<MakeMe> makeMeManager;
    private GEntityManager<MakeMeGroup> groupManager;
    private MEntityManager<MakeMeStatus> statusManager;

    //

    private MakeMeController controller;

    // ===

    @Override
    protected void start() {
        super.start();

        this.makeMeManager = new GEntityManager<>(ENTITY_MAKE_ME);
        this.groupManager = new GEntityManager<>(ENTITY_GROUP);
        this.statusManager = new MEntityManager<>(ENTITY_STATUS);

        //

        this.controller = new MakeMeController();

        //

        CommandRegistry registry = Module.get(CommandsModule.class)
                .getRegistry();

        registry.register(MakeMeCommand.class);
        registry.register(UnMakeMeCommand.class);
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

    //

    @Nonnull
    public MakeMeController controller() {
        return this.controller;
    }
}
