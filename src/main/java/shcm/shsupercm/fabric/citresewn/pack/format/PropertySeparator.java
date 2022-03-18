package shcm.shsupercm.fabric.citresewn.pack.format;

/**
 * Marker for the connection between a {@link PropertyKey} and its {@link PropertyValue}.
 */
public enum PropertySeparator {
    /**
     * Marks either a check for equality or an action to set a value.
     */
    EQUALS("=")
    ;

    /**
     * String representation of the separator.
     */
    public final String separator;

    PropertySeparator(String separator) {
        this.separator = separator;
    }
}
