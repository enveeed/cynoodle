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

package cynoodle.base.profiles;

import cynoodle.base.commands.CommandRegistry;
import cynoodle.base.commands.CommandsModule;
import cynoodle.discord.DiscordPointer;
import cynoodle.discord.UEntityManager;
import cynoodle.entity.EntityType;
import cynoodle.module.MIdentifier;
import cynoodle.module.MRequires;
import cynoodle.module.Module;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

@MIdentifier("base:profiles")
@MRequires("base:commands")
@MRequires("base:local")
@MRequires("base:fm")
public final class ProfilesModule extends Module {
    private ProfilesModule() {}

    private final static EntityType<Profile> TYPE_PROFILE = EntityType.of(Profile.class);

    private final static ProfileBadge BADGE_DEVELOPER
            = new ProfileBadge(DiscordPointer.to(551816523304534037L), user -> user.getIdLong() == 145575908881727488L);
    private final static ProfileBadge BADGE_HIM
            = new ProfileBadge(DiscordPointer.to((567754580008370176L)), user -> user.getIdLong() == 200688166930350080L);

    private UEntityManager<Profile> profileManager;

    private Set<ProfileBadge> badges = new HashSet<>();

    // ===

    @Override
    protected void start() {
        super.start();

        this.profileManager = new UEntityManager<>(TYPE_PROFILE);

        CommandRegistry registry = Module.get(CommandsModule.class).getRegistry();

        registry.register(ProfileCommand.class);
        registry.register(ProfileEditCommand.class);

        this.addBadge(BADGE_DEVELOPER);
        this.addBadge(BADGE_HIM);

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

    // ===

    @Nonnull
    public Set<ProfileBadge> getBadges() {
        return Set.copyOf(this.badges);
    }

    //

    public void addBadge(@Nonnull ProfileBadge badge) {
        this.badges.add(badge);
    }

    public void removeBadge(@Nonnull ProfileBadge badge) {
        this.badges.remove(badge);
    }
}
