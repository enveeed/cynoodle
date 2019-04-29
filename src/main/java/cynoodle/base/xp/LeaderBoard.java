/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.base.xp;

import cynoodle.discord.DiscordPointer;
import net.dv8tion.jda.api.entities.Guild;

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
    public static LeaderBoard generate(@Nonnull Guild guild) {

        XPModule module = XPModule.get();

        List<XPStatus> statusList = module.getXPStatusEntityManager()
                .stream(XPStatus.filterGuild(guild))
                .filter(XPStatus::hasUser) // NOTE: This may be not needed since we never create any XPStatus instances without a user set
                .filter(status -> guild.getMemberById(status.getUserID()) != null)
                .sorted(XPStatus.orderDescending())
                .collect(Collectors.toList());

        List<Entry> entries = new ArrayList<>();

        for (int i = 0; i < statusList.size(); i++) {
            XPStatus xp = statusList.get(i);
            int rank = i + 1;
            entries.add(new Entry(xp.requireUser(), xp.getXP(), rank));
        }

        return new LeaderBoard(entries, Instant.now());
    }

}
