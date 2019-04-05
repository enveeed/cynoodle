/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.makeme;

import com.mongodb.client.model.Filters;
import cynoodle.core.base.access.AccessList;
import cynoodle.core.base.access.AccessLists;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.GEntity;
import cynoodle.core.discord.GEntityManager;
import cynoodle.core.entities.EIdentifier;
import cynoodle.core.entities.EIndex;
import cynoodle.core.entities.EntityIOException;
import cynoodle.core.entities.EntityReference;
import cynoodle.core.module.Module;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentDocument;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Make-me (as in "make me something") are self assignable roles.
 */
@EIdentifier("base:makeme:makeme")
@EIndex(MakeMe.KEY_KEY)
@EIndex(MakeMe.KEY_GROUP)
public final class MakeMe extends GEntity {
    private MakeMe() {}

    static final String KEY_KEY = "key";
    static final String KEY_GROUP = "group";

    // ===

    private MakeMeModule module = Module.get(MakeMeModule.class);

    private GEntityManager<MakeMeGroup> groupManager = module.getGroupManager();

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
     * The access list for this make-me (default status is ALLOW)
     */
    private AccessList access = AccessLists.create(this, AccessList.Status.ALLOW);

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

    //

    @Nonnull
    public AccessList getAccess() {
        return this.access;
    }

    //

    /**
     * Check if the given user can access this make-me.
     * @param user the user
     * @return true if accessible, false otherwise.
     */
    public boolean canAccess(@Nonnull DiscordPointer user) {

        Optional<MakeMeGroup> groupO = getGroup();
        if(groupO.isPresent()) {
            MakeMeGroup group = groupO.get();
            if(!group.getAccess().checkAccess(user)) return false;
        }

        return this.getAccess().checkAccess(user);
    }

    // ===

    @Nonnull
    public static Bson filterKey(@Nonnull String key) {
        return Filters.eq(KEY_KEY, key);
    }

    @Nonnull
    public static Bson filterGroup(@Nullable MakeMeGroup group) {
        return Filters.eq(KEY_GROUP, group == null ? null : group.getID());
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
        this.role = source.getAt("role").as(DiscordPointer.fromBson()).or(this.role);
        this.access = source.getAt("access").as(AccessLists.load(this)).or(this.access);
        this.group = source.getAt(KEY_GROUP).asNullable(EntityReference.load(groupManager)).or(this.group);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt(KEY_KEY).asString().to(this.key);
        data.setAt("name").asString().to(this.name);
        data.setAt("role").as(DiscordPointer.toBson()).to(this.role);
        data.setAt("access").as(AccessLists.store()).to(this.access);
        data.setAt(KEY_GROUP).asNullable(EntityReference.<MakeMeGroup>store()).to(this.group);

        return data;
    }
}
