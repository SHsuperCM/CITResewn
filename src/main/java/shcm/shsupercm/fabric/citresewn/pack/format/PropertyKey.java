package shcm.shsupercm.fabric.citresewn.pack.format;

import net.minecraft.util.Identifier;

/**
 * Namespace/path pair of strings. Similar to {@link Identifier} but without validity restrictions.
 * @see Identifier
 */
public record PropertyKey(String namespace, String path) {
    /**
     * Attempts to split a given string into a namespace and path by the first occurrence of a colon.<br>
     * If a namespace cannot be extracted from the given string, "citresewn" is set instead.
     * @param key key to parse
     * @return parsed property key
     */
    public static PropertyKey of(String key) {
        String[] split = new String[] {"citresewn", key};
        int i = key.indexOf(':');
        if (i >= 0) {
            split[1] = key.substring(i + 1);
            if (i >= 1)
                split[0] = key.substring(0, i);
        }
        return new PropertyKey(split[0], split[1]);
    }

    @Override
    public String toString() {
        return namespace + ":" + path;
    }
}
