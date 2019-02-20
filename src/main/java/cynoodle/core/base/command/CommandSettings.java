/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.command;

import cynoodle.core.api.Checks;
import cynoodle.core.discord.GEntity;
import cynoodle.core.entities.EIdentifier;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;

/**
 * Settings for a guilds commands.
 */
@EIdentifier("base:command:settings")
public final class CommandSettings extends GEntity {
    private CommandSettings() {}

    // ===

    /**
     * The prefix for all commands.
     */
    private String prefix = "!";

    /**
     * No-such-command policy.
     */
    private NSCPolicy nscPolicy = NSCPolicy.IGNORE;

    // ===

    @Nonnull
    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(@Nonnull String prefix) {
        Checks.notBlank(prefix, "prefix");
        this.prefix = prefix;
    }

    @Nonnull
    public NSCPolicy getNSCPolicy() {
        return this.nscPolicy;
    }

    public void setNSCPolicy(@Nonnull NSCPolicy nscPolicy) {
        this.nscPolicy = nscPolicy;
    }

    // ===

    /**
     * Policy for when a command is issued that is not known ("no such command").
     */
    public enum NSCPolicy {

        /**
         * Ignore and don't do anything.
         */
        IGNORE,
        /**
         * Report that the command is not known.
         */
        REPORT,
        /**
         * Report that the command is not known and provide examples
         * for similar commands.
         */
        REPORT_DETAILED
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.prefix = source.getAt("prefix").asString().or(this.prefix);
        this.nscPolicy = source.getAt("nsc_policy").asInteger().map(i -> NSCPolicy.values()[i]).or(this.nscPolicy);

    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("prefix").asString().to(this.prefix);
        data.setAt("nsc_policy").asInteger().map(NSCPolicy::ordinal).to(this.nscPolicy);

        return data;
    }
}
