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

package cynoodle.discord;

import com.mongodb.client.model.Filters;
import cynoodle.entity.EIndex;
import cynoodle.entity.Entity;
import cynoodle.mongodb.BsonDataException;
import cynoodle.mongodb.fluent.FluentDocument;
import net.dv8tion.jda.api.entities.User;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * An Entity which belongs to a {@link User}.
 */
@EIndex(UEntity.KEY_USER)
public abstract class UEntity extends Entity implements IUEntity {
    protected UEntity() {}

    // ===

    static final String KEY_USER = "user";

    // ===

    /**
     * The User.
     */
    @Nullable
    private UReference user = null;

    // === USER ===

    @Nonnull
    @Override
    public final Optional<UReference> getUser() {
        return Optional.ofNullable(this.user);
    }

    @Override
    public final void setUser(@Nullable UReference user) {
        this.user = user;
    }

    // === FILTER ===

    @Nonnull
    public static Bson filterUser(@Nonnull UReference user) {
        return Filters.eq(KEY_USER, user.getID());
    }

    @Nonnull
    public static Bson filterUser(@Nonnull User user) {
        return filterUser(UReference.to(user));
    }

    @Nonnull
    @Deprecated
    public static Bson filterUser(@Nonnull DiscordPointer user) {
        return filterUser(UReference.to(user.getID()));
    }

    // ===

    @Override
    public String toString() {
        return "UEntity(U:" + this.user + ")";
    }

    // == DATA ==

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {

        this.user = source.getAt(KEY_USER).as(UReference.codec()).or(this.user);

    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = FluentDocument.wrapNew();

        data.setAt(KEY_USER).as(UReference.codec()).to(this.user);

        return data;
    }

}