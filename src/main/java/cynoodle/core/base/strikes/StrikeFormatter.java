/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.strikes;

import cynoodle.core.api.text.TemporalFormatter;
import cynoodle.core.discord.MFormatter;
import cynoodle.core.discord.Members;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;

/**
 * Formatter for {@link Strike Strikes}.
 */
public final class StrikeFormatter {

    // TODO mode option to specify detail level of strike output

    private final StrikesModule module = Module.get(StrikesModule.class);

    @Nonnull
    public String format(@Nonnull Strike strike) {

        StringBuilder out = new StringBuilder();

        out.append("**|** Strike for **")
                .append(Members.formatAt(strike.requireGuild()).withMode(MFormatter.Mode.USER_FULL)
                        .format(strike.requireUser()))
                .append("**\n\n");

        out.append(" ").append(strike.getReason()).append("\n\n");

        out.append("`")
                .append(TemporalFormatter.ofDefault().format(strike.getTimestamp()))
                .append("`");

        if(strike.isDecayable()) {
            out.append(" | ");
            if(strike.isDecayed()) out.append("Decayed at ");
            else out.append("Decays at ");
            out.append("`")
                    .append(TemporalFormatter.ofDefault().format(strike.getDecayAt().orElseThrow()))
                    .append("`");
        }
        else out.append(" | Not decayable");

        if(strike.isRemoved()) {
            out.append(" | **Removed**");
        }

        long amount = module.getStrikes()
                .stream(Strike.filterMember(strike.requireGuild(), strike.requireUser()))
                .filter(Strike::isEffective)
                .count();

        out.append(" | ").append(amount).append(" effective strike(s) in total");

        return out.toString();

    }
}
