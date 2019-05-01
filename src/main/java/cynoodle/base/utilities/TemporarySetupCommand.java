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

package cynoodle.base.utilities;

import cynoodle.api.Random;
import cynoodle.api.parser.PrimitiveParsers;
import cynoodle.base.access.AccessList;
import cynoodle.base.access.AccessModule;
import cynoodle.base.commands.*;
import cynoodle.base.local.LocalContext;
import cynoodle.base.makeme.MakeMe;
import cynoodle.base.makeme.MakeMeController;
import cynoodle.base.makeme.MakeMeGroup;
import cynoodle.base.makeme.MakeMeModule;
import cynoodle.base.moderation.*;
import cynoodle.base.notifications.NotificationProperties;
import cynoodle.base.notifications.NotificationSettings;
import cynoodle.base.notifications.NotificationsModule;
import cynoodle.base.profiles.ProfilesModule;
import cynoodle.base.spamfilter.SpamFilterModule;
import cynoodle.base.spamfilter.SpamFilterSettings;
import cynoodle.base.xp.Rank;
import cynoodle.base.xp.RankManager;
import cynoodle.base.xp.XPModule;
import cynoodle.base.xp.XPSettings;
import cynoodle.discord.DiscordPointer;
import cynoodle.module.Module;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CIdentifier("base:utilities:setup_temporary")
@CAliases("setup-test")
public final class TemporarySetupCommand extends Command {
    private TemporarySetupCommand() {}

    private int confirm = Random.nextInt(0, Integer.MAX_VALUE - 1);


    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        if(input.hasParameter(0)) {

            int in = input.requireParameterAs(0, "input", PrimitiveParsers.parseInteger());

            if(in != confirm) throw CommandErrors.simple("Wrong confirmation code.");
            // continue to setup
        }
        else {

            confirm = Random.nextInt(0, Integer.MAX_VALUE - 1);

            System.out.println("CONFIRMATION CODE: " + confirm);

            return;
        }

        // setup stuff

        DiscordPointer GUILD = DiscordPointer.to(274439447234347010L);

        XPModule xpModule                           = Module.get(XPModule.class);
        ModerationModule moderationModule           = Module.get(ModerationModule.class);
        ProfilesModule profilesModule               = Module.get(ProfilesModule.class);
        SpamFilterModule spamFilterModule           = Module.get(SpamFilterModule.class);
        NotificationsModule notificationsModule     = Module.get(NotificationsModule.class);
        MakeMeModule makeMeModule                   = Module.get(MakeMeModule.class);
        AccessModule accessModule                   = Module.get(AccessModule.class);
        CommandsModule commandsModule               = Module.get(CommandsModule.class);

        // xp ranks

        System.out.println("Creating Ranks ...");

        RankManager ranks = xpModule.getRanks();

        Rank rank3 = ranks.create(GUILD, "Highway Constructor", 3);
        rank3.addRoles(Rank.createRole(DiscordPointer.to(274442653058465793L)));
        rank3.persist();
        Rank rank7 = ranks.create(GUILD, "House Rocker", 7);
        rank7.addRoles(Rank.createRole(DiscordPointer.to(274442601099427851L)));
        rank7.persist();
        Rank rank12 = ranks.create(GUILD, "Magic City Tourist", 12);
        rank12.addRoles(Rank.createRole(DiscordPointer.to(274442574620524544L)));
        rank12.persist();
        Rank rank19 = ranks.create(GUILD, "Floating Island Captain", 19);
        rank19.addRoles(Rank.createRole(DiscordPointer.to(274442536687370240L)));
        rank19.persist();
        Rank rank26 = ranks.create(GUILD, "Aspen Forest Ranger", 26);
        rank26.addRoles(Rank.createRole(DiscordPointer.to(274442504349286411L)));
        rank26.persist();
        Rank rank35 = ranks.create(GUILD, "Ghost Train Passenger", 35);
        rank35.addRoles(Rank.createRole(DiscordPointer.to(274442457632997376L)));
        rank35.persist();
        Rank rank44 = ranks.create(GUILD, "Lake Zurich Lifeguard", 44);
        rank44.addRoles(Rank.createRole(DiscordPointer.to(457987036864053252L)));
        rank44.persist();
        Rank rank56 = ranks.create(GUILD, "Spirit House Exorcist", 56);
        rank56.addRoles(Rank.createRole(DiscordPointer.to(332844204185092098L)));
        rank56.persist();
        Rank rank67 = ranks.create(GUILD, "Andromeda Astronaut", 67);
        rank67.addRoles(Rank.createRole(DiscordPointer.to(274442418928091137L)));
        rank67.persist();
        Rank rank78 = ranks.create(GUILD, "Space Monkey", 78);
        rank78.addRoles(Rank.createRole(DiscordPointer.to(274442383368650763L)));
        rank78.persist();
        Rank rank91 = ranks.create(GUILD, "Ghost Pirate", 91);
        rank91.addRoles(Rank.createRole(DiscordPointer.to(274442340683481089L)));
        rank91.persist();
        Rank rank100 = ranks.create(GUILD, "Kool Klown", 100);
        rank100.addRoles(Rank.createRole(DiscordPointer.to(274442249381871617L)));
        rank100.persist();
        Rank rank115 = ranks.create(GUILD, "Starshiner", 115);
        rank115.addRoles(Rank.createRole(DiscordPointer.to(443820383511117825L)));
        rank115.persist();
        Rank rank125 = ranks.create(GUILD, "Spat Out Demon", 125);
        rank125.addRoles(Rank.createRole(DiscordPointer.to(443855738205634563L)));
        rank125.persist();
        Rank rank137 = ranks.create(GUILD, "Empire Ant", 137);
        rank137.addRoles(Rank.createRole(DiscordPointer.to(443856669643112449L)));
        rank137.persist();
        Rank rank150 = ranks.create(GUILD, "Last Living Soul", 150);
        rank150.addRoles(Rank.createRole(DiscordPointer.to(332843998316199948L)));
        rank150.persist();

        // xp settings

        System.out.println("Setup XP Settings ...");

        XPSettings xpSettings = xpModule.getSettings(GUILD);

        xpSettings.setGainMin(15);
        xpSettings.setGainMax(55);
        xpSettings.setGainTimeout(Duration.ofSeconds(60));
        xpSettings.persist();

        // make-me

        System.out.println("Creating MMs and Group ...");

        MakeMeController.OnGuild mmController = makeMeModule.controller().onGuild(GUILD);

        DiscordPointer mmRoleCortez     = DiscordPointer.to(304005750194438144L);
        DiscordPointer mmRoleAce        = DiscordPointer.to(451884739666771978L);
        DiscordPointer mmRoleMike       = DiscordPointer.to(304004960885014528L);
        DiscordPointer mmRoleRussel     = DiscordPointer.to(276722842442399744L);
        DiscordPointer mmRoleMurdoc     = DiscordPointer.to(276723101579083776L);
        DiscordPointer mmRolePazuzu     = DiscordPointer.to(304005541754175488L);
        DiscordPointer mmRoleDel        = DiscordPointer.to(313387840367099904L);
        DiscordPointer mmRoleNoodle     = DiscordPointer.to(276722668005228544L);
        DiscordPointer mmRoleKatsu      = DiscordPointer.to(483027634801082400L);
        DiscordPointer mmRole2D         = DiscordPointer.to(276723007853035522L);
        DiscordPointer mmRoleEvangelist = DiscordPointer.to(304005865223094283L);
        DiscordPointer mmRoleWurzel     = DiscordPointer.to(304004739878748160L);
        DiscordPointer mmRoleBoogieman  = DiscordPointer.to(304003794734415874L);
        DiscordPointer mmRoleBruce      = DiscordPointer.to(304003565259587584L);
        DiscordPointer mmRoleCyborg     = DiscordPointer.to(312181376365494272L);

        MakeMe mmCortez     = mmController.create("cortez", "Cortez", mmRoleCortez);
        MakeMe mmAce        = mmController.create("ace", "Ace", mmRoleAce);
        MakeMe mmMike       = mmController.create("mike", "Mike The Monkey", mmRoleMike);
        MakeMe mmRussel     = mmController.create("russel", "Russel", mmRoleRussel);
        MakeMe mmMurdoc     = mmController.create("murdoc", "Murdoc", mmRoleMurdoc);
        MakeMe mmPazuzu     = mmController.create("pazuzu", "Pazuzu", mmRolePazuzu);
        MakeMe mmDel        = mmController.create("del", "Del Tha Ghost Rapper", mmRoleDel);
        MakeMe mmNoodle     = mmController.create("noodle", "Noodle", mmRoleNoodle);
        MakeMe mmKatsu      = mmController.create("katsu", "Katsu <:katsu:562553798904774656>", mmRoleKatsu);
        MakeMe mm2D         = mmController.create("2d", "2D", mmRole2D);
        MakeMe mmEvangelist = mmController.create("evangelist", "Evangelist", mmRoleEvangelist);
        MakeMe mmWurzel     = mmController.create("wurzel", "Dr. Wurzel", mmRoleWurzel);
        MakeMe mmBoogieman  = mmController.create("boogieman", "Boogieman", mmRoleBoogieman);
        MakeMe mmBruce      = mmController.create("bruce", "Bruce Willis", mmRoleBruce);
        MakeMe mmCyborg     = mmController.create("cyborg", "Cyborg Noodle", mmRoleCyborg);

        MakeMeGroup charactersGroup = mmController.createGroup("characters", "Characters");

        mmCortez.setGroup(charactersGroup);
        mmCortez.persist();
        mmAce.setGroup(charactersGroup);
        mmAce.persist();
        mmMike.setGroup(charactersGroup);
        mmMike.persist();
        mmRussel.setGroup(charactersGroup);
        mmRussel.persist();
        mmMurdoc.setGroup(charactersGroup);
        mmMurdoc.persist();
        mmPazuzu.setGroup(charactersGroup);
        mmPazuzu.persist();
        mmDel.setGroup(charactersGroup);
        mmDel.persist();
        mmNoodle.setGroup(charactersGroup);
        mmNoodle.persist();
        mmKatsu.setGroup(charactersGroup);
        mmKatsu.persist();
        mm2D.setGroup(charactersGroup);
        mm2D.persist();
        mmEvangelist.setGroup(charactersGroup);
        mmEvangelist.persist();
        mmWurzel.setGroup(charactersGroup);
        mmWurzel.persist();
        mmBoogieman.setGroup(charactersGroup);
        mmBoogieman.persist();
        mmBruce.setGroup(charactersGroup);
        mmBruce.persist();
        mmCyborg.setGroup(charactersGroup);
        mmCyborg.persist();

        charactersGroup.setUniqueEnabled(true);
        charactersGroup.persist();

        // notifications

        System.out.println("Setup Notification Settings ...");

        NotificationSettings notificationSettings = notificationsModule.getSettingsManager().firstOrCreate(GUILD);

        NotificationProperties npJoin = notificationSettings.getOrCreateProperties("base:utilities:member_join");
        npJoin.setChannel(DiscordPointer.to(291922013701013505L));
        npJoin.setMessages(Set.of(
                "**{member}** joined the server! Please read <#274441684144619521> and <#285820659618152448>!",
                "Welcome **{member}** to the server, please read <#274441684144619521> and <#285820659618152448>!",
                "Welcome **{member}**! Please read <#274441684144619521> and <#285820659618152448>!"
        ));
        NotificationProperties npLeave = notificationSettings.getOrCreateProperties("base:utilities:member_leave");
        npLeave.setChannel(DiscordPointer.to(291922013701013505L));
        npLeave.setMessages(Set.of(
                "**{member}** left the server.",
                "**{member}** just left the server.",
                "**{member}** has left the server."
        ));

        NotificationProperties npLevelUp = notificationSettings.getOrCreateProperties("base:xp:level_up");
        npLevelUp.setMessages(Set.of(
                "**{member}** reached **Level {level}**!",
                "**{member}** advanced to **Level {level}**!",
                "**Level {level}** was reached by **{member}**!"
        ));
        NotificationProperties npLevelDown = notificationSettings.getOrCreateProperties("base:xp:level_down");
        npLevelDown.setMessages(Set.of(
                "**{member}** leveled down to **Level {level}**."
        ));
        NotificationProperties npRankUp = notificationSettings.getOrCreateProperties("base:xp:rank_up");
        npRankUp.setMessages(Set.of(
                "**{member}** is now a **{rank}**!",
                "**{member}** has reached the Rank of **{rank}**!"
        ));
        NotificationProperties npBomb = notificationSettings.getOrCreateProperties("base:xp:bomb");
        npBomb.setMessages(Set.of(
                "**{member}** was lucky enough to win an **XP Bomb of {size} XP**!",
                "**{member}** just won an **XP Bomb of {size} XP**!",
                "An **XP Bomb of {size} XP** was won by **{member}**!"
        ));

        notificationSettings.persist();

        // strikes

        System.out.println("Setup Strike Settings ...");

        StrikeSettings strikeSettings = moderationModule.getStrikeSettingsManager().forGuild(GUILD);

        strikeSettings.setDefaultDecay(Decay.never());
        strikeSettings.persist();

        ModerationController.OnGuild moderationController = moderationModule.controller().onGuild(GUILD);
        MuteSettings muteSettings = moderationController.getMuteSettings();

        muteSettings.setDefaultDuration(Duration.ofMinutes(15));
        muteSettings.setRole(DiscordPointer.to(374881263384788992L));
        muteSettings.persist();

        // permissions

        System.out.println("Setup access lists for commands ...");

        DiscordPointer roleOwner = DiscordPointer.to(274440138992648195L);
        DiscordPointer roleMod = DiscordPointer.to(274441063463125004L);
        DiscordPointer roleEveryone = DiscordPointer.to(GUILD.asGuild().orElseThrow().getPublicRole());

        CommandSettings commandSettings = commandsModule.getSettings().firstOrCreate(GUILD);

        CommandSettings.Properties p1 = commandSettings.getProperties().findOrCreate("base:makeme:unmakeme");
        p1.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p2 = commandSettings.getProperties().findOrCreate("base:xp:forxp");
        p2.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p3 = commandSettings.getProperties().findOrCreate("base:xp:add");
        p3.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.DENY);
        p3.getAccess().forRoleOrCreate(roleMod).setStatus(AccessList.Status.ALLOW);
        p3.getAccess().forRoleOrCreate(roleOwner).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p4 = commandSettings.getProperties().findOrCreate("base:moderation:strike_restore");
        p4.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.DENY);
        p4.getAccess().forRoleOrCreate(roleMod).setStatus(AccessList.Status.ALLOW);
        p4.getAccess().forRoleOrCreate(roleOwner).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p5 = commandSettings.getProperties().findOrCreate("base:profile:profile");
        p5.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p6 = commandSettings.getProperties().findOrCreate("base:utilities:choose_of_role");
        p6.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p7 = commandSettings.getProperties().findOrCreate("base:moderation:mute");
        p7.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.DENY);
        p7.getAccess().forRoleOrCreate(roleMod).setStatus(AccessList.Status.ALLOW);
        p7.getAccess().forRoleOrCreate(roleOwner).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p8 = commandSettings.getProperties().findOrCreate("base:utilities:version");
        p8.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p9 = commandSettings.getProperties().findOrCreate("base:fm:edit");
        p9.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p10 = commandSettings.getProperties().findOrCreate("base:xp:leaderboard");
        p10.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p11 = commandSettings.getProperties().findOrCreate("base:moderation:strike_view");
        p11.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p12 = commandSettings.getProperties().findOrCreate("base:moderation:unmute");
        p12.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.DENY);
        p12.getAccess().forRoleOrCreate(roleMod).setStatus(AccessList.Status.ALLOW);
        p12.getAccess().forRoleOrCreate(roleOwner).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p13 = commandSettings.getProperties().findOrCreate("base:xp:forlevel");
        p13.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p14 = commandSettings.getProperties().findOrCreate("base:moderation:delete");
        p14.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.DENY);
        p14.getAccess().forRoleOrCreate(roleMod).setStatus(AccessList.Status.ALLOW);
        p14.getAccess().forRoleOrCreate(roleOwner).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p15 = commandSettings.getProperties().findOrCreate("base:moderation:strike_add");
        p15.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.DENY);
        p15.getAccess().forRoleOrCreate(roleMod).setStatus(AccessList.Status.ALLOW);
        p15.getAccess().forRoleOrCreate(roleOwner).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p16 = commandSettings.getProperties().findOrCreate("base:profile:edit");
        p16.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p17 = commandSettings.getProperties().findOrCreate("base:moderation:strike_edit");
        p17.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.DENY);
        p17.getAccess().forRoleOrCreate(roleMod).setStatus(AccessList.Status.ALLOW);
        p17.getAccess().forRoleOrCreate(roleOwner).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p18 = commandSettings.getProperties().findOrCreate("base:makeme:makeme");
        p18.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p19 = commandSettings.getProperties().findOrCreate("base:moderation:strike_remove");
        p19.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.DENY);
        p19.getAccess().forRoleOrCreate(roleMod).setStatus(AccessList.Status.ALLOW);
        p19.getAccess().forRoleOrCreate(roleOwner).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p20 = commandSettings.getProperties().findOrCreate("base:utilities:help_temporary");
        p20.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p21 = commandSettings.getProperties().findOrCreate("base:xp:transfer");
        p21.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.DENY);
        p21.getAccess().forRoleOrCreate(roleMod).setStatus(AccessList.Status.ALLOW);
        p21.getAccess().forRoleOrCreate(roleOwner).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p22 = commandSettings.getProperties().findOrCreate("base:moderation:strike_list");
        p22.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p23 = commandSettings.getProperties().findOrCreate("base:fm:fm");
        p23.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p24 = commandSettings.getProperties().findOrCreate("base:xp:remove");
        p24.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.DENY);
        p24.getAccess().forRoleOrCreate(roleMod).setStatus(AccessList.Status.ALLOW);
        p24.getAccess().forRoleOrCreate(roleOwner).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p25 = commandSettings.getProperties().findOrCreate("base:xp:info");
        p25.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p26 = commandSettings.getProperties().findOrCreate("base:xp:ranks");
        p26.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.ALLOW);
        CommandSettings.Properties p27 = commandSettings.getProperties().findOrCreate("base:xp:xp");
        p27.getAccess().forRoleOrCreate(roleEveryone).setStatus(AccessList.Status.ALLOW);

        commandSettings.persist();

        // disable spam filter for now

        System.out.println("Setup spam filter ...");

        SpamFilterSettings spamFilterSettings = spamFilterModule.getSettingsManager().firstOrCreate(GUILD);

        spamFilterSettings.setEnabled(false);
        spamFilterSettings.persist();

        // make sure make-me are set

        System.out.println("Assigning existing MMs ...");

        for (Member member : GUILD.asGuild().orElseThrow().getMembers()) {
            List<Long> roles = member.getRoles()
                    .stream()
                    .map(ISnowflake::getIdLong)
                    .collect(Collectors.toList());

            MakeMeController.OnMember mc
                    = makeMeModule.controller().onMember(GUILD, DiscordPointer.to(member.getUser()));

            if(roles.contains(mmRoleCortez.getID())) mc.make(mmCortez);
            if(roles.contains(mmRoleAce.getID())) mc.make(mmAce);
            if(roles.contains(mmRoleMike.getID())) mc.make(mmMike);
            if(roles.contains(mmRoleRussel.getID())) mc.make(mmRussel);
            if(roles.contains(mmRoleMurdoc.getID())) mc.make(mmMurdoc);
            if(roles.contains(mmRolePazuzu.getID())) mc.make(mmPazuzu);
            if(roles.contains(mmRoleDel.getID())) mc.make(mmDel);
            if(roles.contains(mmRoleNoodle.getID())) mc.make(mmNoodle);
            if(roles.contains(mmRoleKatsu.getID())) mc.make(mmKatsu);
            if(roles.contains(mmRole2D.getID())) mc.make(mm2D);
            if(roles.contains(mmRoleEvangelist.getID())) mc.make(mmEvangelist);
            if(roles.contains(mmRoleWurzel.getID())) mc.make(mmWurzel);
            if(roles.contains(mmRoleBoogieman.getID())) mc.make(mmBoogieman);
            if(roles.contains(mmRoleBruce.getID())) mc.make(mmBruce);
            if(roles.contains(mmRoleCyborg.getID())) mc.make(mmCyborg);
        }

        // done.
        System.out.println("Done.");

    }
}
