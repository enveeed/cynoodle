/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.condition;

import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.Bsonable;
import cynoodle.core.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;

public abstract class Condition implements Bsonable {
    protected Condition() {}

    static final String KEY_TYPE = "type";

    // ===

    private ConditionType type;

    // ===

    final void init(@Nonnull ConditionType type) {
        this.type = type;
    }

    // ===

    @Nonnull
    public ConditionType getType() {
        return this.type;
    }

    @Nonnull
    public String getTypeIdentifier() {
        return this.type.getIdentifier();
    }

    // ===

    /**
     * Test if the given user meets this condition.
     * @param guild the guild
     * @param user the user
     * @return true if the condition is met, otherwise false
     */
    public abstract boolean test(@Nonnull DiscordPointer guild,
                                 @Nonnull DiscordPointer user);

    // ===

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = FluentDocument.wrapNew();

        // store the type identifier so it is known what type it is on disk
        data.setAt(KEY_TYPE).asString().to(this.type.getIdentifier());

        return data;
    }

}
