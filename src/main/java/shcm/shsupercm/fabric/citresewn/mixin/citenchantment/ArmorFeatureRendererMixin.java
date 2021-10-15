package shcm.shsupercm.fabric.citresewn.mixin.citenchantment;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;
import shcm.shsupercm.fabric.citresewn.pack.cits.CITEnchantment;

@Mixin(ArmorFeatureRenderer.class)
public class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> {
    @Inject(method = "renderArmor", at = @At("HEAD"))
    private void setAppliedContextAndStartApplyingArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T livingEntity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
        if (CITResewnConfig.INSTANCE().enabled && CITResewn.INSTANCE.activeCITs != null) {
            CITResewn.INSTANCE.activeCITs.setEnchantmentAppliedContextCached(livingEntity.getEquippedStack(armorSlot), livingEntity.world, livingEntity);
            CITEnchantment.shouldApply = true;
        }
    }

    @Inject(method = "renderArmor", at = @At("RETURN"))
    private void stopApplyingArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T livingEntity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
        CITEnchantment.shouldApply = false;
        if (CITResewn.INSTANCE.activeCITs != null)
            CITResewn.INSTANCE.activeCITs.setEnchantmentAppliedContextCached(null, null, null);
    }
}
