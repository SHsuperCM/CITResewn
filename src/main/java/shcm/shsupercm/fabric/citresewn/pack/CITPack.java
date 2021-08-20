package shcm.shsupercm.fabric.citresewn.pack;

import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.ex.CITParseException;
import shcm.shsupercm.fabric.citresewn.pack.cits.CIT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

public class CITPack {
    public final ResourcePack resourcePack;
    public final Collection<CIT> cits = new ArrayList<>();

    private EnchantmentMergeMethod method = EnchantmentMergeMethod.AVERAGE;
    private int cap = 8;
    private float fade = 0.5f;
    private boolean useGlint = true;

    public CITPack(ResourcePack resourcePack) {
        this.resourcePack = resourcePack;
    }

    public void loadProperties(Properties properties) {
        method = CITPack.EnchantmentMergeMethod.valueOf(properties.getProperty("method", "average"));
        try {
            cap = Integer.parseInt(properties.getProperty("cap", "8"));
        } catch (NumberFormatException e) {
            CITResewn.LOG.error(new CITParseException(resourcePack, new Identifier("cit.properties"), "cap is not a whole number").getMessage());
        }
        try {
            fade = Float.parseFloat(properties.getProperty("fade", "0.5"));
        } catch (NumberFormatException e) {
            CITResewn.LOG.error(new CITParseException(resourcePack, new Identifier("cit.properties"), "fade is not a number").getMessage());
        }
        switch (properties.getProperty("useGlint", "true")) {
            case "true" -> useGlint = true;
            case "false" -> useGlint = false;
            default -> CITResewn.LOG.error(new CITParseException(resourcePack, new Identifier("cit.properties"), "useGlint is not a boolean").getMessage());
        }
    }

    public enum EnchantmentMergeMethod {
        AVERAGE,
        LAYERED,
        CYCLE
    }
}
