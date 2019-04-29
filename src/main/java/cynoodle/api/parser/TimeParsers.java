/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.api.parser;

import javax.annotation.Nonnull;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.zone.ZoneRulesException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Static utility for parsing of various types from <code>java.time</code>.
 */
public final class TimeParsers {
    private TimeParsers() {}

    // ===

    // NOTE: Those are units which have an "exact duration",
    // that means their duration is always equal.

    private static final String SYMBOL_UNIT_MILLIS      = "ms";
    private static final String SYMBOL_UNIT_SECONDS     = "s";
    private static final String SYMBOL_UNIT_MINUTES     = "m";
    private static final String SYMBOL_UNIT_HOURS       = "h";
    private static final String SYMBOL_UNIT_DAYS        = "d";

    // matches (possibly negative) integer number followed by string (possibly with a space)
    private static final Pattern PATTERN_DURATION =
            Pattern.compile("(-?\\d+)\\s?([a-z]+)");

    // matches 01.01.1970 or 01/01/1970
    private static final Pattern PATTERN_DATE =
            Pattern.compile("(\\d{1,2})[/.](\\d{1,2})[/.](\\d{4})");
    // matches 01.01.1970-23:59:59 or 01/01/1970-23:59:59, seconds optional
    private static final Pattern PATTERN_DATE_TIME =
            Pattern.compile("(\\d{1,2})[/.](\\d{1,2})[/.](\\d{4})-(\\d{1,2}):(\\d{1,2})(?::(\\d{1,2}))?");
    // matches 01.01.1970-23:59:59-UTC or 01/01/1970-23:59:59-America/New_York, seconds optional
    private static final Pattern PATTERN_DATE_TIME_ZONE =
            Pattern.compile("(\\d{1,2})[/.](\\d{1,2})[/.](\\d{4})-(\\d{1,2}):(\\d{1,2})(?::(\\d{1,2}))?-([A-Za-z/_]{2,})");

    // ===

    @Nonnull
    public static Parser<ZonedDateTime> parseZonedDateTime() {
        return input -> {

            Matcher matcher = PATTERN_DATE_TIME_ZONE.matcher(input);

            if(matcher.matches()) {

                int day         = PrimitiveParsers.parseInteger().parse(matcher.group(1));
                int month       = PrimitiveParsers.parseInteger().parse(matcher.group(2));
                int year        = PrimitiveParsers.parseInteger().parse(matcher.group(3));

                int hour        = PrimitiveParsers.parseInteger().parse(matcher.group(4));
                int minute      = PrimitiveParsers.parseInteger().parse(matcher.group(5));
                int second      = matcher.group(6).isEmpty() ? 0 :
                        PrimitiveParsers.parseInteger().parse(matcher.group(6));

                ZoneId zone;

                try {
                    zone = ZoneId.of(matcher.group(7));
                } catch (DateTimeException e) {
                    if(e instanceof ZoneRulesException)
                        throw new ParsingException("Timezone not found: `" + matcher.group(7) + "`");
                    else
                        throw new ParsingException("Invalid timezone format: `" + matcher.group(7) + "`");
                }

                try {
                    return ZonedDateTime.of(year, month, day, hour, minute, second, 0, zone);
                } catch (DateTimeException e) {
                    throw new ParsingException("Invalid date time with timezone: `" + input + "`! " + e.getMessage());
                }
            }
            else throw new ParsingException("Invalid date time with timezone: `" + input + "`!" +
                    " Try e.g. `01/01/1970-23:59:59-America/New_York`.");
        };
    }

    @Nonnull
    public static Parser<ZonedDateTime> parseZonedDateTime(@Nonnull ZoneId fallback) {
        try {
            return parseZonedDateTime();
        } catch (DateTimeException e) {
            return input -> parseLocalDateTime().parse(input).atZone(fallback);
        }
    }

    @Nonnull
    public static Parser<LocalDateTime> parseLocalDateTime() {
        return input -> {

            Matcher matcher = PATTERN_DATE_TIME.matcher(input);

            if(matcher.matches()) {

                int day         = PrimitiveParsers.parseInteger().parse(matcher.group(1));
                int month       = PrimitiveParsers.parseInteger().parse(matcher.group(2));
                int year        = PrimitiveParsers.parseInteger().parse(matcher.group(3));

                int hour        = PrimitiveParsers.parseInteger().parse(matcher.group(4));
                int minute      = PrimitiveParsers.parseInteger().parse(matcher.group(5));
                int second      = matcher.group(6).isEmpty() ? 0 :
                        PrimitiveParsers.parseInteger().parse(matcher.group(6));

                try {
                    return LocalDateTime.of(year, month, day, hour, minute, second, 0);
                } catch (Exception e) {
                    throw new ParsingException("Invalid date time: `" + input + "`! " + e.getMessage());
                }
            }
            else throw new ParsingException("Invalid date time: `" + input + "`!" +
                    " Try e.g. `01/01/1970-23:59:59`.");

        };
    }

    @Nonnull
    public static Parser<LocalDate> parseLocalDate() {
        return input -> {
            Matcher matcher = PATTERN_DATE.matcher(input);

            if(matcher.matches()) {

                int day         = PrimitiveParsers.parseInteger().parse(matcher.group(1));
                int month       = PrimitiveParsers.parseInteger().parse(matcher.group(2));
                int year        = PrimitiveParsers.parseInteger().parse(matcher.group(3));

                try {
                    return LocalDate.of(year, month, day);
                } catch (Exception e) {
                    throw new ParsingException("Invalid date: `" + input + "`! " + e.getMessage());
                }
            }
            else throw new ParsingException("Invalid date: `" + input + "`!" +
                    " Try e.g. `01/01/1970`.");
        };
    }

    @Nonnull
    public static Parser<LocalTime> parseLocalTime() {
        return input -> {
            throw new UnsupportedOperationException("TODO"); // TODO
        };
    }

    //

    @Nonnull
    public static Parser<Instant> parseInstant() {
        return input -> parseZonedDateTime().parse(input).toInstant();
    }

    @Nonnull
    public static Parser<Instant> parseInstant(@Nonnull ZoneId fallback) {
        return input -> parseZonedDateTime(fallback).parse(input).toInstant();
    }

    //

    @Nonnull
    public static Parser<Duration> parseDuration() {
        return input -> {

            Matcher matcher = PATTERN_DURATION.matcher(input);

            if(!matcher.matches())
                throw new ParsingException("Invalid format, expected e.g. `236ms`");

            String inAmount = matcher.group(1);
            String inUnit = matcher.group(2);

            long amount = PrimitiveParsers.parseLong().parse(inAmount);
            ChronoUnit unit = findUnit(inUnit);

            return Duration.of(amount, unit);
        };
    }

    // ===

    /**
     * Find one of the supported {@link ChronoUnit ChronoUnits} by symbol.
     * @param symbol the symbol
     * @return the found unit
     * @throws ParsingException if the symbol did not match any unit
     */
    @Nonnull
    private static ChronoUnit findUnit(@Nonnull String symbol) throws ParsingException {
        switch (symbol) {
            case SYMBOL_UNIT_MILLIS: return ChronoUnit.MILLIS;
            case SYMBOL_UNIT_SECONDS: return ChronoUnit.SECONDS;
            case SYMBOL_UNIT_MINUTES: return ChronoUnit.MINUTES;
            case SYMBOL_UNIT_HOURS: return ChronoUnit.HOURS;
            case SYMBOL_UNIT_DAYS: return ChronoUnit.DAYS;
            default: throw new ParsingException("Unknown temporal unit symbol: `" + symbol + "`");
        }
    }
}
