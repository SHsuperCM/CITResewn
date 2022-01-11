package shcm.shsupercm.fabric.citresewn.pack;

import net.minecraft.resource.ResourcePack;
import shcm.shsupercm.fabric.citresewn.pack.cits.CIT;
import shcm.shsupercm.fabric.citresewn.pack.cits.CITEnchantment;

import java.util.*;

public class CITPack {
    public final ResourcePack resourcePack;
    public final Collection<CIT> cits = new ArrayList<>();

    public CITEnchantment.MergeMethod method = CITEnchantment.MergeMethod.AVERAGE;
    public Integer cap = 8;
    public Float fade = 0.5f;
    public Boolean useGlint = true;

    public CITPack(ResourcePack resourcePack) {
        this.resourcePack = resourcePack;
    }

    public void loadGlobalProperties(Properties properties) throws Exception {
        try {
            this.method = properties.containsKey("method") ? CITEnchantment.MergeMethod.valueOf(properties.getProperty("method").toUpperCase(Locale.ENGLISH)) : null;

            if (properties.containsKey("cap")) {
                this.cap = Integer.parseInt(properties.getProperty("cap"));
                if (this.cap < 0)
                    throw new Exception("cap cannot be negative");
            } else
                this.cap = null;

            if (properties.containsKey("fade")) {
                this.fade = Float.parseFloat(properties.getProperty("fade"));
                if (this.fade < 0f)
                    throw new Exception("fade cannot be negative");
            } else
                this.fade = null;

            this.useGlint = properties.containsKey("useGlint") ? switch (properties.getProperty("useGlint").toLowerCase(Locale.ENGLISH)) {
                case "true" -> true;
                case "false" -> false;
                default -> throw new Exception("useGlint is not a boolean");
            } : null;
        } catch (Exception e) {
            this.method = null;
            this.cap = null;
            this.fade = null;
            this.useGlint = null;
            throw e;
        }
    }

    public void loadGlobalProperties(CITPack properties) {
        if (properties.method != null)
            this.method = properties.method;
        if (properties.cap != null)
            this.cap = properties.cap;
        if (properties.fade != null)
            this.fade = properties.fade;
        if (properties.useGlint != null)
            this.useGlint = properties.useGlint;
    }

}
