/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api.measurements;

public abstract class BaseUnit<U extends BaseUnit<U>> implements Unit<U> {

    private final String identifier;
    private final String symbol;
    private final String name;

    // ===

    BaseUnit(String identifier, String symbol, String name) {
        this.identifier = identifier;
        this.symbol = symbol;
        this.name = name;
    }

    // ===

    @Override
    public final String identifier() {
        return this.identifier;
    }

    @Override
    public final String symbol() {
        return this.symbol;
    }

    @Override
    public final String name() {
        return this.name;
    }
}
