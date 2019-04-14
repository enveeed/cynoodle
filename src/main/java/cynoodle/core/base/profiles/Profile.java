/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.profiles;

import com.google.common.base.Joiner;
import cynoodle.core.api.Strings;
import cynoodle.core.base.fm.FMModule;
import cynoodle.core.base.fm.FMPreferences;
import cynoodle.core.base.local.LocalModule;
import cynoodle.core.base.local.LocalPreferences;
import cynoodle.core.discord.DiscordPointer;
import cynoodle.core.discord.UEntity;
import cynoodle.core.entities.EIdentifier;
import cynoodle.core.module.Module;
import cynoodle.core.mongo.BsonDataException;
import cynoodle.core.mongo.fluent.FluentDocument;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.bson.BsonString;
import org.bson.BsonValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

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
     * The pronouns.
     */
    private Pronouns pronouns = null;

    /**
     * Image URL for the profile image.
     */
    private String image = null;

    /**
     * URL to a linked personal website.
     */
    private String website = null;

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

    /**
     * linked GitHub username / organization name.
     */
    private String linkedGitHub = null;

    /**
     * linked rate your music username.
     */
    private String linkedRateYourMusic = null;

    /**
     * linked letterboxd username.
     */
    private String linkedLetterboxd = null;

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
    public Optional<Pronouns> getPronouns() {
        return Optional.ofNullable(this.pronouns);
    }

    @Nonnull
    public Pronouns getEffectivePronouns() {
        return this.getPronouns().orElse(Pronouns.INDEFINITE);
    }

    public void setPronouns(@Nullable Pronouns pronouns) {
        this.pronouns = pronouns;
    }

    @Nonnull
    public Optional<String> getImage() {
        return Optional.ofNullable(this.image);
    }

    public void setImage(@Nullable String image) {
        this.image = image;
    }

    @Nonnull
    public Optional<String> getWebsite() {
        return Optional.ofNullable(website);
    }

    public void setWebsite(@Nullable String website) {
        this.website = website;
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

    public void setLinkedDeviantArt(@Nullable String linkedDeviantArt) {
        this.linkedDeviantArt = linkedDeviantArt;
    }

    @Nonnull
    public Optional<String> getLinkedGitHub() {
        return Optional.ofNullable(this.linkedGitHub);
    }

    public void setLinkedGitHub(@Nonnull String linkedGitHub) {
        this.linkedGitHub = linkedGitHub;
    }

    @Nonnull
    public Optional<String> getLinkedRateYourMusic() {
        return Optional.ofNullable(this.linkedRateYourMusic);
    }

    public void setLinkedRateYourMusic(@Nullable String linkedRateYourMusic) {
        this.linkedRateYourMusic = linkedRateYourMusic;
    }

    @Nonnull
    public Optional<String> getLinkedLetterboxd() {
        return Optional.ofNullable(this.linkedLetterboxd);
    }

    public void setLinkedLetterboxd(@Nullable String linkedLetterboxd) {
        this.linkedLetterboxd = linkedLetterboxd;
    }

    // ===

    @Override
    public void fromBson(@Nonnull FluentDocument source) throws BsonDataException {
        super.fromBson(source);

        this.text = source.getAt("text").asStringNullable().or(this.text);
        this.birthday = source.getAt("birthday").asNullable(toLocalDate()).or(this.birthday);
        this.pronouns = source.getAt("pronouns")
                .asNullable(value -> Pronouns.find(value.asString().getValue()).orElse(null)).or(this.pronouns);
        this.image = source.getAt("image").asStringNullable().or(this.image);
        this.website = source.getAt("website").asStringNullable().or(this.website);

        this.linkedInstagram = source.getAt("linked_instagram").asStringNullable().or(this.linkedInstagram);
        this.linkedSnapchat = source.getAt("linked_snapchat").asStringNullable().or(this.linkedSnapchat);
        this.linkedDeviantArt = source.getAt("linked_deviant_art").asStringNullable().or(this.linkedDeviantArt);
        this.linkedGitHub = source.getAt("linked_github").asStringNullable().or(this.linkedGitHub);
        this.linkedRateYourMusic = source.getAt("linked_rateyourmusic").asStringNullable().or(this.linkedRateYourMusic);
        this.linkedLetterboxd = source.getAt("linked_letterboxd").asStringNullable().or(this.linkedLetterboxd);
    }

    @Nonnull
    @Override
    public FluentDocument toBson() throws BsonDataException {
        FluentDocument data = super.toBson();

        data.setAt("text").asStringNullable().to(this.text);
        data.setAt("birthday").asNullable(fromLocalDate()).to(this.birthday);
        data.setAt("pronouns")
                .asNullable((Function<Pronouns, BsonValue>) pronouns -> new BsonString(pronouns.key())).to(this.pronouns);
        data.setAt("image").asStringNullable().to(this.image);
        data.setAt("website").asStringNullable().to(this.website);

        data.setAt("linked_instagram").asStringNullable().to(this.linkedInstagram);
        data.setAt("linked_snapchat").asStringNullable().to(this.linkedSnapchat);
        data.setAt("linked_deviant_art").asStringNullable().to(this.linkedDeviantArt);
        data.setAt("linked_github").asStringNullable().to(this.linkedGitHub);
        data.setAt("linked_rateyourmusic").asStringNullable().to(this.linkedRateYourMusic);
        data.setAt("linked_letterboxd").asStringNullable().to(this.linkedLetterboxd);

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

        LocalPreferences preferences = Module.get(LocalModule.class).getPreferencesManager().firstOrCreate(userP);

        // ===

        EmbedBuilder out = new EmbedBuilder();

        // === TITLE ===

        String title;

        if(member.getNickname() != null) title = "@" + user.getName() + " (" + member.getNickname() + ") ";
        else title = "@" + user.getName() + " ";

        out.setAuthor(title, null, user.getAvatarUrl());

        // === TOP ===

        StringBuilder topOut = new StringBuilder();
        // TEXT

        Optional<String> textResult = this.getText();
        if(textResult.isPresent()) {

            String text = textResult.orElseThrow();

            topOut.append(Strings.ZERO_WIDTH_WHITESPACE + "\n").append(text);
        }

        Optional<String> websiteResult = this.getWebsite();
        if(websiteResult.isPresent()) {

            String website = websiteResult.orElseThrow();

            URI uri;
            try {
                uri = new URI(website);
            } catch (URISyntaxException e) {
                // TODO report invalid website
                throw new IllegalStateException(e);
            }

            String host = uri.getHost();

            topOut.append("\n\n")
                    .append("[").append(host).append("](").append(uri.toString()).append(")");
        }

        // TOP / PERSONAL

        List<String> personalOut = new ArrayList<>();

        // BIRTHDAY / AGE

        Optional<LocalDate> birthdayResult = this.getBirthday();
        if(birthdayResult.isPresent()) {

            LocalDate now = LocalDate.now();
            LocalDate birthday = birthdayResult.orElseThrow();

            long age = ChronoUnit.YEARS.between(birthday, now);

            StringBuilder birthdayOut = new StringBuilder();

            birthdayOut.append("**").append(age).append("**").append(" ");

            if(birthday.getDayOfYear() == now.getDayOfYear())
                birthdayOut.append(EMBED_SEPARATOR).append("\uD83C\uDF82"); // the cake is (not) a lie.

            personalOut.add(birthdayOut.toString());
        }

        // PRONOUNS

        Optional<Pronouns> pronounsResult = this.getPronouns();
        if(pronounsResult.isPresent()) {

            String pronounsOut;

            Pronouns pronouns = pronounsResult.orElseThrow();

            if(pronouns == Pronouns.MASCULINE) pronounsOut = "he / him";
            else if(pronouns == Pronouns.FEMININE) pronounsOut = "she / her";
            else if(pronouns == Pronouns.INDEFINITE) pronounsOut = "they / them";
            else throw new IllegalStateException("Invalid Pronouns: " + pronouns);

            personalOut.add(pronounsOut);
        }

        // TIME

        Optional<ZoneId> timezoneResult = preferences.getTimezone();
        if(timezoneResult.isPresent()) {

            ZoneId timezone = timezoneResult.orElseThrow();
            ZonedDateTime time = Instant.now().atZone(timezone);

            String timeOut = String.format("`%02d:%02d %s`", time.getHour(), time.getMinute(),
                    timezone.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));

            personalOut.add(timeOut);
        }

        // BADGES

        // TODO automate badges (add to personalOut list)
        // personalOut.append(" **|** ").append("<:developer:551816523304534037>").append("<:patron:551824439558537236>");

        if(personalOut.size() > 0) {

            String personal = Joiner.on(" **|** ")
                    .join(personalOut);

            topOut.append("\n\n").append(personal);
        }

        // ===

        if(topOut.length() > 0) out.setDescription(topOut.toString());
        else out.setDescription("*alright then, keep your secrets ...*");

        // === LEFT ===

        StringBuilder leftOut = new StringBuilder();

        // FM

        FMModule fmModule = Module.get(FMModule.class);

        FMPreferences fmPreferences = fmModule.getPreferencesManager().firstOrCreate(userP);

        Optional<String> username = fmPreferences.getUsername();
        boolean profileEnabled = fmPreferences.isProfileEnabled();

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
        Optional<String> linkedGitHubResult = this.getLinkedGitHub();
        Optional<String> linkedRateYourMusicResult = this.getLinkedRateYourMusic();
        Optional<String> linkedLetterboxdResult = this.getLinkedLetterboxd();

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

        if(linkedGitHubResult.isPresent())
            leftOut.append("<:github:555819341766066207>")
                    .append(EMBED_SEPARATOR)
                    .append("[GitHub](https://github.com/")
                    .append(linkedGitHubResult.orElseThrow())
                    .append("/)\n");

        if(linkedRateYourMusicResult.isPresent())
            leftOut.append("<:rateyourmusic:561995619959439362>")
                    .append(EMBED_SEPARATOR)
                    .append("[RYM](https://rateyourmusic.com/~")
                    .append(linkedRateYourMusicResult.orElseThrow())
                    .append(")\n");

        if(linkedLetterboxdResult.isPresent())
            leftOut.append("<:letterboxd:561995619791798302>")
                    .append(EMBED_SEPARATOR)
                    .append("[Letterboxd](https://letterboxd.com/")
                    .append(linkedLetterboxdResult.orElseThrow())
                    .append(")\n");

        //

        if(leftOut.length() > 0) {
            out.addField("Links", leftOut.toString(), true);
        }

        // === RIGHT ===

        /*
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

        ModerationModule strikesModule = Module.get(ModerationModule.class);

        Map<Boolean, List<Strike>> strikes = strikesModule.getStrikeManager()
                .stream(Strike.filterMember(guildP, userP))
                .collect(Collectors.groupingBy(Strike::isEffective));

        long strikesEffective = strikes.get(true).size();
        long strikesTotal = strikesEffective + strikes.get(false).size();

        rightOut.append("**Strikes**").append(EMBED_SEPARATOR).append("`")
                .append(strikesEffective).append(" (").append(strikesTotal).append(")`");

        //

        if(rightOut.length() > 0) {
            out.addField("Local", rightOut.toString(), true);
        }*/

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
