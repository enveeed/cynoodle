/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.incubating.condition;

import cynoodle.core.mongo.fluent.FluentDocument;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

public final class ConditionType<C extends Condition> {

    private final Class<C> conditionClass;

    private final String identifier;

    // ===

    private ConditionType(@Nonnull Builder<C> builder) {
        this.conditionClass = builder.conditionClass;

        this.identifier = builder.identifier;
    }

    // ===

    @Nonnull
    public Class<C> getConditionClass() {
        return this.conditionClass;
    }

    //

    @Nonnull
    public String getIdentifier() {
        return this.identifier;
    }

    // ===

    @Nonnull
    public C create(@Nonnull Consumer<C> action) {
        C instance = createInstance();
        action.accept(instance);
        return instance;
    }

    @Nonnull
    public C create() {
        return create(c -> {});
    }

    //

    @Nonnull
    public C createFrom(@Nonnull FluentDocument data) {
        return create(c -> c.fromBson(data));
    }

    // ===

    @Nonnull
    private C createInstance() {

        Constructor<C> constructor;

        try {
            // get the constructor and make sure its accessible
            constructor = this.getConditionClass().getDeclaredConstructor();

            constructor.setAccessible(true);

        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Required constructor not found!", e);
        } catch (SecurityException | InaccessibleObjectException e) {
            throw new IllegalStateException("Access to constructor failed!", e);
        }

        // create a new instance
        C condition;

        try {
            condition = constructor.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create new Condition instance!", e);
        }

        // initialize the condition
        condition.init(this);

        //

        return condition;
    }

    // ===

    private static final class Builder<C extends Condition> {

        private Class<C> conditionClass;

        private String identifier;

        // ===

        private Builder() {}

        // ===

        @Nonnull
        public Builder<C> setConditionClass(@Nonnull Class<C> conditionClass) {
            this.conditionClass = conditionClass;
            return this;
        }

        //

        @Nonnull
        public Builder<C> setIdentifier(@Nonnull String identifier) {
            this.identifier = identifier;
            return this;
        }

        // ===

        ConditionType<C> build() {
            return new ConditionType<>(this);
        }

    }

    // ===

    @Nonnull
    static <C extends Condition> ConditionType<C> of(@Nonnull Class<C> conditionClass) throws ConditionClassException {

        Builder<C> builder = new Builder<>();

        builder.setConditionClass(conditionClass);

        // ======

        // ensure class and constructor properties

        if(!Modifier.isFinal(conditionClass.getModifiers())) throw new ConditionClassException("Condition class is not final!");

        if(conditionClass.getDeclaredConstructors().length != 1)
            throw new ConditionClassException("Condition class requires exactly one constructor!");

        try {

            // try to get the constructor, fail otherwise
            Constructor<? extends Condition> constructor = conditionClass.getDeclaredConstructor();

            // check if the constructor is private
            if(!Modifier.isPrivate(constructor.getModifiers()))
                throw new ConditionClassException("Condition class constructor is not private!");

        } catch (NoSuchMethodException e) {
            throw new ConditionClassException("Condition class does not contain a no-argument constructor!", e);
        }

        // ======

        // find annotations
        ConditionIdentifier annIdentifier = conditionClass.getAnnotation(ConditionIdentifier.class);

        // ensure required annotations
        if(annIdentifier == null) throw new ConditionClassException("Condition class is missing @EIdentifier annotation!");

        // ======

        String identifier = annIdentifier.value();

        // TODO validation

        builder.setIdentifier(identifier);

        // ======

        return builder.build();

    }
}
