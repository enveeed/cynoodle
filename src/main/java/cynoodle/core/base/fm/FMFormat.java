/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.fm;

import cynoodle.core.base.commands.CommandContext;
import de.umass.lastfm.Track;
import net.dv8tion.jda.core.entities.MessageEmbed;

import javax.annotation.Nonnull;

/**
 * Formatting options for {@link FMCommand}.
 */
public interface FMFormat {

    @Nonnull
    MessageEmbed createEmbed(@Nonnull CommandContext context,
                             @Nonnull Track track);

}
