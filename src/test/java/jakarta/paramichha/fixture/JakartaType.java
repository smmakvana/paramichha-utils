package jakarta.paramichha.fixture;

/**
 * Test-only class in a jakarta.* package, so JavaTypes.isJavaType can be checked for Jakarta types
 * without adding a Jakarta dependency to this dependency-free library.
 */
public final class JakartaType {
    private JakartaType() {
    }
}
