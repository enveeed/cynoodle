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

import cynoodle.util.Random;
import cynoodle.util.parsing.PrimitiveParsers;
import cynoodle.base.access.AccessModule;
import cynoodle.base.commands.*;
import cynoodle.base.local.LocalContext;
import cynoodle.base.makeme.MakeMeModule;
import cynoodle.base.moderation.ModerationModule;
import cynoodle.base.notifications.NotificationsModule;
import cynoodle.base.permissions.Permissions;
import cynoodle.base.profiles.ProfilesModule;
import cynoodle.base.spamfilter.SpamFilterModule;
import cynoodle.base.xp.XPModule;
import cynoodle.discord.DiscordPointer;
import cynoodle.module.Module;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nonnull;

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
        Guild guild = GUILD.requireGuild();

        XPModule xpModule                           = Module.get(XPModule.class);
        ModerationModule moderationModule           = Module.get(ModerationModule.class);
        ProfilesModule profilesModule               = Module.get(ProfilesModule.class);
        SpamFilterModule spamFilterModule           = Module.get(SpamFilterModule.class);
        NotificationsModule notificationsModule     = Module.get(NotificationsModule.class);
        MakeMeModule makeMeModule                   = Module.get(MakeMeModule.class);
        AccessModule accessModule                   = Module.get(AccessModule.class);
        CommandsModule commandsModule               = Module.get(CommandsModule.class);

        // new permissions

        System.out.println("Setting up new permissions ...");

        Permissions permissions = Permissions.get();
        
        System.out.println("Setting up new make-me ...");

        // done.
        System.out.println("Done.");

    }
}
