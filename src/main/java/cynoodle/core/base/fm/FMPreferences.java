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
    public static final String      DEF_FORMAT = null;
    public static final boolean     DEF_PROFILE_ENABLED = false;

    // ===

    /**
     * The last.fm username.
     */
    private String username = null;

    /**
     * The format name for {@link FMCommand}.
     */
    private String format = null;

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
    public Optional<String> getFormat() {
        return Optional.ofNullable(this.format);
    }

    public void setFormat(@Nullable String format) {
        this.format = format;
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
        this.format = source.getAt("format").asStringNullable().or(this.format);
        this.profileEnabled = source.getAt("profile").asBoolean().or(this.profileEnabled);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("username").asStringNullable().to(this.username);
        data.setAt("format").asStringNullable().to(this.format);
        data.setAt("profile").asBoolean().to(this.profileEnabled);

        return data;
    }
}
