package shcm.shsupercm.fabric.citresewn.mixin.citarmor;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;

import java.lang.ref.WeakReference;
import java.util.Map;

@Mixin(ArmorFeatureRenderer.class)
public class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> {
    private WeakReference<Map<String, Identifier>> armorTexturesCached = null;

    @Inject(method = "renderArmor", cancellable = true, at = @At("HEAD"))
    public void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
        if (!CITResewnConfig.INSTANCE().enabled || CITResewn.INSTANCE.activeCITs == null)
            return;

        ItemStack itemStack = entity.getEquippedStack(armorSlot);
        Map<String, Identifier> armorTextures = CITResewn.INSTANCE.activeCITs.getArmorTextures(itemStack, entity.world, entity);
        if (armorTextures != null) {
            armorTexturesCached = new WeakReference<>(armorTextures);
            return;
        }

        armorTexturesCached = null;
    }

    @Inject(method = "getArmorTexture", cancellable = true, at = @At("HEAD"))
    public void getArmorTexture(ArmorItem item, boolean legs, String overlay, CallbackInfoReturnable<Identifier> cir) {
        if (armorTexturesCached == null)
            return;
        Map<String, Identifier> armorTextures = armorTexturesCached.get();
        if (armorTextures == null)
            return;

        Identifier identifier = armorTextures.get(item.getMaterial().getName() + "_layer_" + (legs ? "2" : "1") + (overlay == null ? "" : "_" + overlay));
        if (identifier != null)
            cir.setReturnValue(identifier);
    }
}
