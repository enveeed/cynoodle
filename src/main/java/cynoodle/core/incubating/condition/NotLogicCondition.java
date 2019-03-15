/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.incubating.condition;


import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.module.Module;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Condition for logic NOT (inversion) of a single other condition.
 */
@ConditionIdentifier("base:condition:logic_not")
public final class NotLogicCondition extends Condition {
    private NotLogicCondition() {}

    private final ConditionRegistry registry = Module.get(ConditionModule.class).getRegistry();

    // ===

    private Condition condition = null;

    // ===

    @Nonnull
    public Optional<Condition> getCondition() {
        return Optional.ofNullable(this.condition);
    }

    public void setCondition(@Nullable Condition condition) {
        this.condition = condition;
    }

    // ===

    @Override
    public boolean test(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user) {
        return !testSafe(this.condition, guild, user);
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument data) throws BsonDataException {
        super.fromBson(data);

        this.condition = data.getAt("condition").as(registry.fromBson()).or(this.condition);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("condition").as(registry.toBson()).to(this.condition);

        return data;
    }
}
