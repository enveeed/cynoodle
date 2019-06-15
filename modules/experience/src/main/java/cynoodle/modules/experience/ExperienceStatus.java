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

import com.google.common.flogger.FluentLogger;
import cynoodle.discord.MEntity;
import cynoodle.entity.EIdentifier;
import cynoodle.entity.EntityType;
import cynoodle.mongodb.BsonDataException;
import cynoodle.mongodb.IBsonDocument;
import cynoodle.mongodb.IBsonDocumentCodec;
import cynoodle.mongodb.fluent.Codec;
import cynoodle.mongodb.fluent.FluentDocument;
import cynoodle.mongodb.fluent.MoreCodecs;
import org.bson.BSONException;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongUnaryOperator;

/**
 * The experience status of a Member.
 */
@EIdentifier(ExperienceModule.IDENTIFIER + ":status")
public final class ExperienceStatus extends MEntity {
    private ExperienceStatus() {}

    private final static FluentLogger LOG = FluentLogger.forEnclosingClass();

    final static EntityType<ExperienceStatus> TYPE = EntityType.of(ExperienceStatus.class);

    // ===

    /**
     * The current experience value.
     */
    private final AtomicLong value = new AtomicLong(Experience.MIN_VALUE);

    /**
     * The timeout (-until) timestamp.
     */
    private Map<GainType, GainTimeout> timeouts = new HashMap<>();

    // === VALUE ===

    public long getValue() {
        return this.value.get();
    }

    //

    /**
     * Modify the experience value using the given operator.
     * Should the operator result in less than {@link Experience#MIN_VALUE},
     * the result will be {@link Experience#MIN_VALUE} and should it result in more
     * than {@link Experience#MAX_VALUE} the result will be {@link Experience#MAX_VALUE}.
     * @param operator the operator
     * @return the previous value, before applying the operator
     */
    public long modifyValue(@Nonnull LongUnaryOperator operator) {
        return this.value.getAndUpdate(operand -> {
            long modified = operator.applyAsLong(operand);
            if(modified < Experience.MIN_VALUE) return Experience.MIN_VALUE;
            if(modified > Experience.MAX_VALUE) return Experience.MAX_VALUE;
            return modified;
        });
    }

    // === TIMEOUT ===

    @Nonnull
    private GainTimeout getTimeout(@Nonnull GainType type) {
        GainTimeout timeout = this.timeouts.get(type);
        if(timeout == null) {
            timeout = new GainTimeout(type, Instant.EPOCH);
            this.timeouts.put(type, timeout);
            this.persist();
        }
        return timeout;
    }

    @Nonnull
    public Instant getTimeoutExpiry(@Nonnull GainType type) {
        return getTimeout(type).getExpiry();
    }

    public boolean isTimeoutExpired(@Nonnull GainType type) {
        return getTimeout(type).isExpired();
    }

    public void setTimeout(@Nonnull GainType type, @Nonnull Instant expiry) {
        GainTimeout timeout = getTimeout(type);
        timeout.setExpiry(expiry);
    }

    public void setTimeoutDuration(@Nonnull GainType type, @Nonnull Duration duration) {
        setTimeout(type, Instant.now().plus(duration));
    }

    // ===

    public int getLevel() {
        return ExperienceFormula.getReachedLevel(getValue());
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.value.set(source.getAt("value").asLong().or(this.value.get()));
        this.timeouts = source.getAt("timeouts").as(MoreCodecs.forValueMap(GainTimeout.codec(), GainTimeout::getType)).or(this.timeouts);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("value").asLong().to(this.value.get());
        data.setAt("timeout").as(MoreCodecs.forValueMap(GainTimeout.codec(), GainTimeout::getType)).to(this.timeouts);

        return data;
    }

    // ===

    @Nonnull
    public static Comparator<ExperienceStatus> orderAscending() {
        return Comparator.comparingLong(ExperienceStatus::getValue);
    }

    @Nonnull
    public static Comparator<ExperienceStatus> orderDescending() {
        return Comparator.comparingLong(ExperienceStatus::getValue).reversed();
    }

    // ===

    /**
     * Keeps track of the timeout status for a {@link GainType}.
     */
    private final static class GainTimeout implements IBsonDocument {
        private GainTimeout() {}

        /**
         * The type of gain.
         */
        private GainType type;

        /**
         * The time at which the timeout expires.
         */
        private Instant expiry;

        // ===

        GainTimeout(@Nonnull GainType type, @Nonnull Instant expiry) {
            this.type = type;
            this.expiry = expiry;
        }

        // ===

        @Nonnull
        public GainType getType() {
            return this.type;
        }

        //

        @Nonnull
        public Instant getExpiry() {
            return this.expiry;
        }

        public void setExpiry(@Nonnull Instant expiry) {
            this.expiry = expiry;
        }

        public boolean isExpired() {
            return this.expiry.isBefore(Instant.now());
        }

        // ===

        @Override
        public void fromBson(@Nonnull FluentDocument data) throws BSONException {
            this.type = data.getAt("type").as(GainType.codec()).or(this.type);
            this.expiry = data.getAt("expiry").as(MoreCodecs.forInstant()).or(this.expiry);
        }

        @Nonnull
        @Override
        public FluentDocument toBson() throws BSONException {
            FluentDocument data = FluentDocument.wrapNew();
            data.setAt("type").as(GainType.codec()).to(this.type);
            data.setAt("expiry").as(MoreCodecs.forInstant()).to(this.expiry);
            return data;
        }

        // ===

        static Codec<GainTimeout> codec() {
            return new IBsonDocumentCodec<>(GainTimeout::new);
        }
    }
}
