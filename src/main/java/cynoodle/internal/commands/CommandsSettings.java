/*
 * cynoodle, a bot for the chat platform Discord
 *
 * Copyright (C) 2019 enveeed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * All trademarks are the property of their respective owners, including, but not limited to Discord Inc.
 */

package cynoodle.test.commands;

import cynoodle.discord.GEntity;
import cynoodle.entity.EIdentifier;
import cynoodle.entity.EntityType;
import cynoodle.mongodb.BsonDataException;
import cynoodle.mongodb.fluent.FluentArray;
import cynoodle.mongodb.fluent.FluentDocument;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Settings for commands on a guild.
 */
@EIdentifier(CommandsModule.IDENTIFIER + ":settings")
public final class CommandsSettings extends GEntity {
    private CommandsSettings() {}

    static final EntityType<CommandsSettings> TYPE = EntityType.of(CommandsSettings.class);

    // ===

    /**
     * The regex all prefixes must match (1 to 16 characters, no whitespace)
     */
    static final Pattern REGEX_PREFIX = Pattern.compile("^[^\\s]{1,16}$");

    // ===

    /**
     * The prefix for all commands.
     */
    private String prefix = "!";

    /**
     * The "no such command" policy.
     */
    private NSCPolicy nscPolicy = NSCPolicy.IGNORE;

    /**
     * {@link CommandSettings} for each command.
     */
    private Map<String, CommandSettings> commandSettings = new HashMap<>();

    // ===

    @Nonnull
    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(@Nonnull String prefix)
            throws IllegalArgumentException {
        if(!isValidPrefix(prefix)) throw new IllegalArgumentException("Invalid prefix: " + prefix);
        this.prefix = prefix;
    }

    @Nonnull
    public NSCPolicy getNSCPolicy() {
        return this.nscPolicy;
    }

    public void setNSCPolicy(@Nonnull NSCPolicy nscPolicy) {
        this.nscPolicy = nscPolicy;
    }

    //

    @Nonnull
    public CommandSettings getCommandSettings(@Nonnull String commandKey) {
        CommandSettings settings = this.commandSettings.get(commandKey);
        if(settings == null) {
            settings = new CommandSettings(commandKey);
            this.commandSettings.put(commandKey, settings);
            this.persist();
        }
        return settings;
    }

    // ===

    public static boolean isValidPrefix(@Nonnull String prefix) {
        return REGEX_PREFIX.matcher(prefix).matches();
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.prefix     = source.getAt("prefix").asString().or(this.prefix);
        this.nscPolicy  = source.getAt("nsc_policy").as(NSCPolicy.codec()).or(this.nscPolicy);
        this.commandSettings = source.getAt("commands").asArray().or(FluentArray.wrapNew())
                .collect().as(CommandSettings.codec()).toMap(CommandSettings::getCommand);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("prefix").asString().to(this.prefix);
        data.setAt("nsc_policy").as(NSCPolicy.codec()).to(this.nscPolicy);
        data.setAt("commands").asArray().to(FluentArray.wrapNew()
                .insert().as(CommandSettings.codec()).atEnd(this.commandSettings.values()));

        return data;
    }
}
