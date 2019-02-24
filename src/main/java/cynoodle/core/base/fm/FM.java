/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.fm;

import cynoodle.core.discord.UEntity;
import cynoodle.core.entities.EIdentifier;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * A users last.fm settings.
 */
@EIdentifier("base:fm:fm")
public final class FM extends UEntity {
    private FM() {}

    // ===

    /**
     * The last.fm username.
     */
    private String username = null;

    /**
     * The preferred format for {@link FMCommand}.
     */
    private FMFormat preferredFormat = FMFormat.SIMPLE;

    // ===

    @Nonnull
    public Optional<String> getUsername() {
        return Optional.ofNullable(this.username);
    }

    public void setUsername(@Nullable String username) {
        this.username = username;
    }

    @Nonnull
    public FMFormat getPreferredFormat() {
        return this.preferredFormat;
    }

    public void setPreferredFormat(@Nonnull FMFormat preferredFormat) {
        this.preferredFormat = preferredFormat;
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.username = source.getAt("username").asStringNullable().or(this.username);
        this.preferredFormat = FMFormat
                .values()[source.getAt("preferred_format").asInteger().or(this.preferredFormat.ordinal())];
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("username").asStringNullable().to(this.username);
        data.setAt("preferred_format").asInteger().to(this.preferredFormat.ordinal());

        return data;
    }
}
