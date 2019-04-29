/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.base.makeme;

import cynoodle.discord.GEntityManager;
import cynoodle.discord.MEntity;
import cynoodle.entities.EIdentifier;
import cynoodle.entities.EntityReference;
import cynoodle.module.Module;
import cynoodle.mongo.BsonDataException;
import cynoodle.mongo.fluent.FluentArray;
import cynoodle.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@EIdentifier("base:makeme:status")
public final class MakeMeStatus extends MEntity {
    private MakeMeStatus() {}

    private final GEntityManager<MakeMe> makeMeManager =
            Module.get(MakeMeModule.class).getMakeMeManager();

    // ===

    /**
     * The make-me the member has.
     */
    private Set<EntityReference<MakeMe>> makeMe = new HashSet<>();

    // ===

    @Nonnull
    public Set<MakeMe> get() {

        // TODO improve this

        return this.makeMe.stream()
                .filter(r -> r.get().isPresent())
                .map(r -> r.get().orElseThrow())
                .collect(Collectors.toSet());
    }

    // ===

    public void add(@Nonnull MakeMe mm) {
        this.makeMe.add(mm.reference(MakeMe.class));
    }

    public void remove(@Nonnull MakeMe mm) {
        this.makeMe.remove(mm.reference(MakeMe.class));
    }

    // ===

    public boolean has(@Nonnull MakeMe mm) {
        return this.makeMe.contains(mm.reference(MakeMe.class));
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.makeMe = source.getAt("make_me").asArray().or(FluentArray.wrapNew())
                .collect().as(EntityReference.load(this.makeMeManager)).toSet();
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("make_me").asArray().to(FluentArray.wrapNew()
                .insert().as(EntityReference.<MakeMe>store()).atEnd(this.makeMe));

        return data;
    }
}