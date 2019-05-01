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

package cynoodle.base.fm;

import cynoodle.discord.UEntity;
import cynoodle.entities.EIdentifier;
import cynoodle.mongo.BsonDataException;
import cynoodle.mongo.fluent.FluentDocument;

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
