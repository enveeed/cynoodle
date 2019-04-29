/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.discord;

import cynoodle.entities.IEntity;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nullable;

/**
 * superinterface for public interfaces to {@link MEntity}.
 */
public interface IMEntity extends IEntity, IGEntity, IUEntity {

    /**
     * Set the given member.
     * @param member the member or null
     */
    default void setMember(@Nullable Member member) {
        this.setGuild(member == null ? null : DiscordPointer.to(member.getGuild()));
        this.setUser(member == null ? null : DiscordPointer.to(member.getUser()));
    }

    //

    /**
     * Check if there is a member set.
     * @return true if there is a member, false otherwise.
     */
    default boolean hasMember() {
        return this.hasGuild() && this.hasUser();
    }

}
