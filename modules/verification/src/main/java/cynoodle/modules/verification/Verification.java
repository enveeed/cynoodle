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

package cynoodle.modules.verification;

import cynoodle.discord.GEntityManager;
import cynoodle.discord.MEntityManager;
import cynoodle.module.Module;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nonnull;

public final class Verification {
    Verification() {}

    // ===

    final GEntityManager<VerificationSettings> settingsEntityManager
            = new GEntityManager<>(VerificationSettings.TYPE);
    final MEntityManager<VerificationStatus> statusEntityManager
            = new MEntityManager<>(VerificationStatus.TYPE);

    // ===

    @Nonnull
    public VerificationSettings getSettings(@Nonnull Guild guild) {
        return this.settingsEntityManager.firstOrCreate(guild);
    }

    @Nonnull
    public VerificationStatus getStatus(@Nonnull Member member) {
        return this.statusEntityManager.firstOrCreate(member);
    }

    // ===

    @Nonnull
    public static Verification get() {
        return Module.get(VerificationModule.class).getVerification();
    }

}
