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
 * A users last.fm preferences.
 */
@EIdentifier("base:fm:preferences")
public final class FMPreferences extends UEntity {
    private FMPreferences() {}

    // ===

    public static final String      DEF_USERNAME = null;
    public static final FMFormat    DEF_PREFERRED_FORMAT = FMFormat.SIMPLE;
    public static final boolean     DEF_PROFILE_ENABLED = false;

    // ===

    /**
     * The last.fm username.
     */
    private String username = null;

    /**
     * The preferred format for {@link FMCommand}.
     */
    private FMFormat preferredFormat = FMFormat.SIMPLE;

    /**
     * If the last.fm account should be linked on the profile.
     */
    private boolean profileEnabled = false;

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

    public boolean isProfileEnabled() {
        return this.profileEnabled;
    }

    public void setProfileEnabled(boolean profileEnabled) {
        this.profileEnabled = profileEnabled;
    }

// ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.username = source.getAt("username").asStringNullable().or(this.username);
        this.preferredFormat = source.getAt("format_preferred").asInteger()
                .map(i -> FMFormat.values()[i]).or(this.preferredFormat);
        this.profileEnabled = source.getAt("profile").asBoolean().or(this.profileEnabled);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("username").asStringNullable().to(this.username);
        data.setAt("format_preferred").asInteger().map(FMFormat::ordinal).to(this.preferredFormat);
        data.setAt("profile").asBoolean().to(this.profileEnabled);

        return data;
    }
}
