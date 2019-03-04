/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.discord;

import cynoodle.core.entities.embed.Embed;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentDocument;
import net.dv8tion.jda.core.entities.Guild;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

import static cynoodle.core.discord.DiscordPointer.fromBsonNullable;
import static cynoodle.core.discord.DiscordPointer.toBsonNullable;

/**
 * An {@link Embed} which belongs to a {@link Guild}.
 */
public abstract class GEmbed extends Embed implements GHolder {
    protected GEmbed() {}

    // ===

    static final String KEY_GUILD = "guild";

    // ===

    /**
     * The Guild.
     */
    @Nullable
    private DiscordPointer guild = null;

    // === GUILD ===

    @Nonnull
    @Override
    public final Optional<DiscordPointer> getGuild() {
        return Optional.ofNullable(this.guild);
    }

    @Override
    public final void setGuild(@Nullable DiscordPointer guild) {
        this.guild = guild;
    }

    @Nonnull
    public final DiscordPointer requireGuild() throws IllegalStateException {
        return getGuild().orElseThrow(() -> new IllegalStateException("No Guild set."));
    }

    // === DATA ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.guild = source.getAt(KEY_GUILD).as(fromBsonNullable()).or(this.guild);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt(KEY_GUILD).as(toBsonNullable()).to(this.guild);

        return data;
    }
}
