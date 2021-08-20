package shcm.shsupercm.fabric.citresewn;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.profiler.Profiler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Environment(EnvType.CLIENT)
public class CITResewnClient implements ClientModInitializer {
    public static CITResewnClient INSTANCE;

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
