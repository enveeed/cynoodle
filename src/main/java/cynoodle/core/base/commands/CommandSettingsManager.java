/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.commands;

import cynoodle.core.discord.GEntityManager;

public final class CommandSettingsManager extends GEntityManager<CommandSettings> {

    public CommandSettingsManager() {
        super(CommandsModule.TYPE_SETTINGS);
    }
}
