/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.permission;

import cynoodle.core.base.condition.Condition;
import cynoodle.core.base.condition.ConditionModule;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.GEntity;
import cynoodle.core.entities.EIdentifier;
import cynoodle.core.entities.embed.EmbeddableTypeRegistry;
import cynoodle.core.module.Module;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * A permission, includes a condition that must be met and properties
 * such as error and informative messages.
 */
@EIdentifier("base:permission:permission")
public final class Permission extends GEntity {
    private Permission() {}

    private final EmbeddableTypeRegistry<Condition> conditions
            = Module.get(ConditionModule.class).getConditionTypes();

    // ===

    /**
     * The name.
     */
    private String name = "Permission";

    /**
     * The info message.
     */
    private String messageInfo = null;

    /**
     * The error message.
     */
    private String messageError = null;

    //

    /**
     * The condition that must be met.
     */
    private Condition condition = null;

    // ===

    @Nonnull
    public String getName() {
        return this.name;
    }

    public void setName(@Nonnull String name) {
        this.name = name;
    }

    //

    @Nonnull
    public Optional<String> getMessageInfo() {
        return Optional.ofNullable(this.messageInfo);
    }

    public void setMessageInfo(@Nullable String messageInfo) {
        this.messageInfo = messageInfo;
    }

    @Nonnull
    public Optional<String> getMessageError() {
        return Optional.ofNullable(this.messageError);
    }

    public void setMessageError(@Nullable String messageError) {
        this.messageError = messageError;
    }

    //

    @Nonnull
    public Optional<Condition> getCondition() {
        return Optional.ofNullable(this.condition);
    }

    public void setCondition(@Nullable Condition condition) {
        this.condition = condition;
    }

    // ===

    /**
     * Check if this permission is met for the given user.
     * If there is no condition set for this permission, this returns always false.
     * @param user the user
     * @return true if met, false if not or no condition was set
     */
    public boolean check(@Nonnull DiscordPointer user) {

        Optional<Condition> condition = getCondition();

        if(condition.isPresent()) return condition.orElseThrow().check(requireGuild(), user);
        else return false;
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.name = source.getAt("name").asString().or(this.name);
        this.messageInfo = source.getAt("message_info").asStringNullable().or(this.messageInfo);
        this.messageError = source.getAt("message_error").asStringNullable().or(this.messageError);
        this.condition = source.getAt("condition").asNullable(conditions.fromBson()).or(this.condition);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("name").asString().to(this.name);
        data.setAt("message_info").asStringNullable().to(this.messageInfo);
        data.setAt("message_error").asStringNullable().to(this.messageError);
        data.setAt("condition").asNullable(conditions.toBson()).to(this.condition);

        return data;
    }
}
