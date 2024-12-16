package io.shcm.fabric.citresewn;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CITResewn {
    public static final String MODID = "citresewn";
    public static final Logger LOG = LogManager.getLogger(MODID);

    public static Identifier id(String path) {
        return Identifier.of(MODID, path);
    }
}
