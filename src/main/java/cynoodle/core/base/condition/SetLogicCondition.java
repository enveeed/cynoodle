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
 * Condition for set input logic operations.
 */
@ConditionIdentifier("base:condition:logic_set")
public final class SetLogicCondition extends Condition {
    private SetLogicCondition() {}

    private final ConditionRegistry registry = Module.get(ConditionModule.class).getRegistry();

    // ===

    private Set<Condition> conditions = new HashSet<>();

    private Operator operator = Operator.ALL;

    // ===

    @Nonnull
    public Set<Condition> getConditions() {
        return this.conditions;
    }

    public void setConditions(@Nonnull Set<Condition> conditions) {
        this.conditions = conditions;
    }

    //

    @Nonnull
    public Operator getOperator() {
        return this.operator;
    }

    public void setOperator(@Nonnull Operator operator) {
        this.operator = operator;
    }

    // ===

    @Override
    public boolean test(@Nonnull DiscordPointer guild, @Nonnull DiscordPointer user) {
        switch (operator) {
            case ALL:
                return this.conditions.stream().allMatch(c -> testSafe(c, guild, user));
            case NONE:
                return this.conditions.stream().noneMatch(c -> testSafe(c, guild, user));
            case ANY:
                return this.conditions.stream().anyMatch(c -> testSafe(c, guild, user));
            default:
                throw new IllegalStateException("Invalid Operator: " + operator);
        }
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument data) throws BsonDataException {
        super.fromBson(data);

        this.conditions = data.getAt("conditions").asArray().or(FluentArray.wrapNew())
                .collect().as(registry.fromBson()).toSet();
        this.operator = data.getAt("operator").asInteger().map(i -> Operator.values()[i]).or(this.operator);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("conditions").asArray().to(FluentArray.wrapNew()
                .insert().as(registry.toBson()).atEnd(this.conditions));
        data.setAt("operator").asInteger().map(Operator::ordinal).to(this.operator);

        return data;
    }

    // ===

    public enum Operator {

        // 0
        ALL,

        // 1
        NONE,

        // 2
        ANY,

    }
}
