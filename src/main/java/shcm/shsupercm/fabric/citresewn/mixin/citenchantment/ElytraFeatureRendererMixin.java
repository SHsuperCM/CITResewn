package shcm.shsupercm.fabric.citresewn.mixin.citenchantment;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;
import shcm.shsupercm.fabric.citresewn.pack.cits.CITEnchantment;

@Mixin(ElytraFeatureRenderer.class)
public class ElytraFeatureRendererMixin {
    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"))
    private void setAppliedContextAndStartApplyingElytra(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (CITResewnConfig.INSTANCE().enabled && CITResewn.INSTANCE.activeCITs != null) {
            CITResewn.INSTANCE.activeCITs.setEnchantmentAppliedContextCached(livingEntity.getEquippedStack(EquipmentSlot.CHEST), livingEntity.world, livingEntity);
            CITEnchantment.shouldApply = true;
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("RETURN"))
    private void stopApplyingElytra(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        CITEnchantment.shouldApply = false;
        if (CITResewn.INSTANCE.activeCITs != null)
            CITResewn.INSTANCE.activeCITs.setEnchantmentAppliedContextCached(null, null, null);
    }
}
