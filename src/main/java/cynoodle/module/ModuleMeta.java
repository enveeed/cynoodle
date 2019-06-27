package cynoodle.module;

import javax.annotation.Nonnull;
import java.net.URL;
import java.util.Optional;

/**
 * Optional meta data values for {@link Module Modules} in <code>module.json</code>.
 */
public final class ModuleMeta {

    private final String description;
    private final String author;

    private final String link_website;
    private final String link_documentation;

    // ===

    private ModuleMeta(@Nonnull Builder builder) {
        this.description = builder.description;
        this.author = builder.author;
        this.link_website = builder.link_website;
        this.link_documentation = builder.link_documentation;
    }

    // ===

    @Nonnull
    public Optional<String> getDescription() {
        return Optional.ofNullable(this.description);
    }

    @Nonnull
    public Optional<String> getAuthor() {
        return Optional.ofNullable(this.author);
    }

    @Nonnull
    public Optional<String> getLinkWebsite() {
        return Optional.ofNullable(this.link_website);
    }

    @Nonnull
    public Optional<String> getLinkDocumentation() {
        return Optional.ofNullable(this.link_documentation);
    }

    // ===

    public final static class Builder {

        private String description = null;
        private String author = null;
        private String link_website = null;
        private String link_documentation = null;

        // ===

        private Builder() {}

        // ===

        @Nonnull
        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        @Nonnull
        public Builder setAuthor(String author) {
            this.author = author;
            return this;
        }

        @Nonnull
        public Builder setLinkWebsite(String link_website) {
            this.link_website = link_website;
            return this;
        }

        @Nonnull
        public Builder setLinkDocumentation(String link_documentation) {
            this.link_documentation = link_documentation;
            return this;
        }

        // ===

        @Nonnull
        public ModuleMeta build() {
            return new ModuleMeta(this);
        }
    }

    // ===

    @Nonnull
    public static Builder newBuilder() {
        return new Builder();
    }
}
