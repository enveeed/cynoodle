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
 * Condition for two-input logic operations.
 */
@ConditionIdentifier("base:condition:logic_bi")
public final class BiLogicCondition extends Condition {
    private BiLogicCondition() {}

    private final ConditionRegistry registry = Module.get(ConditionModule.class).getRegistry();

    // ===

    private Condition conditionA = null;
    private Condition conditionB = null;

    private Operator operator = Operator.AND;

    // ===

    @Nonnull
    public Optional<Condition> getConditionA() {
        return Optional.ofNullable(this.conditionA);
    }

    public void setConditionA(@Nullable Condition conditionA) {
        this.conditionA = conditionA;
    }

    @Nonnull
    public Optional<Condition> getConditionB() {
        return Optional.ofNullable(this.conditionB);
    }

    public void setConditionB(@Nullable Condition conditionB) {
        this.conditionB = conditionB;
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
            case AND:
                return testSafe(conditionA, guild, user) && testSafe(conditionB, guild, user);
            case OR:
                return testSafe(conditionA, guild, user) || testSafe(conditionB, guild, user);
            case XOR:
                return testSafe(conditionA, guild, user) ^ testSafe(conditionB, guild, user);
            case NAND:
                return !testSafe(conditionA, guild, user) || !testSafe(conditionB, guild, user);
            case NOR:
                return !(testSafe(conditionA, guild, user) || testSafe(conditionB, guild, user));
            case XNOR:
                return testSafe(conditionA, guild, user) == testSafe(conditionB, guild, user);
            default:
                throw new IllegalStateException("Invalid Operator: " + operator);
        }
    }

    //

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument data) throws BsonDataException {
        super.fromBson(data);

        this.conditionA = data.getAt("condition_a").as(registry.fromBson()).or(this.conditionA);
        this.conditionB = data.getAt("condition_b").as(registry.fromBson()).or(this.conditionB);
        this.operator = data.getAt("operator").asInteger().map(i -> Operator.values()[i]).or(this.operator);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("condition_a").as(registry.toBson()).to(this.conditionA);
        data.setAt("condition_b").as(registry.toBson()).to(this.conditionB);
        data.setAt("operator").asInteger().map(Operator::ordinal).to(this.operator);

        return data;
    }

    // ===

    public enum Operator {

        // 0
        AND,

        // 1
        OR,

        // 2
        XOR,

        // 3
        NAND,

        // 4
        NOR,

        // 5
        XNOR,

    }
}
