package shcm.shsupercm.fabric.citresewn;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class CITResewn implements ClientModInitializer {
    public static final Logger LOG = LogManager.getLogger("citresewn");
    public static CITResewn INSTANCE;

    public ActiveCITs activeCITs = null;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        /*net.minecraft.client.render.model.json.ModelOverrideList#apply
            eval override
         */

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
