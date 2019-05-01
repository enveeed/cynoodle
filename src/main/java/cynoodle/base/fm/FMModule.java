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

package cynoodle.base.fm;

import cynoodle.Configuration;
import cynoodle.CyNoodle;
import cynoodle.base.commands.CommandsModule;
import cynoodle.base.commands.CommandRegistry;
import cynoodle.discord.UEntityManager;
import cynoodle.entities.EntityType;
import cynoodle.module.MIdentifier;
import cynoodle.module.MRequires;
import cynoodle.module.Module;

import javax.annotation.Nonnull;

/**
 * <code>base:fm</code>
 */
@MIdentifier("base:fm")
@MRequires("base:commands")
public final class FMModule extends Module {
    private FMModule() {}

    // ===

    private static final EntityType<FMPreferences> TYPE_PREFERENCES = EntityType.of(FMPreferences.class);

    // ===

    private FMModuleConfiguration configuration;

    //

    private UEntityManager<FMPreferences> preferencesManager;

    private FMFormatRegistry formatRegistry;

    // ===

    @Override
    protected void start() {
        super.start();

        Configuration.Section section = CyNoodle.get().getConfiguration().get("fm")
                .orElseThrow(() -> new RuntimeException("last.fm configuration is missing! (section 'fm')"));

        this.configuration = FMModuleConfiguration.parse(section);

        //

        this.preferencesManager = new UEntityManager<>(TYPE_PREFERENCES);

        this.formatRegistry = new FMFormatRegistry();

        //

        CommandRegistry registry = Module.get(CommandsModule.class).getRegistry();

        registry.register(FMCommand.class);
        registry.register(FMEditCommand.class);

        //

        this.formatRegistry.register("simple", new SimpleFMFormat());
        this.formatRegistry.register("simple-cover", new SimpleCoverFMFormat());
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    // ===

    @Nonnull
    public FMModuleConfiguration getConfiguration() {
        return this.configuration;
    }

    //

    @Nonnull
    public UEntityManager<FMPreferences> getPreferencesManager() {
        return this.preferencesManager;
    }

    //

    @Nonnull
    public FMFormatRegistry getFormatRegistry() {
        return this.formatRegistry;
    }
}
