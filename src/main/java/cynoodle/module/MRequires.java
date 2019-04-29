/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.module;

import javax.annotation.Nonnull;
import java.lang.annotation.*;

/**
 * Declares a single dependency for a {@link Module},
 * can be repeated to declare multiple dependencies.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(MDependencies.class)
public @interface MRequires {

    /**
     * The module identifier string of the module the annotated module shall depend on.
     * @return the module identifier string of the dependency module
     * @see ModuleIdentifier
     */
    @Nonnull
    String value();

}
