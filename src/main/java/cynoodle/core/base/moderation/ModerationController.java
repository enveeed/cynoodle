/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.moderation;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.GEntityManager;
import cynoodle.core.discord.RModifier;
import cynoodle.core.module.Module;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

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

    private final StrikeManager strikeManager =
            module.getStrikeManager();
    private final GEntityManager<StrikeSettings> strikeSettingsManager =
            module.getStrikeSettingsManager();
    private final GEntityManager<MuteSettings> muteSettingsManager =
            module.getMuteSettingsManager();

    private final MuteManager muteManager =
            module.getMuteManager();

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
                    .asRole(guild)
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

            for (Channel channel : channels) {

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

        @Nonnull
        @CanIgnoreReturnValue
        public Mute muteFinite(@Nonnull Duration duration) {

            Mute mute = Mute.finite(this.guild, this.user, duration);

            muteManager.mute(mute);

            this.applyMute();

            return mute;
        }

        @Nonnull
        @CanIgnoreReturnValue
        public Mute muteInfinite() {

            Mute mute = Mute.infinite(this.guild, this.user);

            muteManager.mute(mute);

            this.applyMute();

            return mute;
        }

        public void unmute() {
            muteManager.unmute(this.guild, user);
        }

        //

        @Nonnull
        public Optional<Mute> getMute() {
            return muteManager.get(this.guild, user);
        }

        @Nonnull
        public Optional<Mute> getEffectiveMute() {
            return muteManager.getEffective(this.guild, user);
        }

        //

        public boolean isMuted() {
            return getEffectiveMute().isPresent();
        }

        //

        public void applyMute(boolean ensureEnvironment) {

            System.out.println("applying mute ...");

            OnGuild onGuild = onGuild(this.guild);

            Optional<DiscordPointer> rolePResult = onGuild.getMuteSettings().getRole();
            if(rolePResult.isEmpty()) {
                // TODO warn
                return;
            }

            Role role = rolePResult.orElseThrow()
                    .asRole(guild.asGuild().orElseThrow())
                    .orElseThrow(); // TODO warn role not exists

            //

            if(ensureEnvironment)
                onGuild.ensureMuteEnvironment();

            //

            RModifier modifier = RModifier.on(this.user.asMember(guild.asGuild().orElseThrow()).orElseThrow());

            if(isMuted()) {
                modifier.add(role);
            }
            else {
                modifier.remove(role);
            }

            //

            modifier.done()
                    .reason("Applied mute")
                    .queue();

            System.out.println("mute applied (muted = "+isMuted()+")");
        }

        public void applyMute() {
            this.applyMute(true);
        }
    }

    //

    void applyMutes() {

        Set<DiscordPointer> affected = new HashSet<>();

        // TODO currently this tracks past and current mutes, this needs to be more efficient

        // apply mutes for all members
        muteManager.all().forEach(mute -> {

            DiscordPointer guild = mute.getGuild();

            affected.add(guild);

            onMember(guild, mute.getUser())
                    .applyMute(false);
        });

        // ensure mute environments for all affected guilds
        affected.forEach(guild -> onGuild(guild).ensureMuteEnvironment());
    }

}
