package shcm.shsupercm.fabric.citresewn;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;

@Environment(EnvType.CLIENT)
public class CITResewn implements ClientModInitializer {
    public static final Logger LOG = LogManager.getLogger("CITResewn");
    public static CITResewn INSTANCE;

    public ActiveCITs activeCITs = null;

    public CITResewnConfig config = null;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        config = CITResewnConfig.read();

        /*net.minecraft.client.render.entity.feature.ArmorFeatureRenderer#renderArmor
            take entity.getEquippedStack(armorSlot)
            eval override
            save shadow String cachedOverride /null

        /*net.minecraft.client.render.entity.feature.ArmorFeatureRenderer#getArmorTexture
            if(shadow cachedOverride != null)
                mxreturn shadow cachedOverride

         */

        /*

         */
    }
}
