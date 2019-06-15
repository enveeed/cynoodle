package cynoodle.util.options;

import cynoodle.util.text.Parameters;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * The result of a parsing operation using {@link Options}.
 */
public final class OptionsResult {

    private final Options parser;
    private final String source;

    // ===

    private final List<String> parameters;
    private final List<Option> options;

    private final Map<Option, String> optionsValues;

    private final Parameters wrappedParameters;

    // ===

    OptionsResult(@Nonnull Options parser,
                          @Nonnull String source,
                          @Nonnull List<String> parameters,
                          @Nonnull List<Option> options,
                          @Nonnull Map<Option, String> optionsValues) {
        this.parser = parser;
        this.source = source;

        this.parameters = Collections.unmodifiableList(parameters);
        this.options = Collections.unmodifiableList(options);
        this.optionsValues = Collections.unmodifiableMap(optionsValues);

        this.wrappedParameters = Parameters.of(this.parameters);
    }

    // ===

    /**
     * Get the {@link Options} instance which was used to create this result.
     * @return the parser instance
     */
    @Nonnull
    public Options getParser() {
        return this.parser;
    }

    /**
     * Get the source string which was parsed for the creation of this result.
     * @return the source string
     */
    @Nonnull
    public String getSource() {
        return this.source;
    }

    // ===

    /**
     * Get a list of all parameters which were given, in order of occurrence.
     * @return the parameter list
     */
    @Nonnull
    public Parameters getParameters() {
        return this.wrappedParameters;
    }

    @Nonnull
    public List<String> getParametersRaw() {
        return this.parameters;
    }

    /**
     * Get a set of all options which were given, in order of occurrence.
     * @return the options list
     */
    @Nonnull
    public List<Option> getOptions() {
        return this.options;
    }

    // ===

    public boolean hasOption(@Nonnull Option option) {
        return this.options.contains(option);
    }

    @Nonnull
    public String getOptionValue(@Nonnull Option option)
            throws IllegalArgumentException, NoSuchElementException {

        if (!option.isValueRequired())
            throw new IllegalArgumentException("Option which do not require values can not have values!");
        if (!hasOption(option))
            throw new NoSuchElementException("Option was not given!");

        else return this.optionsValues.get(option);
    }

    // ===

    @Override
    public String toString() {
        return "Result{" +
                "parameters=" + parameters +
                ", options=" + options +
                ", values=" + optionsValues +
                '}';
    }

}
