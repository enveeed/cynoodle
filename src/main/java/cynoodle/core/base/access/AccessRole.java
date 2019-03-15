/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.access;

import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.Bsonable;
import cynoodle.core.mongo.fluent.FluentArray;
import cynoodle.core.mongo.fluent.FluentDocument;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Access control properties for a single Role of a Guild.
 * @see AccessControl
 */
public final class AccessRole implements Bsonable {
    private AccessRole() {}

    // ===

    /**
     * The role itself.
     */
    private DiscordPointer role;

    /**
     * The permissions this role has.
     */
    private Set<String> permissions = new HashSet<>();

    // ===

    AccessRole(@Nonnull DiscordPointer role) {
        this.role = role;
    }

    // ===

    @Nonnull
    public DiscordPointer getRole() {
        return this.role;
    }

    // ===

    public boolean test(@Nonnull String permission) {
        return this.permissions.contains(permission);
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument data) throws BsonDataException {
        this.role = data.getAt("role").as(DiscordPointer.fromBson()).or(this.role);
        this.permissions = data.getAt("permissions").asArray().or(FluentArray.wrapNew())
                .collect().asString().toSet();
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = FluentDocument.wrapNew();

        data.setAt("role").as(DiscordPointer.toBson()).to(this.role);
        data.setAt("permissions").asArray().to(FluentArray.wrapNew()
            .insert().asString().atEnd(this.permissions));

        return data;
    }

    // ===

    @Nonnull
    static Function<BsonValue, AccessRole> load() {
        return value -> {
            AccessRole role = new AccessRole();
            role.fromBson(FluentDocument.wrap(value.asDocument()));
            return role;
        };
    }

    @Nonnull
    static Function<AccessRole, BsonValue> store() {
        return role -> role.toBson().asBson();
    }
}
