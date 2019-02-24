/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import cynoodle.core.api.Checks;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.MEntity;
import cynoodle.core.discord.RModifier;
import cynoodle.core.entities.EIdentifier;
import cynoodle.core.module.Module;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentDocument;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * The XP of a Member.
 */
@EIdentifier("base:xp:xp")
public final class XP extends MEntity implements Comparable<XP> {
    private XP() {}

    private final XPModule module = Module.get(XPModule.class);

    // ===

    private final AtomicLong xp = new AtomicLong(0L);

    // ===

    public void add(long value) {
        Checks.notNegative(value, "value");

        long previous = this.xp.getAndUpdate(x -> x + value);

        this.handleModification(previous, this.xp.get());
    }

    public void remove(long value) {
        Checks.notNegative(value, "value");

        long previous = this.xp.getAndUpdate(x -> {
            long next = x - value;
            if (next < 0L) next = 0L;
            return next;
        });

        this.handleModification(previous, this.xp.get());
    }

    //

    public long get() {
        return this.xp.get();
    }

    //

    public void addAndPersist(long xp) {
        add(xp);
        persist();
    }

    public void removeAndPersist(long xp) {
        remove(xp);
        persist();
    }

    // ===

    /**
     * Apply the Ranks for this Member.
     * This will add all roles from the effective Rank of the level of the member
     * and all roles from all previous Ranks which are set to 'keep'.
     */
    public void applyRanks() {

        XPFormula formula = module.getFormula();
        RankManager manager = module.getRankManager();

        Guild guild = requireGuild().asGuild()
                .orElseThrow(() -> new IllegalStateException("No Guild!"));

        Member member = requireUser().asMember(guild)
                .orElseThrow(() -> new IllegalStateException("No Member!"));

        //

        int level = formula.getReachedLevel(get());

        //

        List<Rank> allRanks = manager.list(Rank.filterGuild(requireGuild()));

        List<Rank> previousAndThisRanks = allRanks.stream()
                .sorted()
                .filter(rank -> rank.getLevel() <= level).collect(Collectors.toList());

        //

        List<Role> allRoles = new ArrayList<>();

        for (Rank rank : allRanks) {
            for (Rank.Role role : rank.getRoles()) {

                Role actualRole = guild.getRoleById(role.getRole().getID());

                if(actualRole == null) {

                    // TODO warn that there was an unknown role

                    continue;
                }

                allRoles.add(actualRole);
            }
        }

        List<Role> effectiveRoles = new ArrayList<>();

        for (int i = 0; i < previousAndThisRanks.size(); i++) {
            Rank rank = previousAndThisRanks.get(i);

            if(rank.getRoles().size() == 0) continue;

            for (Rank.Role role : rank.getRoles()) {
                // non-keep roles would only apply if this rank is the effective one right now,
                // which means its the last one
                if(!role.isKeepEnabled() && i != previousAndThisRanks.size() - 1) continue;

                DiscordPointer pointer = role.getRole();

                Role actualRole = guild.getRoleById(pointer.getID());

                if(actualRole == null) {

                    // TODO warn that there was an unknown role

                    continue;
                }

                effectiveRoles.add(actualRole);
            }
        }

        //

        RModifier modifier = RModifier.on(member);

        modifier.remove(allRoles);
        modifier.add(effectiveRoles);

        //

        modifier.done().reason("Applied Ranks").queue();
    }

    // ===

    private void handleModification(long xpBefore, long xpAfter) {

        XPFormula formula = module.getFormula();

        int levelBefore = formula.getReachedLevel(xpBefore);
        int levelAfter = formula.getReachedLevel(xpAfter);

        if(levelBefore != levelAfter) {

            this.applyRanks();

            if(levelBefore < levelAfter) {

                // TODO emmit level-up event / announcement

            } else {

                // TODO emmit level-down event / announcement

            }

            // TODO rank-up event / announcement
        }
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.xp.set(source.getAt("xp").asLong().or(this.xp.get()));
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("xp").asLong().to(this.xp.get());

        return data;
    }

    // ===

    @Override
    public int compareTo(@NotNull XP o) {

        // this is the reverse order, that means more XP comes first
        // (natural order for XP)

        return Long.compare(o.get(), this.get());
    }
}
