/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.moderation;

import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.MEntityManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import javax.annotation.Nonnull;

public final class StrikeManager extends MEntityManager<Strike> {

    StrikeManager() {
        super(ModerationModule.TYPE_STRIKE);
    }

    //

    @Nonnull
    public Strike create(@Nonnull Member member, @Nonnull String reason) {
        return this.create(member, strike -> strike.create(reason));
    }

    @Nonnull
    public Strike create(@Nonnull Guild guild, @Nonnull User user, @Nonnull String reason) {
        return this.create(guild, user, strike -> strike.create(reason));
    }

    @Nonnull
    public Strike create(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user, @Nonnull String reason) {
        return this.create(guild, user, strike -> strike.create(reason));
    }
}
