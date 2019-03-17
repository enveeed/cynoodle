/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.commands;

import cynoodle.core.api.Checks;
import cynoodle.core.discord.GEntity;
import cynoodle.core.entities.EIdentifier;
import cynoodle.core.module.Module;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.IBson;
import cynoodle.core.mongo.fluent.FluentArray;
import cynoodle.core.mongo.fluent.FluentDocument;
import org.bson.BsonArray;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Settings for all of a guilds commands.
 */
@EIdentifier("base:command:settings")
public final class CommandSettings extends GEntity {
    private CommandSettings() {}

    /**
     * Maximum prefix string length.
     */
    private static final int PREFIX_LIMIT = 16;

    // ===

    /**
     * The prefix for all commands.
     */
    private String prefix = "!";

    /**
     * No-such-command policy.
     */
    private CommandNSCPolicy nscPolicy = CommandNSCPolicy.IGNORE;

    /**
     * Properties for commands.
     */
    private PropertiesStore properties = new PropertiesStore();

    // ===

    @Nonnull
    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(@Nonnull String prefix) {
        Checks.notBlank(prefix, "prefix");
        if(prefix.length() > PREFIX_LIMIT)
            throw new IllegalArgumentException("Prefix cannot be longer than 16 characters!");
        this.prefix = prefix;
    }

    @Nonnull
    public CommandNSCPolicy getNSCPolicy() {
        return this.nscPolicy;
    }

    public void setNSCPolicy(@Nonnull CommandNSCPolicy nscPolicy) {
        this.nscPolicy = nscPolicy;
    }

    //

    @Nonnull
    public PropertiesStore getProperties() {
        return this.properties;
    }

    // ===

    /**
     * Properties for a single command.
     */
    public final class Properties implements IBson {

        private Properties() {}

        private Properties(@Nonnull String identifier) {
            this.identifier = identifier;
        }

        // ===

        /**
         * The command identifier.
         */
        private String identifier;

        /**
         * All alias strings which are mapped to the command
         */
        private Set<String> aliases = new HashSet<>();

        // ===

        /**
         * Apply the default properties set in the descriptor to these properties,
         * overriding any existing ones.
         * @param descriptor the descriptor
         */
        public void applyDescriptor(@Nonnull CommandDescriptor descriptor) {
            this.aliases = Arrays.stream(descriptor.getAliases()).collect(Collectors.toSet());
        }

        //

        @Nonnull
        public String getIdentifier() {
            return this.identifier;
        }

        @Nonnull
        public Set<String> getAliases() {
            return this.aliases;
        }

        public void setAliases(@Nonnull Set<String> aliases) {
            this.aliases = aliases;
        }

        // ===

        @Nonnull
        public String getPrimaryAlias() {
            return this.aliases.stream().sorted().findFirst()
                    .orElseThrow(() -> new IllegalStateException("Aliases are empty, can't get primary alias."));
        }

        // ===

        @Override
        public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {

            this.identifier = source.getAt("identifier").asString().value();
            this.aliases = source.getAt("aliases").asArray().or(FluentArray.wrapNew())
                    .collect().asString().toSetOr(this.aliases);

        }

        @Nonnull
        @Override
        public FluentDocument toBson() throws BsonDataException {
            FluentDocument data = FluentDocument.wrapNew();

            data.setAt("identifier").asString().to(this.identifier);
            data.setAt("aliases").asArray()
                    .to(FluentArray.wrapNew().insert().asString().atEnd(this.aliases));

            return data;
        }

    }

    // TODO improve PropertiesStore / replace with an immutable variant ?
    //  also make clear how nested fields in entities should be handled (persisting, etc..)

    public final class PropertiesStore {

        private PropertiesStore() {}

        // ===

        private final Map<String, Properties> properties = new HashMap<>();

        // ===

        @Nonnull
        public Set<Properties> asSet() {
            return Set.copyOf(properties.values());
        }

        // ===

        @Nonnull
        public Optional<Properties> find(@Nonnull String identifier) {
            return Optional.ofNullable(this.properties.get(identifier));
        }

        @Nonnull
        public Properties findOrCreate(@Nonnull String identifier) {
            return find(identifier).orElseGet(() -> {

                Properties instance = new Properties(identifier);

                CommandDescriptor descriptor = Module.get(CommandsModule.class).getRegistry().get(identifier)
                        .orElseThrow().getDescriptor();

                instance.applyDescriptor(descriptor);

                properties.put(identifier, instance);

                persist();

                return instance;
            });
        }

    }

    // ===

    private Function<BsonValue, PropertiesStore> toPropertiesStore() {
        return value -> {

            PropertiesStore store = new PropertiesStore();

            for (BsonValue val : value.asArray()) {

                FluentDocument data = FluentDocument.wrap(val.asDocument());

                Properties properties = new Properties();
                properties.fromBson(data);

                store.properties.put(properties.getIdentifier(), properties);
            }

            return store;
        };
    }

    private Function<PropertiesStore, BsonValue> fromPropertiesStore() {
        return store -> {

            BsonArray array = new BsonArray();

            for (Properties properties : store.asSet()) {
                array.add(properties.toBson().asBson());
            }

            return array;
        };
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.prefix = source.getAt("prefix").asString().or(this.prefix);
        this.nscPolicy = source.getAt("nsc_policy").asInteger().map(i -> CommandNSCPolicy.values()[i]).or(this.nscPolicy);
        this.properties = source.getAt("properties").as(toPropertiesStore()).or(this.properties);

    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("prefix").asString().to(this.prefix);
        data.setAt("nsc_policy").asInteger().map(CommandNSCPolicy::ordinal).to(this.nscPolicy);
        data.setAt("properties").as(fromPropertiesStore()).to(this.properties);

        return data;
    }
}
