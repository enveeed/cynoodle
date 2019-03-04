/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.profile;

import cynoodle.core.api.Numbers;
import cynoodle.core.api.Strings;
import cynoodle.core.base.fm.FM;
import cynoodle.core.base.fm.FMModule;
import cynoodle.core.base.localization.Localization;
import cynoodle.core.base.localization.LocalizationModule;
import cynoodle.core.base.strikes.Strike;
import cynoodle.core.base.strikes.StrikesModule;
import cynoodle.core.base.xp.LeaderBoard;
import cynoodle.core.base.xp.XP;
import cynoodle.core.base.xp.XPFormula;
import cynoodle.core.base.xp.XPModule;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.UEntity;
import cynoodle.core.entities.EIdentifier;
import cynoodle.core.module.Module;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentDocument;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static cynoodle.core.mongo.fluent.FluentValues.fromLocalDate;
import static cynoodle.core.mongo.fluent.FluentValues.toLocalDate;

/**
 * A user profile, which contains user properties for different purposes.
 */
@EIdentifier("base:profile:profile")
public final class Profile extends UEntity {
    private Profile() {}

    // ===

    /**
     * The profile text / bio / description.
     */
    private String text = null;

    /**
     * The birthday.
     */
    private LocalDate birthday = null;

    /**
     * The gender.
     */
    private Gender gender = null;

    /**
     * Image URL for the profile image.
     */
    private String image = null;

    //

    /**
     * linked Instagram username.
     */
    private String linkedInstagram = null;

    /**
     * linked Snapchat username.
     */
    private String linkedSnapchat = null;

    /**
     * linked Deviant Art username.
     */
    private String linkedDeviantArt = null;

    // ===

    @Nonnull
    public Optional<String> getText() {
        return Optional.ofNullable(this.text);
    }

    public void setText(@Nullable String text) {
        this.text = text;
    }

    @Nonnull
    public Optional<LocalDate> getBirthday() {
        return Optional.ofNullable(birthday);
    }

    public void setBirthday(@Nullable LocalDate birthday) {
        this.birthday = birthday;
    }

    @Nonnull
    public Optional<Gender> getGender() {
        return Optional.ofNullable(this.gender);
    }

    public void setGender(@Nullable Gender gender) {
        this.gender = gender;
    }

    @Nonnull
    public Optional<String> getImage() {
        return Optional.ofNullable(this.image);
    }

    public void setImage(@Nullable String image) {
        this.image = image;
    }

    //

    @Nonnull
    public Optional<String> getLinkedInstagram() {
        return Optional.ofNullable(linkedInstagram);
    }

    public void setLinkedInstagram(@Nullable String linkedInstagram) {
        this.linkedInstagram = linkedInstagram;
    }

    @Nonnull
    public Optional<String> getLinkedSnapchat() {
        return Optional.ofNullable(linkedSnapchat);
    }

    public void setLinkedSnapchat(@Nullable String linkedSnapchat) {
        this.linkedSnapchat = linkedSnapchat;
    }

    @Nonnull
    public Optional<String> getLinkedDeviantArt() {
        return Optional.ofNullable(linkedDeviantArt);
    }

    public void setLinkedDeviantart(@Nullable String linkedDeviantArt) {
        this.linkedDeviantArt = linkedDeviantArt;
    }


    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.text = source.getAt("text").asStringNullable().or(this.text);
        this.birthday = source.getAt("birthday").asNullable(toLocalDate()).or(this.birthday);
        this.gender = source.getAt("gender").asNullable(Gender.fromBson()).or(this.gender);
        this.image = source.getAt("image").asStringNullable().or(this.image);

        this.linkedInstagram = source.getAt("linked_instagram").asStringNullable().or(this.linkedInstagram);
        this.linkedSnapchat = source.getAt("linked_snapchat").asStringNullable().or(this.linkedSnapchat);
        this.linkedDeviantArt = source.getAt("linked_deviant_art").asStringNullable().or(this.linkedDeviantArt);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("text").asStringNullable().to(this.text);
        data.setAt("birthday").asNullable(fromLocalDate()).to(this.birthday);
        data.setAt("gender").asNullable(Gender.toBson()).to(this.gender);
        data.setAt("image").asStringNullable().to(this.image);

        data.setAt("linked_instagram").asStringNullable().to(this.linkedInstagram);
        data.setAt("linked_snapchat").asStringNullable().to(this.linkedSnapchat);
        data.setAt("linked_deviant_art").asStringNullable().to(this.linkedDeviantArt);

        return data;
    }

    // ===

    private static final String EMBED_SEPARATOR = Strings.chain(Strings.NON_BREAKING_WHITESPACE, 3);

    /**
     * Generate the profile embed for this Profile.
     * @param guildP the guild which requested the embed
     * @return the profile embed
     */
    @Nonnull
    public MessageEmbed createEmbed(@Nonnull DiscordPointer guildP) {

        DiscordPointer userP = requireUser();

        // === DISCORD ===

        User user = userP.asUser().orElseThrow(() -> new IllegalStateException("User does not exist."));
        Guild guild = guildP.asGuild().orElseThrow(() -> new IllegalStateException("Guild does not exist."));

        Member member = guild.getMember(user);

        if(member == null) throw new IllegalStateException("User is not a Member of the Guild!");

        // === DATA ===

        Localization localization = Module.get(LocalizationModule.class).getLocalizationManager().firstOrCreate(userP);

        // ===

        EmbedBuilder out = new EmbedBuilder();

        // === TITLE ===

        String title;

        if(member.getNickname() != null) title = "@" + user.getName() + " (" + member.getNickname() + ") ";
        else title = "@" + user.getName() + " ";

        out.setAuthor(title, null, user.getAvatarUrl());

        // === DESC ===

        StringBuilder descOut = new StringBuilder();

        // === TOP ===

        // TEXT

        Optional<String> textResult = this.getText();
        if(textResult.isPresent()) {

            String text = textResult.orElseThrow();

            descOut.append(Strings.ZERO_WIDTH_WHITESPACE + "\n").append(text);
        }

        StringBuilder personalOut = new StringBuilder();

        // BIRTHDAY / AGE

        Optional<LocalDate> birthdayResult = this.getBirthday();
        if(birthdayResult.isPresent()) {

            LocalDate now = LocalDate.now();
            LocalDate birthday = birthdayResult.orElseThrow();

            long age = ChronoUnit.YEARS.between(birthday, now);

            personalOut.append("**").append(age).append("**").append(" ");

            if(birthday.getDayOfYear() == now.getDayOfYear())
                personalOut.append(EMBED_SEPARATOR).append("\uD83C\uDF82"); // the cake is a lie.
        }

        // GENDER / ADDRESS AS

        Optional<Gender> genderResult = this.getGender();
        if(genderResult.isPresent()) {

            String genderOut;

            Gender gender = genderResult.orElseThrow();
            Gender.AddressAs addressAs = gender.getAddressAs();

            if(addressAs == Gender.AddressAs.MALE) genderOut = "he / him";
            else if(addressAs == Gender.AddressAs.FEMALE) genderOut = "she / her";
            else if(addressAs == Gender.AddressAs.NEUTRAL) genderOut = "they / them";
            else throw new IllegalStateException("Illegal Gender.AddressAs: " + addressAs);

            personalOut.append(" **|** ").append(genderOut);
        }

        // TIME

        Optional<ZoneId> timezoneResult = localization.getTimezone();
        if(timezoneResult.isPresent()) {

            ZoneId timezone = timezoneResult.orElseThrow();
            ZonedDateTime time = Instant.now().atZone(timezone);

            String timeOut = String.format("%02d:%02d %s", time.getHour(), time.getMinute(),
                    timezone.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));

            personalOut.append(" **|** `").append(timeOut).append("`");
        }

        // BADGES

        // TODO automate badges
        // personalOut.append(" **|** ").append("<:developer:551816523304534037>").append("<:patron:551824439558537236>");

        descOut.append("\n\n").append(personalOut.toString());

        // ===

        out.setDescription(descOut.toString());

        // === LEFT ===

        StringBuilder leftOut = new StringBuilder();

        // FM

        FMModule fmModule = Module.get(FMModule.class);

        FM fm = fmModule.getFMManager().firstOrCreate(userP);

        Optional<String> username = fm.getUsername();
        boolean profileEnabled = fm.isProfileEnabled();

        if(username.isPresent() && profileEnabled) {
            leftOut.append("<:lastfm:551807297316192277>")
                    .append(EMBED_SEPARATOR)
                    .append("[last.fm](https://last.fm/user/")
                    .append(username.orElseThrow())
                    .append("/)\n");
        }

        // LINKED

        Optional<String> linkedInstagramResult = this.getLinkedInstagram();
        Optional<String> linkedSnapchatResult = this.getLinkedSnapchat();
        Optional<String> linkedDeviantArtResult = this.getLinkedDeviantArt();

        if(linkedInstagramResult.isPresent())
            leftOut.append("<:instagram:551807327653724160>")
                    .append(EMBED_SEPARATOR)
                    .append("[Instagram](https://instagram.com/")
                    .append(linkedInstagramResult.orElseThrow())
                    .append("/)\n");

        if(linkedSnapchatResult.isPresent())
            leftOut.append("<:snapchat:551807317956362256>")
                    .append(EMBED_SEPARATOR)
                    .append("[Snapchat](https://snapchat.com/add/")
                    .append(linkedSnapchatResult.orElseThrow())
                    .append("/)\n");

        if(linkedDeviantArtResult.isPresent())
            leftOut.append("<:deviantart:551807307193778177>")
                    .append(EMBED_SEPARATOR)
                    .append("[Deviant Art](https://deviantart.com/")
                    .append(linkedDeviantArtResult.orElseThrow())
                    .append("/)\n");

        //

        if(leftOut.length() > 0) {
            out.addField("Links", leftOut.toString(), true);
        }

        // === RIGHT ===

        StringBuilder rightOut = new StringBuilder();

        // XP

        XPModule xpModule = Module.get(XPModule.class);
        XPFormula formula = xpModule.getFormula();

        XP xp = xpModule.getXPManager().firstOrCreate(guildP, userP);

        StringBuilder xpOut = new StringBuilder();

        long xpVal = xp.get();
        String xpValOut;
        if(xpVal > 1000000L){
            double num = xpVal / 1000000d;
            xpValOut = Numbers.format(num, 1) + "M";
        } else if(xpVal > 1000L){
            double num = xpVal / 1000d;
            xpValOut = Numbers.format(num,1) + "k";
        } else xpValOut = Numbers.format(xpVal);

        xpOut.append("**XP**").append(EMBED_SEPARATOR).append("`").append(xpValOut)
                .append("` **|** **`").append(formula.getReachedLevel(xpVal))
                .append("`** ");

        Optional<LeaderBoard> leaderBoardResult = xpModule.getLeaderBoardManager().get(guildP);
        if(leaderBoardResult.isPresent()) {
            LeaderBoard leaderBoard = leaderBoardResult.get();
            Optional<LeaderBoard.Entry> userResult = leaderBoard.findByMember(userP);
            if(userResult.isPresent()) {
                LeaderBoard.Entry entry = userResult.orElseThrow();
                xpOut.append("**|** `# ").append(entry.getRank()).append("`");
            }
        }

        rightOut.append(xpOut.toString()).append("\n");

        // STRIKES

        StrikesModule strikesModule = Module.get(StrikesModule.class);

        Map<Boolean, List<Strike>> strikes = strikesModule.getStrikes()
                .stream(Strike.filterMember(guildP, userP))
                .collect(Collectors.groupingBy(Strike::isEffective));

        long strikesEffective = strikes.get(true).size();
        long strikesTotal = strikesEffective + strikes.get(false).size();

        rightOut.append("**Strikes**").append(EMBED_SEPARATOR).append("`")
                .append(strikesEffective).append(" (").append(strikesTotal).append(")`");

        //

        if(rightOut.length() > 0) {
            out.addField("Local", rightOut.toString(), true);
        }

        // === IMAGE ===

        Optional<String> imageResult = this.getImage();
        if(imageResult.isPresent()) {

            String imageURL = imageResult.orElseThrow();
            // TODO URL validation

            out.setImage(imageURL);
        }

        // ===

        return out.build();
    }
}
