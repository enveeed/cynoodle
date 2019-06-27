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

package cynoodle.test.commands;

import cynoodle.mongodb.fluent.Codec;
import org.bson.BSONException;
import org.bson.BsonString;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * "no such command" policy.
 */
public enum NSCPolicy {

    /**
     * Ignore and don't do anything.
     */
    IGNORE("ignore"),
    /**
     * Report that the command is not known.
     */
    REPORT("report"),
    /**
     * Report that the command is not known and provide examples
     * for similar commands.
     */
    REPORT_DETAILED("report_detailed"),

    ;

    // ===

    private final String key;

    // ===

    NSCPolicy(String key) {
        this.key = key;
    }

    // ===

    @Nonnull
    public String getKey() {
        return this.key;
    }

    // ===

    @Nonnull
    public static Optional<NSCPolicy> get(@Nonnull String key) {
        switch (key) {
            case "ignore": return Optional.of(IGNORE);
            case "report": return Optional.of(REPORT);
            case "report_detailed": return Optional.of(REPORT_DETAILED);
            default: return Optional.empty();
        }
    }

    // ===

    // NOTE: codec falls back to IGNORE if it reads an unknown policy
    @Nonnull
    static Codec<NSCPolicy> codec() {
        return new Codec<>() {
            @Override
            public NSCPolicy load(BsonValue bson) throws BSONException {
                return get(bson.asString().getValue()).orElse(NSCPolicy.IGNORE);
            }

            @Override
            public BsonValue store(NSCPolicy object) throws BSONException {
                return new BsonString(object.getKey());
            }
        };
    }
}
