package cynoodle.module;

/**
 * Used in {@link ModuleFiles} for exceptions involving the loading of module files.
 */
public final class ModuleFileException extends RuntimeException {

    ModuleFileException() {
        super();
    }

    ModuleFileException(String message) {
        super(message);
    }

    ModuleFileException(String message, Throwable cause) {
        super(message, cause);
    }

    ModuleFileException(Throwable cause) {
        super(cause);
    }
}
