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

package cynoodle.base.profiles;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * A gender.
 * Supports any term combined with an option for preferred pronouns.
 *
 * @deprecated This was supposed to be used for profiles, but now {@link cynoodle.base.profiles.Pronouns}
 *  are used instead now. This may be useful for the future, that's why its still here.
 */
@Deprecated
public final class Gender {

    private final String name;
    private final Pronouns pronouns;

    // ===

    private Gender(@Nonnull String name, @Nonnull Pronouns addressAs) {
        this.name = name;
        this.pronouns = addressAs;
    }

    // ===

    @Nonnull
    public String getName() {
        return this.name;
    }

    @Nonnull
    public Pronouns getPronouns() {
        return this.pronouns;
    }

    // ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Gender gender = (Gender) o;

        if (!name.equals(gender.name)) return false;
        return pronouns == gender.pronouns;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + pronouns.hashCode();
        return result;
    }

    // ===

    @Nonnull
    public static Gender male() {
        return new Gender("male", Pronouns.MASCULINE);
    }

    @Nonnull
    public static Gender female() {
        return new Gender("female", Pronouns.FEMININE);
    }

    @Nonnull
    public static Gender of(@Nonnull String name, @Nonnull Pronouns addressAs) {
        return new Gender(name, addressAs);
    }

    // ===

    /**
     * Options for the preferred addressing of a user.
     */
    public enum Pronouns {

        /**
         * Addresses the user grammatically masculine.
         */
        MASCULINE,
        /**
         * Addresses the user grammatically feminine.
         */
        FEMININE,
        /**
         * Addresses the user grammatically indefinite.
         */
        INDEFINITE

    }

    // ===

    @Nonnull
    public static Function<Gender, BsonValue> toBson() {
        return gender -> new BsonDocument()
                .append("name", new BsonString(gender.name))
                .append("pronouns", new BsonInt32(gender.pronouns.ordinal()));
    }

    @Nonnull
    public static Function<BsonValue, Gender> fromBson() {
        return value -> {
            BsonDocument document = value.asDocument();
            return Gender.of(
                    document.getString("name").getValue(),
                    Pronouns.values()[document.getInt32("pronouns").getValue()]
            );
        };
    }

}
