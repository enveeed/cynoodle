/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.utilities;

import cynoodle.core.base.commands.CommandRegistry;
import cynoodle.core.base.commands.CommandsModule;
import cynoodle.core.module.MIdentifier;
import cynoodle.core.module.MRequires;
import cynoodle.core.module.Module;

/**
 * Contains utility commands, notifications, etc.
 */
@MIdentifier("base:utilities")
@MRequires("base:commands")
@MRequires("base:notifications")
public final class UtilitiesModule extends Module {
    private UtilitiesModule() {}

    // ===

    @Override
    protected void start() {
        super.start();

        CommandRegistry commandRegistry = Module.get(CommandsModule.class).getRegistry();

        commandRegistry.register(VersionCommand.class);
        commandRegistry.register(ChooseOfRoleCommand.class);
        commandRegistry.register(TemporaryHelpCommand.class);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }
}
