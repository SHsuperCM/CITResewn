package shcm.shsupercm.fabric.citresewn.pack;

import net.minecraft.resource.ResourcePack;
import shcm.shsupercm.fabric.citresewn.pack.cits.CIT;

import java.util.*;

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

    public void loadGlobalProperties(Properties properties) throws Exception {
        try {
            this.method = CITPack.EnchantmentMergeMethod.valueOf(properties.getProperty("method", "average").toUpperCase(Locale.ENGLISH));
            this.cap = Integer.parseInt(properties.getProperty("cap", "8"));
            if (this.cap < 0)
                throw new Exception("cap cannot be negative");
            this.fade = Float.parseFloat(properties.getProperty("fade", "0.5"));
            if (this.fade < 0f)
                throw new Exception("fade cannot be negative");
            this.useGlint = switch (properties.getProperty("useGlint", "true").toLowerCase(Locale.ENGLISH)) {
                case "true" -> true;
                case "false" -> false;
                default -> throw new Exception("useGlint is not a boolean");
            };
        } catch (Exception e) {
            this.method = EnchantmentMergeMethod.AVERAGE;
            this.cap = 8;
            this.fade = 0.5f;
            this.useGlint = true;
            throw e;
        }
    }

    public enum EnchantmentMergeMethod {
        AVERAGE,
        LAYERED,
        CYCLE
    }
}
