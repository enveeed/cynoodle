/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.incubating.condition;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import cynoodle.core.mongo.fluent.FluentDocument;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public final class ConditionRegistry {
    ConditionRegistry() {}

    // ===

    private final Map<String, ConditionType<? extends Condition>> conditions = new HashMap<>();

    // ===

    @Nonnull
    public Set<ConditionType<? extends Condition>> all() {
        return Set.copyOf(this.conditions.values());
    }

    // ===

    @Nonnull
    public Optional<ConditionType<? extends Condition>> get(@Nonnull String identifier) {
        return Optional.ofNullable(this.conditions.get(identifier));
    }

    // ===

    @CanIgnoreReturnValue
    @Nonnull
    public final <C extends Condition> ConditionType<C> register(@Nonnull Class<C> conditionClass) throws ConditionClassException {

        // parse the class into a type
        ConditionType<C> type = ConditionType.of(conditionClass);

        // register the type
        this.conditions.put(type.getIdentifier(), type);

        // ===

        return type;
    }

    // ===

    @Nonnull
    public Function<BsonValue, Condition> fromBson() {
        return value -> {

            FluentDocument data = FluentDocument.wrap(value.asDocument());

            String identifier = data.getAt(Condition.KEY_TYPE).asString().value();

            ConditionType<? extends Condition> type = get(identifier).orElseThrow();

            return type.createFrom(data);
        };
    }

    @Nonnull
    public Function<Condition, BsonValue> toBson() {
        return e -> e.toBson().asBson();
    }
}
