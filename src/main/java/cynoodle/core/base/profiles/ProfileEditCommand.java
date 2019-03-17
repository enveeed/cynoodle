/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.profiles;

import cynoodle.core.api.Strings;
import cynoodle.core.api.text.Options;
import cynoodle.core.base.commands.*;
import cynoodle.core.base.local.LocalContext;
import cynoodle.core.discord.UEntityManager;
import cynoodle.core.module.Module;

import javax.annotation.Nonnull;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Optional;

@CIdentifier("base:profile:edit")
@CAliases({"profileedit", "pedit", "pe"})
public final class ProfileEditCommand extends Command {
    private ProfileEditCommand() {}

    private final static Options.Option OPT_RESET = Options.newFlagOption("reset", 'r');

    // ===

    private final ProfilesModule module = Module.get(ProfilesModule.class);

    // ===

    @Override
    protected void onInit() {
        this.options.addOptions(OPT_RESET);
    }

    // ===

    @Override
    protected void run(@Nonnull CommandContext context, @Nonnull CommandInput input, @Nonnull LocalContext local) throws Exception {

        UEntityManager<Profile> profileManager = module.getProfileManager();

        Profile profile = profileManager.firstOrCreate(context.getUserPointer());

        //

        if(!input.hasParameter(0)) {

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
                    birthdayResult.isPresent() ? local.formatDate(birthdayResult.orElseThrow()) : " - ", // TODO format birthday
                    "Birthday"));

            Optional<Gender> genderResult = profile.getGender();
            out.append(formatPropertyListing(
                    "gender",
                    genderResult.isPresent() ? "(gender / pronouns are set)" : " - ", // TODO format gender
                    "Preferred pronouns / gender"));

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

            //

            context.queueReply(out.toString());

            return;
        }

        //

        boolean reset = input.hasOption(OPT_RESET);
        String property = input.requireParameter(0, "property");

        if(property.equals("text")) {
            if(reset) {
                profile.setText(null);
                context.queueReply("**|** Your profile text was reset.");
            } else {
                String text = input.requireParameter(1, "text");
                profile.setText(text);
                context.queueReply("**|** Your profile text was set.");
            }
            profile.persist();
        } else if(property.equals("birthday")) {
            // TODO
        } else if(property.equals("gender")) {
            // TODO
        } else if(property.equals("image")) {
            if(reset) {
                profile.setImage(null);
                context.queueReply("**|** Your profile image was reset.");
            } else {
                String imageURL = input.requireParameter(1, "image URL");
                // TODO verify URL
                profile.setImage(imageURL);
                context.queueReply("**|** Your profile image was set.");
            }
            profile.persist();
        } else if(property.equals("website")) {
            if(reset) {
                profile.setWebsite(null);
                context.queueReply("**|** Your website was reset.");
            } else {
                String websiteURL = input.requireParameter(1, "website URL");
                // TODO verify URL
                profile.setWebsite(websiteURL);
                context.queueReply("**|** Your website was set.");
            }
            profile.persist();
        } else if(property.equals("instagram")) {
            if(reset) {
                profile.setLinkedInstagram(null);
                context.queueReply("**|** Your linked Instagram account was reset.");
            } else {
                String name = input.requireParameter(1, "name");
                // TODO verify name
                profile.setLinkedInstagram(name);
                context.queueReply("**|** Your linked Instagram account was set.");
            }
            profile.persist();
        } else if(property.equals("snapchat")) {
            if(reset) {
                profile.setLinkedSnapchat(null);
                context.queueReply("**|** Your linked Snapchat account was reset.");
            } else {
                String name = input.requireParameter(1, "name");
                // TODO verify name
                profile.setLinkedSnapchat(name);
                context.queueReply("**|** Your linked Snapchat account was set.");
            }
            profile.persist();
        } else if(property.equals("deviantart")) {
            if(reset) {
                profile.setLinkedDeviantArt(null);
                context.queueReply("**|** Your linked DeviantArt account was reset.");
            } else {
                String name = input.requireParameter(1, "name");
                // TODO verify name
                profile.setLinkedDeviantArt(name);
                context.queueReply("**|** Your linked DeviantArt account was set.");
            }
            profile.persist();
        } else if(property.equals("github")) {
            if(reset) {
                profile.setLinkedGitHub(null);
                context.queueReply("**|** Your linked GitHub account was reset.");
            } else {
                String name = input.requireParameter(1, "name");
                // TODO verify name
                profile.setLinkedGitHub(name);
                context.queueReply("**|** Your linked GitHub account was set.");
            }
            profile.persist();
        } else throw CommandErrors.simple(this, "Unknown property `" + property + "`.");

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
