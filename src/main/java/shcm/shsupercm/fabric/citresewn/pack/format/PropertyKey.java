package shcm.shsupercm.fabric.citresewn.pack.format;

public record PropertyKey(String namespace, String path) {
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
