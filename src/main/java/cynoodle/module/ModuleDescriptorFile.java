package cynoodle.module;

import com.github.jsonj.JsonArray;
import com.github.jsonj.JsonObject;
import com.github.jsonj.tools.JsonParser;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

/**
 * The parsed form of a <code>module.json</code> {@link Module} descriptor file.
 */
public final class ModuleDescriptorFile {

    public static final String KEY_IDENTIFIER = "id";
    public static final String KEY_VERSION = "version";
    public static final String KEY_CLASS = "class";
    public static final String KEY_DEPENDENCIES = "dependencies";

    public static final String KEY_META = "meta";

    public static final String KEY_META_DESCRIPTION = "description";
    public static final String KEY_META_AUTHOR = "author";
    public static final String KEY_META_LINK_WEBSITE = "link_website";
    public static final String KEY_META_LINK_DOCUMENTATION = "link_documentation";

    // ===

    private final ModuleIdentifier identifier;
    private final String version;

    private final String className;
    private final Set<ModuleIdentifier> dependencies;

    private final ModuleMeta meta;

    // ===

    private ModuleDescriptorFile(@Nonnull ModuleIdentifier identifier, @Nonnull String version,
                                 @Nonnull String className,
                                 @Nonnull Set<ModuleIdentifier> dependencies,
                                 @Nonnull ModuleMeta meta) {
        this.identifier = identifier;
        this.version = version;
        this.className = className;
        this.dependencies = dependencies;
        this.meta = meta;
    }

    // ===

    @Nonnull
    public ModuleIdentifier getIdentifier() {
        return this.identifier;
    }

    @Nonnull
    public String getVersion() {
        return this.version;
    }

    //

    @Nonnull
    public String getClassName() {
        return this.className;
    }

    @Nonnull
    public Set<ModuleIdentifier> getDependencies() {
        return this.dependencies;
    }

    //

    @Nonnull
    public ModuleMeta getMeta() {
        return this.meta;
    }

    // ===

    @Nonnull
    public static ModuleDescriptorFile parse(@Nonnull String json) throws IllegalArgumentException {

        JsonParser parser = new JsonParser();

        JsonObject root = parser.parseObject(json);

        // required properties

        String rawIdentifier = root.maybeGetString(KEY_IDENTIFIER)
                .orElseThrow(() -> new IllegalArgumentException(KEY_IDENTIFIER + " is missing from JSON file but is required!"));
        String version = root.maybeGetString(KEY_VERSION)
                .orElseThrow(() -> new IllegalArgumentException(KEY_VERSION + " is missing from JSON file but is required!"));

        String className = root.maybeGetString(KEY_CLASS)
                .orElseThrow(() -> new IllegalArgumentException(KEY_CLASS + " is missing from JSON file but is required!"));

        JsonArray dependenciesArray = root.maybeGetArray(KEY_DEPENDENCIES)
                .orElseThrow(() -> new IllegalArgumentException(KEY_DEPENDENCIES + " is missing from JSON file but is required!"));

        String[] rawDependencies = dependenciesArray.asStringArray();

        // meta data

        JsonObject metaObject = root.maybeGetObject(KEY_META).orElse(null);

        ModuleMeta.Builder metaBuilder = ModuleMeta.newBuilder();
        if(metaObject != null) {
            root.maybeGetString(KEY_META_DESCRIPTION).ifPresent(metaBuilder::setDescription);
            root.maybeGetString(KEY_META_AUTHOR).ifPresent(metaBuilder::setAuthor);
            root.maybeGetString(KEY_META_LINK_WEBSITE).ifPresent(metaBuilder::setLinkWebsite);
            root.maybeGetString(KEY_META_LINK_DOCUMENTATION).ifPresent(metaBuilder::setLinkDocumentation);
        }

        ModuleMeta meta = metaBuilder.build();

        // parse identifiers

        ModuleIdentifier identifier = ModuleIdentifier.parse(rawIdentifier);

        Set<ModuleIdentifier> dependencies = new HashSet<>();

        for (String rawDependency : rawDependencies) {
            dependencies.add(ModuleIdentifier.parse(rawDependency));
        }

        // ===

        return new ModuleDescriptorFile(identifier, version,
                className,
                dependencies,
                meta);
    }
}
