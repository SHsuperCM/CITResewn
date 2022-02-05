package shcm.shsupercm.fabric.citresewn.pack.format;

import net.minecraft.util.Identifier;

public record PropertyValue(String keyMetadata,
                            String value,
                            String delimiter,
                            int position,
                            Identifier propertiesIdentifier,
                            String packName) {
}
