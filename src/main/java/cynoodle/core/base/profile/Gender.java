/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.profile;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * A gender.
 * Supports any term combined with either male, female or neutral addressing.
 */
public final class Gender {

    private final String name;
    private final AddressAs addressAs;

    // ===

    private Gender(@Nonnull String name, @Nonnull AddressAs addressAs) {
        this.name = name;
        this.addressAs = addressAs;
    }

    // ===

    @Nonnull
    public String getName() {
        return this.name;
    }

    @Nonnull
    public AddressAs getAddressAs() {
        return this.addressAs;
    }

    // ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Gender gender = (Gender) o;

        if (!name.equals(gender.name)) return false;
        return addressAs == gender.addressAs;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + addressAs.hashCode();
        return result;
    }

    // ===

    @Nonnull
    public static Gender male() {
        return new Gender("male", AddressAs.MALE);
    }

    @Nonnull
    public static Gender female() {
        return new Gender("female", AddressAs.FEMALE);
    }

    @Nonnull
    public static Gender of(@Nonnull String name, @Nonnull AddressAs addressAs) {
        return new Gender(name, addressAs);
    }

    // ===

    /**
     * Options for the preferred addressing of a user.
     */
    public enum AddressAs {

        /**
         * Addresses the user grammatically male.
         */
        MALE,
        /**
         * Addresses the user grammatically female.
         */
        FEMALE,
        /**
         * Addresses the user grammatically neutral.
         */
        NEUTRAL

    }

    // ===

    @Nonnull
    public static Function<Gender, BsonValue> toBson() {
        return gender -> new BsonDocument()
                .append("name", new BsonString(gender.name))
                .append("address_as", new BsonInt32(gender.addressAs.ordinal()));
    }

    @Nonnull
    public static Function<BsonValue, Gender> fromBson() {
        return value -> {
            BsonDocument document = value.asDocument();
            return Gender.of(
                    document.getString("name").getValue(),
                    AddressAs.values()[document.getInt32("address_as").getValue()]
            );
        };
    }

}
