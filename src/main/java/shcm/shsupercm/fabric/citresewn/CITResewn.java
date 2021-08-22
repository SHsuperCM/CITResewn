package shcm.shsupercm.fabric.citresewn;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.WeakHashMap;

@Environment(EnvType.CLIENT)
public class CITResewn implements ClientModInitializer {
    public static final Logger LOG = LogManager.getLogger("citresewn");
    public static CITResewn INSTANCE;

    public final WeakHashMap<BakedModel, String> bakedOverridesCache = new WeakHashMap<>();

    public ActiveCITs activeCITs = null;

    @Override
    public void onInitializeClient() {
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
