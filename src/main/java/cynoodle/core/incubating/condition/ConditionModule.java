/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.incubating.condition;

import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

/**
 * <code>base:condition</code>
 */
@MIdentifier("base:condition")
public final class ConditionModule extends Module {
    private ConditionModule() {}

    private final ConditionRegistry registry = new ConditionRegistry();

    // ===

    @Override
    protected void start() {
        super.start();

        //

        this.registry.register(BiLogicCondition.class);
        this.registry.register(SetLogicCondition.class);
        this.registry.register(NotLogicCondition.class);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    // ===

    @Nonnull
    public ConditionRegistry getRegistry() {
        return this.registry;
    }
}