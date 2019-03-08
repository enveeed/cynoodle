/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.condition;

import cynoodle.core.entities.embed.EmbedTypeRegistry;
import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

/**
 * <code>base:condition</code>
 */
@MIdentifier("base:condition")
public final class ConditionModule extends Module {
    private ConditionModule() {}

    // ===

    private EmbedTypeRegistry<Condition> conditionTypeRegistry = new EmbedTypeRegistry<>();

    // ===

    @Override
    protected void start() {
        super.start();
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    // ===

    @Nonnull
    public EmbedTypeRegistry<Condition> getConditionTypes() {
        return this.conditionTypeRegistry;
    }
}
