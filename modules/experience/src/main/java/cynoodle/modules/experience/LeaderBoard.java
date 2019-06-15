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

package cynoodle.modules.experience;

import cynoodle.discord.UReference;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * The experience leader board of a guild, contains a sorted snapshot
 * of the experience values and ranks of all members.
 */
public final class LeaderBoard {

    private final static Duration EXPIRY = Duration.ofMinutes(60);

    // ===

    private final List<Entry> entries;

    private final Instant timestamp;

    // ===

    private final Map<UReference, Entry> users;
    private final Map<Integer, Entry> ranks;

    //

    private LeaderBoard(@Nonnull List<Entry> entries, @Nonnull Instant timestamp) {

        this.entries = Collections.unmodifiableList(entries);
        this.timestamp = timestamp;

        // map members and ranks for fast access

        this.users = new HashMap<>();
        this.ranks = new HashMap<>();

        for (Entry entry : this.entries) {
            this.users.put(entry.getUser(), entry);
            this.ranks.put(entry.getRank(), entry);
        }

    }

    //

    @Nonnull
    public List<Entry> getEntries() {
        return this.entries;
    }

    @Nonnull
    public Instant getTimestamp() {
        return this.timestamp;
    }

    //

    public boolean isExpired() {
        return this.timestamp.plus(EXPIRY).isBefore(Instant.now());
    }

    //

    @Nonnull
    public Optional<Entry> findByUser(@Nonnull UReference user) {
        return Optional.ofNullable(this.users.get(user));
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

    // ===

    /**
     * An entry into the leader board for a member.
     */
    public static final class Entry {

        private UReference user;

        private long value;
        private int rank;

        // ===

        private Entry(@Nonnull UReference user, long value, int rank) {
            this.user = user;
            this.value = value;
            this.rank = rank;
        }

        // ===

        @Nonnull
        public UReference getUser() {
            return this.user;
        }

        public long getValue() {
            return this.value;
        }

        public int getRank() {
            return this.rank;
        }

    }

    // ===

    @Nonnull
    static LeaderBoard ofDescendingStatus(@Nonnull List<ExperienceStatus> statusList) {

        List<Entry> entries = new ArrayList<>();

        for (int i = 1; i <= statusList.size(); i++) {
            ExperienceStatus status = statusList.get(i - 1);
            new Entry(status.requireUser(), status.getValue(), i);
        }

        return new LeaderBoard(entries, Instant.now());
    }

}
