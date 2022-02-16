package shcm.shsupercm.fabric.citresewn.pack.format;

import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class PropertiesGroupAdapter extends PropertyGroup {
    public static final String EXTENSION = ".properties";

    protected PropertiesGroupAdapter(String packName, Identifier identifier) {
        super(packName, identifier);
    }

    @Override
    public String getExtension() {
        return EXTENSION;
    }

    @Override
    public PropertyGroup load(String packName, Identifier identifier, InputStream is) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            int linePos = 0, multilineSkip = 0;
            while ((line = reader.readLine()) != null) {
                linePos++;
                line = line.stripLeading();
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("!"))
                    continue;

                while (line.endsWith("\\")) {
                    String nextLine = reader.readLine();
                    linePos++;
                    multilineSkip++;
                    if (nextLine == null)
                        nextLine = "";
                    nextLine = nextLine.stripLeading();

                    if (nextLine.startsWith("#") || nextLine.startsWith("!"))
                        continue;

                    line = line.substring(0, line.length() - 1) + "\\n" + nextLine;
                }

                StringBuilder builder = new StringBuilder();

                String key = null, keyMetadata = null;
                final boolean checkColonSeparator = !line.contains("=");

                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);

                    if (c == '\\') { // escape
                        c = switch (c = line.charAt(++i)) {
                            case 'n' -> '\n';
                            case 'r' -> '\r';
                            case 'f' -> '\f';
                            case 't' -> '\t';
                            case 'u' -> {
                                if (i + 4 >= line.length())
                                    yield c;

                                //todo implement manually
                                java.util.Properties properties = new Properties();
                                properties.load(new StringReader("k=\\u" + line.charAt(i + 1) + line.charAt(i + 2) + line.charAt(i + 3) + line.charAt(i + 4)));
                                String k = properties.getProperty("k");
                                if (k.length() == 1) {
                                    i += 4;
                                    yield k.charAt(0);
                                }
                                yield c;
                            }

                            default -> c;
                        };

                    } else if (key == null && (c == '=' || (checkColonSeparator && c == ':'))) {
                        key = builder.toString().stripTrailing();
                        int metadataIndex = key.indexOf('.');
                        if (metadataIndex >= 0) {
                            keyMetadata = key.substring(metadataIndex + 1);
                            key = key.substring(0, metadataIndex);
                        }

                        builder = new StringBuilder();
                        for (i++; i < line.length() && Character.isWhitespace(line.charAt(i)); i++);
                        i--;
                        continue;
                    }

                    builder.append(c);
                }

                if (key == null)
                    throw new IOException("Missing separator in line " + linePos);

                int pos = linePos - multilineSkip;
                multilineSkip = 0;
                this.put(pos, packName, identifier, key, keyMetadata, "=", builder.toString());
            }
        }
        return this;
    }
}
