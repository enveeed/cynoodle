package cynoodle.core.api.input;

import cynoodle.core.api.Checks;
import cynoodle.core.api.text.Parser;
import cynoodle.core.api.text.ParserException;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * An options parser similar to GNU getopt.
 * Supports short options, long options, options with values, block-based and character-based escaping.
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

    private final Map<String, Option> options_long;
    private final Map<Character, Option> options_short;

    // ===

    private Options(char char_separator, char char_option, char char_escape, char char_block, @Nonnull Set<Option> options) {

        this.char_separator = char_separator;
        this.char_option = char_option;
        this.char_escape = char_escape;
        this.char_block = char_block;

        this.options = Collections.unmodifiableSet(options);

        //

        this.options_long = this.options.stream().collect(Collectors.toMap(Option::getLong, option -> option));
        this.options_short = this.options.stream().collect(Collectors.toMap(Option::getShort, option -> option));
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

        ResultBuilder result = new ResultBuilder();

        Collector collector = new Collector();

        // 0 start or separator, 1 parameter, 2 oc1, 3 oc2, 4 lo, 5 so, 6 value
        int state = 0;
        // 0 nothing, 1 parameter, 2 short option, 3 long option, 4 value
        int drain = 0;

        boolean inEscape = false;
        boolean inBlock = false;

        // if an option which requires a value is encountered, this is this option
        // this is passed to state 6, the value parser
        Option value_option = null;

        for (int i = 0; i < input.length(); i++) {

            char character = input.charAt(i);

            // if this is the last character
            boolean last = i == input.length() - 1;

            // toggle block
            if (character == char_block) {
                inBlock = !inBlock;
                continue; // go to next
            }
            // set escape
            if (character == char_escape) {
                inEscape = true;
                continue; // go to next
            }

            // if this character is in a block or is escaped,
            // ignored (false) when last
            boolean be = (inBlock || inEscape) && !last;

            if (state == 0) {
                // start or separator
                if (character == char_separator) {
                    if (be) {
                        collector.append(character);
                        state = 1;
                    }
                } else if (character == char_option) {
                    if (be) {
                        collector.append(character);
                        state = 1;
                    } else state = 2;
                } else {
                    collector.append(character);
                    state = 1;
                    if(last) drain = 1;
                }
            } else if (state == 1) {
                // parameter
                if (character == char_separator || last) {
                    if (be) {
                        collector.append(character);
                        state = 1;
                    } else {
                        if (last) collector.append(character);
                        drain = 1;
                    }
                } else {
                    collector.append(character);
                    state = 1;
                }
            } else if (state == 2) {
                // option-char 1
                if (character == char_separator || last) {
                    if(last) {
                        collector.append(character);
                        drain = 2;
                    }
                    state = 0;
                } else if (character == char_option) {
                    state = 3;
                } else {
                    collector.append(character);
                    state = 5;
                }
            } else if (state == 3) {
                // option-char 2
                if (character == char_separator || last) {
                    state = 0;
                } else if (character == char_option) {
                    state = 3;
                } else {
                    collector.append(character);
                    state = 4;
                }
            } else if (state == 4) {
                // long option
                if (character == char_separator || last) {
                    if (last) collector.append(character);
                    drain = 3;
                } else {
                    collector.append(character);
                    state = 4;
                }
            } else if (state == 5) {
                // short option
                if (character == char_separator || last) {
                    if (last) collector.append(character);
                    drain = 2;
                } else if (character == char_option) {
                    state = 5;
                } else {
                    collector.append(character);
                    state = 5;
                }
            } else { // state == 6
                // value for an option

                // this shouldn't happen since all redirects to
                // this state set value_option beforehand
                if (value_option == null) throw new IllegalStateException();

                if (character == char_separator || last) {
                    if (be) {
                        collector.append(character);
                        state = 6;
                    } else {
                        if (last) collector.append(character);
                        drain = 4;
                    }
                } else {
                    collector.append(character);
                    state = 6;
                }
            }

            // drain if finished

            if(drain == 1) {

                result.addParameter(collector.drainString());
                state = 0;

            } else if(drain == 2) {

                char[] characters = collector.drainChars();

                for (int j = 0; j < characters.length; j++) {

                    char character_short = characters[j];
                    boolean last_short = j == characters.length - 1;

                    Option option = this.options_short.get(character_short);

                    if (option == null)
                        throw new ParserException("Unknown option: `" + this.char_option + character_short + "`");
                    else {
                        if (option.isValueRequired()) {
                            if (!last_short)
                                throw new ParserException("Options which require values must be the last element " +
                                        "in a option listing, if used in the short form: `" + this.char_option + character_short + "`");
                            else {
                                value_option = option;
                                state = 6;
                            }
                        } else {
                            result.addOption(option);
                            state = 0;
                        }
                    }
                }

            } else if(drain == 3) {

                String option_long = collector.drainString();
                Option option = this.options_long.get(option_long);

                if (option == null)
                    throw new ParserException("Unknown option: `" + this.char_option + this.char_option + option_long + "`");
                else {
                    if (option.isValueRequired()) {
                        value_option = option;
                        state = 6;
                    } else {
                        result.addOption(option);
                        state = 0;
                    }
                }

            } else if (drain == 4) {

                result.addOptionWithValue(value_option, collector.drainString());

                state = 0;
                value_option = null;
            }

            // reset drain
            drain = 0;

            // reset escape, as its only for a single character
            inEscape = false;

            // if value_option is set and the next state is not 6 (value for an option) or
            // if this was the last character
            // that means that the value was missing
            if (value_option != null && (state != 6 || last))
                throw new ParserException("Option requires value but there was no value given: `"
                        + this.char_option + this.char_option + value_option.getLong() + "`");
        }

        //

        return result.build();
    }

    // ===

    /**
     * Internal utility to collect characters while parsing.
     */
    private final static class Collector {

        private final StringBuilder builder = new StringBuilder();

        // ===

        /**
         * Append the character to this collector.
         *
         * @param character the character
         */
        void append(char character) {
            this.builder.append(character);
        }

        // ===

        /**
         * Clear this collector.
         */
        void clear() {
            this.builder.setLength(0);
        }

        // ===

        /**
         * Drain the collector to a String.
         *
         * @return the String
         */
        @Nonnull
        String drainString() {
            String value = this.builder.toString();
            this.clear();
            return value;
        }

        /**
         * Drain the collector to a char array.
         *
         * @return the char array
         */
        @Nonnull
        char[] drainChars() {
            char[] charArray = new char[this.builder.length()];
            this.builder.getChars(0, this.builder.length(), charArray, 0);
            this.clear();
            return charArray;
        }
    }

    // ===

    /**
     * Defines an available option for parsing of {@link Options}.
     */
    public static final class Option {

        private final String option_long;
        private final char option_short;

        private final boolean requires_value;

        // ===

        private Option(@Nonnull String option_long, char option_short, boolean requires_value) {
            this.option_long = option_long;
            this.option_short = option_short;
            this.requires_value = requires_value;
        }

        // ===

        /**
         * Get the long form of this option.
         *
         * @return the option long form
         */
        @Nonnull
        public String getLong() {
            return this.option_long;
        }

        /**
         * Get the short form of this option.
         *
         * @return the option short form
         */
        public char getShort() {
            return this.option_short;
        }

        //

        /**
         * Get if this option requires a value when used.
         *
         * @return true if it requires a value, false if not
         */
        public boolean isValueRequired() {
            return this.requires_value;
        }

        //

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Option option = (Option) o;

            if (option_short != option.option_short) return false;
            return option_long.equals(option.option_long);
        }

        @Override
        public int hashCode() {
            int result = option_long.hashCode();
            result = 31 * result + (int) option_short;
            return result;
        }
    }

    // ===

    @Nonnull
    public static Option newValueOption(@Nonnull String option_long, char option_short) {
        Checks.notEmpty(option_long);
        return new Option(option_long, option_short, true);
    }

    @Nonnull
    public static Option newFlagOption(@Nonnull String option_long, char option_short) {
        Checks.notEmpty(option_long);
        return new Option(option_long, option_short, false);
    }

    // ===

    /**
     * Builder for creating options parser instances.
     */
    public static final class Builder {
        private Builder() {
        }

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
    public static final class Result {

        private final Options source;

        // ===

        private final Parameters parameters;
        private final Set<Option> options;

        private final Map<Option, String> values;

        // ===

        private Result(@Nonnull Options options, @Nonnull ResultBuilder builder) {
            this.source = options;

            this.parameters = Parameters.of(builder.parameters);
            this.options = Collections.unmodifiableSet(builder.options);
            this.values = Collections.unmodifiableMap(builder.values);
        }

        // ===

        @Nonnull
        public Options getSource() {
            return this.source;
        }

        // ===

        @Nonnull
        public Parameters getParameters() {
            return this.parameters;
        }

        @Nonnull
        public Set<Option> getOptions() {
            return this.options;
        }

        // ===

        public boolean hasOption(@Nonnull Option option) {
            return this.options.contains(option);
        }

        @Nonnull
        public String getOptionValue(@Nonnull Option option) {

            if (!option.isValueRequired())
                throw new IllegalArgumentException("Option which do not require values can not have values!");
            if (!hasOption(option))
                throw new IllegalArgumentException("Option was not given!");

            else return this.values.get(option);
        }
    }

    //

    /**
     * Internal Builder for creating a parsing result.
     */
    private final class ResultBuilder {

        private final List<String> parameters = new ArrayList<>();
        private final Set<Option> options = new HashSet<>();

        private final Map<Option, String> values = new HashMap<>();

        // ===

        void addParameter(@Nonnull String parameter) {
            this.parameters.add(parameter);
        }

        void addOption(@Nonnull Option option) {
            this.options.add(option);
        }

        void addOptionWithValue(@Nonnull Option option, @Nonnull String value) {
            this.options.add(option);
            this.values.put(option, value);
        }

        // ===

        public Result build() {
            return new Result(Options.this, this);
        }
    }

}
