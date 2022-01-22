package shcm.shsupercm.fabric.citresewn.format;

public class PropertyValue {
    public final String keyMetadata;
    public final String stringValue;
    public final String delimiter;
    public final int position;

    public Object value = null;

    public PropertyValue(String keyMetadata, String stringValue, String delimiter, int position) {
        this.keyMetadata = keyMetadata;
        this.stringValue = stringValue;
        this.delimiter = delimiter;
        this.position = position;
    }
}
