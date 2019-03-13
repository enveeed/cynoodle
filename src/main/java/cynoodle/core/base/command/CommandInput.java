/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.base.command;

import cynoodle.core.api.text.Options;
import cynoodle.core.api.text.ParsingException;

import javax.annotation.Nonnull;
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
    public <T> Optional<T> getParameterAs(int index, @Nonnull String name, @Nonnull Function<String, T> parser) throws CommandError {
        Optional<String> parameter = this.result.getParameters().get(index);
        if(parameter.isEmpty()) return Optional.empty();
        else {
            String content = parameter.orElseThrow();
            T parsed;

            try {
                parsed = parser.apply(content);
            } catch (ParsingException ex) {
                throw CommandErrors.parameterParsingFailed(command, name, ex);
            }

            return Optional.of(parsed);
        }
    }

    //

    @Nonnull
    public String requireParameter(int index, @Nonnull String name) throws CommandError {
        return getParameter(index).orElseThrow(() -> CommandErrors.parameterMissing(command, name));
    }

    @Nonnull
    public <T> T requireParameterAs(int index, @Nonnull String name, @Nonnull Function<String, T> parser) throws CommandError {
        return getParameterAs(index, name, parser).orElseThrow(() -> CommandErrors.parameterMissing(command, name));
    }

    // === OPTIONS ===

    public boolean hasOption(@Nonnull Options.Option option) {
        return this.result.hasOption(option);
    }
}
