/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.access;

import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.GHolder;
import cynoodle.core.entities.NestedEntity;
import cynoodle.core.entities.NestedEntityType;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentArray;
import cynoodle.core.mongo.fluent.FluentDocument;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * {@link AccessList} contains cascading allow / neutral / deny based
 * permissions for roles and members.
 */
public final class AccessList extends NestedEntity {
    private AccessList() {}

    static final NestedEntityType<AccessList> TYPE = NestedEntityType.of(AccessList.class);

    private static final NestedEntityType<RoleEntry> NESTED_ROLE_ENTRY = NestedEntityType.of(RoleEntry.class);
    private static final NestedEntityType<UserEntry> NESTED_USER_ENTRY = NestedEntityType.of(UserEntry.class);

    // ===

    /**
     * Role entries.
     */
    private Map<DiscordPointer, RoleEntry> roles = new HashMap<>();
    /**
     * User entries.
     */
    private Map<DiscordPointer, UserEntry> users = new HashMap<>();

    // ===

    @Nonnull
    public Collection<RoleEntry> getRoles() {
        return Collections.unmodifiableCollection(this.roles.values());
    }

    @Nonnull
    public Collection<UserEntry> getUsers() {
        return Collections.unmodifiableCollection(this.users.values());
    }

    //

    @Nonnull
    public Optional<RoleEntry> forRole(@Nonnull DiscordPointer role) {
        return Optional.ofNullable(this.roles.get(role));
    }

    @Nonnull
    public Optional<UserEntry> forUser(@Nonnull DiscordPointer user) {
        return Optional.ofNullable(this.users.get(user));
    }

    @Nonnull
    public RoleEntry forRoleOrCreate(@Nonnull DiscordPointer role) {
        return this.roles.computeIfAbsent(role, pointer ->
                NESTED_ROLE_ENTRY.create(getParent(), entry -> entry.create(role, PermissionFlag.NEUTRAL)));
    }

    @Nonnull
    public UserEntry forUserOrCreate(@Nonnull DiscordPointer user) {
        return this.users.computeIfAbsent(user, pointer ->
                NESTED_USER_ENTRY.create(getParent(), entry -> entry.create(user, PermissionFlag.NEUTRAL)));
    }

    public void removeRole(@Nonnull DiscordPointer role) {
        this.roles.remove(role);
    }

    public void removeUser(@Nonnull DiscordPointer user) {
        this.users.remove(user);
    }

    // ===

    /**
     * Test the given user for any permission flags set for
     * themselves or for their roles and return the effective
     * permission flag ({@link PermissionFlag#NEUTRAL} by default).
     * @param user the user
     * @return the effective permission flag
     */
    @Nonnull
    public PermissionFlag test(@Nonnull DiscordPointer user) {

        // TODO This may needs to be corrected to reflect discords behavior, this isn't really that clear to me right now
        // TODO also test @everyone public role (which has everything ALLOW by default)

        DiscordPointer guild = ((GHolder) getParent()).getGuild()
                .orElseThrow(() -> new IllegalStateException("No Guild!"));

        Member member = user.asMember(guild)
                .orElseThrow(() -> new IllegalStateException("No known Member: " + guild + ":" + user));

        //

        // check user entries
        Optional<UserEntry> userResult = forUser(user);
        if(userResult.isPresent()) {
            UserEntry entry = userResult.get();

            if(entry.getFlag() != PermissionFlag.NEUTRAL) {
                return entry.getFlag();
            }
        }

        // check role entries
        for (Role role : member.getRoles()) {

            Optional<RoleEntry> entryResult = forRole(DiscordPointer.to(role));
            if(entryResult.isEmpty()) continue;

            RoleEntry entry = entryResult.get();

            if(entry.getFlag() != PermissionFlag.NEUTRAL) {
                return entry.getFlag();
            }
        }

        //

        return PermissionFlag.NEUTRAL; // no settings encountered, so stay neutral
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument data) throws BsonDataException {

        this.roles = data.getAt("roles").asArray().or(FluentArray.wrapNew())
                .collect().as(NESTED_ROLE_ENTRY.load(getParent())).toMapOr(RoleEntry::getRole, this.roles);
        this.users = data.getAt("users").asArray().or(FluentArray.wrapNew())
                .collect().as(NESTED_USER_ENTRY.load(getParent())).toMapOr(UserEntry::getUser, this.users);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = FluentDocument.wrapNew();

        data.setAt("roles").asArray().to(FluentArray.wrapNew()
                .insert().as(NESTED_ROLE_ENTRY.store()).atEnd(this.roles.values()));
        data.setAt("users").asArray().to(FluentArray.wrapNew()
                .insert().as(NESTED_USER_ENTRY.store()).atEnd(this.users.values()));

        return data;
    }

    // ===

    /**
     * An access list entry for a role.
     */
    public static final class RoleEntry extends NestedEntity {
        private RoleEntry() {}

        /**
         * The role.
         */
        private DiscordPointer role;
        /**
         * The permission flag.
         */
        private PermissionFlag permission;

        // ===

        private void create(@Nonnull DiscordPointer role, @Nonnull PermissionFlag flag) {
            this.role = role;
            this.permission = flag;
        }

        // ===

        @Nonnull
        public DiscordPointer getRole() {
            return this.role;
        }

        @Nonnull
        public PermissionFlag getFlag() {
            return this.permission;
        }

        public void setFlag(@Nonnull PermissionFlag flag) {
            this.permission = flag;
        }

        // ===

        @Override
        public void fromBson(@Nonnull FluentDocument data) throws BsonDataException {
            this.role = data.getAt("role").as(DiscordPointer.fromBson()).or(this.role);
            this.permission = data.getAt("flag").asInteger().map(i -> PermissionFlag.values()[i]).or(this.permission);
        }

        @Nonnull
        @Override
        public FluentDocument toBson() throws BsonDataException {
            FluentDocument data = FluentDocument.wrapNew();

            data.setAt("role").as(DiscordPointer.toBson()).to(this.role);
            data.setAt("flag").asInteger().map(PermissionFlag::ordinal).to(this.permission);

            return data;
        }
    }

    /**
     * An access list entry for a user.
     */
    public static final class UserEntry extends NestedEntity {
        private UserEntry() {}

        /**
         * The user.
         */
        private DiscordPointer user;
        /**
         * The permission flag.
         */
        private PermissionFlag permission;

        // ===

        private void create(@Nonnull DiscordPointer user, @Nonnull PermissionFlag flag) {
            this.user = user;
            this.permission = flag;
        }

        // ===

        @Nonnull
        public DiscordPointer getUser() {
            return this.user;
        }

        @Nonnull
        public PermissionFlag getFlag() {
            return this.permission;
        }

        public void setFlag(@Nonnull PermissionFlag flag) {
            this.permission = flag;
        }

        // ===

        @Override
        public void fromBson(@Nonnull FluentDocument data) throws BsonDataException {
            this.user = data.getAt("user").as(DiscordPointer.fromBson()).or(this.user);
            this.permission = data.getAt("flag").asInteger().map(i -> PermissionFlag.values()[i]).or(this.permission);
        }

        @Nonnull
        @Override
        public FluentDocument toBson() throws BsonDataException {
            FluentDocument data = FluentDocument.wrapNew();

            data.setAt("user").as(DiscordPointer.toBson()).to(this.user);
            data.setAt("flag").asInteger().map(PermissionFlag::ordinal).to(this.permission);

            return data;
        }
    }

    //

    /**
     * Permission flag for a single entry.
     */
    public enum PermissionFlag {

        /**
         * Neutral: Do not affect the permissions set by other roles / users. (0)
         */
        NEUTRAL,
        /**
         * Deny: Deny this permission. (1)
         */
        DENY,
        /**
         * Allow: Allow this permission. (2)
         */
        ALLOW,

    }
}
