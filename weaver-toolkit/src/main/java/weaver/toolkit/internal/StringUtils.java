package weaver.toolkit.internal;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public final class StringUtils {
    public static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
}
