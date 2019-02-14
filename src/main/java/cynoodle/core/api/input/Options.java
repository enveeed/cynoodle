package cynoodle.core.api.input;

import cynoodle.core.api.text.Parser;
import cynoodle.core.api.text.ParserException;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * An options parser similar to GNU getopt.
 * Supports short options, long options, blocks and character escaping.
 */
public final class Options implements Parser<Options.Result> {

    private final static char CHAR_SEPARATOR_DEFAULT = ' ';
    private final static char CHAR_OPTION_DEFAULT = '-';
    private final static char CHAR_ESCAPE_DEFAULT = '\\';
    private final static char CHAR_BLOCK_DEFAULT = '"';

    // ===

    private final char char_separator;
    private final char char_option;
    private final char char_escape;
    private final char char_block;

    // ===

    private final Set<Option> options;

    // ===

    private Options(char char_separator, char char_option, char char_escape, char char_block, @Nonnull Set<Option> options) {

        this.char_separator = char_separator;
        this.char_option = char_option;
        this.char_escape = char_escape;
        this.char_block = char_block;

        this.options = Collections.unmodifiableSet(options);
    }

    // ===

    public char getSeparatorChar() {
        return this.char_separator;
    }

    public char getOptionChar() {
        return this.char_option;
    }

    public char getEscapeChar() {
        return this.char_escape;
    }

    public char getBlockChar() {
        return this.char_block;
    }

    //

    @Nonnull
    public Set<Option> getOptions() {
        return this.options;
    }

    // ===

    @Override
    public Result parse(@Nonnull String input) throws ParserException {
        throw new UnsupportedOperationException(); // TODO
    }

    // ===

    /**
     * Defines an available option for parsing of {@link Options}.
     */
    public static final class Option {

        private final String  option_long;
        private final char    option_short;

        private final boolean requires_value;

        // ===

        private Option(@Nonnull String option_long, char option_short, boolean requires_value) {
            this.option_long = option_long;
            this.option_short = option_short;
            this.requires_value = requires_value;
        }

        // ===

        @Nonnull
        public String getLong() {
            return this.option_long;
        }

        public char getShort() {
            return this.option_short;
        }

        //

        public boolean isValueRequired() {
            return this.requires_value;
        }

    }

    // ===

    @Nonnull
    public static Option newValueOption(@Nonnull String option_long, char option_short) {
        return new Option(option_long, option_short, true);
    }

    @Nonnull
    public static Option newFlagOption(@Nonnull String option_long, char option_short) {
        return new Option(option_long, option_short, false);
    }

    // ===

    /**
     * Builder for creating options parser instances.
     */
    public static final class Builder {
        private Builder() {}

        private char char_separator = CHAR_SEPARATOR_DEFAULT;
        private char char_option = CHAR_OPTION_DEFAULT;
        private char char_escape = CHAR_ESCAPE_DEFAULT;
        private char char_block = CHAR_BLOCK_DEFAULT;

        private Set<Option> options = new HashSet<>();

        // ===

        @Nonnull
        public Builder setSeparatorChar(char char_separator) {
            this.char_separator = char_separator;
            return this;
        }

        @Nonnull
        public Builder setOptionChar(char char_option) {
            this.char_option = char_option;
            return this;
        }

        @Nonnull
        public Builder setEscapeChar(char char_escape) {
            this.char_escape = char_escape;
            return this;
        }

        @Nonnull
        public Builder setBlockChar(char char_block) {
            this.char_block = char_block;
            return this;
        }

        //

        @Nonnull
        public Builder addOptions(@Nonnull Option... options) {
            this.options.addAll(Arrays.asList(options));
            return this;
        }

        //

        @Nonnull
        public Options build() {
            return new Options(
                    this.char_separator,
                    this.char_option,
                    this.char_escape,
                    this.char_block,
                    this.options);
        }
    }

    //

    @Nonnull
    public static Builder newBuilder() {
        return new Builder();
    }

    // ===

    /**
     * Parsing result of the parsing of options.
     */
    public static final class Result {}

}
