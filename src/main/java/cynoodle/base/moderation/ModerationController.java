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

package cynoodle.base.moderation;

import cynoodle.discord.*;
import cynoodle.module.Module;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class ModerationController {
    ModerationController() {}

    private final ModerationModule module =
            Module.get(ModerationModule.class);

    private final MEntityManager<MuteStatus> muteStatusManager =
            module.getMuteStatusEntities();
    private final GEntityManager<MuteSettings> muteSettingsManager =
            module.getMuteSettingsEntities();

    private final DiscordModule discord =
            Module.get(DiscordModule.class);

    // ===

    @Nonnull
    public OnGuild onGuild(@Nonnull DiscordPointer guild) {
        return new OnGuild(guild);
    }

    @Nonnull
    public OnMember onMember(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user) {
        return new OnMember(guild, user);
    }

    // ===

    public final class OnGuild {

        private final DiscordPointer guild;

        // ===

        private OnGuild(@Nonnull DiscordPointer guild) {
            this.guild = guild;
        }

        // === MUTES ===

        /**
         * Get the {@link MuteSettings} for this Guild.
         * @return the mute settings
         */
        @Nonnull
        public MuteSettings getMuteSettings() {
            return muteSettingsManager.firstOrCreate(this.guild);
        }

        //

        /**
         * Ensure that the guild environment is setup to allow mutes,
         * if any part is not, it will be set up.
         *
         * This will check the setup of the mute role and all channels.
         */
        public void ensureMuteEnvironment() {

            Guild guild = this.guild.asGuild()
                    .orElseThrow();
            MuteSettings settings = getMuteSettings();

            Optional<DiscordPointer> rolePResult = settings.getRole();
            if(rolePResult.isEmpty()) {
                // TODO warn
                return;
            }

            Role role = rolePResult.orElseThrow()
                    .asRole()
                    .orElseThrow(); // TODO warn role not exists

            // check role
            if(role.getPermissionsRaw() != 0L) {
                role.getManager()
                        .setPermissions(0L)
                        .reason("Setting up mute role")
                        .complete();
            }

            final long channelOverrideDeny = Permission.getRaw(
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_TTS,
                    Permission.MESSAGE_ADD_REACTION
            );

            // check channels
            List<TextChannel> channels = guild.getTextChannels();

            for (TextChannel channel : channels) {

                PermissionOverride override = channel.getPermissionOverride(role);

                if(override == null || override.getAllowedRaw() != 0L || override.getDeniedRaw() != channelOverrideDeny) {
                    channel.putPermissionOverride(role)
                            .setPermissions(0L, channelOverrideDeny)
                            .reason("Setting up muting environment")
                            .complete();
                }
            }
        }
    }

    // ===

    public final class OnMember {

        private final DiscordPointer guild;
        private final DiscordPointer user;

        // ===

        private OnMember(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user) {
            this.guild = guild;
            this.user = user;
        }

        // === STRIKES ===

        // TODO

        // === MUTES ===

        public void muteFinite(@Nonnull Duration duration) {

            MuteStatus status = muteStatusManager.firstOrCreate(this.guild, this.user);

            status.setFinite(duration);
            status.persist();

            this.applyMute();
        }

        public void muteInfinite() {

            MuteStatus status = muteStatusManager.firstOrCreate(this.guild, this.user);

            status.setInfinite();
            status.persist();

            this.applyMute();
        }

        public void unmute() {

            MuteStatus status = muteStatusManager.firstOrCreate(this.guild, this.user);

            status.unset();
            status.persist();

            this.applyMute();
        }

        //

        public boolean isMuted() {

            MuteStatus status = muteStatusManager.firstOrCreate(this.guild, this.user);

            return status.isEffectivelyMuted();
        }

        //

        public void applyMute(boolean ensureEnvironment) {

            OnGuild onGuild = onGuild(this.guild);

            Optional<DiscordPointer> rolePResult = onGuild.getMuteSettings().getRole();
            if(rolePResult.isEmpty()) {
                // TODO warn
                return;
            }

            Role role = rolePResult.orElseThrow()
                    .asRole()
                    .orElseThrow(); // TODO warn role not exists

            //

            if(ensureEnvironment)
                onGuild.ensureMuteEnvironment();

            //

            MuteStatus status = muteStatusManager.firstOrCreate(this.guild, this.user);

            // check if mute has expired, unset if this is the case

            if(status.hasExpired()) {
                status.unset();
                status.persist();
            }

            //

            RModifier modifier = RModifier.on(this.user.asMember(guild.asGuild().orElseThrow()).orElseThrow());

            if(status.isEffectivelyMuted())
                modifier.add(role);
            else
                modifier.remove(role);

            //

            modifier.done()
                    .reason("Muting applied")
                    .queue();
        }

        public void applyMute() {
            this.applyMute(true);
        }
    }

    //

    void applyMutes() {

        Set<DiscordPointer> affectedGuilds = new HashSet<>();

        // apply mutes for each muted status
        muteStatusManager.stream(MuteStatus.filterMuted())
                .forEach(status -> {

            DiscordPointer guild = status.requireGuild();
            DiscordPointer user = status.requireUser();

            onMember(guild, user).applyMute(false);

            affectedGuilds.add(guild);

        });


        // ensure mute environment for every affected guild
        for (DiscordPointer guild : affectedGuilds) {

            onGuild(guild).ensureMuteEnvironment();
        }

    }

}
