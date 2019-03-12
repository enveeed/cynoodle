/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.mm;

import com.mongodb.client.model.Filters;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.GEntity;
import cynoodle.core.discord.GEntityManager;
import cynoodle.core.entities.EIdentifier;
import cynoodle.core.entities.EIndex;
import cynoodle.core.entities.EntityReference;
import cynoodle.core.module.Module;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentDocument;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Make-me (as in "make me something") are self assignable roles.
 */
@EIdentifier("base:mm:mm")
@EIndex(MakeMe.KEY_KEY)
public final class MakeMe extends GEntity {
    private MakeMe() {}

    private GEntityManager<MakeMeGroup> groupManager
            = Module.get(MakeMeModule.class).getGroupManager();

    static final String KEY_KEY = "key";
    static final String KEY_GROUP = "group";

    // ===

    /**
     * The key of the make-me.
     */
    private String key;

    /**
     * The name of the make-me.
     */
    private String name;

    /**
     * The role for the make-me.
     */
    private DiscordPointer role;

    /**
     * The group this make-me belongs to.
     */
    private EntityReference<MakeMeGroup> group = null;

    // ===

    void create(@Nonnull String key, @Nonnull String name, @Nonnull DiscordPointer role) {
        this.key = key;
        this.name = name;
        this.role = role;
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

    //

    @Nonnull
    public DiscordPointer getRole() {
        return this.role;
    }

    public void setRole(@Nonnull DiscordPointer role) {
        this.role = role;
    }

    @Nonnull
    public Optional<MakeMeGroup> getGroup() {
        return this.group == null ? Optional.empty() : this.group.get();
    }

    public void setGroup(@Nullable MakeMeGroup group) {
        this.group = group == null ? null : group.reference(MakeMeGroup.class);
    }

    // ===

    @Nonnull
    public static Bson filterKey(@Nonnull String key) {
        return Filters.eq(KEY_KEY, key);
    }

    @Nonnull
    public static Bson filterGroup(@Nonnull MakeMeGroup group) {
        return Filters.eq(KEY_GROUP, group.getID());
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.key = source.getAt(KEY_KEY).asString().or(this.key);
        this.name = source.getAt("name").asString().or(this.name);
        this.role = source.getAt("role").as(DiscordPointer.fromBson()).or(this.role);
        this.group = source.getAt(KEY_GROUP).as(EntityReference.load(groupManager)).or(this.group);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt(KEY_KEY).asString().to(this.key);
        data.setAt("name").asString().to(this.name);
        data.setAt("role").as(DiscordPointer.toBson()).to(this.role);
        data.setAt(KEY_GROUP).as(EntityReference.<MakeMeGroup>store()).to(this.group);

        return data;
    }
}
