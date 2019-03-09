/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.condition;

import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.module.Module;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentArray;
import cynoodle.core.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

/**
 * Condition for logic AND.
 */
@ConditionIdentifier("base:condition:and")
public final class AndCondition extends Condition {
    private AndCondition() {}

    private final ConditionRegistry registry = Module.get(ConditionModule.class).getRegistry();

    // ===

    private Set<Condition> conditions = new HashSet<>();

    // ===

    @Nonnull
    public Set<Condition> getConditions() {
        return this.conditions;
    }

    public void setConditions(@Nonnull Set<Condition> conditions) {
        this.conditions = conditions;
    }

    // ===

    @Override
    public boolean test(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user) {
        return conditions.stream().allMatch(condition -> condition.test(guild, user));
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument data) throws BsonDataException {
        super.fromBson(data);

        this.conditions = data.getAt("conditions").asArray().or(FluentArray.wrapNew())
                .collect().as(registry.fromBson()).toSet();
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("conditions").asArray().to(FluentArray.wrapNew()
                .insert().as(registry.toBson()).atEnd(this.conditions));

        return data;
    }
}
