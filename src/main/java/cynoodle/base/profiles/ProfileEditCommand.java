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

package cynoodle.base.profiles;

import cynoodle.api.Strings;
import cynoodle.api.text.Options;
import cynoodle.api.parser.TimeParsers;
import cynoodle.base.commands.*;
import cynoodle.base.local.LocalContext;
import cynoodle.discord.UEntityManager;
import cynoodle.module.Module;

import javax.annotation.Nonnull;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Optional;

import static cynoodle.base.commands.CommandErrors.simple;

@CIdentifier("base:profile:edit")
@CAliases({"profileedit", "pedit", "pe"})
public final class ProfileEditCommand extends Command {
    private ProfileEditCommand() {
    }

    private final static Options.Option OPT_RESET = Options.newFlagOption("reset", 'r');

    private static final String[] VALUES_PRONOUNS_MASCULINE
            = new String[]{"masculine", "male", "m", "he", "him", "his", "he/him"};
    private static final String[] VALUES_PRONOUNS_FEMININE
            = new String[]{"feminine", "female", "f", "she", "her", "she/her"};
    private static final String[] VALUES_PRONOUNS_INDEFINITE
            = new String[]{"indefinite", "x", "o", "other", "they", "them", "they/them"};

    // ===

    private final ProfilesModule module = Module.get(ProfilesModule.class);

    // ===

    {
        this.getOptionsBuilder()
                .add(OPT_RESET);
    }

    // ===

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        UEntityManager<Profile> profileManager = module.getProfileManager();

        Profile profile = profileManager.firstOrCreate(context.getUserPointer());

        //

        if (!input.hasParameter(0)) {

            StringBuilder out = new StringBuilder();

            out.append("**Profile**")
                    .append("\n\n");

            Optional<String> textResult = profile.getText();
            out.append(formatPropertyListing(
                    "text",
                    textResult.isPresent() ? textResult.orElseThrow() : " - ",
                    "Text / bio"));

            Optional<LocalDate> birthdayResult = profile.getBirthday();
            out.append(formatPropertyListing(
                    "birthday",
                    birthdayResult.isPresent() ? local.formatDate(birthdayResult.orElseThrow()) : " - ",
                    "Birthday"));

            Optional<Pronouns> pronounsResult = profile.getPronouns();
            out.append(formatPropertyListing(
                    "pronouns",
                    pronounsResult.isPresent() ? "(pronouns are set)" : " - ", // TODO format gender
                    "Pronouns"));

            Optional<String> imageResult = profile.getImage();
            out.append(formatPropertyListing(
                    "image",
                    imageResult.isPresent() ? "on " + extractHost(imageResult.orElseThrow()) : " - ",
                    "Image"));

            Optional<String> websiteResult = profile.getWebsite();
            out.append(formatPropertyListing(
                    "website",
                    websiteResult.isPresent() ? "on " + extractHost(websiteResult.orElseThrow()) : " - ",
                    "Website"));

            out.append("\n")
                    .append(" **>>** Linked accounts")
                    .append("\n\n");

            Optional<String> instagramResult = profile.getLinkedInstagram();
            out.append(formatPropertyListing(
                    "instagram",
                    instagramResult.isPresent() ? instagramResult.orElseThrow() : " - ",
                    "Instagram account"));

            Optional<String> snapchatResult = profile.getLinkedSnapchat();
            out.append(formatPropertyListing(
                    "snapchat",
                    snapchatResult.isPresent() ? snapchatResult.orElseThrow() : " - ",
                    "Snapchat account"));

            Optional<String> deviantArtResult = profile.getLinkedDeviantArt();
            out.append(formatPropertyListing(
                    "deviantart",
                    deviantArtResult.isPresent() ? deviantArtResult.orElseThrow() : " - ",
                    "DeviantArt account"));

            Optional<String> gitHubResult = profile.getLinkedGitHub();
            out.append(formatPropertyListing(
                    "github",
                    gitHubResult.isPresent() ? gitHubResult.orElseThrow() : " - ",
                    "GitHub account"));

            Optional<String> rateYourMusicResult = profile.getLinkedRateYourMusic();
            out.append(formatPropertyListing(
                    "rym",
                    rateYourMusicResult.isPresent() ? rateYourMusicResult.orElseThrow() : " - ",
                    "RateYourMusic account"));

            Optional<String> letterboxdResult = profile.getLinkedLetterboxd();
            out.append(formatPropertyListing(
                    "letterboxd",
                    letterboxdResult.isPresent() ? letterboxdResult.orElseThrow() : " - ",
                    "Letterboxd account"));

            out.append("\n")
                    .append("`!pedit [property] (--reset) (value)`");

            //

            context.queueReply(out.toString());

            return;
        }

        //

        boolean reset = input.hasOption(OPT_RESET);
        String property = input.requireParameter(0, "property");

        if (property.equals("text")) {
            if (reset) {
                profile.setText(null);
                context.queueReply("**|** Your profile text was reset.");
            } else {
                String text = input.requireParameter(1, "text");
                if(text.length() > 200) throw simple("Text cannot be longer than `200` characters.");
                profile.setText(text);
                context.queueReply("**|** Your profile text was set.");
            }
            profile.persist();
        } else if (property.equals("birthday")) {
            if (reset) {
                profile.setText(null);
                context.queueReply("**|** Your birthday was reset.");
            } else {
                LocalDate birthday = input.requireParameterAs(1, "birthday", TimeParsers.parseLocalDate());
                profile.setBirthday(birthday);
                context.queueReply("**|** Your birthday was set to " + local.formatDate(birthday));
            }
            profile.persist();
        } else if (property.equals("pronouns")) {
            if (reset) {
                profile.setPronouns(null);
                context.queueReply("**|** Your pronouns were reset.");
            } else {
                String pronounsIn = input.requireParameter(1, "pronouns");
                Pronouns pronouns = null;
                for (String test : VALUES_PRONOUNS_MASCULINE)
                    if (test.equalsIgnoreCase(pronounsIn) && pronouns == null) pronouns = Pronouns.MASCULINE;
                for (String test : VALUES_PRONOUNS_FEMININE)
                    if (test.equalsIgnoreCase(pronounsIn) && pronouns == null) pronouns = Pronouns.FEMININE;
                for (String test : VALUES_PRONOUNS_INDEFINITE)
                    if (test.equalsIgnoreCase(pronounsIn) && pronouns == null) pronouns = Pronouns.INDEFINITE;
                if(pronouns == null) throw simple("Could not find any matching pronouns for `" + pronounsIn + "`!");
                profile.setPronouns(pronouns);
                context.queueReply("**|** Your pronouns were set.");
            }
            profile.persist();
        } else if (property.equals("image")) {
            if (reset) {
                profile.setImage(null);
                context.queueReply("**|** Your profile image was reset.");
            } else {
                String imageURL = input.requireParameter(1, "image URL");
                // TODO verify URL
                profile.setImage(imageURL);
                context.queueReply("**|** Your profile image was set.");
            }
            profile.persist();
        } else if (property.equals("website")) {
            if (reset) {
                profile.setWebsite(null);
                context.queueReply("**|** Your website was reset.");
            } else {
                String websiteURL = input.requireParameter(1, "website URL");
                // TODO verify URL
                profile.setWebsite(websiteURL);
                context.queueReply("**|** Your website was set.");
            }
            profile.persist();
        } else if (property.equals("instagram")) {
            if (reset) {
                profile.setLinkedInstagram(null);
                context.queueReply("**|** Your linked Instagram account was reset.");
            } else {
                String name = input.requireParameter(1, "name");
                // TODO verify name
                profile.setLinkedInstagram(name);
                context.queueReply("**|** Your linked Instagram account was set.");
            }
            profile.persist();
        } else if (property.equals("snapchat")) {
            if (reset) {
                profile.setLinkedSnapchat(null);
                context.queueReply("**|** Your linked Snapchat account was reset.");
            } else {
                String name = input.requireParameter(1, "name");
                // TODO verify name
                profile.setLinkedSnapchat(name);
                context.queueReply("**|** Your linked Snapchat account was set.");
            }
            profile.persist();
        } else if (property.equals("deviantart")) {
            if (reset) {
                profile.setLinkedDeviantArt(null);
                context.queueReply("**|** Your linked DeviantArt account was reset.");
            } else {
                String name = input.requireParameter(1, "name");
                // TODO verify name
                profile.setLinkedDeviantArt(name);
                context.queueReply("**|** Your linked DeviantArt account was set.");
            }
            profile.persist();
        } else if (property.equals("github")) {
            if (reset) {
                profile.setLinkedGitHub(null);
                context.queueReply("**|** Your linked GitHub account was reset.");
            } else {
                String name = input.requireParameter(1, "name");
                // TODO verify name
                profile.setLinkedGitHub(name);
                context.queueReply("**|** Your linked GitHub account was set.");
            }
            profile.persist();
        } else if (property.equals("rym")) {
            if (reset) {
                profile.setLinkedRateYourMusic(null);
                context.queueReply("**|** Your linked RateYourMusic account was reset.");
            } else {
                String name = input.requireParameter(1, "name");
                // TODO verify name
                profile.setLinkedRateYourMusic(name);
                context.queueReply("**|** Your linked RateYourMusic account was set.");
            }
            profile.persist();
        } else if (property.equals("letterboxd")) {
            if (reset) {
                profile.setLinkedLetterboxd(null);
                context.queueReply("**|** Your linked Letterboxd account was reset.");
            } else {
                String name = input.requireParameter(1, "name");
                // TODO verify name
                profile.setLinkedLetterboxd(name);
                context.queueReply("**|** Your linked Letterboxd account was set.");
            }
            profile.persist();
        } else throw simple("Unknown property `" + property + "`.");

    }

    // ===

    @Nonnull
    private static String formatPropertyListing(@Nonnull String property,
                                                @Nonnull String value,
                                                @Nonnull String description) {

        StringBuilder out = new StringBuilder();

        out.append("**`\u200b ").append(Strings.box(property, 12)).append(" \u200b`** ")
                .append("`\u200b ")
                .append(Strings.box(value, 30))
                .append("\u200b`")
                .append(" **|** ")
                .append(description)
                .append("\n");

        return out.toString();
    }

    @Nonnull
    public static String extractHost(@Nonnull String uri) {

        URI parsed;

        try {
            parsed = new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Can't find hostname for invalid URI \"" + uri + "\"", e);
        }

        return parsed.getHost();

    }
}
