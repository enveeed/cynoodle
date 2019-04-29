/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.base.xp;

import com.google.common.collect.Range;
import com.google.common.primitives.Longs;
import com.mongodb.client.model.Filters;
import cynoodle.api.Checks;
import cynoodle.discord.DiscordPointer;
import cynoodle.discord.GEntity;
import cynoodle.entities.EIdentifier;
import cynoodle.entities.EIndex;
import cynoodle.mongo.BsonDataException;
import cynoodle.mongo.IBson;
import cynoodle.mongo.fluent.FluentArray;
import cynoodle.mongo.fluent.FluentDocument;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;

import static cynoodle.base.xp.XPChecks.validLevel;
import static cynoodle.base.xp.XPChecks.validRankName;

/**
 * A rank. Defines a set of roles with properties to apply to a member
 * when the member has a specific level. Ranks are always effective on the set level and all levels above the set
 * level until there is another Rank defined.
 */
@EIdentifier("base:xp:rank")
@EIndex("level")
public final class Rank extends GEntity implements Comparable<Rank> {
    private Rank() {}

    /**
     * The (display) name of the Rank.
     */
    private String name = "Rank";

    /**
     * The level at which the Rank is set (this is unique)
     */
    private int level;

    /**
     * The roles this Rank contains.
     */
    private Set<Role> roles = new HashSet<>();

    // ===

    @Nonnull
    public String getName() {
        return this.name;
    }

    public void setName(@Nonnull String name) {
        validRankName(name);
        this.name = name;
    }

    public int getLevel() {
        return this.level;
    }

    //

    // package-private to ensure level uniqueness
    void setLevel(int level) {
        validLevel(level);
        this.level = level;
    }

    //

    public long getRequiredXP() {
        return XPModule.get()
                .getFormula()
                .getRequiredXP(this.level);
    }

    // ===

    @Nonnull
    public Set<Role> getRoles() {
        return this.roles;
    }

    //

    public void addRoles(@Nonnull Role... roles) {
        getRoles().addAll(Arrays.asList(roles));
    }

    public void removeRoles(@Nonnull Role... roles) {
        getRoles().removeAll(Arrays.asList(roles));
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
        if(next.isPresent()) return Range.closedOpen(this.level, next.orElseThrow().getLevel());
        else return Range.atLeast(this.level);
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

    // ===

    @Nonnull
    public static Role createRole(@Nonnull DiscordPointer role) {
        Role r = new Role();
        r.setRole(role);
        return r;
    }

    // ===

    @Nonnull
    static Bson filterLevel(int level) {
        // match level exactly
        return Filters.eq("level", level);
    }

    @Nonnull
    static Bson filterLevelPrevious(int level) {
        // match all previous levels, not this one
        return Filters.lt("level", level);
    }

    @Nonnull
    static Bson filterLevelAndPrevious(int level) {
        // match all previous levels and this one
        return Filters.lte("level", level);
    }

    @Nonnull
    private static Bson filterLevelNext(int level) {
        // match all next levels, not this one
        return Filters.gt("level", level);
    }

    @Nonnull
    static Bson filterLevelAndNext(int level) {
        // match all previous levels and this one
        return Filters.gte("level", level);
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.name = source.getAt("name").asString().or(this.name);
        this.level = source.getAt("level").asInteger().or(this.level);
        this.roles = source.getAt("roles").asArray().or(FluentArray.wrapNew())
                .collect().asDocument().map(toRankRole()).toSetOr(this.roles);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("name").asString().to(this.name);
        data.setAt("level").asInteger().to(this.level);
        data.setAt("roles").asArray().to(FluentArray.wrapNew()
                .insert().asDocument().map(fromRankRole()).atEnd(this.roles));

        return data;
    }

    //

    @Override
    public int compareTo(@Nonnull Rank o) {
        return Longs.compare(this.getLevel(), o.getLevel());
    }

    // ===

    public static final class Role implements IBson {

        /**
         * The role.
         */
        private DiscordPointer role = null;

        /**
         * If this role should be kept over the effective range of the Rank.
         */
        private boolean keep = false;

        /**
         * If this role should be hidden for displays.
         */
        private boolean hidden = false;

        // ===

        private Role() {}

        // ===

        @Nonnull
        public DiscordPointer getRole() {
            return this.role;
        }

        public void setRole(@Nonnull DiscordPointer role) {
            this.role = role;
        }

        //

        public boolean isKeepEnabled() {
            return keep;
        }

        public void setKeepEnabled(boolean keep) {
            this.keep = keep;
        }

        public boolean isHidden() {
            return this.hidden;
        }

        public void setHidden(boolean hidden) {
            this.hidden = hidden;
        }

        // ===

        @Override
        public void fromBson(@Nonnull FluentDocument data) throws BsonDataException {

            this.role = data.getAt("role").as(DiscordPointer.fromBson()).or(this.role);
            this.keep = data.getAt("keep").asBoolean().or(this.keep);
            this.hidden = data.getAt("hidden").asBoolean().or(this.hidden);
        }

        @Nonnull
        @Override
        public FluentDocument toBson() throws BsonDataException {

            Checks.notNull(this.role, "role");

            FluentDocument data = FluentDocument.wrapNew();

            data.setAt("role").as(DiscordPointer.toBson()).to(this.role);
            data.setAt("keep").asBoolean().to(this.keep);
            data.setAt("hidden").asBoolean().to(this.hidden);

            return data;
        }
    }

    // ===

    @Nonnull
    private static Function<Role, FluentDocument> fromRankRole() {
        return Role::toBson;
    }

    @Nonnull
    private static Function<FluentDocument, Role> toRankRole() {
        return d -> {
            Role r = new Role();
            r.fromBson(d);
            return r;
        };
    }
}
