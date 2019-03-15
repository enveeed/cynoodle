/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.incubating.condition;

import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.Bsonable;
import cynoodle.core.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Conditions are functional objects, for the purpose of checking conditions on guild members,
 * which can be embedded into {@link cynoodle.core.entities.Entity Entities}.
 * Condition instances must be created via {@link ConditionType}.
 */
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

    /**
     * Utility method to safely test a condition, even if its null.
     * If the given condition is null, false is always returned.
     * @param condition the condition, may be null
     * @param guild the guild
     * @param user the user
     * @return the condition test result or false if the condition was null
     */
    static boolean testSafe(@Nullable Condition condition,
                            @Nonnull DiscordPointer guild,
                            @Nonnull DiscordPointer user) {
        return condition != null && condition.test(guild, user);
    }

    // ===


    @Override
    public void fromBson(@Nonnull FluentDocument data) throws BsonDataException {
        String identifier = data.getAt(KEY_TYPE).asString().value();

        if(!identifier.equals(this.getTypeIdentifier()))
            throw new BsonDataException("Identifier mismatch: Expected "
                    + this.getTypeIdentifier() + " but got " + identifier + "!");
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = FluentDocument.wrapNew();

        // store the type identifier so it is known what type it is on disk
        data.setAt(KEY_TYPE).asString().to(this.type.getIdentifier());

        return data;
    }

}
