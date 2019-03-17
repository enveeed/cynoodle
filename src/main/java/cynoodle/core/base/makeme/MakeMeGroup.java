/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.makeme;

import com.mongodb.client.model.Filters;
import cynoodle.core.discord.GEntity;
import cynoodle.core.entities.EIdentifier;
import cynoodle.core.entities.EIndex;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentDocument;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;

/**
 * A make-me group is a collection of {@link MakeMe make-me}.
 */
@EIdentifier("base:makeme:group")
@EIndex(MakeMeGroup.KEY_KEY)
public final class MakeMeGroup extends GEntity {
    private MakeMeGroup() {}

    static final String KEY_KEY = "key";

    // ===

    /**
     * The key of the group.
     */
    private String key;

    /**
     * The name of the group.
     */
    private String name;

    //

    /**
     * 'unique' flag, allows only one make-me of the group for the Member.
     */
    private boolean unique = false;

    // ===

    void create(@Nonnull String key, @Nonnull String name) {
        this.key = key;
        this.name = name;
    }

    // ===

    @Nonnull
    public String getKey() {
        return this.key;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    //

    public boolean isUniqueEnabled() {
        return this.unique;
    }

    // ===

    @Nonnull
    public static Bson filterKey(@Nonnull String key) {
        return Filters.eq(KEY_KEY, key);
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.key = source.getAt(KEY_KEY).asString().or(this.key);
        this.name = source.getAt("name").asString().or(this.name);

        this.unique = source.getAt("unique").asBoolean().or(this.unique);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt(KEY_KEY).asString().to(this.key);
        data.setAt("name").asString().to(this.name);

        data.setAt("unique").asBoolean().to(this.unique);

        return data;
    }
}
