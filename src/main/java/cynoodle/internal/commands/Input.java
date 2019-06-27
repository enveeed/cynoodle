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

package cynoodle.test.commands;

import cynoodle.util.options.Option;
import cynoodle.util.options.OptionsResult;
import cynoodle.util.parsing.Parser;
import cynoodle.util.parsing.ParsingException;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Input for a command execution, within {@link Context}.
 */
public final class Input {

    private final OptionsResult result;

    // ===

    Input(@Nonnull OptionsResult result) {
        this.result = result;
    }

    // === OPTIONS ===

    public boolean hasOption(@Nonnull Option option) {
        return this.result.hasOption(option);
    }

    //

    @Nonnull
    public String getOptionValue(@Nonnull Option option)
            throws NoSuchElementException {
        return this.result.getOptionValue(option);
    }

    @Nonnull
    public <T> T getOptionValueAs(@Nonnull Option option, @Nonnull Parser<T> parser)
            throws NoSuchElementException, CommandException {
        String result = getOptionValue(option);
        try {
            return parser.parse(result);
        } catch (ParsingException e) {
            throw errorOptionValueParsingFailed(option, e.getMessage());
        }
    }

    // === PARAMETERS ===

    public boolean hasParameter(int index) {
        return index >= 0 && index < this.result.getParameters().size();
    }

    //

    @Nonnull
    public Optional<String> getParameter(int index) {
        return this.result.getParameters().get(index);
    }

    @Nonnull
    public <T> Optional<T> getParameterAs(int index, @Nonnull String name, @Nonnull Parser<T> parser)
            throws CommandException {
        Optional<String> result = getParameter(index);
        if(result.isPresent()) {
            try {
                return Optional.of(parser.parse(result.get()));
            } catch (ParsingException e) {
                throw errorParameterParsingFailed(name, e.getMessage());
            }
        }
        else return Optional.empty();
    }

    //

    @Nonnull
    public String requireParameter(int index, @Nonnull String name)
            throws CommandException {
        return getParameter(index).orElseThrow(() -> errorParameterMissing(name));
    }

    @Nonnull
    public <T> T requireParameterAs(int index, @Nonnull String name, @Nonnull Parser<T> parser)
            throws CommandException {
        return getParameterAs(index, name, parser).orElseThrow(() -> errorParameterMissing(name));
    }

    // === ERRORS ===

    private CommandException errorParameterMissing(@Nonnull String name) {
        return CommandError.newError(
                CommandError.DEFAULT,
                "Parameter `" + name + "` was missing but it is required!",
                "Missing Parameter"
        ).asException();
    }

    private CommandException errorParameterParsingFailed(@Nonnull String name, @Nonnull String message) {
        return CommandError.newError(
                CommandError.DEFAULT,
                "Parameter `" + name + "` could not be parsed: \n"
                + message,
                "Parameter Parsing Error"
        ).asException();
    }

    private CommandException errorOptionValueParsingFailed(@Nonnull Option option, @Nonnull String message) {
        return CommandError.newError(
                CommandError.DEFAULT,
                "The value of option `--" + option.getLong() + "` / `-" + option.getShort() + "` could not be parsed: \n"
                        + message,
                "Option Value Parsing Error"
        ).asException();
    }
}
