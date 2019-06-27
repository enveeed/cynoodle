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

import cynoodle.entity.IEntity;
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
        this.setGuild(member == null ? null : GReference.of(member));
        this.setUser(member == null ? null : UReference.of(member));
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
