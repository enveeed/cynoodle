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

package cynoodle.util.options;

import cynoodle.util.parsing.ParsingException;
import cynoodle.util.text.Parameters;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * An options parser similar to GNU getopt.
 * Supports short options, long options, options with values, block-based and character-based escaping.
 */
public final class Options {

    private final static char CHAR_SEPARATOR_DEFAULT = ' ';
    private final static char CHAR_OPTION_DEFAULT = '-';
    private final static char CHAR_ESCAPE_DEFAULT = '\\';
    private final static char CHAR_BLOCK_DEFAULT = '"';

    // ===

    private final char char_separator;
    private final char char_option;
    private final char char_escape;
    private final char char_block;

    private final boolean ignoreUnknownOptions;

    // ===

    private final Set<Option> options;

    private final Map<String, Option> options_long;
    private final Map<Character, Option> options_short;

    // ===

    private Options(char char_separator, char char_option, char char_escape, char char_block, boolean ignoreUnknownOptions,
                    @Nonnull Set<Option> options) {

        this.char_separator = char_separator;
        this.char_option = char_option;
        this.char_escape = char_escape;
        this.char_block = char_block;

        this.ignoreUnknownOptions = ignoreUnknownOptions;

        this.options = Collections.unmodifiableSet(options);

        //

        this.options_long = this.options.stream()
                .collect(Collectors.toMap(Option::getLong, option -> option));
        this.options_short = this.options.stream()
                .filter(option -> option.getShort() != null) // dont map null short options
                .collect(Collectors.toMap(Option::getShort, option -> option));
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

    /**
     * Parse the given input string to a result using these options.
     * @param input the input string
     * @return the parsing result
     * @throws OptionsException if there were any issues encountered while parsing
     */
    @Nonnull
    public OptionsResult parse(@Nonnull String input) throws OptionsException {

        ResultBuilder result = new ResultBuilder(input);

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

            // this (last) character is an escape or block character
            boolean isEscapeOrBlockChar = false;

            // toggle block (not if escaped)
            if (character == char_block && !inEscape) {
                inBlock = !inBlock;
                if (!last) continue; // go to next (if not last one)
                isEscapeOrBlockChar = true;
            }
            // set escape (not if escaped)
            if (character == char_escape && !inEscape) {
                inEscape = true;
                if (!last) continue; // go to next (if not last one)
                isEscapeOrBlockChar = true;
            }

            // if this character is in a block or is escaped,
            // ignored (false) when last
            boolean isEscapedOrInBlock = (inBlock || inEscape) && !last;

            if (state == 0) {
                // start or separator
                if (character == char_separator) {
                    if (isEscapedOrInBlock) {
                        collector.append(character);
                        state = 1;
                    }
                } else if (character == char_option) {
                    if (isEscapedOrInBlock) {
                        collector.append(character);
                        state = 1;
                    } else state = 2;
                } else {
                    if (!isEscapeOrBlockChar) collector.append(character);
                    state = 1;
                    if (last) drain = 1;
                }
            } else if (state == 1) {
                // parameter
                if (character == char_separator || last) {
                    if (isEscapedOrInBlock) {
                        collector.append(character);
                        state = 1;
                    } else {
                        if (last && !isEscapeOrBlockChar) collector.append(character);
                        drain = 1;
                    }
                } else {
                    collector.append(character);
                    state = 1;
                }
            } else if (state == 2) {
                // option-char 1
                if (character == char_separator || last) {
                    if (last) {
                        if (!isEscapeOrBlockChar) collector.append(character);
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
                    if (last) {
                        if (!isEscapeOrBlockChar) collector.append(character);
                        drain = 3;
                    }
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
                    if (last && !isEscapeOrBlockChar) collector.append(character);
                    drain = 3;
                } else {
                    collector.append(character);
                    state = 4;
                }
            } else if (state == 5) {
                // short option
                if (character == char_separator || last) {
                    if (last && !isEscapeOrBlockChar) collector.append(character);
                    drain = 2;
                } else if (character == char_option) {
                    state = 5;
                } else {
                    collector.append(character);
                    state = 5;
                }
            } else {
                // value for an option

                // this shouldn't happen since all redirects to
                // this state set value_option beforehand
                if (value_option == null) throw new IllegalStateException();

                if (character == char_separator || last) {
                    if (isEscapedOrInBlock) {
                        collector.append(character);
                        state = 6;
                    } else {
                        if (last && !isEscapeOrBlockChar) collector.append(character);
                        drain = 4;
                    }
                } else {
                    collector.append(character);
                    state = 6;
                }
            }

            // drain if finished

            if (drain == 1) {

                result.addParameter(collector.drainString());
                state = 0;

            } else if (drain == 2) {

                char[] characters = collector.drainChars();

                for (int j = 0; j < characters.length; j++) {

                    char character_short = characters[j];
                    boolean last_short = j == characters.length - 1;

                    Option option = this.options_short.get(character_short);

                    if (option == null)
                        if (ignoreUnknownOptions) state = 0;
                        else
                            throw new OptionUnknownException("Unknown option: `" + this.char_option + character_short + "`");
                    else {
                        if (option.isValueRequired()) {
                            if (!last_short)
                                throw new OptionsSyntaxException("Options which require values must be the last element " +
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

            } else if (drain == 3) {

                String option_long = collector.drainString();
                Option option = this.options_long.get(option_long);

                if (option == null)
                    if (ignoreUnknownOptions) state = 0;
                    else
                        throw new OptionUnknownException("Unknown option: `" + this.char_option + this.char_option + option_long + "`");
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
                throw new OptionValueMissingException("Option requires value but there was no value given: `"
                        + this.char_option + this.char_option + value_option.getLong() + "`", value_option);
        }

        //

        return result.build();
    }

    //

    @Nonnull
    public OptionsResult parse(@Nonnull Parameters parameters) throws ParsingException {
        return parse(parameters.join());
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
     * Builder for creating options parser instances.
     */
    public static final class Builder {
        private Builder() {
        }

        private char char_separator = CHAR_SEPARATOR_DEFAULT;
        private char char_option = CHAR_OPTION_DEFAULT;
        private char char_escape = CHAR_ESCAPE_DEFAULT;
        private char char_block = CHAR_BLOCK_DEFAULT;

        private boolean ignoreUnknownOptions = false;

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
        public Builder setIgnoreUnknownOptions(boolean ignoreUnknownOptions) {
            this.ignoreUnknownOptions = ignoreUnknownOptions;
            return this;
        }

        //

        @Nonnull
        public Builder add(@Nonnull Option... options) {
            this.options.addAll(Arrays.asList(options));
            return this;
        }

        @Nonnull
        public Builder add(@Nonnull Collection<Option> options) {
            this.options.addAll(options);
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
                    this.ignoreUnknownOptions,
                    this.options);
        }
    }

    //

    @Nonnull
    public static Builder newBuilder() {
        return new Builder();
    }

    //

    /**
     * Internal Builder for creating a parsing result.
     */
    final class ResultBuilder {

        private final String source;

        private final List<String> parameters = new ArrayList<>();
        private final List<Option> options = new ArrayList<>();

        private final Map<Option, String> optionsValues = new HashMap<>();

        // ===

        private ResultBuilder(String source) {
            this.source = source;
        }

        // ===

        void addParameter(@Nonnull String parameter) {
            this.parameters.add(parameter);
        }

        void addOption(@Nonnull Option option) {
            this.options.add(option);
        }

        void addOptionWithValue(@Nonnull Option option, @Nonnull String value) {
            this.options.add(option);
            this.optionsValues.put(option, value);
        }

        // ===

        public OptionsResult build() {
            return new OptionsResult(Options.this,
                    this.source,
                    this.parameters,
                    this.options,
                    this.optionsValues);
        }
    }

}
