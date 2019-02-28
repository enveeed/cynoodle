/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.profile;

import cynoodle.core.discord.UEntity;
import cynoodle.core.entities.EIdentifier;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.Optional;

import static cynoodle.core.mongo.fluent.FluentValues.fromLocalDate;
import static cynoodle.core.mongo.fluent.FluentValues.toLocalDate;

/**
 * A user profile, which contains user properties for different purposes.
 */
@EIdentifier("base:profile:profile")
public final class Profile extends UEntity {
    private Profile() {}

    // ===

    /**
     * The profile text / bio / description.
     */
    private String text = null;

    /**
     * The birthday.
     */
    private LocalDate birthday = null;

    /**
     * The gender.
     */
    private Gender gender = null;

    // ===

    @Nonnull
    public Optional<String> getText() {
        return Optional.ofNullable(this.text);
    }

    public void setText(@Nullable String text) {
        this.text = text;
    }

    @Nonnull
    public Optional<LocalDate> getBirthday() {
        return Optional.ofNullable(birthday);
    }

    public void setBirthday(@Nullable LocalDate birthday) {
        this.birthday = birthday;
    }

    @Nonnull
    public Optional<Gender> getGender() {
        return Optional.ofNullable(this.gender);
    }

    public void setGender(@Nullable Gender gender) {
        this.gender = gender;
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.text = source.getAt("text").asStringNullable().or(this.text);
        this.birthday = source.getAt("birthday").asNullable(toLocalDate()).or(this.birthday);
        this.gender = source.getAt("gender").asNullable(Gender.fromBson()).or(this.gender);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("text").asStringNullable().to(this.text);
        data.setAt("birthday").asNullable(fromLocalDate()).to(this.birthday);
        data.setAt("gender").asNullable(Gender.toBson()).to(this.gender);

        return data;
    }
}
