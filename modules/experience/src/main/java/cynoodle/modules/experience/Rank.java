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

import com.google.common.collect.Range;
import com.google.common.primitives.Longs;
import com.mongodb.client.model.Filters;
import cynoodle.discord.GEntity;
import cynoodle.discord.RReference;
import cynoodle.entity.EIdentifier;
import cynoodle.entity.EIndex;
import cynoodle.entity.EntityType;
import cynoodle.mongodb.BsonDataException;
import cynoodle.mongodb.fluent.FluentDocument;
import cynoodle.mongodb.fluent.MoreCodecs;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * A rank is a named level which upon being reached can assign the member an optional set of roles.
 */
@EIdentifier(ExperienceModule.IDENTIFIER + ":rank")
@EIndex(Rank.KEY_LEVEL)
public final class Rank extends GEntity implements Comparable<Rank> {
    private Rank() {}

    static final EntityType<Rank> TYPE = EntityType.of(Rank.class);

    static final String KEY_LEVEL = "level";

    // ===

    /**
     * The level at which the Rank is set.
     */
    private int level;

    /**
     * The name of the Rank.
     */
    private String name;

    /**
     * All {@link RankRole RankRoles} this rank contains.
     */
    private Map<RReference, RankRole> roles = new HashMap<>();

    // ===

    void create(int level, @Nonnull String name) {
        // no level uniqueness check here cause that's done in Experience
        ExperienceChecks.validLevel(level);
        this.level = level;
        ExperienceChecks.validName(name);
        this.name = name;
    }

    // ===

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        ExperienceChecks.validLevel(level);

        Experience experience = Experience.get();
        if(experience
                .findRank(requireGuild().requireGuild(), level)
                .isPresent())
            throw new IllegalArgumentException("There is already a Rank at this level: " + level);


        this.level = level;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    public void setName(@Nonnull String name) {
        ExperienceChecks.validName(name);
        this.name = name;
    }

    // ===

    @Nonnull
    public Set<RankRole> getRoles() {
        return Set.copyOf(this.roles.values());
    }

    @Nonnull
    public Optional<RankRole> getRole(@Nonnull RReference role) {
        return Optional.ofNullable(this.roles.get(role));
    }

    //

    @Nonnull
    public RankRole addRole(@Nonnull RReference role) {
        if(this.roles.containsKey(role))
            throw new IllegalArgumentException("Rank " + getIDStringBase62() + " already contains this role: " + role.getID());

        RankRole rankRole = new RankRole(role);
        this.roles.put(role, rankRole);
        this.persist();

        return rankRole;
    }

    public void removeRole(@Nonnull RReference role) {
        this.roles.remove(role);
        this.persist();
    }

    // ===

    /**
     * Get the previous Rank, that is the next lower Rank level-wise.
     * If there is none, then this is the first Rank.
     * @return previous Rank optional
     */
    @Nonnull
    public Optional<Rank> getPrevious() {
        return getManager(Rank.class)
                .stream(filterLevelPrevious(this.level))
                .sorted(Comparator.reverseOrder())
                .limit(1)
                .findFirst();
    }

    /**
     * Get the next Rank, that is the next higher Rank level-wise.
     * If there is none, then this is the last Rank.
     * @return next Rank optional
     */
    @Nonnull
    public Optional<Rank> getNext() {
        return getManager(Rank.class)
                .stream(filterLevelNext(this.level))
                .sorted()
                .limit(1)
                .findFirst();
    }

    //

    /**
     * Get the range of levels at which this Rank is effective.
     * @return the effective level range of this Rank
     */
    @Nonnull
    public Range<Integer> getEffectiveRange() {
        Optional<Rank> next = getNext();
        if(next.isPresent())
            return Range.closedOpen(this.level, next.orElseThrow().getLevel());
        else
            return Range.atLeast(this.level);
    }

    /**
     * Check if this Rank is effective for the given level.
     * @param level the level to test
     * @return true if effective, false if not
     * @see #getEffectiveRange()
     */
    public boolean isEffectiveFor(int level) {
        return getEffectiveRange().contains(level);
    }

    //

    public boolean isLowest() {
        return getPrevious().isEmpty();
    }

    public boolean isHighest() {
        return getNext().isEmpty();
    }

    // ===

    @Nonnull
    static Bson filterLevel(int level) {
        // match level exactly
        return Filters.eq(KEY_LEVEL, level);
    }

    @Nonnull
    static Bson filterLevelPrevious(int level) {
        // match all previous levels, not this one
        return Filters.lt(KEY_LEVEL, level);
    }

    @Nonnull
    static Bson filterLevelAndPrevious(int level) {
        // match all previous levels and this one
        return Filters.lte(KEY_LEVEL, level);
    }

    @Nonnull
    static Bson filterLevelNext(int level) {
        // match all next levels, not this one
        return Filters.gt(KEY_LEVEL, level);
    }

    @Nonnull
    static Bson filterLevelAndNext(int level) {
        // match all previous levels and this one
        return Filters.gte(KEY_LEVEL, level);
    }

    // ===

    @Override
    public int compareTo(@Nonnull Rank o) {
        return Longs.compare(this.getLevel(), o.getLevel());
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.level = source.getAt(KEY_LEVEL).asInteger().or(this.level);
        this.name = source.getAt("name").asString().or(this.name);
        this.roles = source.getAt("roles").as(MoreCodecs.forValueMap(RankRole.codec(), RankRole::getRole)).or(this.roles);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt(KEY_LEVEL).asInteger().to(this.level);
        data.setAt("name").asString().to(this.name);
        data.setAt("roles").as(MoreCodecs.forValueMap(RankRole.codec(), RankRole::getRole)).to(this.roles);

        return data;
    }
}
