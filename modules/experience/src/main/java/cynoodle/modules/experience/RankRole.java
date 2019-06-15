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

package cynoodle.modules.experience;

import cynoodle.discord.RReference;
import cynoodle.mongodb.BsonDataException;
import cynoodle.mongodb.IBsonDocument;
import cynoodle.mongodb.IBsonDocumentCodec;
import cynoodle.mongodb.fluent.Codec;
import cynoodle.mongodb.fluent.FluentDocument;

import javax.annotation.Nonnull;

/**
 * Keeps information about a role which can be assigned with a {@link Rank}.
 */
public final class RankRole implements IBsonDocument {
    private RankRole() {}

    /**
     * The role.
     */
    private RReference role;

    /**
     * If this role should be kept over the effective range of the Rank.
     */
    private boolean keep = false;

    /**
     * If this role should be hidden for displays.
     */
    private boolean hidden = false;

    // ===

    RankRole(@Nonnull RReference role) {
        this.role = role;
    }

    // ===

    @Nonnull
    public RReference getRole() {
        return this.role;
    }

    //

    public boolean isKeepEnabled() {
        return keep;
    }

    public void setKeepEnabled(boolean keep) {
        this.keep = keep;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    // ===

    public boolean hasValidRole() {
        return getRole().getRole().isPresent();
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument data) throws BsonDataException {

        this.role = data.getAt("role").as(RReference.codec()).or(this.role);
        this.keep = data.getAt("keep").asBoolean().or(this.keep);
        this.hidden = data.getAt("hidden").asBoolean().or(this.hidden);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = FluentDocument.wrapNew();

        data.setAt("role").as(RReference.codec()).to(this.role);
        data.setAt("keep").asBoolean().to(this.keep);
        data.setAt("hidden").asBoolean().to(this.hidden);

        return data;
    }

    // ===

    static Codec<RankRole> codec() {
        return new IBsonDocumentCodec<>(RankRole::new);
    }
}
