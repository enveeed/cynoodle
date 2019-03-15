/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import cynoodle.core.incubating.condition.Condition;
import cynoodle.core.incubating.condition.ConditionIdentifier;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.module.Module;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * A condition which requires a minimum amount of XP.
 */
@ConditionIdentifier("base:xp:xp")
public final class XPCondition extends Condition {
    private XPCondition() {}

    private final XPModule module = Module.get(XPModule.class);

    // ===

    /**
     * The amount of XP required at a minimum to meet this condition.
     */
    private long xp = 100;

    // ===

    public long getXP() {
        return this.xp;
    }

    public void setXP(long xp) {
        this.xp = xp;
    }

    // ===

    @Override
    public boolean test(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user) {

        Optional<XP> xpResult = module.getXPManager().first(XP.filterMember(guild, user));

        if(xpResult.isEmpty()) return false;
        else return xpResult.orElseThrow().get() >= this.xp;
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument data) throws BsonDataException {
        super.fromBson(data);

        this.xp = data.getAt("xp").asLong().or(this.xp);

    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("xp").asLong().to(this.xp);

        return data;
    }
}
