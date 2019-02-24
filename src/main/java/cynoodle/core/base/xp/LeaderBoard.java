/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.xp;

import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.MEntityManager;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Immutable leader board at a specific time.
 */
public final class LeaderBoard {

    private final static Duration EXPIRY = Duration.ofMinutes(60);

    // ===

    private final List<Entry> entries;

    //

    private final Map<DiscordPointer, Entry> members;
    private final Map<Integer, Entry> ranks;

    //

    private final Instant timestamp;

    //

    private LeaderBoard(@Nonnull List<Entry> entries, @Nonnull Instant timestamp) {

        this.entries = Collections.unmodifiableList(entries);
        this.timestamp = timestamp;

        //

        this.members = new HashMap<>();
        this.ranks = new HashMap<>();

        for (Entry entry : this.entries) {
            this.members.put(entry.member, entry);
            this.ranks.put(entry.rank, entry);
        }

    }

    //

    @Nonnull
    public Instant getTimestamp() {
        return this.timestamp;
    }

    @Nonnull
    public List<Entry> getEntries() {
        return this.entries;
    }

    //

    public boolean isExpired() {
        return this.timestamp.plus(EXPIRY).isBefore(Instant.now());
    }

    //

    @Nonnull
    public Optional<Entry> findByMember(@Nonnull DiscordPointer member) {
        return Optional.ofNullable(this.members.get(member));
    }

    @Nonnull
    public Optional<Entry> findByRank(int rank) {
        return Optional.ofNullable(this.ranks.get(rank));
    }

    //

    @Nonnull
    public List<Entry> sub(int from, int toMax) {
        if(from < 0) throw new IllegalArgumentException("The 'from' index cannot be less than zero!");
        if(toMax >= this.entries.size()) toMax = this.entries.size() - 1;

        // NOTE: in this method, both parameters are INCLUSIVE, unlike List.subList() !!!

        return this.entries.subList(from, toMax + 1);
    }

    //

    /**
     * A single members xp data as a snapshot
     */
    public final static class Entry {

        private DiscordPointer member;

        private long xp;

        private int rank;

        private Entry(@Nonnull DiscordPointer member, long xp, int rank) {
            this.member = member;
            this.xp = xp;
            this.rank = rank;
        }

        // ===

        @Nonnull
        public DiscordPointer getMember() {
            return member;
        }

        public long getXP() {
            return xp;
        }

        public int getRank() {
            return rank;
        }
    }

    // ===

    @Nonnull
    public static LeaderBoard generate(@Nonnull DiscordPointer guild) {

        XPModule module = Module.get(XPModule.class);

        MEntityManager<XP> xpManager = module.getXPManager();

        //

        List<Entry> entries = new ArrayList<>();

        List<XP> xpList = xpManager
                .stream(XP.filterGuild(guild))
                .sorted()
                .collect(Collectors.toList());

        for (int i = 0; i < xpList.size(); i++) {
            XP xp = xpList.get(i);
            int rank = i + 1;
            entries.add(new Entry(xp.requireUser(), xp.get(), rank));
        }

        return new LeaderBoard(entries, Instant.now());
    }

}
