/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.access;

import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.GEntity;
import cynoodle.core.entities.EIdentifier;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentArray;
import cynoodle.core.mongo.fluent.FluentDocument;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.impl.map.mutable.primitive.LongObjectHashMap;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Access control settings for a Guild.
 */
@EIdentifier("base:ac:ac")
public final class AccessControl extends GEntity {
    private AccessControl() {}

    //

    private MutableLongObjectMap<AccessRole> roles = new LongObjectHashMap<AccessRole>().asSynchronized();

    // ===

    /**
     * Get the {@link AccessRole} properties for the given Role.
     * @param role the role
     * @return the AC properties for the role
     */
    @Nonnull
    public AccessRole getOrCreate(@Nonnull DiscordPointer role) {

        AccessRole acr = this.roles.get(role.getID());

        if(acr == null) {
            acr = new AccessRole(role);
            this.roles.put(role.getID(), acr);
            this.persist();
        }

        return acr;
    }

    // ===

    public boolean test(@Nonnull DiscordPointer userP, @Nonnull String permission) {

        DiscordPointer guildP = requireGuild();

        //

        Guild guild = guildP.asGuild().orElseThrow();
        User user = userP.asUser().orElseThrow();

        //

        Member member = guild.getMember(user);
        if(member == null) throw new IllegalArgumentException(); // TODO

        // test public role

        if(getOrCreate(DiscordPointer.to(guild.getPublicRole())).test(permission))
            return true;

        // test each role

        Set<DiscordPointer> roles = member.getRoles().stream()
                .map(DiscordPointer::to)
                .collect(Collectors.toSet());

        for (DiscordPointer role : roles) {
            if(getOrCreate(role).test(permission)) return true;
        }


        return false;
    }

    public boolean test(@Nonnull User user, @Nonnull String permission) {
        return test(DiscordPointer.to(user), permission);
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        Set<AccessRole> roles = source.getAt("roles").asArray().or(FluentArray.wrapNew())
                .collect().as(AccessRole.load()).toSet();

        // TODO replace with safer implementation

        LongObjectHashMap<AccessRole> map = new LongObjectHashMap<>();
        for (AccessRole role : roles) {
            map.put(role.getRole().getID(), role);
        }

        this.roles = map;
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        // TODO replace with safer implementation

        data.setAt("roles").asArray().to(FluentArray.wrapNew()
                .insert().as(AccessRole.store()).atEnd(this.roles.values()));

        return data;
    }
}
