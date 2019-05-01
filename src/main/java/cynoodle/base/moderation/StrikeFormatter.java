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

package cynoodle.base.moderation;

import cynoodle.base.local.LocalContext;
import cynoodle.discord.MFormatter;
import cynoodle.discord.Members;
import cynoodle.module.Module;

import javax.annotation.Nonnull;

/**
 * Formatter for {@link Strike Strikes}.
 */
public final class StrikeFormatter {

    // TODO mode option to specify detail level of strike output

    private final ModerationModule module = Module.get(ModerationModule.class);

    @Nonnull
    public String format(@Nonnull Strike strike, @Nonnull LocalContext local) {

        StringBuilder out = new StringBuilder();

        out.append("**|** Strike for **")
                .append(Members.formatAt(strike.requireGuild()).withMode(MFormatter.Mode.USER_FULL)
                        .format(strike.requireUser()))
                .append("**\n\n");

        out.append(" ").append(strike.getReason()).append("\n\n");

        out.append("`")
                .append(local.formatDateTime(strike.getTimestamp()))
                .append("`");

        if(strike.isDecayable()) {
            out.append(" | ");
            if(strike.isDecayed()) out.append("Decayed at ");
            else out.append("Decays at ");
            out.append("`")
                    .append(local.formatDateTime(strike.getDecayAt().orElseThrow()))
                    .append("`");
        }
        else out.append(" | Not decayable");

        if(strike.isRemoved()) {
            out.append(" | **Removed**");
        }

        long amount = module.getStrikeEntities()
                .stream(Strike.filterMember(strike.requireGuild(), strike.requireUser()))
                .filter(Strike::isEffective)
                .count();

        out.append(" | ").append(amount).append(" effective strike(s) in total");

        return out.toString();

    }
}
