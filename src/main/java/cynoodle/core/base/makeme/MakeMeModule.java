/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.makeme;

import cynoodle.core.base.commands.CommandsModule;
import cynoodle.core.base.commands.CommandRegistry;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.GEntityManager;
import cynoodle.core.discord.MEntityManager;
import cynoodle.core.entities.EntityType;
import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.MRequires;
import cynoodle.core.module.Module;

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

        // === TEST ONLY ===

        if(noodle().getLaunchSettings().isSetupTestEnabled()) {

            DiscordPointer guild = DiscordPointer.to(394436622994636801L);

            MakeMeController.OnGuild controller = controller().onGuild(guild);

            MakeMe mm_a = controller.create("mm_a", "Test Make-Me A", DiscordPointer.to(555107066138591253L));
            MakeMe mm_b = controller.create("mm_b", "Test Make-Me B", DiscordPointer.to(556866896662757376L));
            MakeMe mm_c = controller.create("mm_c", "Test Make-Me C", DiscordPointer.to(556866931995443211L));
            MakeMe mm_d = controller.create("mm_d", "Test Make-Me D", DiscordPointer.to(556866974697652235L));
            MakeMe mm_e = controller.create("mm_e", "Test Make-Me E", DiscordPointer.to(556867020281610271L));
            MakeMe mm_f = controller.create("mm_f", "Test Make-Me F", DiscordPointer.to(556867096181473322L));

            MakeMeGroup group_first = controller.createGroup("mm_first", "Test Group I");
            MakeMeGroup group_second = controller.createGroup("mm_second", "Test Group II");

            mm_a.setGroup(group_first);
            mm_b.setGroup(group_first);
            mm_c.setGroup(group_first);
            mm_d.setGroup(group_second);
            mm_e.setGroup(group_second);
            mm_f.setGroup(null);

            getMakeMeManager().persistAll(mm_a, mm_b, mm_c, mm_d, mm_e, mm_f);
            getGroupManager().persistAll(group_first, group_second);
        }
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
