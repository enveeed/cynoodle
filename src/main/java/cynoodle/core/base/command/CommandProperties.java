/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.command;

import com.mongodb.client.model.Filters;
import cynoodle.core.base.permission.Permission;
import cynoodle.core.base.permission.PermissionModule;
import cynoodle.core.discord.GEntity;
import cynoodle.core.discord.GEntityManager;
import cynoodle.core.entities.EIdentifier;
import cynoodle.core.entities.EIndex;
import cynoodle.core.entities.EntityReference;
import cynoodle.core.module.Module;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentArray;
import cynoodle.core.mongo.fluent.FluentDocument;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Properties for a single command on a guild.
 */
@EIdentifier("base:command:properties")
@EIndex("identifier")
public final class CommandProperties extends GEntity {
    private CommandProperties() {}

    private final GEntityManager<Permission> permissions = Module.get(PermissionModule.class)
            .getPermissionManager();

    // ===

    /**
     * The command identifier
     */
    private String identifier;

    // ===

    /**
     * The permission for the command
     */
    private EntityReference<Permission> permission = null;

    /**
     * All alias strings which are mapped to the command
     */
    private Set<String> aliases = new HashSet<>();

    // ===

    void create(@Nonnull CommandDescriptor descriptor) {
        this.identifier = descriptor.getIdentifier();
        this.aliases = Arrays.stream(descriptor.getAliases()).collect(Collectors.toSet());
    }

    //

    @Nonnull
    public String getIdentifier() {
        return this.identifier;
    }

    //

    @Nonnull
    public Optional<Permission> getPermission() {
        return this.permission == null ? Optional.empty() : this.permission.get();
    }

    public void setPermission(@Nullable Permission permission) {
        this.permission = permission == null ? null : permission.reference(Permission.class);
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
    public static Bson filterIdentifier(@Nonnull String identifier) {
        return Filters.eq("identifier", identifier);
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.identifier = source.getAt("identifier").asString().value();
        this.permission = source.getAt("permission")
                .asNullable(EntityReference.fromBson(this.permissions)).or(this.permission);
        this.aliases = source.getAt("aliases").asArray().or(FluentArray.wrapNew())
                .collect().asString().toSetOr(this.aliases);

    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("identifier").asString().to(this.identifier);
        data.setAt("permission").asNullable(EntityReference.<Permission>toBson()).to(this.permission);
        data.setAt("aliases").asArray()
                .to(FluentArray.wrapNew().insert().asString().atEnd(this.aliases));

        return data;
    }
}
