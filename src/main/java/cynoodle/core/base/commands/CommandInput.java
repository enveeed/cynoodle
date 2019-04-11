/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.commands;

import cynoodle.core.api.text.Options;
import cynoodle.core.api.parser.Parser;
import cynoodle.core.api.parser.ParsingException;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

public final class CommandInput {

    private final Command command;
    private final Options.Result result;

    // ===

    private CommandInput(@Nonnull Command command, @Nonnull Options.Result result) {
        this.command = command;
        this.result = result;
    }

    // ===

    @Nonnull
    static CommandInput wrap(@Nonnull Command command, @Nonnull Options.Result result) {
        return new CommandInput(command, result);
    }

    // ===

    @Nonnull
    public Options.Result raw() {
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

    public boolean hasOption(@Nonnull Options.Option option) {
        return this.result.hasOption(option);
    }

    //

    @Nonnull
    public String getOptionValue(@Nonnull Options.Option option) throws NoSuchElementException {
        if(!hasOption(option))
            throw new NoSuchElementException("No such option: " + option.getLong());
        return this.result.getOptionValue(option);
    }

    @Nonnull
    public <T> T getOptionValueAs(@Nonnull Options.Option option, @Nonnull Parser<T> parser) throws NoSuchElementException, CommandError {
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
