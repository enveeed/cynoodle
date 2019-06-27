package cynoodle.module;

import com.google.common.flogger.FluentLogger;
import com.google.common.io.Resources;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rainerhahnekamp.sneakythrow.Sneaky.sneak;

/**
 * Internal utility to handle {@link Module Modules} packaged in <code>.jar</code> files.
 */
final class ModuleFiles {
    private ModuleFiles() {}

    private final static FluentLogger LOG = FluentLogger.forEnclosingClass();

    // ===

    /**
     * The path inside of the .jar file to the {@link ModuleDescriptorFile}.
     */
    public static final Path PATH_DESCRIPTOR = Path.of("module.json");

    /**
     * The content type for <code>.jar</code> archives.
     */
    private static final String MIME_JAR = "application/java-archive";
    /**
     * The content type for <code>.zip</code> archives.
     */
    private static final String MIME_ZIP = "application/zip";

    // ===

    @Nonnull
    public static ModuleDescriptor loadModule(@Nonnull ModuleManager manager, @Nonnull Path file)
        throws IllegalArgumentException, ModuleFileException {
        // ensure that this file is usable
        if(!isModuleFile(file))
            throw new IllegalArgumentException("Path " + file + " is not a valid Module file!");

        // create an URL array to please the good ol' URLClassLoader
        // sneak the exception cause a file path is very unlikely to not be a valid url
        URL[] fileURL = new URL[]{sneak(() -> file.toUri().toURL())};

        // create the classloader for the module file, inheriting platform class loader
        URLClassLoader loader = new URLClassLoader(fileURL, ClassLoader.getPlatformClassLoader());

        // try to find and load the descriptor file
        URL descriptorFileURL = loader.getResource(PATH_DESCRIPTOR.toString());

        if(descriptorFileURL == null)
            throw new ModuleFileException("Module file " + file + " did not contain descriptor file " + PATH_DESCRIPTOR + "!");

        String descriptorJSON;
        try {
            descriptorJSON = Resources.toString(descriptorFileURL, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ModuleFileException("Failed to read descriptor file in Module file " + file + "!", e);
        }

        ModuleDescriptorFile descriptorFile;
        try {
            descriptorFile = ModuleDescriptorFile.parse(descriptorJSON);
        } catch (IllegalArgumentException e) {
            throw new ModuleFileException("Failed to parse descriptor file in Module file " + file + "!", e);
        }

        return ModuleDescriptor.ofFile(descriptorFile);
    }

    @Nonnull
    public static Set<ModuleDescriptor> loadModulesIn(@Nonnull ModuleManager manager, @Nonnull Path directory)
            throws IllegalArgumentException, ModuleFileException{
        // ensure that we have a directory at hand
        if(!Files.isDirectory(directory))
            throw new IllegalArgumentException("Path " + directory + " is not a directory!");


        Stream<Path> files;
        try {
            files = Files.list(directory);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read module directory file list!", e);
        }

        return files.filter(ModuleFiles::isModuleFile)
                .map(path -> loadModule(manager, path))
                .collect(Collectors.toUnmodifiableSet());
    }

    // ===

    public static boolean isModuleFile(@Nonnull Path file) {
        if(!Files.exists(file)) return false; // non-existing files are not module files
        if(!Files.isRegularFile(file)) return false; // not a regular file

        String contentType;
        try {
            contentType = Files.probeContentType(file);
        } catch (IOException e) {
            LOG.atWarning().withCause(e)
                    .log("Encountered IOException during content type probing of %s " +
                            "in ModuleFiles.isModuleFile(Path), file was not allowed!", file);
            return false;
        }
        if(contentType == null) return false; // we can't be sure that this is a jar file

        if(contentType.equals(MIME_JAR)) return true;

        // we allow zip files for compatibility
        if(contentType.equals(MIME_ZIP)) {
            LOG.atWarning().log("Module file %s is " + MIME_ZIP + " instead of " + MIME_JAR + ", allowed" +
                    " it for compatibility but please use " + MIME_JAR + " files instead!", file);
            return true;
        }

        return false;
    }

}
