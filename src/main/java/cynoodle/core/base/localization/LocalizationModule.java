/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.localization;

import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.MRequires;
import cynoodle.core.module.Module;

@MIdentifier("base:localization")
@MRequires("base:command")
public final class LocalizationModule extends Module {
    private LocalizationModule() {}
}
