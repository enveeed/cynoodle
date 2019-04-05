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
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import javax.annotation.Nonnull;
import java.util.*;

final class EAccessList extends NestedEntity implements AccessList {
    private EAccessList() {}

    static final NestedEntityType<EAccessList> TYPE = NestedEntityType.of(EAccessList.class);

    private static final NestedEntityType<ERoleEntry> NESTED_ROLE_ENTRY = NestedEntityType.of(ERoleEntry.class);
    private static final NestedEntityType<EUserEntry> NESTED_USER_ENTRY = NestedEntityType.of(EUserEntry.class);

    // ===

    /**
     * Role entries.
     */
    private Map<DiscordPointer, ERoleEntry> roles = new HashMap<>();
    /**
     * User entries.
     */
    private Map<DiscordPointer, EUserEntry> users = new HashMap<>();

    /**
     * The default status at the bottom of the list.
     */
    private Status defaultStatus = Status.NEUTRAL;

    // ===

    @Override
    @Nonnull
    public Collection<RoleEntry> getRoles() {
        return Collections.unmodifiableCollection(this.roles.values());
    }

    @Override
    @Nonnull
    public Collection<UserEntry> getUsers() {
        return Collections.unmodifiableCollection(this.users.values());
    }

    //

    @Override
    @Nonnull
    public Optional<RoleEntry> forRole(@Nonnull DiscordPointer role) {
        return Optional.ofNullable(this.roles.get(role));
    }

    @Override
    @Nonnull
    public Optional<UserEntry> forUser(@Nonnull DiscordPointer user) {
        return Optional.ofNullable(this.users.get(user));
    }

    @Override
    @Nonnull
    public ERoleEntry forRoleOrCreate(@Nonnull DiscordPointer role) {
        return this.roles.computeIfAbsent(role, pointer ->
                NESTED_ROLE_ENTRY.create(getParent(), entry -> entry.create(role, Status.NEUTRAL)));
    }

    @Override
    @Nonnull
    public EUserEntry forUserOrCreate(@Nonnull DiscordPointer user) {
        return this.users.computeIfAbsent(user, pointer ->
                NESTED_USER_ENTRY.create(getParent(), entry -> entry.create(user, Status.NEUTRAL)));
    }

    @Override
    public void removeRole(@Nonnull DiscordPointer role) {
        this.roles.remove(role);
    }

    @Override
    public void removeUser(@Nonnull DiscordPointer user) {
        this.users.remove(user);
    }

    //

    @Nonnull
    public Status getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(@Nonnull Status defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

    // ===

    @Override
    @Nonnull
    public Status check(@Nonnull DiscordPointer user) {

        DiscordPointer guildP = ((GHolder) getParent()).getGuild()
                .orElseThrow(() -> new IllegalStateException("No Guild!"));

        Member member = user.asMember(guildP)
                .orElseThrow(() -> new IllegalStateException("No known Member: " + guildP + ":" + user));
        Guild guild = member.getGuild();

        // ===

        // check for explicit user entry
        Optional<UserEntry> userEntry = forUser(user);
        if(userEntry.isPresent()) {
            Status status = userEntry.get().getStatus();
            if(status == Status.ALLOW) return Status.ALLOW;
            else if(status == Status.DENY) return Status.DENY;
        }

        // check for role entries
        for (Role role : member.getRoles()) {
            Optional<RoleEntry> roleEntry = forRole(DiscordPointer.to(role));
            if(roleEntry.isEmpty()) continue;
            Status status = roleEntry.get().getStatus();
            if(status == Status.ALLOW) return Status.ALLOW;
            else if(status == Status.DENY) return Status.DENY;
        }

        // check for @everyone
        Optional<RoleEntry> publicRoleEntry = forRole(DiscordPointer.to(guild.getPublicRole()));
        if(publicRoleEntry.isPresent()) {
            Status status = publicRoleEntry.get().getStatus();
            if(status == Status.ALLOW) return Status.ALLOW;
            else if(status == Status.DENY) return Status.DENY;
        }

        // return default
        return this.defaultStatus;
    }

    @Override
    public boolean checkAccess(@Nonnull DiscordPointer user) {
        Status status = this.check(user);
        return status == Status.ALLOW || status == Status.NEUTRAL;
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument data) throws BsonDataException {

        this.roles = data.getAt("roles").asArray().or(FluentArray.wrapNew())
                .collect().as(NESTED_ROLE_ENTRY.load(getParent())).toMapOr(ERoleEntry::getRole, this.roles);
        this.users = data.getAt("users").asArray().or(FluentArray.wrapNew())
                .collect().as(NESTED_USER_ENTRY.load(getParent())).toMapOr(EUserEntry::getUser, this.users);
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
    public static final class ERoleEntry extends NestedEntity implements AccessList.RoleEntry {
        private ERoleEntry() {}

        /**
         * The role.
         */
        private DiscordPointer role;
        /**
         * The permission flag.
         */
        private Status permission;

        // ===

        private void create(@Nonnull DiscordPointer role, @Nonnull Status flag) {
            this.role = role;
            this.permission = flag;
        }

        // ===

        @Nonnull
        public DiscordPointer getRole() {
            return this.role;
        }

        @Nonnull
        public Status getStatus() {
            return this.permission;
        }

        public void setStatus(@Nonnull Status flag) {
            this.permission = flag;
        }

        // ===

        @Override
        public void fromBson(@Nonnull FluentDocument data) throws BsonDataException {
            this.role = data.getAt("role").as(DiscordPointer.fromBson()).or(this.role);
            this.permission = data.getAt("status").asInteger().map(i -> Status.values()[i]).or(this.permission);
        }

        @Nonnull
        @Override
        public FluentDocument toBson() throws BsonDataException {
            FluentDocument data = FluentDocument.wrapNew();

            data.setAt("role").as(DiscordPointer.toBson()).to(this.role);
            data.setAt("status").asInteger().map(Status::ordinal).to(this.permission);

            return data;
        }
    }

    /**
     * An access list entry for a user.
     */
    public static final class EUserEntry extends NestedEntity implements AccessList.UserEntry {
        private EUserEntry() {}

        /**
         * The user.
         */
        private DiscordPointer user;
        /**
         * The permission flag.
         */
        private Status permission;

        // ===

        private void create(@Nonnull DiscordPointer user, @Nonnull Status flag) {
            this.user = user;
            this.permission = flag;
        }

        // ===

        @Nonnull
        public DiscordPointer getUser() {
            return this.user;
        }

        @Nonnull
        public Status getStatus() {
            return this.permission;
        }

        public void setStatus(@Nonnull Status flag) {
            this.permission = flag;
        }

        // ===

        @Override
        public void fromBson(@Nonnull FluentDocument data) throws BsonDataException {
            this.user = data.getAt("user").as(DiscordPointer.fromBson()).or(this.user);
            this.permission = data.getAt("status").asInteger().map(i -> Status.values()[i]).or(this.permission);
        }

        @Nonnull
        @Override
        public FluentDocument toBson() throws BsonDataException {
            FluentDocument data = FluentDocument.wrapNew();

            data.setAt("user").as(DiscordPointer.toBson()).to(this.user);
            data.setAt("status").asInteger().map(Status::ordinal).to(this.permission);

            return data;
        }
    }

}
