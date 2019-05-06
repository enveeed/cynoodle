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

package cynoodle.base.makeme;

import com.mongodb.client.model.Filters;
import cynoodle.base.permissions.Permission;
import cynoodle.base.permissions.PermissionReference;
import cynoodle.discord.GEntity;
import cynoodle.entities.EIdentifier;
import cynoodle.entities.EIndex;
import cynoodle.entities.EntityIOException;
import cynoodle.mongo.BsonDataException;
import cynoodle.mongo.fluent.FluentDocument;
import net.dv8tion.jda.api.entities.Member;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * A make-me group is a collection of {@link MakeMe make-me}.
 */
@EIdentifier("base:makeme:group")
@EIndex(MakeMeGroup.KEY_KEY)
public final class MakeMeGroup extends GEntity {
    private MakeMeGroup() {}

    static final String KEY_KEY = "key";

    // ===

    /**
     * The key of the group.
     */
    private String key;

    /**
     * The name of the group.
     */
    private String name;

    /**
     * The permission for this group.
     */
    private PermissionReference permission = null;

    //

    /**
     * 'unique' flag, allows only one make-me of the group for the Member.
     */
    private boolean unique = false;

    // ===

    void create(@Nonnull String key, @Nonnull String name) {
        this.key = key;
        this.name = name;
    }

    // ===

    @Nonnull
    public String getKey() {
        return this.key;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    public void setName(@Nonnull String name) {
        this.name = name;
    }

    //

    @Nonnull
    public Optional<Permission> getPermission() {
        return this.permission == null ? Optional.empty() : this.permission.get();
    }

    public void setPermission(@Nullable Permission permission) {
        this.permission = permission == null ? null : PermissionReference.of(permission);
    }

    //

    public boolean isUniqueEnabled() {
        return this.unique;
    }

    public void setUniqueEnabled(boolean unique) {
        this.unique = unique;
    }

    // ===

    public boolean canAccess(@Nonnull Member member) {
        return this.permission == null || this.permission.require().test(member);
    }

    // ===

    @Nonnull
    public static Bson filterKey(@Nonnull String key) {
        return Filters.eq(KEY_KEY, key);
    }

    // ===

    @Override
    public void delete() throws NoSuchElementException, EntityIOException {
        super.delete();
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.key = source.getAt(KEY_KEY).asString().or(this.key);
        this.name = source.getAt("name").asString().or(this.name);
        this.permission = source.getAt("permission").asNullable(PermissionReference.codec()).or(this.permission);
        this.unique = source.getAt("unique").asBoolean().or(this.unique);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt(KEY_KEY).asString().to(this.key);
        data.setAt("name").asString().to(this.name);
        data.setAt("permission").asNullable(PermissionReference.codec()).to(this.permission);
        data.setAt("unique").asBoolean().to(this.unique);

        return data;
    }
}
