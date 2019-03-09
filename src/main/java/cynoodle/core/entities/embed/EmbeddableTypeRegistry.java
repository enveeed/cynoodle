/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.entities.embed;

import cynoodle.core.mongo.fluent.FluentDocument;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * A type registry for {@link EmbeddableType EmbedTypes}, this is useful
 * if there should be kept track of multiple different implementations of an Embeddable supertype.
 */
public final class EmbeddableTypeRegistry<E extends Embeddable> {

    /**
     * Stores the Embeddable types.
     */
    private final Map<String, EmbeddableType<? extends E>> types = new HashMap<>();

    // ===

    public EmbeddableTypeRegistry() {}

    // ===

    @Nonnull
    public Optional<EmbeddableType<? extends E>> find(@Nonnull String identifier) {
        return Optional.ofNullable(this.types.get(identifier));
    }

    @Nonnull
    public Optional<EmbeddableType<? extends E>> find(@Nonnull FluentDocument data) {
        String typeIdentifier = data.getAt(Embeddable.KEY_TYPE).asString().value();
        return find(typeIdentifier);
    }

    // ===

    /**
     * Register an Embeddable type.
     * @param type the type to register
     */
    public void register(@Nonnull EmbeddableType<? extends E> type) {

        String identifier = type.getIdentifier();

        // register
        this.types.put(identifier, type);
    }

    // ===

    @Nonnull
    public Function<BsonValue, E> fromBson() {
        return value -> {

            FluentDocument data = FluentDocument.wrap(value.asDocument());

            String identifier = data.getAt(Embeddable.KEY_TYPE).asString().value();

            EmbeddableType<? extends E> type = find(identifier).orElseThrow();

            return type.createAndLoad(data);
        };
    }

    @Nonnull
    public Function<E, BsonValue> toBson() {
        return e -> e.toBson().asBson();
    }
}
