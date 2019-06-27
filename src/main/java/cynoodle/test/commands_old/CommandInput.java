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

package cynoodle.base.commands;

import cynoodle.util.options.Option;
import cynoodle.util.options.OptionsResult;
import cynoodle.util.parser.Parser;
import cynoodle.util.parser.ParsingException;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

public final class CommandInput {

    private final Command command;
    private final OptionsResult result;

    // ===

    private CommandInput(@Nonnull Command command, @Nonnull OptionsResult result) {
        this.command = command;
        this.result = result;
    }

    // ===

    @Nonnull
    static CommandInput wrap(@Nonnull Command command, @Nonnull OptionsResult result) {
        return new CommandInput(command, result);
    }

    // ===

    @Nonnull
    public OptionsResult raw() {
        return this.result;
    }

    // === PARAMETERS ===

    public boolean hasParameter(int index) {
        return this.result.getParameters()
                .has(index);
    }

    //

    @Nonnull
    public Optional<String> getParameter(int index) {
        return this.result.getParameters()
                .get(index);
    }

    @Nonnull
    @Deprecated
    public <T> Optional<T> getParameterAs(int index, @Nonnull String name, @Nonnull Function<String, T> parser) throws CommandError {
        Optional<String> parameter = this.result.getParameters().get(index);
        if(parameter.isEmpty()) return Optional.empty();
        else {
            String content = parameter.orElseThrow();
            T parsed;

            try {
                parsed = parser.apply(content);
            } catch (ParsingException ex) {
                throw CommandErrors.parameterParsingFailed(name, ex);
            }

            return Optional.of(parsed);
        }
    }

    @Nonnull
    public <T> Optional<T> getParameterAs(int index, @Nonnull String name, @Nonnull Parser<T> parser) throws CommandError {
        return getParameterAs(index, name, parser.asFunction());
    }

    //

    @Nonnull
    public String requireParameter(int index, @Nonnull String name) throws CommandError {
        return getParameter(index).orElseThrow(() -> CommandErrors.parameterMissing(name));
    }

    @Nonnull
    @Deprecated
    public <T> T requireParameterAs(int index, @Nonnull String name, @Nonnull Function<String, T> parser) throws CommandError {
        return getParameterAs(index, name, parser).orElseThrow(() -> CommandErrors.parameterMissing(name));
    }

    @Nonnull
    public <T> T requireParameterAs(int index, @Nonnull String name, @Nonnull Parser<T> parser) throws CommandError {
        return requireParameterAs(index, name, parser.asFunction());
    }

    // === OPTIONS ===

    public boolean hasOption(@Nonnull Option option) {
        return this.result.hasOption(option);
    }

    //

    @Nonnull
    public String getOptionValue(@Nonnull Option option) throws NoSuchElementException {
        if(!hasOption(option))
            throw new NoSuchElementException("No such option: " + option.getLong());
        return this.result.getOptionValue(option);
    }

    @Nonnull
    public <T> T getOptionValueAs(@Nonnull Option option, @Nonnull Parser<T> parser) throws NoSuchElementException, CommandError {
        String value = getOptionValue(option);
        T parsed;
        try {
            parsed = parser.parse(value);
        } catch (ParsingException ex) {
            throw CommandErrors.optionParsingFailed(option, ex);
        }
        return parsed;
    }
}
