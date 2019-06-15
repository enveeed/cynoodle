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

package cynoodle.discord;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import cynoodle.util.Checks;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nonnull;

public final class MChecks {
    private MChecks() {}

    // === USER ===

    @Nonnull
    @CanIgnoreReturnValue
    public static Member notBot(Member member) {
        Checks.notNull(member, "member");
        UChecks.notBot(member.getUser());
        return member;
    }
}
