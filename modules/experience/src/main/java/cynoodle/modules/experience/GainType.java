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

import cynoodle.mongodb.MappingCodec;
import cynoodle.mongodb.fluent.Codec;
import cynoodle.mongodb.fluent.PrimitiveCodecs;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;

/**
 * Different types of experience gaining.
 */
public enum GainType {

    /**
     * Gain from any message.
     */
    MESSAGE("message"),

    /**
     * Gain from any attachment to a message.
     */
    ATTACHMENT("attachment"),

    /**
     * Gain from any reaction to a message.
     */
    REACTION("reaction"),

    ;

    // ===

    private final String key;

    //

    GainType(String key) {
        this.key = key;
    }

    //

    @Nonnull
    public String getKey() {
        return this.key;
    }

    // ===

    @Nonnull
    public static GainType findByKey(@Nonnull String key) {
        switch (key) {
            case "message": return MESSAGE;
            case "attachment": return ATTACHMENT;
            case "reaction": return REACTION;
            default: throw new NoSuchElementException();
        }
    }

    // ===

    @Nonnull
    static Codec<GainType> codec() {
        return new MappingCodec<>(PrimitiveCodecs.forString(), GainType::findByKey, GainType::getKey);
    }
}
